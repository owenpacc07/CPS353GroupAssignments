package network.grpc;

import io.grpc.stub.StreamObserver; 
import productsums.api.user.UserAPI;
import productsums.models.user.UserRequest;
import productsums.models.user.UserResponse; 

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {
	
	private final UserAPI userAPI; 
	
	public UserServiceImpl(UserAPI userAPI) {
		
		this.userAPI = userAPI; 
	}
	
	@Override 
	public void user(UserProto.UserRequest request, StreamObserver<UserProto.UserResponse> responseObserver) {
		
		UserRequest userRequest = new UserRequest() {
			
			@Override 
			public String getInputSource() {
				
				return request.getInputSource();
			}
			
			@Override
			public String getOutputSource() {
				
				return request.getOutputSource();
			}
			
			@Override 
			public String getDelimiters() {
				
				return request.getDelimiters();
			}
		};
		
		UserResponse userResponse = userAPI.user(userRequest);
		
		UserProto.UserResponse protoResponse = UserProto.UserResponse.newBuilder().setIsError(userResponse.isError()).setResult(userResponse.getResult()).build();
		
		responseObserver.onNext(protoResponse);
		responseObserver.onCompleted();
	}
}