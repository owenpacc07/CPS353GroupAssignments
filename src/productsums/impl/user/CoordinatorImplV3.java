package productsums.impl.user;

import productsums.api.user.UserAPIV2;
import productsums.models.user.UserRequestV2;
import productsums.models.user.UserResponseV2;
import productsums.models.user.UserResponseModelV2;
import productsums.api.compute.EngineProcessAPI;
import productsums.api.process.DataStorageProcessAPIV2;
import productsums.models.compute.EngineInput;
import productsums.models.compute.EngineOutput;
import productsums.models.process.DataStorageProcessRequestV2;
import productsums.models.process.DataStorageProcessResponseV2;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit; 

public class CoordinatorImplV3 implements UserAPIV2 {

    private final DataStorageProcessAPIV2 dataStorage;
    private final EngineProcessAPI engineProcess;
    private final ExecutorService executorService;
    private static final int THREAD_POOL_SIZE = 4;

    public CoordinatorImplV3(DataStorageProcessAPIV2 dataStorage, EngineProcessAPI engineProcess) {
        this.engineProcess = Objects.requireNonNull(engineProcess, "EngineProcess cannot be null.");
        this.dataStorage = Objects.requireNonNull(dataStorage, "DataStorage cannot be null.");
        this.executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    @Override
    public UserResponseV2 user2(UserRequestV2 request2) {
        if (request2 == null) {
            throw new IllegalArgumentException("UserRequest cannot be null.");
        }
        if (request2.getInputSource() == null || request2.getInputSource().isEmpty()) {
            return errorMessage("Input source cannot be null or empty.");
        }

        DataStorageProcessResponseV2 response;
        try {
            InputStream inputStream = request2.isInputFromFile()
                ? new FileInputStream(new File(request2.getInputSource()))
                : new ByteArrayInputStream(request2.getInputSource().getBytes());

            response = dataStorage.readInputs(inputStream, request2.getDelimiters());
        } catch (FileNotFoundException e) {
            return errorMessage("Input file not found.");
        }

        if (response.arguments().isEmpty()) {
            return errorMessageFromSentinel(response.sentinel());
        }

        Map<Integer, Integer> responses = new HashMap<>();
        List<Future<EngineOutput>> futures = new ArrayList<>();
        List<Integer> argumentList = response.arguments().get();

        for (int i = 0; i < argumentList.size(); i++) {
            final int index = i;
            futures.add(executorService.submit(() -> {
                int argument = argumentList.get(index);
                return engineProcess.compute(new EngineInput(argument));
            }));
        }

        for (int i = 0; i < futures.size(); i++) {
            try {
                EngineOutput eo = futures.get(i).get();
                if (eo.isSentinel()) {
                    return errorMessageFromEngineOutput(eo);
                }
                responses.put(argumentList.get(i), eo.answer());
            } catch (InterruptedException | ExecutionException e) {
                return errorMessage("Worker threads interrupted or faced other exceptions.");
            }
        }

        String outputString;
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            OutputStream finalOutputStream = request2.isOutputToFile()
                ? new FileOutputStream(new File(request2.getOutputSource()))
                : outputStream;

            DataStorageProcessRequestV2 dspRequest = new DataStorageProcessRequestV2(
                Optional.of(responses),
                finalOutputStream,
                Optional.empty()
            );

            Optional<Integer> sentinel = dataStorage.writeOutputs(dspRequest);
            if (sentinel.isPresent()) {
                return errorMessageFromWriteSentinel(sentinel.get());
            }

            outputString = request2.isOutputToFile() ? "" : outputStream.toString();
        } catch (IOException e) {
            return errorMessage("Output stream failed.");
        }

        return new UserResponseModelV2("Success", outputString);
    }

    private UserResponseV2 errorMessage(String message) {
        return new UserResponseModelV2("Error: " + message, "");
    }

    private UserResponseV2 errorMessageFromSentinel(Optional<Integer> sentinel) {
        if (sentinel.isEmpty()) {return errorMessage("Unknown input error.");}
        switch (sentinel.get()) {
            case 1: return errorMessage("Delimiter before input tokens or malformed input.");
            case 2: return errorMessage("Failed to parse a token (non-numeric?).");
            case 3: return errorMessage("Exception during reading tokens.");
            case 4: return errorMessage("Empty input.");
            default: return errorMessage("Unknown sentinel value.");
        }
    }

    private UserResponseV2 errorMessageFromEngineOutput(EngineOutput eo) {
        if (eo == EngineOutput.incomputableK) {
            return errorMessage("Engine could not compute ProductSum.");
        } else if (eo == EngineOutput.nullPointer) {
            return errorMessage("Engine encountered null pointer.");
        } else if (eo == EngineOutput.outOfBounds) {
            return errorMessage("Engine received out-of-bounds K value.");
        } else {
            return errorMessage("Unknown engine error.");
        }
    }

    private UserResponseV2 errorMessageFromWriteSentinel(int sentinel) {
        switch (sentinel) {
            case 1: return errorMessage("Failed to write to output.");
            case 2: return errorMessage("Nothing to output.");
            default: return errorMessage("Unknown output write sentinel.");
        }
    }

    public void shutdown() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }
}
