# Trains

Task 1 for JetBrains Internship.

## Algorithm Idea

My algorithm is a modification of the Bellman-Ford algorithm. It uses a relaxation approach where cargo information is propagated to neighboring stations $N$ times (where $N$ is the number of stations). This ensures that even in cyclic graphs, all reachable cargo types are correctly identified for every station.

The project implements two versions for performance comparison:
1. **Standard:** Uses Kotlin's built-in `Set` operations (`union`, `minus`).
2. **Optimized:** Uses sorted arrays and two-pointer logic to perform union and difference operations in linear time ($O(N)$).

**Time Complexity:** $O(V \cdot E \cdot C)$
**Space Complexity:** $O(V \cdot C)$
(where $V$ is vertices, $E$ is edges, and $C$ is cargo types).

## Usage

Run the main application:
You can pass an input file as an argument.
```bash
./gradlew run --args="example/train.txt"
```

Run tests:
```Bash
./gradlew test
```

Run benchmarks:
```Bash
./gradlew benchmark
```

## Input Format

The input file should follow this structure:
1. S T (Number of stations, Number of tracks)
2. S lines describing stations: id consume_cargo load_cargo
3. T lines describing tracks: source_station destination_station
4. Start Station ID
