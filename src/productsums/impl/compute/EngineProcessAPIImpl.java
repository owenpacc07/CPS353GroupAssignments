package productsums.impl.compute;

import productsums.api.compute.EngineProcessAPI;
import productsums.impl.compute.EngineProcessUtils.Result;
import productsums.models.compute.EngineInput;
import productsums.models.compute.EngineOutput;
import productsums.utils.Constants;

import java.util.LinkedList;
import java.util.List;

public class EngineProcessAPIImpl implements EngineProcessAPI {
	
    public EngineProcessAPIImpl() {
    	
    }

    @Override
    public EngineOutput compute(EngineInput request) {
    	if (request == null) {
    		return EngineOutput.nullPointer;
    	}
    	Result temp = null; 
    	try {
    		temp = calculateKNum(request.inputIndex());
    	} catch (ArithmeticException e) {
    		return EngineOutput.incomputableK;
    	} catch (IllegalArgumentException e) {
    		return EngineOutput.outOfBounds;
    	}
    	List<Integer> ones = new LinkedList<>();
    	for (int i = 0; i < (temp.k() - temp.factors().size()); i++) {
    		ones.add(1);
    	}
    	ones.addAll(temp.factors());
        return new EngineOutput(request.inputIndex(), temp.curr(), ones);
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
			LinkedList<LinkedList<Integer>> factorlist = EngineProcessUtils.factors(curr);
			boolean factorsCanBeSplit = true;
			while (factorsCanBeSplit && factorlist.size() > 0) {
				if (EngineProcessUtils.validateK(factorlist.get(0), k, curr)) {
					return new Result(factorlist.get(0),k,curr);
				}
				factorsCanBeSplit = EngineProcessUtils.splitLargest(factorlist);
			}
			curr++;
		}
		throw new ArithmeticException("Product-sum number for " + k + " cannot be represented by an integer. Integer overflow has occured where should not be possible.");
	}
}