package productsums.impl.user;

import productsums.api.user.UserAPI;
import productsums.models.user.UserRequest;
import productsums.models.user.UserResponse;
import productsums.models.user.UserResponseModel;
import productsums.api.process.DataStorageProcessAPI;
import productsums.models.process.DataStorageProcessRequest;
import productsums.models.process.DataStorageProcessResponse;
import productsums.api.compute.EngineProcessAPI;
import productsums.models.compute.EngineInput;
import productsums.models.compute.EngineOutput;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Objects;

public class CoordinatorImpl implements UserAPI {
	
	private final DataStorageProcessAPI dataStorage; 
	private final EngineProcessAPI computeEngine;
	
	//Parameter Validation 
	public CoordinatorImpl(DataStorageProcessAPI dataStorage, EngineProcessAPI computeEngine) {
		
		this.dataStorage = Objects.requireNonNull(dataStorage, "DataStorage cannot be null.");
		this.computeEngine = Objects.requireNonNull(computeEngine, "ComputeEngine cannot be null.");
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
		
			Map<Integer, Integer> productSumResults = storageResponse.getProductSumResults();
		
			List<Integer> keys = new ArrayList<>(productSumResults.keySet());
			List<EngineOutput> computedResults = new ArrayList<>();
		
			for(int i = 0; i < keys.size(); i++) {
			
				int k = keys.get(i);
			
				//Validating input before passed to Engine
				if(k <= 0) {
				
					return errorMessage("k value must be larger than 0.");
				}
			
				EngineInput input = new EngineInput(k);
				EngineOutput output = computeEngine.compute(input);
			
				//Validating output from Engine
				if(output == null) {
				
					return errorMessage("Engine return null as the input.");
				}
			
				computedResults.add(output);
			}
		
			//Final results before return validation
			if(computedResults.isEmpty()) {
			
				return errorMessage("Computation finished. No appropiate results generated.");
			}
		
			Map<Integer, Integer> finalResults = productSumResults; 
		
			//Storage Request 
			dataStorage.processData(new DataStorageProcessRequest(2, 100, null, request.getOutputSource()));
		
			//Final Results shown
			return new UserResponseModel("Computation worked. Results: " + finalResults);
		}
		//Handling Validation
		catch(IllegalArgumentException e) {
			
			return errorMessage("Bad Request: " + e.getMessage());
		}
		//Handling DataStorage or Engine
		catch(IllegalStateException e) {
			
			return errorMessage("Error with processing: " + e.getMessage());
		}
		//Handling Unexpected Exceptions 
		catch(Exception e) {
			
			return errorMessage("Unexpected Error: " + e.getMessage());
		}
		
	}
	//Generalizing error output for our system 
	private UserResponse errorMessage(String message) {
				
			return new UserResponseModel("Error: " + message);
	}

}
