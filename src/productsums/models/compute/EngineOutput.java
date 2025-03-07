package productsums.models.compute;
import java.util.List;

public record EngineOutput(int inputIndex, int answer, List<Integer> factors) {
	private static final EngineOutput sentinelKoutOfBounds = new EngineOutput(-1,-1,null);
	private static final EngineOutput sentinelArithmeticException = new EngineOutput(-1,-1,null);
	private static final EngineOutput sentinelNullPointer = new EngineOutput(-1,-1,null);
	public static EngineOutput outOfBounds() {
		return sentinelKoutOfBounds;
	}
	public static EngineOutput incomputableK() {
		return sentinelArithmeticException;
	}
	public static EngineOutput nullPointer() {
		return sentinelNullPointer;
	}
}
