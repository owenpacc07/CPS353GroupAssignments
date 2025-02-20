package productsums.impl.user; 

import projectannotations.NetworkAPI;
import projectannotations.NetworkAPIPrototype;
import productsums.models.user.UserRequest;
import productsums.models.user.UserResponse;
 

public class UserAPIImpl implements UserAPI {
	
	//Default constructor 
	public UserAPIImpl() {
		
		
	}
	
	public UserResponse user(UserRequest request) {
		
		//Empty Response
		return UserResponse();
	}
}