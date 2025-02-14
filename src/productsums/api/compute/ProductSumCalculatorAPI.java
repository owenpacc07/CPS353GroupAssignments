package productsums.api.compute;

import projectannotations.ConceptualAPI;
import projectannotations.ConceptualAPIPrototype;

@ConceptualAPI
public interface ProductSumCalculatorAPI {
	@ConceptualAPIPrototype
	public int[] getNthProductSum(int index);
}
