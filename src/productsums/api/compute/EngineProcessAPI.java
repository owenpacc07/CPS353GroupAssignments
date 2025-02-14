package productsums.api.compute;

import productsums.models.compute.EngineInput;
import productsums.models.compute.EngineOutput;
import projectannotations.ConceptualAPI;
import projectannotations.ConceptualAPIPrototype;

@ConceptualAPI
public interface EngineProcessAPI {
	@ConceptualAPIPrototype
	public EngineOutput compute(EngineInput request);
}
