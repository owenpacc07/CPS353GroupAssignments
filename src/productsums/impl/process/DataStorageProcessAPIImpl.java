package productsums.impl.process;

import productsums.api.process.DataStorageProcessAPI;
import productsums.models.process.DataStorageProcessRequest;
import productsums.models.process.DataStorageProcessResponse;
import productsums.utils.FileReaderUtil;
import productsums.utils.FileWriterUtil;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * Implementation of the DataStorageProcessAPI that calculates minimal product-sum numbers.
 * A number N is called a product-sum number if it can be expressed as both:
 * 1. A sum of k numbers: N = a1 + a2 + ... + ak
 * 2. A product of the same k numbers: N = a1 × a2 × ... × ak
 * This implementation finds the minimal such N for each k in a given range.
 */
public class DataStorageProcessAPIImpl implements DataStorageProcessAPI {

    /**
     * 1. Validates the input request
     * 2. Reads input data if source is provided
     * 3. Computes product-sum numbers
     * 4. Writes results if destination is provided
     */
    @Override
    public DataStorageProcessResponse processData(DataStorageProcessRequest request) {
        validateRequest(request);
        
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

        Map<Integer, Integer> productSumResults = computeProductSumNumbers(request.getMinK(), request.getMaxK());

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
     * checks the request values:
     * Request is not null
     * minK is positive
     * maxK is greater than or equal to MinK
     */
    private void validateRequest(DataStorageProcessRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        if (request.getMinK() < 1) {
            throw new IllegalArgumentException("minK must be positive");
        }
        if (request.getMaxK() < request.getMinK()) {
            throw new IllegalArgumentException("maxK must be greater than or equal to MinK");
        }
    }

    /**
     * Computes minimal product-sum numbers for each k in the range [minK, maxK].
     * Returns a map where:
     * Key: k value
     * Value: minimal product-sum number for that k
     */
    private Map<Integer, Integer> computeProductSumNumbers(int minK, int maxK) {
        Map<Integer, Integer> productSumNumbers = new HashMap<>();

        for (int k = minK; k <= maxK; k++) {
            int minProductSum = findMinimalProductSum(k);
            productSumNumbers.put(k, minProductSum);
        }

        return productSumNumbers;
    }

    /**
     * Finds the minimal product-sum number for a specific k value.
     * A product-sum number must be both the sum and product of exactly k numbers.
     */
    private int findMinimalProductSum(int k) {
        return generateMinimalProductSum(k);
    }

    /**
     * Generates the minimal product-sum number for a given k using backtracking.
     * The algorithm explores possible combinations of numbers that can form both
     * a sum and product equal to N with exactly k terms.
     * 
     * For example, for k=4:
     * One possible solution is N=8 because:
     * 8 = 1 + 1 + 2 + 4 (sum)
     * 8 = 1 × 1 × 2 × 4 (product)
     */
    private int generateMinimalProductSum(int k) {
        Set<Integer> productSumNumbers = new HashSet<>();
        backtrack(1, 0, 1, k, productSumNumbers);
        return productSumNumbers.stream().min(Integer::compareTo).orElse(k * 2);
    }

    /**
     * Recursive backtracking function to find all possible product-sum numbers.
     * 
     * @param sum: Current sum of the numbers
     * @param count: Current count of numbers used
     * @param product: Current product of the numbers
     * @param k: Target number of terms needed
     * @param results: Set to store valid product-sum numbers found
     */
    private void backtrack(int sum, int count, int product, int k, Set<Integer> results) {
        if (count > 1 && sum == product) {
            results.add(sum);
        }
        if (count >= k || sum > product * 2) {
            return;
        }
        for (int i = 1; i <= product * 2; i++) {
            backtrack(sum + i, count + 1, product * i, k, results);
        }
    }
}