package productsums.api.user;

import projectannotations.NetworkAPI;
import productsums.models.user.UserRequest;

@NetworkAPI
public interface UserAPI {
	void user(UserRequest request);
}