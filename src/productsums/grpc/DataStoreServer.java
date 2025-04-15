package productsums.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import productsums.impl.compute.EngineProcessAPIImpl;
import productsums.impl.process.DataStorageProcessAPIImpl;
import java.util.ArrayList;
import java.util.HashMap;

public class DataStoreServer {
    private final Server server;
    private final DataStoreAPI dataStore;

    public DataStoreServer(int port) {
        this.dataStore = new InMemoryDataStore();
        this.server = ServerBuilder.forPort(port)
            .addService(new DataStoreServiceImpl())
            .build();
    }

    private class DataStoreServiceImpl extends DataStoreServiceGrpc.DataStoreServiceImplBase {
        @Override
        public void store(StoreRequest request, StreamObserver<StoreResponse> responseObserver) {
            try {
                if (!request.hasId() || !request.hasProductSum()) {
                    throw new IllegalArgumentException("Missing required fields");
                }

                StoredResult result = new StoredResult(
                    request.getId(),
                    request.getProductSum(),
                    request.getFactorsList() != null ? request.getFactorsList() : new ArrayList<>()
                );
                dataStore.store(result);

                responseObserver.onNext(StoreResponse.newBuilder()
                    .setSuccess(true)
                    .setMessage("Successfully stored")
                    .build());
            } catch (Exception e) {
                responseObserver.onNext(StoreResponse.newBuilder()
                    .setSuccess(false)
                    .setMessage(e.getMessage())
                    .build());
            } finally {
                responseObserver.onCompleted();
            }
        }

        @Override
        public void retrieve(RetrieveRequest request, StreamObserver<RetrieveResponse> responseObserver) {
            try {
                if (!request.hasId()) {
                    throw new IllegalArgumentException("Missing required field: id");
                }

                StoredResult result = dataStore.retrieve(request.getId());
                RetrieveResponse.Builder builder = RetrieveResponse.newBuilder()
                    .setId(request.getId())
                    .setFound(result != null);

                if (result != null) {
                    builder.setProductSum(result.productSum())
                        .addAllFactors(result.factors());
                }

                responseObserver.onNext(builder.build());
            } catch (Exception e) {
                responseObserver.onNext(RetrieveResponse.newBuilder()
                    .setId(request.getId())
                    .setFound(false)
                    .build());
            } finally {
                responseObserver.onCompleted();
            }
        }

        @Override
        public void processData(ProcessDataRequest request, StreamObserver<ProcessDataResponse> responseObserver) {
            try {
                if (!request.hasMinK() || !request.hasMaxK()) {
                    throw new IllegalArgumentException("Missing required fields");
                }

                ProcessDataResponse.Builder builder = ProcessDataResponse.newBuilder()
                    .setIsError(false)
                    .setResults(new HashMap<>()); // Required field must be set

                for (int k = request.getMinK(); k <= request.getMaxK(); k++) {
                    StoredResult result = dataStore.retrieve(k);
                    if (result != null) {
                        builder.putResults(k, (int)result.productSum());
                    }
                }

                responseObserver.onNext(builder.build());
            } catch (Exception e) {
                responseObserver.onNext(ProcessDataResponse.newBuilder()
                    .setIsError(true)
                    .setErrorMessage(e.getMessage())
                    .build());
            } finally {
                responseObserver.onCompleted();
            }
        }
    }

    public void start() throws Exception {
        server.start();
        System.out.println("Data Store Server started on port " + server.getPort());
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down gRPC server");
            this.stop();
        }));
        server.awaitTermination();
    }

    public void stop() {
        if (server != null) {
            server.shutdown();
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Usage: DataStoreServer <port>");
            System.exit(1);
        }

        int port = Integer.parseInt(args[0]);
        DataStoreServer server = new DataStoreServer(port);
        server.start();
    }
}