package web;

import com.sun.net.httpserver.HttpServer;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
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
	public static void main(String[] args) {
		boot();
	}
	public static Thread boot() {
		return boot(80);
	}

	public static Thread boot(int port) {
		Thread t = new Thread(WebServer::setup);
		t.start();
		return t;
	}

	private static void setup() {
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

	// choices:
	// create and delete files
	// read and write to files
	// run commands to and from files, to and from direct string, etc
	private static void fileWriteEndpoint(HttpExchange exchange) throws IOException {
		Map<String,String> map = parseFormData(exchange);

		exchange.getRequestHeaders();
		// exchange.getResponseHeaders().set("Content-Type", "application/json");
		// exchange.sendResponseHeaders(200, response.getBytes().length);
	}
	private static void fileReadEndpoint(HttpExchange exchange) throws IOException {
		Map<String,String> map = parseFormData(exchange);
		//get response and replace in html
		String response = getHTML("");
		exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
		exchange.sendResponseHeaders(200, response.getBytes().length);
		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}

	private static void computeRequestEndpoint(HttpExchange exchange) throws IOException {
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(exchange.getRequestBody(), StandardCharsets.UTF_8));

		String requestBody = reader.lines().collect(Collectors.joining("\n"));
		JSONObject request = new JSONObject(requestBody);
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
		BufferedReader br = new BufferedReader(new FileReader(new File("src/resources/main.js")));
		String response = br.lines().collect(Collectors.joining("\n"));
		return response.replace("<!-- Replace Response Here -->", replacement);
	}
}
