package productsums.coordinator;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import productsums.api.user.UserAPI;
import productsums.impl.compute.EngineProcessAPIImpl;
import productsums.impl.process.DataStorageProcessAPIImpl;
import productsums.impl.user.CoordinatorImpl;

public class TestMultiUser {
	private UserAPI coordinator;
	
	@BeforeEach
	public void initializeComputeEngine() {
		this.coordinator = new CoordinatorImpl(new DataStorageProcessAPIImpl(new EngineProcessAPIImpl()));
	}

	@Test
	public void compareMultiAndSingleThreaded() throws Exception {
		int numThreads = 4;
		List<TestUser> testUsers = new ArrayList<>();
		for (int i = 0; i < numThreads; i++) {
			testUsers.add(new TestUser(coordinator));
		}
		
		// Run single threaded
		String singleThreadFilePrefix = "testMultiUser.compareMultiAndSingleThreaded.test.singleThreadOut.tmp";
		for (int i = 0; i < numThreads; i++) {
			File singleThreadedOut = new File(singleThreadFilePrefix + i);
			singleThreadedOut.deleteOnExit();
			testUsers.get(i).run(singleThreadedOut.getCanonicalPath());
			// Verify files are written correctly
			Assert.assertTrue("Output file should exist", singleThreadedOut.exists());
			Assert.assertTrue("Output file should not be empty", singleThreadedOut.length() > 0);
		}
		
		// Run multi threaded
		ExecutorService threadPool = Executors.newCachedThreadPool();
		List<Future<?>> results = new ArrayList<>();
		String multiThreadFilePrefix = "testMultiUser.compareMultiAndSingleThreaded.test.multiThreadOut.tmp";
		for (int i = 0; i < numThreads; i++) {
			File multiThreadedOut = 
					new File(multiThreadFilePrefix + i);
			multiThreadedOut.deleteOnExit();
			String multiThreadOutputPath = multiThreadedOut.getCanonicalPath();
			TestUser testUser = testUsers.get(i);
			results.add(threadPool.submit(() -> testUser.run(multiThreadOutputPath)));
		}
		
		results.forEach(future -> {
			try {
				future.get();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		
		// Enhanced verification
		List<String> singleThreaded = loadAllOutput(singleThreadFilePrefix, numThreads);
		List<String> multiThreaded = loadAllOutput(multiThreadFilePrefix, numThreads);
		Assert.assertFalse("Output should not be empty", singleThreaded.isEmpty());
		Assert.assertEquals("Single and multi-threaded outputs should match", singleThreaded, multiThreaded);
		
		// Verify output format
		for (String line : singleThreaded) {
			Assert.assertTrue("Each line should contain input and output", line.contains(":"));
			String[] parts = line.split(":");
			Assert.assertEquals("Line should have input and output parts", 2, parts.length);
			// Verify input part is a number
			Integer.parseInt(parts[0].trim());
		}
	}

	private List<String> loadAllOutput(String prefix, int numThreads) throws IOException {
		List<String> result = new ArrayList<>();
		for (int i = 0; i < numThreads; i++) {
			File multiThreadedOut = 
					new File(prefix + i);
			result.addAll(Files.readAllLines(multiThreadedOut.toPath()));
		}
		return result;
	}
}