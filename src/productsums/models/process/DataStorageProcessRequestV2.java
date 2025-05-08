package productsums.models.process;

import java.io.OutputStream;
import java.util.Map;
import java.util.Optional;

public record DataStorageProcessRequestV2(Optional<Map<Integer, Integer>> productSumResults, OutputStream os, Optional<String> errorResponse) {// Maps k -> minimal product-sum number

}
