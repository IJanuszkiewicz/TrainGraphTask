import java.io.File
import java.util.Scanner

data class TrainStation(val id: Int, val consumes: List<Int>, val loads: List<Int>)

object SetOps {
    fun sortedUnion(l1: List<Int>, l2: List<Int>): List<Int> {
        if (l1.isEmpty()) return l2
        if (l2.isEmpty()) return l1

        val res = ArrayList<Int>(l1.size + l2.size)
        var i = 0
        var j = 0
        while (i < l1.size && j < l2.size) {
            val a = l1[i]
            val b = l2[j]
            when {
                a < b -> { res.add(a); i++ }
                b < a -> { res.add(b); j++ }
                else -> { res.add(a); i++; j++ }
            }
        }
        while (i < l1.size) res.add(l1[i++])
        while (j < l2.size) res.add(l2[j++])
        return res
    }

    fun sortedMinus(source: List<Int>, toRemove: List<Int>): List<Int> {
        if (source.isEmpty()) return emptyList()
        if (toRemove.isEmpty()) return source

        val res = ArrayList<Int>(source.size)
        var i = 0
        var j = 0
        while (i < source.size && j < toRemove.size) {
            val s = source[i]
            val r = toRemove[j]
            when {
                s < r -> { res.add(s); i++ }
                s > r -> { j++ }
                else -> { i++; j++ }
            }
        }
        while (i < source.size) res.add(source[i++])
        return res
    }
}

class StationGraph(
    val adjList: List<List<Int>>,
    val stations: List<TrainStation>,
    val stationIdToIndex: Map<Int, Int>
) {

    private fun getIndex(id: Int): Int = stationIdToIndex[id] ?: error("Unknown station $id")

    fun solveStandard(startNodeId: Int): List<List<Int>> {
        val size = adjList.size
        val startIdx = getIndex(startNodeId)

        val posArrival = MutableList(size) { emptyList<Int>() }

        val posDeparture = MutableList(size) { stations[it].loads }

        val reachable = BooleanArray(size)
        reachable[startIdx] = true

        posDeparture[startIdx] = stations[startIdx].loads

        repeat(size) {
            var changed = false
            for (u in 0 until size) {
                if (!reachable[u]) continue

                for (v in adjList[u]) {
                    if (!reachable[v]) {
                        reachable[v] = true
                        changed = true
                    }

                    val oldArrival = posArrival[v]
                    val incoming = posDeparture[u]
                    val newArrival = oldArrival.union(incoming).toList()

                    if (newArrival.size != oldArrival.size) {
                        posArrival[v] = newArrival

                        val afterUnload = newArrival.minus(stations[v].consumes)
                        val afterLoad = afterUnload.union(stations[v].loads).toList()

                        posDeparture[v] = afterLoad
                        changed = true
                    }
                }
            }
            if (!changed) return@repeat
        }
        return posArrival
    }

    fun solveOptimized(startNodeId: Int): List<List<Int>> {
        val size = adjList.size
        val startIdx = getIndex(startNodeId)

        val sortedLoads = stations.map { it.loads.sorted() }
        val sortedConsumes = stations.map { it.consumes.sorted() }

        val posArrival = MutableList(size) { emptyList<Int>() }
        val posDeparture = MutableList(size) { sortedLoads[it] }
        val reachable = BooleanArray(size)

        reachable[startIdx] = true

        repeat(size) {
            var changed = false
            for (u in 0 until size) {
                if (!reachable[u]) continue

                for (v in adjList[u]) {
                    if (!reachable[v]) {
                        reachable[v] = true
                        changed = true
                    }

                    val oldArrival = posArrival[v]
                    val incoming = posDeparture[u]

                    val newArrival = SetOps.sortedUnion(oldArrival, incoming)

                    if (newArrival != oldArrival) {
                        posArrival[v] = newArrival

                        val afterUnload = SetOps.sortedMinus(newArrival, sortedConsumes[v])
                        val afterLoad = SetOps.sortedUnion(afterUnload, sortedLoads[v])

                        posDeparture[v] = afterLoad
                        changed = true
                    }
                }
            }
            if (!changed) return@repeat
        }
        return posArrival
    }
}

// --- Parsing Logic ---
object RailwayParser {
    fun parse(input: String): Pair<StationGraph, Int> {
        val scanner = Scanner(input)
        if (!scanner.hasNext()) error("Empty input")

        val sCount = scanner.nextInt()
        val tCount = scanner.nextInt()

        val rawStations = mutableListOf<TrainStation>()
        val idMap = mutableMapOf<Int, Int>()

        for (i in 0 until sCount) {
            val id = scanner.nextInt()
            val consume = scanner.nextInt()
            val load = scanner.nextInt()

            idMap[id] = i

            val consumeList = if (consume > 0) listOf(consume) else emptyList()
            val loadList = if (load > 0) listOf(load) else emptyList()

            rawStations.add(TrainStation(id, consumeList, loadList))
        }

        val adj = MutableList(sCount) { mutableListOf<Int>() }

        for (i in 0 until tCount) {
            val uRaw = scanner.nextInt()
            val vRaw = scanner.nextInt()
            val u = idMap[uRaw] ?: error("Unknown station $uRaw in tracks")
            val v = idMap[vRaw] ?: error("Unknown station $vRaw in tracks")
            adj[u].add(v)
        }

        val startStationId = scanner.nextInt()

        return Pair(StationGraph(adj, rawStations, idMap), startStationId)
    }
}

fun main(args: Array<String>) {
    val inputData = if (args.isNotEmpty()) {
        val file = File(args[0])
        if (!file.exists()) {
            System.err.println("Error: File not found at ${args[0]}")
            return
        }
        file.readText()
    } else {
        println("Enter input (Ctrl+D to finish):")
        generateSequence(::readLine).joinToString("\n")
    }

    if (inputData.isBlank()) {
        println("No input provided.")
        return
    }

    try {
        val (graph, startId) = RailwayParser.parse(inputData)


        println("\n--- Solve ---")
        val res2 = graph.solveOptimized(startId)
        res2.forEachIndexed { idx, cargo ->
            println("Station ${graph.stations[idx].id} Arrival Cargo: $cargo")
        }

    } catch (e: Exception) {
        System.err.println("Error parsing input: ${e.message}")
    }
}
