package productsums.impl.process;

import productsums.api.process.DataStorageProcessAPI;
import productsums.api.compute.EngineProcessAPI;
import productsums.models.compute.EngineInput;
import productsums.models.process.DataStorageProcessRequest;
import productsums.models.process.DataStorageProcessResponse;
import productsums.utils.Constants;
import productsums.utils.FileReaderUtil;
import productsums.utils.FileWriterUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Implementation of the DataStorageProcessAPI that calculates minimal product-sum numbers.
 */
public class DataStorageProcessAPIImpl implements DataStorageProcessAPI {
    private final EngineProcessAPI engineAPI;

    public DataStorageProcessAPIImpl(EngineProcessAPI engineAPI) {
        this.engineAPI = engineAPI;
    }

    @Override
    public DataStorageProcessResponse processData(DataStorageProcessRequest request) {
        // Validate request object
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }

        int minK = request.getMinK();
        int maxK = request.getMaxK();

        // Validate k range values
        if (minK < Constants.MINIMUM_K) {
            throw new IllegalArgumentException("minK must be at least " + Constants.MINIMUM_K);
        }
        if (maxK < minK) {
            throw new IllegalArgumentException("maxK must be greater than or equal to minK");
        }
        if (maxK > Constants.MAXIMUM_K) {
            throw new IllegalArgumentException("maxK cannot exceed " + Constants.MAXIMUM_K);
        }

        String inputSource = request.getInputSource();
        String outputDestination = request.getOutputDestination();

        // Validate file paths if provided
        if (inputSource != null && !inputSource.isEmpty()) {
            if (!Files.exists(Paths.get(inputSource))) {
                throw new IllegalArgumentException("Input file does not exist: " + inputSource);
            }
        }

        if (outputDestination != null && !outputDestination.isEmpty()) {
            try {
                // Verify we can create/write to the output file
                Files.write(Paths.get(outputDestination), new byte[0]);
            } catch (IOException e) {
                throw new IllegalArgumentException("Cannot write to output file: " + outputDestination);
            }
        }

        //read some input data if needed
        if (inputSource != null && !inputSource.isEmpty()) {
            System.out.println("Reading input data from: " + inputSource);
            String fileContent = FileReaderUtil.readFile(inputSource);
            System.out.println("Input Data: " + fileContent);
        }

        //Compute minimal product-sum numbers
        Map<Integer, Integer> productSumResults = computeProductSumNumbers(minK, maxK);

        //write results to the output destination if given
        if (outputDestination != null && !outputDestination.isEmpty()) {
            System.out.println("Writing results to: " + outputDestination);
            FileWriterUtil.writeFile(outputDestination, productSumResults.toString());
        }

        return new DataStorageProcessResponse(productSumResults);
    }

    //Computes minimal product-sum numbers for k values in the given range.
    private Map<Integer, Integer> computeProductSumNumbers(int minK, int maxK) {
        Map<Integer, Integer> productSumNumbers = new HashMap<>();

        for (int k = minK; k <= maxK; k++) {
            int minProductSum = engineAPI.compute(new EngineInput(k)).answer();
            productSumNumbers.put(k, minProductSum);
        }

        return productSumNumbers;
    }

    //These methods don't need to exist the compute engine should be responsible for this.
    
//    //Finds the smallest N that can be expressed as both the sum and product of k numbers.
//    private int findMinimalProductSum(int k) {
//        return generateMinimalProductSum(k);
//    }
//
//    //Generates the minimal product-sum number for a given k using backtracking.
//    private int generateMinimalProductSum(int k) {
//        Set<Integer> productSumNumbers = new HashSet<>();
//        backtrack(1, 0, 1, k, productSumNumbers);
//        return productSumNumbers.stream().min(Integer::compareTo).orElse(k * 2);
//    }
//
//    //Recursive function to generate minimal product-sum numbers.
//    private void backtrack(int sum, int count, int product, int k, Set<Integer> results) {
//    	//If we have k numbers, check if the sum and product are equal
//        if (count > 1 && sum == product) {
//            results.add(sum);
//        }
//        if (count >= k || sum > product * 2) {	//If we have more than k numbers or the sum exceeds the product, stop
//            return;
//        }
//        for (int i = 1; i <= product * 2; i++) {	//Try all numbers from 1 to product*2
//            backtrack(sum + i, count + 1, product * i, k, results);
//        }
//    }
}