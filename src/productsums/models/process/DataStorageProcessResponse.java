package productsums.models.process;

import java.util.Map;


//Response model containing the computed product-sum results.
public class DataStorageProcessResponse {
    private Map<Integer, Integer> productSumResults; // Maps k -> minimal product-sum number


    //Constructor for DataStorageProcessResponse. 
    public DataStorageProcessResponse(Map<Integer, Integer> productSumResults) {
        this.productSumResults = productSumResults;
    }

    //gets the productsum results
    public Map<Integer, Integer> getProductSumResults() {
        return productSumResults;
    }

    @Override
    public String toString() {
        return "Product-Sum Results: " + productSumResults.toString();
    }
}