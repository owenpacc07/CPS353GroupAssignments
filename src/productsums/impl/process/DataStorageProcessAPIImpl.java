package productsums.impl.process;

import productsums.api.process.DataStorageProcessAPI;
import productsums.models.process.DataStorageProcessRequest;
import productsums.models.process.DataStorageProcessResponse;
import productsums.api.compute.EngineProcessAPI;
import productsums.api.compute.EngineProcessPrototype;
import productsums.models.compute.EngineInput;
import productsums.models.compute.EngineOutput;
import productsums.utils.FileReaderUtil;
import productsums.utils.FileWriterUtil;

import java.util.HashMap;
import java.util.Map;

public class DataStorageProcessAPIImpl implements DataStorageProcessAPI {

    private EngineProcessAPI engineProcessAPI;

    // Default constructor to instantiat the prototype
    public DataStorageProcessAPIImpl() {
        this.engineProcessAPI = new EngineProcessPrototype();
    }

    // test friendly constructor to accept an EngineProcessAPI instance
    public DataStorageProcessAPIImpl(EngineProcessAPI engineProcessAPI) {
        this.engineProcessAPI = engineProcessAPI;
    }

    @Override
    public DataStorageProcessResponse processData(DataStorageProcessRequest request) {
        validateRequest(request);

        //read from file
        if (request.getInputSource() != null && !request.getInputSource().isEmpty()) {
            try {
                String inputData = FileReaderUtil.readFile(request.getInputSource());
                System.out.println("Successfully read input from: " + request.getInputSource());
            } catch (RuntimeException e) {
                System.err.println("Failed to read input file: " + e.getMessage());
                throw new RuntimeException("Data processing failed due to input error", e);
            }
        }

        // Hand the computation over to the  EngineProcessAPI
        Map<Integer, Integer> productSumResults = new HashMap<>();
        for (int k = request.getMinK(); k <= request.getMaxK(); k++) {
            EngineInput engineInput = new EngineInput(k);
            EngineOutput output = engineProcessAPI.compute(engineInput);
            productSumResults.put(k, output.answer());
        }

        // write to a file
        if (request.getOutputDestination() != null && !request.getOutputDestination().isEmpty()) {
            try {
                FileWriterUtil.writeFile(request.getOutputDestination(), productSumResults.toString());
                System.out.println("Successfully wrote results to: " + request.getOutputDestination());
            } catch (RuntimeException e) {
                System.err.println("Failed to write output file: " + e.getMessage());
                throw new RuntimeException("Data processing failed due to output error", e);
            }
        }

        return new DataStorageProcessResponse(productSumResults);
    }

    private void validateRequest(DataStorageProcessRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (request.getMinK() < 1) {
            throw new IllegalArgumentException("minK must be positive");
        }
        if (request.getMaxK() < request.getMinK()) {
            throw new IllegalArgumentException("maxK must be greater than or equal to minK");
        }
    }
}
