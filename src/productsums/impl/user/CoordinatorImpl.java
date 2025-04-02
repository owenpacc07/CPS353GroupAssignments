package productsums.impl.user;

import productsums.api.user.UserAPI;
import productsums.models.user.UserRequest;
import productsums.models.user.UserResponse;
import productsums.models.user.UserResponseModel;
import productsums.api.compute.EngineProcessAPI;
import productsums.api.process.DataStorageProcessAPI;
import productsums.models.process.DataStorageProcessRequest;
import productsums.models.process.DataStorageProcessResponse;
import java.util.Objects;

public class CoordinatorImpl implements UserAPI {
	
	private final DataStorageProcessAPI dataStorage;
	
	//Parameter Validation 
	public CoordinatorImpl(DataStorageProcessAPI dataStorage) {
		
		this.dataStorage = Objects.requireNonNull(dataStorage, "DataStorage cannot be null.");
	}
	
	@Override 
	//Parameter Validation
	public UserResponse user(UserRequest request) {
	try {
			
		if(request == null) {
			
			throw new IllegalArgumentException("UserRequest cannot be null.");
		}
		
		if(request.getInputSource() == null || request.getInputSource().isEmpty()) {
			
			throw new IllegalArgumentException("Input cannot be null or empty");
		}
		
		if(request.getOutputSource() == null || request.getOutputSource().isEmpty()) {
			
			throw new IllegalArgumentException("Output cannot be null or empty");
		}
		
		//Reads integers from DataStorage and passes to Compute Engine
		DataStorageProcessRequest storageRequest = new DataStorageProcessRequest(2,100,request.getInputSource(), request.getOutputSource());
		
		DataStorageProcessResponse storageResponse = dataStorage.processData(storageRequest);
		
		//DataStorage Response Validation
		if(storageResponse == null || storageResponse.getProductSumResults() == null) {
			
			return errorMessage("DataStorage contains no results or just null.");
		}
		
		
		
		//Final Results shown
		return new UserResponseModel("Computation worked. Results: " + storageResponse.getProductSumResults());
	  
	} catch(IllegalArgumentException e) {
			
		return errorMessage("Bad Request: " + e.getMessage()); //Handling Validation
	  
	} catch(IllegalStateException e) {
			
		return errorMessage("Error with processing: " + e.getMessage()); //Handling DataStorage or Engine
	
	} catch(Exception e) {
			
		return errorMessage("Unexpected Error: " + e.getMessage()); //Handling Unexpected Exceptions
	}
		
}
	//Generalizing error output for our system 
	private UserResponse errorMessage(String message) {
				
			return new UserResponseModel("Error: " + message);
	}

}
