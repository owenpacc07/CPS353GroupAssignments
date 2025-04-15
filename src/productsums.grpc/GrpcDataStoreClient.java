package productsums.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import productsums.api.process.DataStorageProcessAPI;
import productsums.api.process.DataStorageProcessAPIV2;
import productsums.models.process.DataStorageProcessRequest;
import productsums.models.process.DataStorageProcessResponse;
import productsums.models.process.DataStorageProcessRequestV2;
import productsums.models.process.DataStorageProcessResponseV2;
import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class GrpcDataStoreClient implements DataStorageProcessAPI, DataStorageProcessAPIV2 {
    private final DataStoreServiceGrpc.DataStoreServiceBlockingStub blockingStub;
    private final ManagedChannel channel;
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 5;

    public GrpcDataStoreClient(String host, int port) {
        this.channel = ManagedChannelBuilder.forAddress(host, port)
            .usePlaintext()
            .build();
        this.blockingStub = DataStoreServiceGrpc.newBlockingStub(channel);
    }

    @Override
    public DataStorageProcessResponse processData(DataStorageProcessRequest request) {
        if (request == null) {
            return DataStorageProcessResponse.nullRequest;
        }

        try {
            ProcessDataRequest.Builder builder = ProcessDataRequest.newBuilder()
                .setMinK(request.getMinK())
                .setMaxK(request.getMaxK());

            // Only set optional fields if present
            if (request.getInputSource() != null) {
                builder.setInputSource(request.getInputSource());
            }
            if (request.getOutputDestination() != null) {
                builder.setOutputSource(request.getOutputDestination());
            }

            ProcessDataResponse response = blockingStub.processData(builder.build());

            // Verify required fields are present
            if (!response.hasIsError() || !response.hasResults()) {
                return DataStorageProcessResponse.IOFailure;
            }

            return new DataStorageProcessResponse(response.getResultsMap());
        } catch (StatusRuntimeException e) {
            return DataStorageProcessResponse.IOFailure;
        }
    }

    @Override
    public DataStorageProcessResponseV2 readInputs(InputStream input, String delimiters) {
        if (input == null) {
            return new DataStorageProcessResponseV2(Optional.empty(), Optional.of(4));
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            String line = reader.readLine();
            if (line == null || line.trim().isEmpty()) {
                return new DataStorageProcessResponseV2(Optional.empty(), Optional.of(4));
            }

            List<Integer> numbers = new ArrayList<>();
            String[] tokens = line.split("[" + (delimiters != null ? delimiters : ",") + "]");

            for (String token : tokens) {
                try {
                    if (!token.trim().isEmpty()) {
                        numbers.add(Integer.parseInt(token.trim()));
                    }
                } catch (NumberFormatException e) {
                    return new DataStorageProcessResponseV2(Optional.empty(), Optional.of(2));
                }
            }

            if (numbers.isEmpty()) {
                return new DataStorageProcessResponseV2(Optional.empty(), Optional.of(1));
            }

            return new DataStorageProcessResponseV2(Optional.of(numbers), Optional.empty());
        } catch (IOException e) {
            return new DataStorageProcessResponseV2(Optional.empty(), Optional.of(3));
        }
    }

    @Override
    public Optional<Integer> writeOutputs(DataStorageProcessRequestV2 request) {
        if (request == null || !request.results().isPresent() || request.results().get().isEmpty()) {
            return Optional.of(2);
        }

        try {
            Map<Integer, Integer> results = request.results().get();
            for (Map.Entry<Integer, Integer> entry : results.entrySet()) {
                StoreRequest storeRequest = StoreRequest.newBuilder()
                    .setId(entry.getKey())
                    .setProductSum(entry.getValue())
                    .addAllFactors(new ArrayList<>())
                    .build();

                StoreResponse response = blockingStub.store(storeRequest);
                if (!response.getSuccess()) {
                    return Optional.of(1);
                }
            }

            return Optional.empty();
        } catch (StatusRuntimeException e) {
            return Optional.of(1);
        }
    }

    public void shutdown() {
        if (channel != null) {
            try {
                channel.shutdown().awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
}