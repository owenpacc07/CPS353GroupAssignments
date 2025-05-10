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
}
