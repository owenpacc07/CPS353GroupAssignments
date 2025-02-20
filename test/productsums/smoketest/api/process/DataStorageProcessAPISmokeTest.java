package productsums.smoketest.api.process;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import productsums.api.compute.EngineProcessAPI;
import productsums.impl.process.DataStorageProcessAPIImpl;
import productsums.models.process.DataStorageProcessRequest;
import productsums.models.process.DataStorageProcessResponse;
import productsums.models.compute.EngineInput;
import productsums.models.compute.EngineOutput;

import java.util.ArrayList;
import java.util.Map;

/**
 * Smoke tests for the DataStorageProcessAPI.
 * Tests basic functionality and validation for the api
 */
public class DataStorageProcessAPISmokeTest {

    @Mock
    private EngineProcessAPI mockEngineAPI;

    @InjectMocks
    private DataStorageProcessAPIImpl dataStorageAPI;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Tests the basic computation of product-sum numbers.
     */
    @Test
    void testBasicProductSumComputation() {
        // Set up test data for k values 2,3,4
        DataStorageProcessRequest request = new DataStorageProcessRequest(2, 4, null, null);
        // this mock returns known values: k=2->4, k=3->6, k=4->8
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
        assertEquals(4, results.get(2)); // k=2 -> 4
        assertEquals(6, results.get(3)); // k=3 -> 6
        assertEquals(8, results.get(4)); // k=4 -> 8
        verify(mockEngineAPI, times(3)).compute(any(EngineInput.class));
    }

    /**
     * Tests input validation and error handling.
     */
    @Test
    void testInvalidInputs() {
        //Testing null request
        assertThrows(IllegalArgumentException.class, () -> 
            dataStorageAPI.processData(null));

        // Test invalid k range
        assertThrows(IllegalArgumentException.class, () -> 
            dataStorageAPI.processData(new DataStorageProcessRequest(5, 3, null, null)));

        // test negative k
        assertThrows(IllegalArgumentException.class, () -> 
            dataStorageAPI.processData(new DataStorageProcessRequest(-1, 3, null, null)));
    }
}
