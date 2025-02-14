package productsums.models.process;

//Request model for product-sum computation.
public class DataStorageProcessRequest {
    private int minK;  
    private int maxK;  
    private String inputSource;
    private String outputDestination;

    //Constructor for DataStorageProcessRequest.
    public DataStorageProcessRequest(int minK, int maxK, String inputSource, String outputDestination) {
        this.minK = minK;
        this.maxK = maxK;
        this.inputSource = inputSource;
        this.outputDestination = outputDestination;
    }

    public int getMinK() {
        return minK;
    }

    public int getMaxK() {
        return maxK;
    }

    public String getInputSource() {
        return inputSource;
    }

    public String getOutputDestination() {
        return outputDestination;
    }

    //to string
    @Override
    public String toString() {
        return "Requesting Product-Sum Numbers for k-range: [" + minK + ", " + maxK + 
               "], Input: " + inputSource + ", Output: " + outputDestination;
    }
}