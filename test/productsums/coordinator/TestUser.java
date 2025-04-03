package productsums.coordinator;

import java.io.File;

import productsums.api.user.UserAPI;
import productsums.impl.user.CoordinatorImpl;
import productsums.models.user.UserResponse;
import productsums.models.user.UserRequest; 
import productsums.models.user.UserResponseModel;


public class TestUser {
	private final UserAPI coordinator;

	public TestUser(UserAPI coordinator) {
		this.coordinator = coordinator;
	}

	public void run(String outputPath) {
		char delimiter = ';';
		String inputPath = "test" + File.separatorChar + "testInputFile.test";
		
		//Anonymous Class UserRequest
		UserRequest request = new UserRequest() {
			@Override 
			public String getDelimiters() {
				return String.valueOf(delimiter);
			}
			
			@Override 
			public String getInputSource() {
				return inputPath;
			}
			
			@Override
			public String getOutputSource() {
				return outputPath;
			}
		};
		
		//Call User()
		UserResponse response = coordinator.user(request);
		System.out.println("Coordinator: " + response.getResult());
	}

}
