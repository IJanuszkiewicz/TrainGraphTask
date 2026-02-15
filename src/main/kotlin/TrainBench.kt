package com.example.benchmark

import StationGraph
import TrainStation
import kotlinx.benchmark.*
import java.util.concurrent.TimeUnit
import kotlin.random.Random

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(BenchmarkTimeUnit.MILLISECONDS)
open class TrainBenchmark {

    private lateinit var graph: StationGraph

    fun randomStation(id: Int, numCargos: Int): TrainStation {
        val consumes = (0 until numCargos).filter { Random.nextBoolean() }
        val loads = (0 until numCargos).filter { Random.nextBoolean() }

        return TrainStation(id, consumes, loads)
    }

    fun genTestCase(n: Int, edgeProb: Float, numCargos: Int): StationGraph {
        val stations = ArrayList<TrainStation>(n)
        val idMap = mutableMapOf<Int, Int>()

        for (i in 0 until n) {
            stations.add(randomStation(i, numCargos))
            idMap[i] = i
        }

        val adj = MutableList(n) { mutableListOf<Int>() }
        for (i in 0 until n) {
            for (j in 0 until n) {
                if (i != j && Random.nextFloat() < edgeProb) {
                    adj[i].add(j)
                }
            }
        }

        return StationGraph(adj, stations, idMap)
    }

    @Setup
    fun setup() {
        graph = genTestCase(n = 500, edgeProb = 0.1f, numCargos = 20)
    }

    @Benchmark
    fun solveStandard(): List<List<Int>> {
        return graph.solveStandard(0)
    }

    @Benchmark
    fun solveOptimized(): List<List<Int>> {
        return graph.solveOptimized(0)
    }
}
