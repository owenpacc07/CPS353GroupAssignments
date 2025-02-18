package productsums.impl.compute;

import productsums.api.compute.ProductSumCalculatorAPI;
import productsums.models.compute.ComputeOutput;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 'index' parameter is k
 * in the minimal product-sum problem. It uses backtracking
 * to find the minimal product-sum number for that k.
 */
public class ProductSumsCalculatorAPIImpl implements ProductSumCalculatorAPI {

    @Override
    public ComputeOutput getNthProductSum(int index) {
        // index is our k value
        int productSumNumber = generateMinimalProductSum(index);

        // Currently, we return an empty list for factors.
        // If you need actual factors, you'll have to record them in backtracking.
        List<Integer> factors = new ArrayList<>();

        return new ComputeOutput(productSumNumber, factors);
    }

    /**
     * Uses a backtracking to find the minimal product sum number for k.
     */
    private int generateMinimalProductSum(int k) {
        Set<Integer> productSumNumbers = new HashSet<>();
        backtrack(1, 0, 1, k, productSumNumbers);
        // If none found, fallback to a default (k*2 in this case)
        return productSumNumbers.stream().min(Integer::compareTo).orElse(k * 2);
    }

    /**
     * Recursive backtracking function to generate potential product-sum numbers.
     * @param sum: Current sum of terms
     * @param count: How many terms have been used so far
     * @param product: Current product of terms
     * @param k: The target number of terms
     * @param results: a set to store valid product-sum numbers found
     */
    private void backtrack(int sum, int count, int product, int k, Set<Integer> results) {
        // If we have more than 1 term and sum == product, it's a product-sum number
        if (count > 1 && sum == product) {
            results.add(sum);
        }
        // Pruning conditions
        if (count >= k || sum > product * 2) {
            return;
        }
        // Trying extending the combination with integers from 1 up to product*2
        for (int i = 1; i <= product * 2; i++) {
            backtrack(sum + i, count + 1, product * i, k, results);
        }
    }
}
