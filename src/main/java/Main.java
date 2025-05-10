package main.java;

import java.util.Scanner;

import com.sun.net.httpserver.HttpServer;
import datastorage.grpc.DataStoreServiceImpl;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import network.grpc.UserServiceImplV2;
import productsums.api.user.UserAPIV2;
import productsums.impl.compute.EngineProcessAPIImpl;
import productsums.impl.process.DataStorageProcessImpl2;
import productsums.impl.user.CoordinatorImplV3;
import web.WebServer;

public class Main {

	public static void main(String[] args) throws Exception {
		UserAPIV2 userApi = new CoordinatorImplV3(new DataStorageProcessImpl2(), new EngineProcessAPIImpl());
		Server server1 = ServerBuilder.forPort(9090)
				.addService(new DataStoreServiceImpl())
				.addService(new UserServiceImplV2(userApi))
				.build()
				.start();
		HttpServer server2 = WebServer.setup();
		System.out.println("Press enter to turn off servers: ");
		Scanner sc = new Scanner(System.in);
		sc.nextLine();
		sc.close();
		server1.shutdown();
		server2.stop(1);
	}
}
