package test.productsums.test.api.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import productsums.models.user.UserRequest;
import productsums.models.user.UserResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import projectannotations.NetworkAPI;
import projectannotations.NetworkAPIPrototype;
import productsums.models.user.UserRequest;
import productsums.models.user.UserResponse;

class UserAPITest {
	
	@Mock 
	public UserRequest mockRequest; 
	
	@InjectMocks 
	public UserAPIImpl userAPI;
	
	@BeforeEach 
	void setup() {
		
		MockitoAnnotations.openMocks(this);
		
		when(mockRequest.getInputSource()).thenReturn("Input");
		when(mockRequest.getOutputSource()).thenReturn("Output");
		when(mockRequest.getDelimiters()).thenReturn("");
		
	}
	
	@Test 
	void test() {
		
		UserResponse response = userAPI.user(mockRequest);
	}
}