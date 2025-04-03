package productsums.coordinator;


import productsums.api.user.UserAPI;
import productsums.models.user.UserResponse;
import productsums.models.user.UserRequest; 


public class TestUser {
	private final UserAPI coordinator;

	public TestUser(UserAPI coordinator) {
		this.coordinator = coordinator;
	}
	public UserResponse run(String outputPath) {
		char delimiter = ';';
		String inputPath = "test/productsums/resources/testInputFile.txt";
		
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
		return coordinator.user(request);
	}

}
