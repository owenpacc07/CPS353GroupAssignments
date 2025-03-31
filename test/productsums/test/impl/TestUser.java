package productsums.test.impl;

import java.io.File;

import productsums.api.user.UserAPI;
import productsums.impl.user.CoordinatorImpl;


public class TestUser {
	private final UserAPI coordinator;

	public TestUser(UserAPI coordinator) {
		this.coordinator = coordinator;
	}

	public void run(String outputPath) {
		char delimiter = ';';
		String inputPath = "test" + File.separatorChar + "testInputFile.test";
		
		// TODO: Call the appropriate method(s) on the coordinator to get it to 
		// run the compute job specified by inputPath, outputPath, and delimiter
	}

}
