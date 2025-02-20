package productsums.test.impl;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mockito;

import productsums.api.user.UserAPI;
import productsums.models.user.UserRequest;
import productsums.models.user.UserResponse;

public class UserToEngineTest {
	private UserAPI uapi;
	
	
	@BeforeEach
	public void setup() {
		//uapi = new USERPAIIMPL();
	}
	
	@Test
	public void testPipeline() {
		var request = Mockito.mock(UserRequest.class);
		Mockito.when(request.getInputSource()).thenReturn("[1,10,25]");
		String[] results = uapi.user(request).getResult().split("[;]");
		
		Decomposition decomp = decompose(results[0]);
		assertEqualsGeneric(1, decomp.input(), 1);
		assertEqualsGeneric(0, decomp.output(), 1);
		assertEqualsGeneric(List.of(0), decomp.factors(), 1);
		
		decomp = decompose(results[1]);
		assertEqualsGeneric(10, decomp.input(), 10);
		assertEqualsGeneric(61, decomp.output(), 10);
		assertEqualsGeneric(List.of(1,1,1,1,1,1,1,1,4,4), decomp.factors(), 10);
		
		decomp = decompose(results[2]);
		assertEqualsGeneric(25, decomp.input(), 25);
		assertEqualsGeneric(288, decomp.output(), 25);
		LinkedList<Integer> list = new LinkedList<>();
		for (int i = 0; i < 22; i++)
			list.add(1);
		list.addAll(List.of(2,4,4));
		assertEqualsGeneric(list, decomp.factors(), 25);
	}
	// : . , ;
	public <K> void assertEqualsGeneric(K k1, K k2, int input) {
		Assertions.assertEquals(k1, k2, 
				String.format("Pipeline returned wrong value for k = %d  \r\nExpected: %s\r\nActual: %s",
						input, k1.toString(), k2.toString())
				);
	}
	public Decomposition decompose(String str) {
		String[] arr = str.split("[:]");
		int input = Integer.parseInt(arr[0]);
		
		arr = arr[1].split("[.]");
		int output = Integer.parseInt(arr[0]);
		
		arr = arr[1].split("[,]");
		ArrayList<Integer> list = new ArrayList<>();
		for (int i = 0; i < arr.length; i++)
			list.add(Integer.parseInt(arr[i]));
		return new Decomposition(input,output,list);
	}
	public record Decomposition(int input, int output, List<Integer> factors) {}
}
