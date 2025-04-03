package productsums.coordinator;

import java.io.File;

import productsums.api.user.UserAPI;
import productsums.impl.user.CoordinatorImpl;
import productsums.models.user.UserRequest;

public class TestUser {
    private final UserAPI coordinator;

    public TestUser(UserAPI coordinator) {
        this.coordinator = coordinator;
    }

    public void run(String outputPath) {
        char delimiter = ';';
        String inputPath = "test" + File.separatorChar + "resources" + File.separatorChar + "testInputFile.test";
        
        UserRequest request = new UserRequest() {
            @Override
            public String getInputSource() {
                return inputPath;
            }
            
            @Override
            public String getOutputSource() {
                return outputPath;
            }
            
            @Override
            public String getDelimiters() {
                return String.valueOf(delimiter);
            }
        };
        
        coordinator.user(request);
    }

}
