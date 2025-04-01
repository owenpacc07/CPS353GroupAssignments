package productsums.models.process;

import java.io.FileOutputStream;
import java.util.Map;
import java.util.Optional;

public record DataStorageProcessRequestV2(Optional<Map<Integer, Integer>> productSumResults, FileOutputStream os, Optional<String> errorResponse) {// Maps k -> minimal product-sum number

}
