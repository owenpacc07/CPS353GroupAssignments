package productsums.unittest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import productsums.api.compute.EngineProcessAPI;
import productsums.impl.compute.EngineProcessAPIImpl;
import productsums.models.compute.EngineInput;
import productsums.models.compute.EngineOutput;
import productsums.utils.Constants;

public class CheckMaxKCalculable {
	@Test
	public void testOverflow() {
		EngineProcessAPI api = new EngineProcessAPIImpl();
		EngineOutput output = api.compute(new EngineInput(Constants.MAXIMUM_K));
		System.out.println(output.answer());
		Assertions.assertTrue(output != EngineOutput.incomputableK, "Maximum K value is too high and will throw exceptions regarding integer overflow");
	}
	@Test
	public void testTimeframe() {
		
	}
}
