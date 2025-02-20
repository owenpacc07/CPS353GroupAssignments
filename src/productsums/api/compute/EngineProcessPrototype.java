package productsums.api.compute;

import productsums.models.compute.EngineInput;
import productsums.models.compute.EngineOutput;
import projectannotations.ConceptualAPIPrototype;


public class EngineProcessPrototype {
	@ConceptualAPIPrototype
//	public EngineOutput compute(EngineInput request) {
//		ComputeOutput co = new ProductSumCalculatorPrototype().getNthProductSum(request.inputIndex());
//		return new EngineOutput(request.inputIndex(),co.answer(),co.factors());
//	}
	public void test(EngineProcessAPI api) {
		api.compute(new EngineInput(1));
	}

}
