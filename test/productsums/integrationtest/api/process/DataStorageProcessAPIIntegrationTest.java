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
        String inputPath = "test_input.txt";
        String outputPath = "test_output.txt";
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
        DataStorageProcessRequest request = new DataStorageProcessRequest(2, 3, "input.txt", "output.txt");
        when(mockEngineAPI.compute(any(EngineInput.class)))
            .thenReturn(new EngineOutput(2, 4, new ArrayList<>()))
            .thenReturn(new EngineOutput(3, 6, new ArrayList<>()));

        DataStorageProcessResponse response = dataStorageAPI.processData(request);

        assertNotNull(response);
        Map<Integer, Integer> results = response.getProductSumResults();
        assertEquals(2, results.size());
        assertEquals(4, results.get(2));
        assertEquals(6, results.get(3));
    }
}
