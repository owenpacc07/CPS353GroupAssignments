package productsums.impl.compute;

import productsums.api.compute.EngineProcessAPI;
import productsums.models.compute.EngineInput;
import productsums.models.compute.EngineOutput;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class EngineProcessAPIImpl implements EngineProcessAPI {
	private final int maxSearch = 1000000;
	private final List<Integer> primes = 
			List.of(1,2,3,5,7,11,13,17,19,23,29,31,37,41,43,47);
	
	
    public EngineProcessAPIImpl() {
    	
    }

    @Override
    public EngineOutput compute(EngineInput request) {
    	Result temp = calculateKNum(request.inputIndex());
    	List<Integer> ones = new LinkedList<>();
    	for (int i = 0; i < (temp.k - temp.factors.size()); i++) {
    		ones.add(1);
    	}
    	ones.addAll(temp.factors);
        return new EngineOutput(request.inputIndex(), temp.k, ones);
    }
    
    /**
     * 
     * @param num
     * @return Contains a List of all combinations of two factors that multiply to num.
     */
	public LinkedList<LinkedList<Integer>> factors(int num) {
		LinkedList<LinkedList<Integer>> listOfFactors = new LinkedList<>();
		int upperlimit = ((int) Math.sqrt(num))+1;
		for (int i = 2; i < upperlimit; i++) {
			if (num % i == 0) {
				LinkedList<Integer> setOfFactors = new LinkedList<>();
				setOfFactors.add(i);
				setOfFactors.add(num/i);
				listOfFactors.add(setOfFactors);
			}
		}
		return listOfFactors;
	}
	/**
	 * 
	 * @param List of all sets of factors.
	 * @return Whether or not the largest group of factors was split.
	 */
	public boolean splitLargest(LinkedList<LinkedList<Integer>> list) {
		LinkedList<Integer> largest = list.get(0);
		list.remove(0);
		if (isPrime(largest.getLast())) {
			return false;
		}
		var factorsOfLargest = factors(largest.getLast());
		largest.removeLast();
		for (var listOfFactors : factorsOfLargest) {
			LinkedList<Integer> temp = new LinkedList<>();
			temp.addAll(listOfFactors);
			temp.addAll(largest);
			temp.sort(Comparator.naturalOrder());
			if (!list.contains(temp)) {
				list.add(temp);
			}
		}
		return true;
	}
	
	//could and should be optimized, currently just does a naive search through every integer. could probably do a binary search
	/**
	 * 
	 * @param k Determines how many factors/addends the product-sum number should have.
	 * @return A record containing the non-one factors, the number, and returns k.
	 * @throws ArithmeticException The algorithm is capped to avoid extreme processing time.
	 * The kth product-sum number must be under EngineProcessAPIImpl.MAX_SEARCH.
	 * @throws IllegalArgumentException k must not be less than one. The algorithm is designed for natural numbers.
	 */
	public Result calculateKNum(int k) throws ArithmeticException, IllegalArgumentException{
		if (k < 1) {
			throw new IllegalArgumentException("Product sum cannot be calculated for non-natural numbers.");
		}
		int curr = 4;
		while (curr < maxSearch) {
			LinkedList<LinkedList<Integer>> factorlist = factors(curr);
			boolean factorsCanBeSplit = true;
			while (factorsCanBeSplit && factorlist.size() > 0) {
				if (validateK(factorlist.get(0), k, curr)) {
					return new Result(factorlist.get(0),k,curr);
				}
				factorsCanBeSplit = splitLargest(factorlist);
			}
			curr++;
		}
		throw new ArithmeticException("Bounds for function calculation exceeded. No number under " + maxSearch + " is a product-sum for number k " + k);
	}
    
	/*
	 * 						Single line functions
	 */
    
	public boolean isPrime(int i) {
		return primes.stream()
				.anyMatch((item)->item == i);
	}
	public boolean allPrime(LinkedList<Integer> l) {
		return l.stream().allMatch((item)->isPrime(item));
	}
	public int sum(LinkedList<Integer> addends) {
		return addends.stream().mapToInt(Integer::intValue).sum();
	}
	public void sort(LinkedList<LinkedList<Integer>> list) {
		list.sort((l1,l2)->Integer.compare(sum(l2), sum(l1)));
	}
	public boolean validateK(LinkedList<Integer> factors, int k, int num) {
		return k - factors.size() == num - sum(factors);
	}
	public record Result(LinkedList<Integer> factors, int curr, int k) {}
}
