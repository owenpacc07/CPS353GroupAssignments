package productsums.impl.compute;

import productsums.api.compute.ProductSumCalculatorAPI;
import productsums.models.compute.ComputeOutput;
import java.util.*;

public class ProductSumsCalculatorAPIImpl implements ProductSumCalculatorAPI {

    @Override
    public ComputeOutput getNthProductSum(int index) {
        // Implement your backtracking algorithm here.
        int productSumNumber = generateMinimalProductSum(index);
        List<Integer> factors = new ArrayList<>(); // Compute factors as needed
        
        // Return a new ComputeOutput record with the computed values.
        return new ComputeOutput(productSumNumber, factors);
    }

    private int generateMinimalProductSum(int k) {
        Set<Integer> productSumNumbers = new HashSet<>();
        backtrack(1, 0, 1, k, productSumNumbers);
        return productSumNumbers.stream().min(Integer::compareTo).orElse(k * 2);
    }

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
