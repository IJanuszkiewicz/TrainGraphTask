import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TrainTest {

    // Helper to create the graph with IDs and Map automatically
    private fun createGraph(adj: List<List<Int>>, stationsData: List<Pair<List<Int>, List<Int>>>): StationGraph {
        val stations = stationsData.mapIndexed { index, (consume, load) ->
            TrainStation(index, consume, load)
        }

        // Since we are manually creating indices 0..N, the map is trivial
        val idMap = stations.associate { it.id to it.id }

        return StationGraph(adj, stations, idMap)
    }

    // Helper to test BOTH implementations
    private fun assertGraphSolves(expected: List<List<Int>>, graph: StationGraph, startNode: Int) {
        // 1. Test Standard
        val standardResult = graph.solveStandard(startNode).map { it.sorted() }
        assertEquals(expected, standardResult, "Standard implementation failed")

        // 2. Test Optimized
        val optimizedResult = graph.solveOptimized(startNode).map { it.sorted() }
        assertEquals(expected, optimizedResult, "Optimized implementation failed")
    }

    @Test
    fun testSimpleTree() {
        val stationsData = listOf<Pair<List<Int>, List<Int>>>(
            Pair(listOf(1), listOf(2)),         // 0
            Pair(listOf(), listOf(2, 3)),       // 1
            Pair(listOf(3, 4), listOf(1)),      // 2
            Pair(listOf(2), listOf(2, 4)),      // 3
            Pair(listOf(1, 4), listOf(1, 2, 3)) // 4
        )

        val adj = listOf(
            listOf(1),
            listOf(2, 3),
            listOf(),
            listOf(4),
            listOf(),
        )

        val graph = createGraph(adj, stationsData)

        val expectedResult = listOf(
            listOf(),
            listOf(2),
            listOf(2, 3),
            listOf(2, 3),
            listOf(2, 3, 4)
        )

        assertGraphSolves(expectedResult, graph, 0)
    }

    @Test
    fun simpleLoop() {
        val stationsData = listOf<Pair<List<Int>, List<Int>>>(
            Pair(listOf(), listOf(0)),
            Pair(listOf(), listOf(1)),
            Pair(listOf(), listOf(2)),
        )

        val adj = listOf(
            listOf(1),
            listOf(2),
            listOf(0),
        )

        val graph = createGraph(adj, stationsData)

        val expectedResult = listOf(
            listOf(0, 1, 2),
            listOf(0, 1, 2),
            listOf(0, 1, 2),
        )

        assertGraphSolves(expectedResult, graph, 0)
    }

    @Test
    fun simpleLoopReversed() {
        val stationsData = listOf<Pair<List<Int>, List<Int>>>(
            Pair(listOf(), listOf(0)),
            Pair(listOf(), listOf(1)),
            Pair(listOf(), listOf(2)),
        )

        val adj = listOf(
            listOf(2),
            listOf(0),
            listOf(1),
        )

        val graph = createGraph(adj, stationsData)

        val expectedResult = listOf(
            listOf(0, 1, 2),
            listOf(0, 1, 2),
            listOf(0, 1, 2),
        )

        assertGraphSolves(expectedResult, graph, 0)
    }

    @Test
    fun fullGraph() {

        val stationsData = listOf(
            Pair(listOf(1, 2), listOf(1, 2)),
            Pair(listOf(3, 4), listOf(0)),
            Pair(listOf(6, 7, 1, 2), listOf(1, 2, 3, 4)),
            Pair(listOf(1, 2, 3), listOf(5, 6, 7)),
            Pair(listOf(2), listOf(2)),
        )

        val adj = listOf(
            listOf(1, 2, 3, 4),
            listOf(0, 2, 3, 4),
            listOf(0, 1, 3, 4),
            listOf(0, 1, 2, 4),
            listOf(0, 1, 2, 3),
        )

        val graph = createGraph(adj, stationsData)

        val fullSet = listOf(0, 1, 2, 3, 4, 5, 6, 7)
        val expectedResult = listOf(
            fullSet,
            fullSet,
            fullSet,
            fullSet,
            fullSet,
        )

        assertGraphSolves(expectedResult, graph, 0)
    }
}
