package productsums.impl.compute;

import productsums.api.compute.EngineProcessAPI;
import productsums.models.compute.EngineInput;
import productsums.models.compute.EngineOutput;
import productsums.utils.Constants;

import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class EngineProcessAPIImpl implements EngineProcessAPI {
    public EngineProcessAPIImpl() {
    	
    }

    @Override
    public EngineOutput compute(EngineInput request) {
    	if (request == null) {
    		return EngineOutput.nullPointer();
    	}
    	Result temp = null; 
    	try {
    		temp = calculateKNum(request.inputIndex());
    	} catch (ArithmeticException e) {
    		return EngineOutput.incomputableK();
    	} catch (IllegalArgumentException e) {
    		return EngineOutput.outOfBounds();
    	}
    	List<Integer> ones = new LinkedList<>();
    	for (int i = 0; i < (temp.k - temp.factors.size()); i++) {
    		ones.add(1);
    	}
    	ones.addAll(temp.factors);
        return new EngineOutput(request.inputIndex(), temp.curr, ones);
    }
    
    /**
     * 
     * @param num
     * @return Contains a List of all combinations of two factors that multiply to num.
     */
	private LinkedList<LinkedList<Integer>> factors(int num) {
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
	private boolean splitLargest(LinkedList<LinkedList<Integer>> list) {
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
	private Result calculateKNum(int k) throws ArithmeticException, IllegalArgumentException{
		if (k < Constants.MINIMUM_K) {
			throw new IllegalArgumentException("Product sum cannot be calculated for non-natural numbers.");
		}
		if (k > Constants.MAXIMUM_K) {
			throw new IllegalArgumentException("Product sum cannot be calculated for non-natural numbers.");
		}
		int curr = 4;
		while (curr > -1) {
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
		throw new ArithmeticException("Product-sum number for " + k + " cannot be represented by an integer. Integer overflow has occured where should not be possible.");
	}
	/*
	 * 						Single line functions
	 */
    
	private boolean isPrime(int i) {
		return Constants.PRIMES.stream()
				.anyMatch((item)->item == i);
	}
	private boolean allPrime(LinkedList<Integer> l) {
		return l.stream().allMatch((item)->isPrime(item));
	}
	private int sum(LinkedList<Integer> addends) {
		return addends.stream().mapToInt(Integer::intValue).sum();
	}
	private void sort(LinkedList<LinkedList<Integer>> list) {
		list.sort((l1,l2)->Integer.compare(sum(l2), sum(l1)));
	}
	private boolean validateK(LinkedList<Integer> factors, int k, int num) {
		return k - factors.size() == num - sum(factors);
	}
	private record Result(LinkedList<Integer> factors, int k, int curr) {}
}