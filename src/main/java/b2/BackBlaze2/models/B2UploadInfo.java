package b2.BackBlaze2.models;

import java.io.Serializable;

public class B2UploadInfo {
    private String bucketId;
    private String uploadUrl;
    private String authorizationToken;

    public B2UploadInfo() {

    }

    public String getBucketId() {
        return bucketId;
    }

    public void setBucketId(String bucketId) {
        this.bucketId = bucketId;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }

    public void setUploadUrl(String uploadUrl) {
        this.uploadUrl = uploadUrl;
    }

    public String getAuthorizationToken() {
        return authorizationToken;
    }

    public void setAuthorizationToken(String authorizationToken) {
        this.authorizationToken = authorizationToken;
    }

    @Override
    public String toString() {
        return "B2UploadInfo{" +
                "bucketId='" + bucketId + '\'' +
                ", uploadUrl='" + uploadUrl + '\'' +
                ", authorizationToken='" + authorizationToken + '\'' +
                '}';
    }
}
