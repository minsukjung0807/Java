package b2.BackBlaze.upload_file.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServerSideEncryption {

    @SerializedName("algorithm")
    @Expose
    private String algorithm;
    
    @SerializedName("mode")
    @Expose
    private String mode;

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

}
