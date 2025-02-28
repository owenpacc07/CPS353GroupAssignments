package productsums.models.user;

import java.io.PrintWriter;
import java.io.StringReader;
import java.util.List;

public interface UserRequest {
	
	//Should be an input stream to be more generic
	StringReader getInputSource();
	//Should be a output stream to be more generic
	PrintWriter getOutputSource();
	//Should be a set of delimiters
	List<Character> getDelimiters();
	
}
