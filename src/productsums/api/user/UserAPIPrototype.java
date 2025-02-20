package productsums.api.user;

import productsums.models.user.UserRequest;
import projectannotations.NetworkAPIPrototype;
import productsums.models.user.UserRequest;
import productsums.models.user.UserResponse;



public class UserAPIPrototype implements UserAPI {
  
/*
User Should:
Provide input source 
Provide output source
Specify delimiters, if not default are provided
*/
  
	@Override 
	@NetworkAPIPrototype 
	public UserResponse user(UserRequest request) {
		String inputSource = request.getInputSource();
		String outputSource = request.getOutputSource();
		String delimiters = request.getDelimiters();
		return null;
	}
	
}
