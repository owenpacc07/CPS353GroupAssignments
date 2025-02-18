package productsums.models.compute;
import java.util.List;

public record EngineOutput(int inputIndex, int answer, List<Integer> factors) {}
