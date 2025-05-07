package productsums.impl.process;

import java.io.InputStream;
import java.util.Optional;
import java.util.Map;
import productsums.api.process.DataStorageProcessAPIV2;
import productsums.models.process.DataStorageProcessRequestV2;
import productsums.models.process.DataStorageProcessResponseV2;
import datastorage.grpc.DataStoreGrpcClient;

public class DataStorageGrpcImpl implements DataStorageProcessAPIV2 {
    private final DataStoreGrpcClient client;
    private final DataStorageProcessImpl2 fileProcessor;

    public DataStorageGrpcImpl(String host, int port) {
        this.client = new DataStoreGrpcClient(host, port);
        this.fileProcessor = new DataStorageProcessImpl2();
    }

    @Override
    public Optional<Integer> writeOutputs(DataStorageProcessRequestV2 request) {
        Optional<Integer> fileResult = fileProcessor.writeOutputs(request);
        if (fileResult.isPresent()) {
            return fileResult;
        }

        if (request.productSumResults().isPresent()) {
            for (Map.Entry<Integer, Integer> entry : request.productSumResults().get().entrySet()) {
                boolean success = client.storeData(entry.getKey(), entry.getValue(), null);
                if (!success) {
                    return Optional.of(1);
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public DataStorageProcessResponseV2 readInputs(InputStream is, String delimiters) {
        return fileProcessor.readInputs(is, delimiters);
    }
}
