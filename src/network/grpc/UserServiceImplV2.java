package network.grpc;

import io.grpc.stub.StreamObserver;
import network.grpc.UserServiceV2Grpc.UserServiceV2ImplBase;
import productsums.api.user.UserAPIV2;
import productsums.models.user.UserRequest;
import productsums.models.user.UserRequestV2;
import productsums.models.user.UserResponse;
import productsums.models.user.UserResponseV2;

public class UserServiceImplV2 extends UserServiceV2ImplBase{
	private final UserAPIV2 userAPI; 
	
	public UserServiceImplV2(UserAPIV2 userAPI) {
		
		this.userAPI = userAPI; 
	}
	
	@Override 
	public void userV2(UserProtoV2.UserRequestV2 request, StreamObserver<UserProtoV2.UserResponseV2> responseObserver) {
		
		UserRequestV2 userRequest = new UserRequestV2() {
			
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

			@Override
			public boolean isInputFromFile() {
				
				return request.getInputIsFile();
			}

			@Override
			public boolean isOutputToFile() {

				return request.getOutputIsFile();
			}
		};
		
		UserResponseV2 userResponse = userAPI.user2(userRequest);
		
		UserProtoV2.UserResponseV2 protoResponse = UserProtoV2.UserResponseV2.newBuilder()
				.setMessage(userResponse.getMessage())
				.setOutput(userResponse.getOutput())
				.build();
		
		responseObserver.onNext(protoResponse);
		responseObserver.onCompleted();
	}
}
