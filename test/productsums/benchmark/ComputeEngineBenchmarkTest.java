package productsums.benchmark;

import org.junit.jupiter.api.Test;
import productsums.api.compute.EngineProcessAPI;
import productsums.impl.compute.EngineProcessAPIImpl;
import productsums.models.compute.EngineInput;
import productsums.models.compute.EngineOutput;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ComputeEngineBenchmarkTest {

    private static class InMemoryDataStoreGrpcClient {
        private final Map<Integer, EngineOutput> dataStore = new HashMap<>();

        public EngineOutput retrieveData(int inputIndex) {
            return dataStore.get(inputIndex);
        }

        public void storeData(int inputIndex, int productSum, Iterable<Long> factors) {
            dataStore.put(inputIndex, new EngineOutput(inputIndex, productSum, factors));
        }
    }

    private long runBenchmark(EngineProcessAPI engine, int[] inputs) {
        long startTime = System.nanoTime();
        for (int value : inputs) {
            engine.compute(new EngineInput(value));
        }
        return System.nanoTime() - startTime;
    }

    @Test
    public void benchmarkComputeEngine() {
        InMemoryDataStoreGrpcClient dataStoreClient = new InMemoryDataStoreGrpcClient();
        EngineProcessAPI originalEngine = new EngineProcessAPIImpl(dataStoreClient);
        EngineProcessAPI fasterEngine = new FasterEngineProcessAPIImpl(dataStoreClient);

        // Test with various input sizes
        int[] testInputs = {10, 50, 100, 200, 500, 1000};

        // Run benchmarks
        long originalDuration = runBenchmark(originalEngine, testInputs);
        long fasterDuration = runBenchmark(fasterEngine, testInputs);

        // Calculate and display results
        double improvement = ((double) originalDuration - fasterDuration) / originalDuration * 100;
        System.out.printf("Original Duration: %d ns%n", originalDuration);
        System.out.printf("Faster Duration: %d ns%n", fasterDuration);
        System.out.printf("Improvement: %.2f%%%n", improvement);

        assertTrue(improvement >= 10, 
            String.format("Faster version is not at least 10%% faster (actual improvement: %.2f%%)", improvement));
    }
}