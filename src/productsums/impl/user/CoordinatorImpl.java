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

public class CoordinatorImpl implements UserAPI {
	
	private final DataStorageProcessAPI dataStorage; 
	private final EngineProcessAPI computeEngine;
	
	public CoordinatorImpl(DataStorageProcessAPI dataStorage, EngineProcessAPI computeEngine) {
		
		this.dataStorage = dataStorage;
		this.computeEngine = computeEngine;
	}
	
	@Override 
	public UserResponse user(UserRequest request) {
		//Reads integers from DataStorage and passes them to compute engine
		//Results need to be readable as well as written back to dataStorage 
		
		DataStorageProcessRequest storageRequest = new DataStorageProcessRequest(2,100,request.getInputSource(), request.getOutputSource());
		
		DataStorageProcessResponse storageResponse = dataStorage.processData(storageRequest);
		Map<Integer, Integer> productSumResults = storageResponse.getProductSumResults();
		
		List<Integer> keys = new ArrayList<>(productSumResults.keySet());
		List<EngineOutput> computedResults = new ArrayList<>();
		
		for(int i = 0; i < keys.size(); i++) {
			
			int k = keys.get(i);
			
			EngineInput input = new EngineInput(k);
			EngineOutput output = computeEngine.compute(input);
			
			computedResults.add(output);
		}
		
		Map<Integer, Integer> finalResults = productSumResults; 
		
		dataStorage.processData(new DataStorageProcessRequest(2, 100, null, request.getOutputSource()));
		
		return new UserResponseModel("Computation worked. Results: " + finalResults);
		
		
		
	}

}
