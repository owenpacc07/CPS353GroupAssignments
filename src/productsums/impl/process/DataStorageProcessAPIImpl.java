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

    // Reference to the EngineProcessAPI
    private EngineProcessAPI engineProcessAPI;

    public DataStorageProcessAPIImpl() {
        // Instantiate the EngineProcessAPI (could be injected instead)
        this.engineProcessAPI = new EngineProcessPrototype();
    }

    /**
     * Processes the data by:
     * 1. Validating the request.
     * 2. Optionally reading input from a file.
     * 3. Delegating computation of product-sum numbers to the EngineProcessAPI.
     * 4. Optionally writing results to an output file.
     */
    @Override
    public DataStorageProcessResponse processData(DataStorageProcessRequest request) {
        validateRequest(request);

        // Optional: read input data if an input source is provided.
        String inputData = null;
        if (request.getInputSource() != null && !request.getInputSource().isEmpty()) {
            try {
                inputData = FileReaderUtil.readFile(request.getInputSource());
                System.out.println("Successfully read input from: " + request.getInputSource());
            } catch (RuntimeException e) {
                System.err.println("Failed to read input file: " + e.getMessage());
                throw new RuntimeException("Data processing failed due to input error", e);
            }
        }

        // For each k in the range, delegate computation to EngineProcessAPI.
        Map<Integer, Integer> productSumResults = new HashMap<>();
        for (int k = request.getMinK(); k <= request.getMaxK(); k++) {
            // Create an EngineInput for the current k (assuming EngineInput has a constructor that accepts k)
            EngineInput engineInput = new EngineInput(k);
            EngineOutput output = engineProcessAPI.compute(engineInput);
            // Extract the computed product-sum number from the output.
            // (Assuming EngineOutput has a method getAnswer() returning the computed number.)
            productSumResults.put(k, output.answer());
        }

        // Optional: write the results to an output file if an output destination is provided.
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

    /**
     * Validates the incoming request ensuring:
     * - The request is not null.
     * - minK is positive.
     * - maxK is greater than or equal to minK.
     */
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
