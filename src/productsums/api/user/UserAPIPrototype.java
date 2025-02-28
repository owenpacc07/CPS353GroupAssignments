package productsums.api.user;

import projectannotations.NetworkAPIPrototype;
import productsums.models.user.UserRequest;
import productsums.models.user.UserResponse;

public class UserAPIPrototype{

/*
User Should:
Provide input source 
Provide output source
Specify delimiters, if not default are provided
*/
	@NetworkAPIPrototype 
	public void user(UserAPI api) {
	 
		UserRequest request = new UserRequest() {
			
			@Override 
			public String getDelimiters() {
				
				return ","; 
			}
			
			@Override
			public String getInputSource() {
				
				return "Input.txt";
			}
			
			@Override
			public String getOutputSource() {
				
				return "Output.txt";
			}
		};
		
		UserResponse response = null;
		
		//Run User Request 
		response = api.user(request); 
		
		if(response != null) {
			
			System.out.println("User request passed.");
		}else {
			
			System.out.println("User request failed.");
		}
	}
	
}
