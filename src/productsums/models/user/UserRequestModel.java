package productsums.models.user;

public class UserRequestModel implements UserRequest {

	private final String inputSource; 
	private final String outputSource;
	private final String delimiters; 
	
	public UserRequestModel(String inputSource, String outputSource, String delimiters) {
		
		this.inputSource = inputSource; 
		this.outputSource = outputSource; 
		this.delimiters = delimiters; 
	}
	
	@Override 
	public String getInputSource() {
		
		return inputSource;
	}
	
	@Override 
	public String getOutputSource() {
		
		return outputSource; 
	}
	
	@Override
	public String getDelimiters() {
		
		return delimiters; 
	}
}
