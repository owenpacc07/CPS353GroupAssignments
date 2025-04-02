package productsums.models.process;

import java.util.List;
import java.util.Optional;

public record DataStorageProcessResponseV2(Optional<List<Integer>> arguments, Optional<Integer> sentinel) {
	
}
