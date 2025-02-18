package productsums.impl.compute;

import productsums.api.compute.EngineProcessAPI;
import productsums.api.compute.ProductSumCalculatorAPI;
import productsums.models.compute.EngineInput;
import productsums.models.compute.EngineOutput;

public class EngineProcessAPIImpl implements EngineProcessAPI{
	private ProductSumCalculatorAPI calcAPI;
	public EngineProcessAPIImpl(ProductSumCalculatorAPI calcAPI) {
		this.calcAPI = calcAPI;
	}
	@Override
	public EngineOutput compute(EngineInput request) {
		return new EngineOutput(0,0,new int[]{0});
	}

}
