package web;

import com.sun.net.httpserver.HttpServer;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import network.grpc.UserServiceV2Grpc;
import network.grpc.UserProtoV2;
import network.grpc.UserProtoV2.UserRequestV2;
import network.grpc.UserProtoV2.UserResponseV2;
import productsums.grpc.DataStoreServiceGrpc;
import productsums.grpc.ReadRequest;
import productsums.grpc.ReadResponse;
import productsums.grpc.WriteRequest;
import productsums.grpc.WriteResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.sun.net.httpserver.HttpExchange;

public class WebServer {
	public static HttpServer setup() {
		HttpServer server = null;
		try {
			server = HttpServer.create(new InetSocketAddress(80), 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		server.createContext("/main.js", WebServer::javascriptEndpoint);
		server.createContext("/", WebServer::htmlEndpoint);
		server.createContext("/fileWrite", WebServer::fileWriteEndpoint);
		server.createContext("/fileRead", WebServer::fileReadEndpoint);
		server.createContext("/computeRequest", WebServer::computeRequestEndpoint);
		server.setExecutor(null); // default executor
		server.start();
		return server;
		
	}

	private static void javascriptEndpoint(HttpExchange exchange) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File("src/resources/main.js")));
		String response = br.lines().collect(Collectors.joining("\n"));
		exchange.getResponseHeaders().set("Content-Type", "application/javascript");
		exchange.sendResponseHeaders(200, response.getBytes().length);
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

	private static void htmlEndpoint(HttpExchange exchange) throws IOException {
		String response = getHTML("");
		exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
		exchange.sendResponseHeaders(200, response.getBytes().length);
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

	private static void fileWriteEndpoint(HttpExchange exchange) throws IOException {
		Map<String,String> map = parseFormData(exchange);
		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
				.usePlaintext()
				.build();

		DataStoreServiceGrpc.DataStoreServiceBlockingStub blockingStub =
				DataStoreServiceGrpc.newBlockingStub(channel);
		WriteResponse response = blockingStub.writeFile(WriteRequest.newBuilder()
				.setContents(map.get("data"))
				.setName(map.get("filename"))
				.build());
		
		
		String payload = getHTML(response.hasError() ? 
				"Error Code: " + response.getError():
				"Success");
		exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
		exchange.sendResponseHeaders(200, payload.getBytes().length);
		OutputStream os = exchange.getResponseBody();
		os.write(payload.getBytes());
		os.close();
	}
	private static void fileReadEndpoint(HttpExchange exchange) throws IOException {
		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
				.usePlaintext()
				.build();
		DataStoreServiceGrpc.DataStoreServiceBlockingStub blockingStub =
				DataStoreServiceGrpc.newBlockingStub(channel);
		
		ReadResponse response = blockingStub.readFile(ReadRequest.newBuilder()
				.setName(parseFormData(exchange).get("filename"))
				.build());
		
		String payload = "";
		if (response.hasError()) {
			payload += "Error: " + response.getError() + "\r\n";
		}
		if (response.hasContent()) {
			payload += "Content: " + response.getContent() + "\r\n";
		}

		payload = getHTML(payload);
		exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
		exchange.sendResponseHeaders(200, payload.getBytes().length);
		OutputStream os = exchange.getResponseBody();
		os.write(payload.getBytes());
		os.close();
	}
	//TODO
	/*
	 * input:
	 * inputIsPath:
	 * outputPath:
	 * showOutput:
	 * delimiter:
	 */
	//COME FIND A BUG!
	private static void computeRequestEndpoint(HttpExchange exchange) throws IOException {
		Map<String, String> map = parseFormData(exchange);
		ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 9090)
				.usePlaintext()
				.build();

		UserServiceV2Grpc.UserServiceV2BlockingStub stub = 
				UserServiceV2Grpc.newBlockingStub(channel);

		UserRequestV2.Builder request = UserProtoV2.UserRequestV2.newBuilder();
		
		request.setInputSource(map.get("input"))
			.setInputIsFile(map.get("inputIsPath").equals("true"))
			.setOutputIsFile(map.get("showOutput").equals("false"))
			.setDelimiters(map.get("delimiter"));
		if (!map.get("outputPath").isBlank()) {
			request.setOutputSource(map.get("outputPath"));
		}

		UserResponseV2 returnvalue = stub.userV2(request.build());
		String payload = null;

		if (!returnvalue.getMessage().equals("Success")) {
			payload = getHTML("Request failed with error message: " + returnvalue.getMessage());
		} else {
			payload = getHTML("Request succeeded: " + returnvalue.getOutput());
		}

		exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
		exchange.sendResponseHeaders(200, payload.getBytes().length);
		OutputStream os = exchange.getResponseBody();
		os.write(payload.getBytes());
		os.close();
	}
	
	
	private static Map<String, String> parseFormData(HttpExchange exchange) throws IOException {
	    String body = new BufferedReader(new InputStreamReader(exchange.getRequestBody()))
	        .lines()
	        .collect(Collectors.joining("\n"));
	    Map<String, String> formData = new HashMap<>();
	    for (String pair : body.split("&")) {
	        String[] parts = pair.split("=", 2);
	        String key = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
	        String value = parts.length > 1 ? URLDecoder.decode(parts[1], StandardCharsets.UTF_8) : "";
	        formData.put(key, value);
	    }
	    return formData;
	}
	private static String getHTML(String replacement) throws IOException {
		BufferedReader br = new BufferedReader(new FileReader(new File("src/resources/main.html")));
		String response = br.lines().collect(Collectors.joining("\n"));
		br.close();
		return response.replace("<!-- Replace Response Here -->", replacement);
	}
}
