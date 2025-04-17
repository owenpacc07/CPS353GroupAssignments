package productsums.coordinator;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Scanner;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import network.grpc.GrpcClient;
import network.grpc.GrpcServer;

public class EndToEndTest {
	@Test
	public void testPipeline() {
		Thread server = new Thread(() -> {
			try {
				GrpcServer.main(null);
			} catch (Exception e1) {
				;
			}
		});
		server.start();
		//wait for server to start for half a second
		try {
			Thread.sleep(500);
		} catch (InterruptedException e1) {
			;
		}
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw, true);
		try {
			GrpcClient.runClient(new Scanner("1\n" + "C\n" + "C\n" + "3;4;7\n" + ";\n" + "2\n"), pw);
		} catch (Exception e) {
			pw.println(e.getMessage());
		}
		Assertions.assertTrue(sw.toString().contains("3:6;4:8;7:12;"));
		server.interrupt();
	}

	public <K> void assertEqualsGeneric(K k1, K k2, int input) {
		Assertions.assertEquals(k1, k2,
				String.format("Pipeline returned wrong value for k = %d  \r\nExpected: %s\r\nActual: %s", input,
						k1.toString(), k2.toString()));
	}
}
