package com.example

import kotlinx.benchmark.*
import org.openjdk.jmh.annotations.*
import java.util.concurrent.TimeUnit

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
open class TrainBenchmark {

    private var data: List<Int> = emptyList()

    @Setup
    fun setup() {
        data = (1..1000).toList()
    }

    @Benchmark
    fun testFilter(): List<Int> {
        return data.filter { it % 2 == 0 }
    }

    @Benchmark
    fun testMap(): List<Int> {
        return data.map { it * 2 }
    }
}