package main.java;

import java.io.IOException;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import network.grpc.UserServiceImpl;
import productsums.api.process.DataStorageProcessAPIV2;
import productsums.api.user.UserAPI;
import productsums.impl.compute.EngineProcessAPIImpl;
import productsums.impl.process.DataStorageProcessImpl2;
import productsums.impl.user.CoordinatorImplV2;

public class Main {

	public static void main(String[] args) throws Exception {
		DataStorageProcessAPIV2 dapi = new DataStorageProcessImpl2();
		UserAPI userApi = new CoordinatorImplV2(new DataStorageProcessImpl2(), new EngineProcessAPIImpl());
		//Server server = ServerBuilder.forPort(9090).addService(new UserServiceImpl(userApi)).addService(null).build().start();
		
		//server.awaitTermination();
	}
}
