package phonebook

import java.io.File
import kotlin.math.sqrt


data class SortResult(val time: Long, val complete: Boolean)
data class SearchResult(val time: Long, val complete: Boolean, val entriesFound: Int)

fun String.removeId(): String = this.split(' ', limit = 2)[1]

fun bubbleSort(mutableDirectory: MutableList<String>, limit: Long): SortResult {
    val timeStart = System.currentTimeMillis()
    for (i in mutableDirectory.size - 2 downTo 0) {
        val currTime = System.currentTimeMillis()
        if (limit != -1L && currTime > timeStart + limit) return SortResult(currTime - timeStart, false)
        for (j in 0..i) {
            if (mutableDirectory[j].removeId() < mutableDirectory[j + 1].removeId()) {
                val tem = mutableDirectory[j]
                mutableDirectory[j] = mutableDirectory[j + 1]
                mutableDirectory[j + 1] = tem
            }
        }
    }
    val timeEnd = System.currentTimeMillis()
    return SortResult(timeEnd - timeStart, true)
}

fun quickSort(mutableDirectory: MutableList<String>): SortResult {

    fun partition(low: Int, high: Int): Int {
        val pivot = mutableDirectory[high].removeId()
        var i = low - 1
        var j = low
        while (j < high) {
            if (mutableDirectory[j].removeId() < pivot) {
                val tem = mutableDirectory[j]
                mutableDirectory[j] = mutableDirectory[i + 1]
                mutableDirectory[i + 1] = tem
                i++
            }
            j++
        }
        val tem = mutableDirectory[high]
        mutableDirectory[high] = mutableDirectory[i + 1]
        mutableDirectory[i + 1] = tem

        return i + 1
    }

    fun quickSortHelper(low: Int, high: Int) {
        if (low < high) {
            val pi = partition(low, high)
            quickSortHelper(low, pi - 1)
            quickSortHelper(pi + 1, high)
        }
    }

    val timeStart = System.currentTimeMillis()
    quickSortHelper(0, mutableDirectory.size - 1)
    val timeEnd = System.currentTimeMillis()
    return SortResult(timeEnd - timeStart, true)
}


fun binarySearch(mutableDirectory: MutableList<String>, queries: List<String>, limit: Long): SearchResult {
    fun jumpSearchHelper(query: String, start: Int, end: Int): Boolean {
        if (start == end) return (mutableDirectory[start].removeId() == query)
        val mid = (start + end) / 2
        return when {
            mutableDirectory[mid].removeId() < query -> jumpSearchHelper(query, mid + 1, end)
            mutableDirectory[mid].removeId() > query -> jumpSearchHelper(query, start, mid)
            else -> true
        }
    }
    var entriesFound = 0
    val timeStart = System.currentTimeMillis()
    for (query in queries) {
        val currTime = System.currentTimeMillis()
        if (limit != -1L && currTime > timeStart + limit) return SearchResult(currTime - timeStart, false, entriesFound)
        if (jumpSearchHelper(query, 0, mutableDirectory.size - 1)) {
            entriesFound++
        }
    }
    val timeEnd = System.currentTimeMillis()
    return SearchResult(timeEnd - timeStart, true, entriesFound)
}

fun linearSearch(mutableDirectory: MutableList<String>, queries: List<String>, limit: Long): SearchResult {
    var entriesFound = 0
    val timeStart = System.currentTimeMillis()
    for (query in queries) {
        val currTime = System.currentTimeMillis()
        if (limit != -1L && currTime > timeStart + limit) return SearchResult(currTime - timeStart, false, entriesFound)
        for (line in mutableDirectory) {
            if (line.removeId() == query) {
                entriesFound++
            }
        }
    }
    val timeEnd = System.currentTimeMillis()
    return SearchResult(timeEnd - timeStart, true, entriesFound)
}

fun jumpSearch(mutableDirectory: MutableList<String>, queries: List<String>, limit: Long): SearchResult {
    var entriesFound = 0
    val timeStart = System.currentTimeMillis()
    for (query in queries) {
        val currTime = System.currentTimeMillis()
        if (limit != -1L && currTime > timeStart + limit) return SearchResult(currTime - timeStart, false, entriesFound)
        val n = mutableDirectory.size
        val step = sqrt(n.toDouble()).toInt()
        var index = step
        while (mutableDirectory[index].removeId() < query) {
            if (index + step >= n) {
                index = n - 1
                break
            }
            index += step
        }

        for (i in index downTo index - step + 1) {
            val k = mutableDirectory[index].removeId()
            if (k >= query) {
                if (k == query) entriesFound++
                break
            }
        }
    }
    val timeEnd = System.currentTimeMillis()
    return SearchResult(timeEnd - timeStart, true, entriesFound)
}

