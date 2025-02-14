package productsums.api.process;

import productsums.models.process.DataStorageProcessRequest;
import productsums.models.process.DataStorageProcessResponse;
import projectannotations.ProcessAPI;

/**
 * Interface for processing product-sum computations.
 */
@ProcessAPI
public interface DataStorageProcessAPI {
    
    /**
     * Computes the minimal product-sum numbers for a given range of k values.
     * @param request of the range of k values.
     * @return A response containing a map of k values to their minimal product-sum numbers.
     */
    DataStorageProcessResponse processData(DataStorageProcessRequest request);
}