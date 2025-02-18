package productsums.api.user;

import projectannotations.NetworkAPI;
import projectannotations.NetworkAPIPrototype;
import productsums.models.user.UserRequest;
import productsums.models.user.UserResponse;

@NetworkAPI
public interface UserAPI {
	UserResponse user(UserRequest request);
}
