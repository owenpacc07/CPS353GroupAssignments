package datastorage.grpc;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;
import productsums.grpc.DataStoreServiceGrpc;
import productsums.grpc.StoreRequest;
import productsums.grpc.StoreResponse;
import productsums.grpc.RetrieveRequest;
import productsums.grpc.RetrieveResponse;
import java.util.concurrent.ConcurrentHashMap;
import java.io.IOException;

public class DataStoreServer {
    private final Server server;
    private final int port;

    public DataStoreServer(int port) {
        this.port = port;
        this.server = ServerBuilder.forPort(port)
            .addService(new DataStoreServiceImpl())
            .build();
    }

    public void start() throws IOException {
        server.start();
        System.out.println("DataStore Server started on port " + port);
    }

    public void shutdown() {
        if (server != null) {
            server.shutdown();
        }
    }

    private static class DataStoreServiceImpl extends DataStoreServiceGrpc.DataStoreServiceImplBase {
        private final ConcurrentHashMap<Long, Long> store = new ConcurrentHashMap<>();

        @Override
        public void store(StoreRequest request, StreamObserver<StoreResponse> responseObserver) {
            store.put(request.getId(), request.getProductSum());
            responseObserver.onNext(StoreResponse.newBuilder()
                .setSuccess(true)
                .build());
            responseObserver.onCompleted();
        }

        @Override
        public void retrieve(RetrieveRequest request, StreamObserver<RetrieveResponse> responseObserver) {
            Long value = store.get(request.getId());
            RetrieveResponse.Builder response = RetrieveResponse.newBuilder()
                .setFound(value != null);
            if (value != null) {
                response.setProductSum(value);
            }
            responseObserver.onNext(response.build());
            responseObserver.onCompleted();
        }
    }
}
