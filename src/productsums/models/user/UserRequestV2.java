package productsums.models.user;

public interface UserRequestV2 {
	public String getInputSource();
	
	public String getOutputSource();
	
	public String getDelimiters();
	
	public boolean isInputFromFile();
	
	public boolean isOutputToFile();
}
