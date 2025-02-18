package productsums.api.compute;

import productsums.models.compute.ComputeOutput;
import productsums.models.compute.EngineInput;
import productsums.models.compute.EngineOutput;
import projectannotations.ConceptualAPIPrototype;

public class EngineProcessPrototype implements EngineProcessAPI{
	@Override
	@ConceptualAPIPrototype
	public EngineOutput compute(EngineInput request) {
		ComputeOutput co = new ProductSumCalculatorPrototype().getNthProductSum(request.inputIndex());
		return new EngineOutput(request.inputIndex(),co.answer(),co.factors());
	}

}
