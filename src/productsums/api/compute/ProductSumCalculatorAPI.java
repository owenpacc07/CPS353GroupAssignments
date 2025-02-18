package productsums.api.compute;

import productsums.models.compute.ComputeOutput;
import projectannotations.ConceptualAPI;
import projectannotations.ConceptualAPIPrototype;

@ConceptualAPI
public interface ProductSumCalculatorAPI {
	/**
	 * 
	 * @param index Denotes the index of the ProductSum number in a set of all ProductSum numbers. ex. index of 3
	 * is the 3rd product sum number possible.
	 * @return returns a record containing both the product sum number and all of its factors.
	 */
	public ComputeOutput getNthProductSum(int index);
}
