package productsums.impl.compute;

import productsums.api.compute.EngineProcessAPI;
import productsums.models.compute.EngineInput;
import productsums.models.compute.EngineOutput;

public class EngineProcessAPIImpl implements EngineProcessAPI{
	public EngineProcessAPIImpl() {
	
	}
	@Override
	public EngineOutput compute(EngineInput request) {
		return new EngineOutput(0,0,new int[]{0});
	}
}