package productsums.coordinator;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import productsums.api.user.UserAPI;
import productsums.impl.compute.EngineProcessAPIImpl;
import productsums.impl.process.DataStorageProcessImpl2;
import productsums.impl.user.CoordinatorImplV2;
import productsums.models.user.UserResponse;

public class TestMultiUser {
	private UserAPI coordinator;
	
	@BeforeEach
	public void initializeComputeEngine() {
		this.coordinator = new CoordinatorImplV2(
				new DataStorageProcessImpl2(),
				new EngineProcessAPIImpl()
		);
	}

	@Test
	public void compareMultiAndSingleThreaded() throws Exception {
		int numThreads = 4;
		List<TestUser> testUsers = new ArrayList<>();
		for (int i = 0; i < numThreads; i++) {
			testUsers.add(new TestUser(coordinator));
		}
		
		// Run single threaded
		String singleThreadFilePrefix = "test/productsums/resources/singleThreadOut";
		for (int i = 0; i < numThreads; i++) {
			File outputFile = 
					new File(singleThreadFilePrefix + i + ".txt");
			outputFile.createNewFile();
			UserResponse ur = testUsers.get(i).run(outputFile.getCanonicalPath());
			Assertions.assertTrue(!ur.isError(),ur.getResult());
		}
		
		// Run multi threaded
		ExecutorService threadPool = Executors.newCachedThreadPool();
		List<Future<UserResponse>> results = new ArrayList<>();
		String multiThreadFilePrefix = "test/productsums/resources/multiThreadOut";
		for (int i = 0; i < numThreads; i++) {
			File outputFile = 
					new File(multiThreadFilePrefix + i + ".txt");
			outputFile.createNewFile();
			String multiThreadOutputPath = outputFile.getCanonicalPath();
			TestUser testUser = testUsers.get(i);
			results.add(threadPool.submit(() -> testUser.run(multiThreadOutputPath)));
		}
		
		results.forEach(future -> {
			try {
				UserResponse ur = future.get();
				Assertions.assertTrue(!ur.isError(),ur.getResult());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		
		
		// Check that the output is the same for multi-threaded and single-threaded
		List<String> singleThreaded = loadAllOutput(singleThreadFilePrefix, numThreads);
		List<String> multiThreaded = loadAllOutput(multiThreadFilePrefix, numThreads);
		Assert.assertEquals(singleThreaded, multiThreaded);
	}

	private List<String> loadAllOutput(String prefix, int numThreads) throws IOException {
		List<String> result = new ArrayList<>();
		for (int i = 0; i < numThreads; i++) {
			File outputFile = 
					new File(prefix + i + ".txt");
			result.addAll(Files.readAllLines(outputFile.toPath()));
		}
		return result;
	}
}
