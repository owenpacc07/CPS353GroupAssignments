package productsums.integrationtest.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import productsums.api.compute.EngineProcessAPI;
import productsums.impl.compute.EngineProcessAPIImpl;
import productsums.models.compute.EngineInput;
import productsums.models.compute.EngineOutput;


public class ExceptionGuardingTest {
	@Test
	public void testEngineImpl() {
		EngineProcessAPI api = new EngineProcessAPIImpl();
		try {
			Assertions.assertTrue(api.compute(null) == EngineOutput.nullPointer(), "Null pointer sentinel value was not returned.");
			Assertions.assertTrue(api.compute(new EngineInput(1)) == EngineOutput.outOfBounds(), "K value out of bounds sentinel value was not returned.");
		} catch (Exception e) {
			Assertions.assertTrue(false, "Uncaught Exception thrown into process boundary: " + e);
		}
	}
	@Test
	public void testDataStorageImpl() {
		//TODO can be implemented if necessary
	}
	@Test
	public void testCoordinatorImpl() {
		//TODO can be implemented if necessary
	}
}
