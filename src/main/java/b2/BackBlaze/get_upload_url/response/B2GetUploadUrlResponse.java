package b2.BackBlaze.get_upload_url.response;

import b2.BackBlaze.create_bucket.response.B2CreateBucketResponse;

public class B2GetUploadUrlResponse {

    private B2CreateBucketResponse bucket;
    private String uploadURL, uploadAuthorizationToken;

    /**
     * B2GetUploadUrlResponse 인스턴스를 구성합니다.
     *
     * @param bucket 파일 업로드시에 저장될 버킷
     * @param uploadURL 파일 업로드시에 사용되는 URL
     * @param uploadAuthorizationToken 파일 업로드를 위해 사용되는 업로드 인증 토큰 (주의: 그냥 인증 토큰과 다름)
     */ 
    public B2GetUploadUrlResponse(B2CreateBucketResponse bucket, String uploadURL, String uploadAuthorizationToken){
        this.bucket = bucket;
        this.uploadURL = uploadURL;
        this.uploadAuthorizationToken = uploadAuthorizationToken;
    }

    /**
     * Gets the B2Bucket which the upload will take place in.
     *
     * @return An instance of B2Bucket representing the upload destination
     */
    public B2CreateBucketResponse getBucket(){
        return bucket;
    }

    /**
     * Get the uploadURL.
     *
     * @return Gets the URL which should be used for uploading the files
     */
    public String getUploadURL(){
        return uploadURL;
    }

    /**
     * Gets the authorizationToken.
     *
     * @return A token which will be used to authenticate the upload
     */
    public String getUploadAuthorizationToken(){
        return uploadAuthorizationToken;
    }

}