fun makeHashMap(mutableDirectory: MutableList<String>, hashMap: HashMap<String, Boolean>): SortResult {
    val timeStart = System.currentTimeMillis()
    for (entry in mutableDirectory) {
        val key = entry.removeId()
        hashMap[key] = true
    }
    val timeEnd = System.currentTimeMillis()
    return SortResult(timeEnd - timeStart, true)
}

fun findHashMap(hashMap: HashMap<String, Boolean>, queries: List<String>): SearchResult {
    var entriesFound = 0
    val timeStart = System.currentTimeMillis()
    for (query in queries) {
        if (hashMap.containsKey(query)) entriesFound++
    }
    val timeEnd = System.currentTimeMillis()
    return SearchResult(timeEnd - timeStart, true, entriesFound)
}

fun timeString(diff: Long): String {
    val milliSeconds = diff % 1000
    val seconds = (diff / 1000 % 60)
    val minutes = (diff / (1000 * 60))
    return "$minutes min. $seconds sec. $milliSeconds ms."
}

fun main() {
    val baseDirectory = "/home/pasha/Desktop"
    val directory = File("$baseDirectory/directory.txt").readLines()
    val findStrings = File("$baseDirectory/find.txt").readLines()
    var mutableDirectory = directory.toMutableList()

    println("Start searching (linear search)...")
    val linearSearchResult = linearSearch(mutableDirectory, findStrings, -1L)
    println("Found ${linearSearchResult.entriesFound} / 500 entries. Time taken: ${timeString(linearSearchResult.time)}")
    println()

    val limit = linearSearchResult.time * 10
    println("Start searching (bubble sort + jump search)...")
    val bubbleSortResult = bubbleSort(mutableDirectory, limit)
    if (bubbleSortResult.complete) {
        val jumpSearchResult = jumpSearch(mutableDirectory, findStrings, -1L)
        val totalTimeTaken = jumpSearchResult.time + bubbleSortResult.time
        println("Found ${jumpSearchResult.entriesFound} / 500 entries. Time taken: ${timeString(totalTimeTaken)}")
        println("Sorting time: ${timeString(bubbleSortResult.time)}")
        println("Searching time: ${timeString(jumpSearchResult.time)}")
    } else {
        val searchResult = linearSearch(mutableDirectory, findStrings, -1L)
        val totalTimeTaken = searchResult.time + bubbleSortResult.time
        println("Found ${linearSearchResult.entriesFound} / 500 entries. Time taken: ${timeString(totalTimeTaken)}")
        println("Sorting time: ${timeString(bubbleSortResult.time)} - STOPPED, moved to linear search")
        println("Searching time: ${timeString(searchResult.time)}")
    }
    println()

    mutableDirectory = directory.toMutableList()
    println("Start searching (quick sort + binary search)...")
    val quickSortResult = quickSort(mutableDirectory)
    val binarySearchResult = binarySearch(mutableDirectory, findStrings, -1L)
    val totalTimeTakenQuick = quickSortResult.time + binarySearchResult.time
    println("Found ${binarySearchResult.entriesFound} / 500 entries. Time taken: ${timeString(totalTimeTakenQuick)}")
    println("Sorting time: ${timeString(quickSortResult.time)}")
    println("Searching time: ${timeString(binarySearchResult.time)}")
    println()

    mutableDirectory = directory.toMutableList()
    val hashMap: HashMap<String, Boolean> = HashMap()
    println("Start searching (hash table)...")
    val makeHashTableResult = makeHashMap(mutableDirectory, hashMap)
    val searchHashtableResult = findHashMap(hashMap, findStrings)
    val totalTimeTakenHash = makeHashTableResult.time + searchHashtableResult.time
    println("Found ${searchHashtableResult.entriesFound} / 500 entries. Time taken: ${timeString(totalTimeTakenHash)}")
    println("Creating time: ${timeString(makeHashTableResult.time)}")
    println("Searching time: ${timeString(searchHashtableResult.time)}")
    println()
}
