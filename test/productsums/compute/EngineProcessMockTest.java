package productsums.compute;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import productsums.api.compute.EngineProcessAPI;
import productsums.impl.compute.EngineProcessAPIImpl;
import productsums.models.compute.EngineInput;
import productsums.models.compute.EngineOutput;

public class EngineProcessMockTest {
	private EngineProcessAPI api;
	
	@BeforeEach
	public void test() {
		api = new EngineProcessAPIImpl();
	}
	public static Stream<Arguments> provideTestCases() {
        return Stream.of(
            Arguments.of(2, 4, List.of(2, 2)),
            Arguments.of(4, 8, List.of(1, 1, 2, 4)),
            Arguments.of(5, 8, List.of(1, 1, 2, 2, 2))
        );
    }
	
	
	@ParameterizedTest
	@MethodSource("provideTestCases")
	public void engineProcessAPISmokeTest(int input, int answer, List<Integer> factors) {
		EngineOutput result = api.compute(new EngineInput(input));
		assertEquals(result.answer(), answer,
				String.format("EngineProcess didn't return correct value. Expected: %d Actual: %d", result.answer(), answer));
		assertEquals(result.factors(), factors,
				String.format("EngineProcess didn't return correct factors. \r\nExpected: %s\r\nActual: %s",
						result.factors().toString(),
						factors.toString()));
		assertEquals(result.inputIndex(), input,
				String.format("EngineProcess didn't return the same input as was passed in. Expected: %d Actual: %d", result.inputIndex(), input));
	}
}
