package productsums.api.user;

import projectannotations.NetworkAPI;
import productsums.models.user.UserRequestV2;
import productsums.models.user.UserResponseV2;


public interface UserAPIV2 {
  
	UserResponseV2 user2(UserRequestV2 request2);
}
