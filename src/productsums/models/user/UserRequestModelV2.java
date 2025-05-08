package productsums.models.user;

import java.util.List; 

public class UserRequestModelV2 {

	private String inputSource; 
	private String outputSource; 
	private List<String> delimiters; 
	private boolean inputIsFile; 
	private boolean outputIsFile;
	
	public UserRequestModelV2(String inputSource, String outputSource, List<String> delimiters, boolean inputIsFile, boolean outputIsFile) {
		
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
	
	public List<String> getDelimiters(){
		
		return delimiters; 
	}
	
	public boolean isInputFromFile() {
		
		return inputIsFile; 
	}
	
	public boolean isOutputToFile() {
		
		return outputIsFile; 
	}
}
