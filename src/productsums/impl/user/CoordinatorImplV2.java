package productsums.impl.user;

import productsums.api.user.UserAPI;
import productsums.models.user.UserRequest;
import productsums.models.user.UserResponse;
import productsums.models.user.UserResponseModel;
import productsums.api.compute.EngineProcessAPI;
import productsums.api.process.DataStorageProcessAPIV2;
import productsums.models.compute.EngineInput;
import productsums.models.compute.EngineOutput;
import productsums.models.process.DataStorageProcessRequest;
import productsums.models.process.DataStorageProcessRequestV2;
import productsums.models.process.DataStorageProcessResponse;
import productsums.models.process.DataStorageProcessResponseV2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit; 

public class CoordinatorImplV2 implements UserAPI {

	private final DataStorageProcessAPIV2 dataStorage;
	private final EngineProcessAPI engineProcess;
	private final ExecutorService executorService; 
	private static final int THREAD_POOL_SIZE = 4;

	// Parameter Validation
	public CoordinatorImplV2(DataStorageProcessAPIV2 dataStorage, EngineProcessAPI engineProcess) {
		this.engineProcess = Objects.requireNonNull(engineProcess, "EngineProcess cannot be null.");
		this.dataStorage = Objects.requireNonNull(dataStorage, "DataStorage cannot be null.");
		this.executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
	}

	@Override
	// Parameter Validation
	public UserResponse user(UserRequest request) {
		
		if (request == null) {
			throw new IllegalArgumentException("UserRequest cannot be null.");
		}
		if (request.getInputSource() == null || request.getInputSource().isEmpty()) {
			throw new IllegalArgumentException("Input cannot be null or empty");
		}
		if (request.getOutputSource() == null || request.getOutputSource().isEmpty()) {
			throw new IllegalArgumentException("Output cannot be null or empty");
		}
		if (!new File(request.getInputSource()).exists()) {
			throw new IllegalArgumentException("Input file does not exist");
		}
		File outputFile = new File(request.getOutputSource());
		if(!outputFile.getParentFile().exists()) {
			throw new IllegalArgumentException("Output directory does not exist");
		}
		
		
		
		DataStorageProcessResponseV2 response = null;
		try {
			response = dataStorage.readInputs(new FileInputStream(new File(request.getInputSource())), request.getDelimiters());
		} catch (FileNotFoundException e) {
			return errorMessage("Input file destroyed or was not created before read operation");
		}
		
		if (response.arguments().isEmpty()) {
			switch (response.sentinel().get()) {
			case 1:
				return errorMessage("Delimiter placed before any input tokens, or general misinput");
			case 2:
				return errorMessage("Failed to parse a token, likely a non-numeric token");
			case 3:
				return errorMessage("Other exception while reading tokens");
			case 4:
				return errorMessage("Empty input file, counting as an error");
			default:
				break;
			}
		}
		
		Map<Integer,Integer> responses = new HashMap<>();
		List<Future<EngineOutput>> futures = new ArrayList<>();
		
		List<Integer> argumentList = response.arguments().get();
		for(int i = 0; i< argumentList.size(); i++) {
			final int index = i;
			Future<EngineOutput> future = executorService.submit(() -> {
				int argument = argumentList.get(index);
				return engineProcess.compute(new EngineInput(argument));
			});
			futures.add(future);
		}
		
		//Check Threads are Finished 
		for(int i = 0; i < futures.size(); i++) {
			try {
				EngineOutput eo = futures.get(i).get();
				if(eo.isSentinel()) {
					if (eo == EngineOutput.incomputableK) {
						return errorMessage("ProductSum for could not be computed by the engine");
					} else if (eo == EngineOutput.nullPointer) {
						return errorMessage("Null pointer exception occured in engine");
					} else if (eo == EngineOutput.outOfBounds) {
						return errorMessage("K value out of bounds.");
					}
				}
				responses.put(response.arguments().get().get(i),eo.answer());
			} catch (InterruptedException | ExecutionException e) {
				return errorMessage("Worker threads were interrupted or faced other exceptions");
			}
		}

		DataStorageProcessRequestV2 dspRequest = null;
		try {
			dspRequest = new DataStorageProcessRequestV2(Optional.of(responses),
					new FileOutputStream(new File(request.getOutputSource())),
					Optional.empty());
		} catch (FileNotFoundException e) {
			return errorMessage("Output file destroyed or was not created before write operation");
		}
		
		Optional<Integer> sentinel = dataStorage.writeOutputs(dspRequest);
		if (sentinel.isPresent()) {
			switch (sentinel.get()) {
			case 1:
				return errorMessage("Failed to write to output file");
			case 2:
				return errorMessage("Nothing to output, should be impossible");
			default:
				break;
			}
		}
		return new UserResponseModel("Success");
	}

	// Generalizing error output for our system
	private UserResponse errorMessage(String message) {
		return new UserResponseModel("Error: " + message);
	}
	
	//Shutdown existing threads for testing/resource purposes
	public void shutdown() {
		executorService.shutdown();
		try {
			if(!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
				executorService.shutdownNow();
			}
		}catch(InterruptedException e) {
			executorService.shutdownNow();
		}
	}
}