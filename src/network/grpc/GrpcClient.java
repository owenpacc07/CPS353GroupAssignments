package network.grpc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.binarylog.v1.Message;
import network.grpc.UserProto.UserRequest;
import network.grpc.UserProto.UserRequest.Builder;
import network.grpc.UserProto.UserResponse;
import productsums.grpc.DataStoreServiceGrpc;
import productsums.grpc.StoreRequest;
import productsums.grpc.StoreResponse;

public class GrpcClient {
	public static void main(String[] args) throws Exception {
		Thread server = new Thread(()->{
			try {
				GrpcServer.main(null);
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		});
		server.start();
		Thread.sleep(450);
		PrintWriter pw = new PrintWriter(System.out, true);
		try {
			runClient(new Scanner(System.in), pw);
		} catch (Exception e) {pw.println(e.getMessage());}
		
		server.interrupt();
	}
    public static void runClient(Scanner sc, PrintWriter pw) throws Exception{
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress("localhost", 9090)
                .usePlaintext() // skip TLS for local dev
                .build();
        // Call the service
        UserServiceGrpc.UserServiceBlockingStub stub = 
        		UserServiceGrpc.newBlockingStub(channel);
//        Message UserRequest {
//
//        	optional string inputSource = 1;
//        	optional string outputSource = 2;
//        	optional string delimiters = 3;
//        }
        while (true) {
        	pw.println("Print file contents, make a new request, or exit: (0/1/2)");
            String userresponse = sc.nextLine();
            if (userresponse.equals("0")) {
            	pw.println("File Path:");
            	try {
					printFileTo(pw, sc.nextLine());
				} catch (IllegalArgumentException e) {
					pw.println(e.getMessage());
				} catch (IOException e) {
					pw.println("IOException occured while reading from file.");
				}
            } else if (userresponse.equals("1")) {
            	//Build request arguments
            	pw.println("Input from file (F) or from console (C)?");
            	userresponse = sc.nextLine();
            	boolean fileInput = userresponse.equals("F");
            	if (!(userresponse.equals("C")||fileInput)) {
            		channel.shutdown();
            		throw new IllegalArgumentException("Picked an unavailable option, choices were file input (F) or console input (C)");
            	}
            	pw.println("Output to file (F) or to console (C) or both (B)?");
            	userresponse = sc.nextLine();
            	if (!(userresponse.equals("C")||userresponse.equals("F")||userresponse.equals("B"))) {
            		channel.shutdown();
            		throw new IllegalArgumentException("Picked an unavailable option, choices were file input (F) or console input (C)");
            	}
            	requestServer(pw,sc,stub,fileInput, userresponse.toCharArray()[0]);
            } else if (!userresponse.equals("2")){
            	// Shut down channel
            	channel.shutdown();
            	throw new IllegalArgumentException("Picked an unavailable option, choices were print file contents (0), make a request (1) or exit safely (2)");
            } else if (userresponse.equals("2")) {
            	break;
            }
        }
        
        

        // Shut down channel
        channel.shutdown();
    }
    public static void printFileTo(PrintWriter pw, String path) throws IllegalArgumentException, IOException{
    	File f = new File(path);
    	if (!f.exists())
    		throw new IllegalArgumentException("File at path doesn't exist");
    	Scanner sc = new Scanner(f);
    	pw.println("Printing contents of " + path);
    	while (sc.hasNext()) {
    		pw.println(sc.nextLine());
    	}
    	pw.println("Done printing contents of " + path);
    	sc.close();
    }
    public static void requestServer(PrintWriter pw,
    		Scanner sc,
    		UserServiceGrpc.UserServiceBlockingStub stub,
    		boolean fileInput,
    		char output) throws Exception {
    	
    	Builder request = UserProto.UserRequest.newBuilder();
    	if (!fileInput) {
    		pw.println("Type out input string: ");
    		String input = sc.nextLine();
    		File tempFile = generateTempFile();
    		PrintWriter tempWriter = new PrintWriter(tempFile);
    		tempWriter.print(input);
    		request.setInputSource(tempFile.getPath());
    		tempWriter.flush();
    		tempWriter.close();
    	} else {
    		pw.println("Type out a input file path: ");
    		request.setInputSource(sc.nextLine());
    	}
    	File outFile = null;
    	if (output != 'C') {
    		pw.println("Type out output file path: ");
    		outFile = new File(sc.nextLine());
    	} else {
    		outFile = generateTempFile();
    	}
    	request.setOutputSource(outFile.getPath());
    	pw.println("List delimiters. Don't seperate characters using other delimiters.\r\n"
    			+ "For example \":,\" will record : and , as delimiters.");  
        UserProto.UserResponse response = stub.user(request.setDelimiters(sc.nextLine()).build());
        
        if (response.getIsError()) {
        	throw new Exception("Request failed: " + response.getResult());
        }
        
        if (output == 'F') {
        	return;
        }
        
        Scanner tempReader = new Scanner(outFile);
        pw.println("Writing result string");
        while (tempReader.hasNext()) {
        	pw.println(tempReader.nextLine());
        }
        pw.println("Done writing result string");
        tempReader.close();
    }
    public static File generateTempFile() {
    	int iter = 0;
    	File file = null;
    	do {
    		file = new File("src/resources/temp" + iter + ".txt");
    		iter++;
    	} while (file.exists());
    	
    	try {
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	file.deleteOnExit();
    	return file;
    }
}