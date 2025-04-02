package productsums.api.process;

import java.io.InputStream;
import java.util.Optional;

import productsums.models.process.DataStorageProcessRequestV2;
import productsums.models.process.DataStorageProcessResponseV2;

public interface DataStorageProcessAPIV2 {
    Optional<Integer> writeOutputs(DataStorageProcessRequestV2 request);
    DataStorageProcessResponseV2 readInputs(InputStream is, String delimiters);
}
