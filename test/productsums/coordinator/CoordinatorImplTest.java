package productsums.coordinator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import productsums.api.user.UserAPI;
import productsums.impl.user.CoordinatorImpl;
import productsums.api.process.DataStorageProcessAPI;
import productsums.models.process.DataStorageProcessRequest;
import productsums.models.user.UserRequest;
import productsums.models.user.UserResponse;

import java.util.HashMap;
import java.util.Map;


public class CoordinatorImplTest {

	@Test
	public void smokeTest() {
		
		DataStorageProcessAPI mockDataStorage = mock(DataStorageProcessAPI.class);
		UserRequest mockRequest = mock(UserRequest.class);
		
		when(mockRequest.getInputSource()).thenReturn("mockInput.txt");
		when(mockRequest.getOutputSource()).thenReturn("mockOutput.txt");
		when(mockRequest.getDelimiters()).thenReturn(",");
		
		Map<Integer, Integer> mockProductSumResults = new HashMap<>();
		mockProductSumResults.put(2, 4);
		mockProductSumResults.put(5, 10);
		when(mockDataStorage.processData(any(DataStorageProcessRequest.class))).thenReturn(new productsums.models.process.DataStorageProcessResponse(mockProductSumResults));
		
		UserAPI userAPI = new CoordinatorImpl(mockDataStorage);
		UserResponse result = userAPI.user(mockRequest);
		
		System.out.println("Response: " + result.getResult());
		Assertions.assertNotNull(result, "No null present.");
		Assertions.assertTrue(result.getResult().contains("Computation worked. Results:"), result.getResult());
	}
}
