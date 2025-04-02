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

public class CoordinatorImplV2 implements UserAPI {

	private final DataStorageProcessAPIV2 dataStorage;
	private final EngineProcessAPI engineProcess;

	// Parameter Validation
	public CoordinatorImplV2(DataStorageProcessAPIV2 dataStorage, EngineProcessAPI engineProcess) {
		this.engineProcess = Objects.requireNonNull(engineProcess, "EngineProcess cannot be null.");
		this.dataStorage = Objects.requireNonNull(dataStorage, "DataStorage cannot be null.");
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
		if (!new File(request.getOutputSource()).exists()) {
			throw new IllegalArgumentException("Output file does not exist");
		}
		
		
		
		DataStorageProcessResponseV2 response = null;
		try {
			response = dataStorage.readInputs(new FileInputStream(new File(request.getInputSource())), request.getDelimiters());
		} catch (FileNotFoundException e) {//File should exist unless destroyed after our argument validation
		}
		
		if (response.arguments().isEmpty()) {
			//TODO behavior for detecting errors in DSP
		}
		
		Map<Integer,Integer> responses = new HashMap<>();
		for (int i = 0; i < response.arguments().get().size(); i++) {
			EngineOutput eo = engineProcess.compute(new EngineInput(response.arguments().get().get(i)));
			if (eo.isSentinel()) {
				//TODO behavior for detecting errors in Engine
			}
			responses.put(response.arguments().get().get(i),eo.answer());
		}
		
		DataStorageProcessRequestV2 dspRequest = null;
		try {
			dspRequest = new DataStorageProcessRequestV2(Optional.of(responses),
					new FileOutputStream(new File(request.getOutputSource())),
					Optional.empty());
		} catch (FileNotFoundException e) {
			//File should exist unless destroyed after our argument validation
		}
		
		
		Optional<Integer> sentinel = dataStorage.writeOutputs(dspRequest);
		if (sentinel.isPresent()) {
			//TODO behavior for detecting errors in DSP
		}
		return new UserResponseModel("Success");
	}

	// Generalizing error output for our system
	private UserResponse errorMessage(String message) {

		return new UserResponseModel("Error: " + message);
	}
}