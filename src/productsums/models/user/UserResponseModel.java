package productsums.models.user;

public class UserResponseModel implements UserResponse {
	
		private final String result;
		
		public UserResponseModel(String result) {
			
			this.result = result;
		}
		
		@Override
		public String getResult() {
			
			return result;
		}
		
		@Override 
		public String toString() {
			
			return result;
		}

		@Override
		public boolean isError() {
			return result.contains("Error");
		}

	}
