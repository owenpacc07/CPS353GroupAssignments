package productsums.impl.compute;

import java.util.Comparator;
import java.util.LinkedList;

import productsums.utils.Constants;

public class EngineProcessUtils {
	/**
     * 
     * @param num
     * @return Contains a List of all combinations of two factors that multiply to num.
     */
	public static LinkedList<LinkedList<Integer>> factors(int num) {
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
	public static boolean splitLargest(LinkedList<LinkedList<Integer>> list) {
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
	/*
	 * 						Single line functions
	 */
    
	public static boolean isPrime(int i) {
		return Constants.PRIMES.stream()
				.anyMatch((item)->item == i);
	}
	public static boolean allPrime(LinkedList<Integer> l) {
		return l.stream().allMatch((item)->isPrime(item));
	}
	public static int sum(LinkedList<Integer> addends) {
		return addends.stream().mapToInt(Integer::intValue).sum();
	}
	public static void sort(LinkedList<LinkedList<Integer>> list) {
		list.sort((l1,l2)->Integer.compare(sum(l2), sum(l1)));
	}
	public static boolean validateK(LinkedList<Integer> factors, int k, int num) {
		return k - factors.size() == num - sum(factors);
	}
	public static record Result(LinkedList<Integer> factors, int k, int curr) {}
	
}
