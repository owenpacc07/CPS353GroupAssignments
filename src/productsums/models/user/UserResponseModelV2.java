package productsums.models.user;

public class UserResponseModelV2 implements UserResponseV2 {

	private final String message; 
	private final String output; 
	
	public UserResponseModelV2(String message) {
		
		this.message = message; 
		this.output = null; 
	}
	
	public UserResponseModelV2(String message, String output) {
		
		this.message = message; 
		this.output = output; 
	}
	
	@Override 
	public String getMessage() {
		
		return message; 
	}
	
	@Override 
	public String getOutput() {
		
		return output; 
	}
	
}
