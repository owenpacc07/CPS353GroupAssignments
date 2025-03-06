package productsums.integrationtest.api.process;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.mock;

import productsums.api.compute.EngineProcessAPI;
import productsums.impl.process.DataStorageProcessAPIImpl;
import productsums.models.process.DataStorageProcessRequest;
import productsums.models.process.DataStorageProcessResponse;
import productsums.models.compute.EngineInput;
import productsums.models.compute.EngineOutput;

import java.util.ArrayList;
import java.util.Map;

/**
 * Integration tests for DataStorageProcessAPI.
 * Tests data flow, file operations, and large scale computations.
 */
public class DataStorageProcessAPIIntegrationTest {
    
    private EngineProcessAPI mockEngineAPI;
    private DataStorageProcessAPIImpl dataStorageAPI;

    @BeforeEach
    void setUp() {
        mockEngineAPI = mock(EngineProcessAPI.class);
        dataStorageAPI = new DataStorageProcessAPIImpl(mockEngineAPI);
    }

    @Test
    void testFileOperationsWithData() {
        // Arrange
        String inputPath = "test/productsums/test/resources/input.txt";
        String outputPath = "test/productsums/test/resources/output.txt";
        DataStorageProcessRequest request = new DataStorageProcessRequest(2, 4, inputPath, outputPath);
        
        when(mockEngineAPI.compute(any(EngineInput.class)))
            .thenReturn(new EngineOutput(2, 4, new ArrayList<>()))
            .thenReturn(new EngineOutput(3, 6, new ArrayList<>()))
            .thenReturn(new EngineOutput(4, 8, new ArrayList<>()));

        // Act
        DataStorageProcessResponse response = dataStorageAPI.processData(request);

        // Assert
        assertNotNull(response);
        Map<Integer, Integer> results = response.getProductSumResults();
        assertEquals(3, results.size());
        verify(mockEngineAPI, times(3)).compute(any(EngineInput.class));
    }

    @Test
    void testLargeRange() {
        // Test the full problem range of 2 to 12000
        DataStorageProcessRequest request = new DataStorageProcessRequest(2, 12000, null, null);
        when(mockEngineAPI.compute(any(EngineInput.class)))
            .thenReturn(new EngineOutput(0, 4, new ArrayList<>()));

        DataStorageProcessResponse response = dataStorageAPI.processData(request);

        assertNotNull(response);
        assertEquals(11999, response.getProductSumResults().size());
        verify(mockEngineAPI, times(11999)).compute(any(EngineInput.class));
    }
    

    @Test
    void testDataProcessingFlow() {
        // Testing data flow from input to output
        DataStorageProcessRequest request = new DataStorageProcessRequest(2, 3, "test/productsums/test/resources/input.txt", "test/productsums/test/resources/output.txt");
        when(mockEngineAPI.compute(any(EngineInput.class)))
            .thenReturn(new EngineOutput(2, 4, new ArrayList<>()))
            .thenReturn(new EngineOutput(3, 6, new ArrayList<>()));

        DataStorageProcessResponse response = dataStorageAPI.processData(request);

        assertNotNull(response);
        //TODO i don't even know what this 2d map is supposed to be
        Map<Integer, Integer> results = response.getProductSumResults();
        assertEquals(2, results.size());
        assertEquals(4, results.get(2));
        assertEquals(6, results.get(3));
    }

    @Test
    void testProcessingWithNullPaths() {
        // Setup request with null paths but valid number range
        DataStorageProcessRequest request = new DataStorageProcessRequest(2, 5, null, null);
        
        // Mock the engine responses for numbers 2 through 5
        // each number  returns a result of N*2 for predictable testing
        when(mockEngineAPI.compute(any(EngineInput.class)))
            .thenReturn(new EngineOutput(2, 4, new ArrayList<>()))
            .thenReturn(new EngineOutput(3, 6, new ArrayList<>()))
            .thenReturn(new EngineOutput(4, 8, new ArrayList<>()))
            .thenReturn(new EngineOutput(5, 10, new ArrayList<>()));

        DataStorageProcessResponse response = dataStorageAPI.processData(request);

        // check the response and its contents
        assertNotNull(response, "Response should not be null");
        Map<Integer, Integer> results = response.getProductSumResults();
        
        // Verify the size and contents of the results map
        assertEquals(4, results.size(), "Should have results for all 4 numbers in range");
        assertEquals(4, results.get(2), "Product sum for 2 should be 4");
        assertEquals(6, results.get(3), "Product sum for 3 should be 6");
        assertEquals(8, results.get(4), "Product sum for 4 should be 8");
        assertEquals(10, results.get(5), "Product sum for 5 should be 10");
        
        // check that the engine was called exactly once for each number in the range
        verify(mockEngineAPI, times(4)).compute(any(EngineInput.class));
    }
}
