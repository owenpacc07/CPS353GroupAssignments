package datastorage.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import productsums.grpc.DataStoreServiceGrpc;
import productsums.grpc.StoreRequest;
import productsums.grpc.StoreResponse;
import productsums.grpc.RetrieveRequest;
import productsums.grpc.RetrieveResponse;

public class DataStoreGrpcClient {
    private final DataStoreServiceGrpc.DataStoreServiceBlockingStub blockingStub;

    public DataStoreGrpcClient(String host, int port) {
        ManagedChannel channel = ManagedChannelBuilder.forAddress(host, port)
                .usePlaintext()
                .build();
        this.blockingStub = DataStoreServiceGrpc.newBlockingStub(channel);
    }

    public boolean storeData(long id, long productSum, Iterable<Long> factors) {
        StoreRequest request = StoreRequest.newBuilder()
                .setId(id)
                .setProductSum(productSum)
                .addAllFactors(factors)
                .build();
        StoreResponse response = blockingStub.store(request);
        return response.getSuccess();
    }

    public RetrieveResponse retrieveData(long id) {
        RetrieveRequest request = RetrieveRequest.newBuilder()
                .setId(id)
                .build();
        return blockingStub.retrieve(request);
    }
}