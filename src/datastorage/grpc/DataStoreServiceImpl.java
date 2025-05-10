package datastorage.grpc;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import io.grpc.stub.StreamObserver;
import network.grpc.UserProto;
import productsums.grpc.DataStore;
import productsums.grpc.DataStoreServiceGrpc;
import productsums.grpc.ReadRequest;
import productsums.grpc.ReadResponse;
import productsums.grpc.RetrieveRequest;
import productsums.grpc.RetrieveResponse;
import productsums.grpc.StoreRequest;
import productsums.grpc.StoreResponse;
import productsums.grpc.WriteRequest;
import productsums.grpc.WriteResponse;
import productsums.grpc.WriteResponse.Builder;
import productsums.impl.process.DataStorageProcessImpl3;
import productsums.impl.process.DataStorageProcessImpl3.DataStorageRead;
import productsums.models.user.UserRequest;
import productsums.models.user.UserResponse;

public class DataStoreServiceImpl extends DataStoreServiceGrpc.DataStoreServiceImplBase {
	private final DataStorageProcessImpl3 api = new DataStorageProcessImpl3();
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
    
    @Override
    public void writeFile(WriteRequest request, StreamObserver<WriteResponse> responseObserver) {
		Optional<Integer> response = api.writeToFile(request.getName(), request.getContents());
		WriteResponse.Builder builder = WriteResponse.newBuilder();
		if (response.isPresent()) {
			builder.setError(response.get());
		}
		WriteResponse protoResponse = builder.build();
		
		responseObserver.onNext(protoResponse);
		responseObserver.onCompleted();
    }
    
    @Override
    public void readFile(ReadRequest request, StreamObserver<ReadResponse> responseObserver) {
    	DataStorageRead response = api.readFromFile(request.getName());
		ReadResponse.Builder builder = ReadResponse.newBuilder();
		if (response.error().isPresent()) {
			builder.setError(response.error().get());
		}
		if (response.message().isPresent()) {
			builder.setContent(response.message().get());
		}
		ReadResponse protoResponse = builder.build();
		
		responseObserver.onNext(protoResponse);
		responseObserver.onCompleted();
    }
}
