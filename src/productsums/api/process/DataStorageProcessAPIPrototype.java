package productsums.api.process;

import productsums.models.process.DataStorageProcessRequest;
import productsums.models.process.DataStorageProcessResponse;
import projectannotations.ProcessAPIPrototype;

import java.util.HashMap;
import java.util.Map;

public class DataStorageProcessAPIPrototype implements DataStorageProcessAPI {
    
    @ProcessAPIPrototype
    public DataStorageProcessResponse processData(DataStorageProcessRequest request) {
        int minK = request.getMinK();
        int maxK = request.getMaxK();

        // Hardcoded to generate a response where k maps to k*2
        Map<Integer, Integer> testResults = new HashMap<>();
        for (int k = minK; k <= maxK; k++) {
            testResults.put(k, k * 2);  // Temporary placeholder computation
        }

        return new DataStorageProcessResponse(testResults);
    }
}