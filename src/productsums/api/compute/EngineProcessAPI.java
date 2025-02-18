package productsums.api.compute;

import productsums.models.compute.EngineInput;
import productsums.models.compute.EngineOutput;
import projectannotations.ConceptualAPI;
import projectannotations.ConceptualAPIPrototype;

@ConceptualAPI
public interface EngineProcessAPI {
	/**
	 * 
	 * @param request A record containing the input index of the ProductSum number in a set of all ProductSum numbers.
	 * @return A record containing the input index in the original request, the actual productSum number, and it's factors.
	 */
	public EngineOutput compute(EngineInput request);
}
