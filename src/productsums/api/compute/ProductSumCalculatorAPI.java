package productsums.api.compute;

import projectannotations.ConceptualAPI;
import projectannotations.ConceptualAPIPrototype;

@ConceptualAPI
public interface ProductSumCalculatorAPI {
	@ConceptualAPIPrototype
	public int addProductSums(int lowerbound, int upperbound);
	@ConceptualAPIPrototype
	public int getNthProductSum(int index);
}
