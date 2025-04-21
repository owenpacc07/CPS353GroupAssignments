package benchmark;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import productsums.api.compute.EngineProcessAPI;
import productsums.impl.compute.EngineProcessAPIImpl;
import productsums.impl.compute.EngineProcessImpl2;
import productsums.models.compute.EngineInput;
import productsums.models.compute.EngineOutput;

public class ComputeEngineBenchmarkTest {
    private EngineProcessAPI originalEngine;
    private EngineProcessAPI fasterEngine;
    private static final int TEST_SIZE = 100; // Number of computations to perform
    private static final double REQUIRED_SPEEDUP = 0.10; // 10% improvement required

    @BeforeEach
    void setUp() {
        originalEngine = new EngineProcessAPIImpl();
        fasterEngine = new EngineProcessImpl2();
    }

    @Test
    void testPerformanceImprovement() {
        // Create test data
        List<EngineInput> inputs = new ArrayList<>();
        for (int i = 2; i <= TEST_SIZE; i++) {
            inputs.add(new EngineInput(i));
        }

        // Test original implementation
        long startTime = System.nanoTime();
        List<EngineOutput> originalResults = computeAll(originalEngine, inputs);
        long originalDuration = System.nanoTime() - startTime;

        // Test improved implementation
        startTime = System.nanoTime();
        List<EngineOutput> fasterResults = computeAll(fasterEngine, inputs);
        long fasterDuration = System.nanoTime() - startTime;

        // Calculate improvement
        double improvementRatio = (double)(originalDuration - fasterDuration) / originalDuration;
        
        // Log performance results
        System.out.println("Original implementation time: " + originalDuration / 1_000_000.0 + "ms");
        System.out.println("Improved implementation time: " + fasterDuration / 1_000_000.0 + "ms");
        System.out.println("Performance improvement: " + (improvementRatio * 100) + "%");

        // Verify results are equivalent
        assertResultsEqual(originalResults, fasterResults);
        
        // Assert performance improvement
        assertTrue(improvementRatio >= REQUIRED_SPEEDUP, 
            String.format("Performance improvement of %.2f%% does not meet required %.2f%%", 
            improvementRatio * 100, REQUIRED_SPEEDUP * 100));
    }

    private List<EngineOutput> computeAll(EngineProcessAPI engine, List<EngineInput> inputs) {
        List<EngineOutput> results = new ArrayList<>();
        for (EngineInput input : inputs) {
            results.add(engine.compute(input));
        }
        return results;
    }

    private void assertResultsEqual(List<EngineOutput> original, List<EngineOutput> faster) {
        assertEquals(original.size(), faster.size(), "Result sets should have same size");
        for (int i = 0; i < original.size(); i++) {
            EngineOutput originalOutput = original.get(i);
            EngineOutput fasterOutput = faster.get(i);
            assertEquals(originalOutput.answer(), fasterOutput.answer(), 
                "Results should be equal for input " + i);
        }
    }
}
