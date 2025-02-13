package productsums.api.compute;

import productsums.models.process.DataStorageProcessRequest;
import productsums.models.process.DataStorageProcessResponse;
import projectannotations.ConceptualAPI;
import projectannotations.ConceptualAPIPrototype;

@ConceptualAPI
public interface EngineProcessAPI {
	@ConceptualAPIPrototype
	public DataStorageProcessResponse compute(DataStorageProcessRequest request);
}
