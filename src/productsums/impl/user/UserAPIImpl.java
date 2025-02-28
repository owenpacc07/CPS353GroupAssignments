package productsums.impl.user;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;
import java.util.List;
import java.util.function.IntPredicate;

import productsums.api.process.DataStorageProcessAPI;
import productsums.api.user.UserAPI;
import productsums.models.user.UserRequest;
public class UserAPIImpl implements UserAPI{
	private DataStorageProcessAPI dspAPI;
	public UserAPIImpl(DataStorageProcessAPI dspAPI) {
		this.dspAPI = dspAPI;
	}
	
	
	@Override
	public void user(UserRequest request) {
		List<Character> delims = request.getDelimiters();
		StringReader reader = request.getInputSource();
		
		List<String> requests = new LinkedList<>();
		StringBuilder sb = new StringBuilder();
		int ch = 0;
		IntPredicate isDelimiter = testchar -> delims.stream().anyMatch(item -> item == (char) testchar);
		try {
			while ((ch = reader.read()) != -1) {
				if (isDelimiter.test((char)ch)) {
					requests.add(sb.toString());
					sb = new StringBuilder();
				} else {
					sb.append((char)ch);
				}
			}
			if (!sb.isEmpty())
				requests.add(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<Integer> requestnums = requests.stream()
			.map(Integer::parseInt)
			.toList();
		
		//TODO modify to pass data into DSP correctly. DSP response and request need to be modified.
		//Response needs to be passed into the output stream as well.
		dspAPI.processData(null).getProductSumResults();
		
		
		//TODO print response to output stream
		request.getOutputSource().print(false);
	}
}
