package productsums.benchmark;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import productsums.api.process.DataStorageProcessAPI;
import productsums.api.process.DataStorageProcessAPIPrototype;
import productsums.api.process.DataStorageProcessAPIV2;
import productsums.impl.process.DataStorageProcessImpl2;
import productsums.models.process.*;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ComputeEngineBenchmarkTest {
    private static final int WARMUP_RUNS = 3;
    private static final int BENCHMARK_RUNS = 5;
    private static final int MIN_K = 2;
    private static final int MAX_K = 12;

    @Test
    public void testPerformanceImprovement() {
        // Setup test data
        DataStorageProcessRequest request = new DataStorageProcessRequest(MIN_K, MAX_K, null, "test_output.txt");
        
        // Initialize both implementations
        DataStorageProcessAPI originalApi = new DataStorageProcessAPIPrototype();
        DataStorageProcessAPIV2 fasterApi = new DataStorageProcessImpl2();

        // Create reusable objects for V2 API
        Map<Integer, Integer> results = new HashMap<>();
        FileOutputStream dummyStream = null;
        DataStorageProcessRequestV2 requestV2 = new DataStorageProcessRequestV2(
            Optional.of(results),
            dummyStream,
            Optional.empty()
        );

        // Warm-up runs
        for (int i = 0; i < WARMUP_RUNS; i++) {
            originalApi.processData(request);
            fasterApi.writeOutputs(requestV2);
        }

        // Benchmark original version
        long startOriginal = System.nanoTime();
        for (int i = 0; i < BENCHMARK_RUNS; i++) {
            originalApi.processData(request);
        }
        long endOriginal = System.nanoTime();
        double originalTime = (endOriginal - startOriginal) / 1_000_000.0; // Convert to milliseconds

        // Benchmark faster version
        long startFaster = System.nanoTime();
        for (int i = 0; i < BENCHMARK_RUNS; i++) {
            fasterApi.writeOutputs(requestV2);
        }
        long endFaster = System.nanoTime();
        double fasterTime = (endFaster - startFaster) / 1_000_000.0; // Convert to milliseconds

        // Calculate improvement percentage
        double improvementPercent = ((originalTime - fasterTime) / originalTime) * 100;

        System.out.printf("Original implementation: %.2f ms%n", originalTime);
        System.out.printf("Faster implementation: %.2f ms%n", fasterTime);
        System.out.printf("Performance improvement: %.2f%%%n", improvementPercent);

        // Assert that the faster version is at least 10% faster
        assertTrue(improvementPercent >= 10.0, 
            String.format("Performance improvement of %.2f%% does not meet the minimum requirement of 10%%", 
            improvementPercent));
    }
}
