package network.grpc;

import io.grpc.Server; 
import io.grpc.ServerBuilder; 
import productsums.api.user.UserAPI;
import productsums.impl.compute.EngineProcessAPIImpl; 
import productsums.impl.process.DataStorageProcessImpl2; 
import productsums.impl.user.CoordinatorImplV2; 

public class GrpcServer {
	
	public static void main(String[] args) throws Exception {
		
		UserAPI userApi = new CoordinatorImplV2(new DataStorageProcessImpl2(), new EngineProcessAPIImpl());
		
		Server server = ServerBuilder.forPort(9090).addService(new UserServiceImpl(userApi)).build().start();
		
		System.out.println("gRPC server started.");
		
		server.awaitTermination();
	}
}
