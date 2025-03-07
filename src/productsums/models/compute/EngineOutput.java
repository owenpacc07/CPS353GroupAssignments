package productsums.models.compute;
import java.util.List;

public record EngineOutput(int inputIndex, int answer, List<Integer> factors) {
	public static final EngineOutput outOfBounds = new EngineOutput(-1,-1,null);
	public static final EngineOutput incomputableK = new EngineOutput(-1,-1,null);
	public static final EngineOutput nullPointer = new EngineOutput(-1,-1,null);
	public boolean isSentinel() {
		return this == outOfBounds ||
				this == incomputableK ||
				this == nullPointer;
	}
}
