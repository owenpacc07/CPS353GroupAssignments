package productsums.models.user;

public class UserRequestV2 {

	private final String inputSource; 
	private final String outputSource; 
	private final String delimiters; 
	private final boolean inputIsFile; 
	private final boolean outputIsFile; 
	
	public UserRequestV2(String inputSource, String outputSource, String delimiters, boolean inputIsFile, boolean outputIsFile) {
		
		this.inputSource = inputSource; 
		this.outputSource = outputSource; 
		this.delimiters = delimiters; 
		this.inputIsFile = inputIsFile; 
		this.outputIsFile = outputIsFile;
	}
	
	public String getInputSource() {
		
		return inputSource;
	}
	
	public String getOutputSource() {
		
		return outputSource; 
	}
	
	public String getDelimiters() {
		
		return delimiters; 
	}
	
	public boolean isInputFromFile() {
		
		return inputIsFile;
	}
	
	public boolean isOutputToFile() {
		
		return outputIsFile; 
	}
}
