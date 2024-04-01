package b2.BackBlaze.get_upload_url;

import org.json.JSONObject;

import b2.BackBlaze.api.httpsRequest.HttpRequest;
import b2.BackBlaze.api.httpsRequest.listener.OnHttpsRequestListener;
import b2.BackBlaze.authorize_account.response.B2AuthResponse;
import b2.BackBlaze.create_bucket.response.B2CreateBucketResponse;
import b2.BackBlaze.get_upload_url.response.B2GetUploadUrlResponse;

public class B2GetUploadUrl {

    private HttpRequest httpRequest;

    public interface OnGetUploadUrlStateListener { 
        abstract void onSuccess(B2GetUploadUrlResponse b2GetUploadUrlResponse);
        abstract void onFailed(int status, String code, String message);
    }

    public OnGetUploadUrlStateListener onGetUploadUrlStateListener;

    public void setOnGetUploadUrlStateListener(OnGetUploadUrlStateListener onGetUploadUrlStateListener){
        this.onGetUploadUrlStateListener = onGetUploadUrlStateListener;
    }

    public B2GetUploadUrl() {
       httpRequest = new HttpRequest();
    }

    public void startGettingUploadUrl(B2AuthResponse b2AuthResponse, B2CreateBucketResponse b2CreateBucketResponse) {
       
        JSONObject parameters = new JSONObject();

        httpRequest.setOnHttpsRequestListener(new OnHttpsRequestListener() {

            @Override
            public void onSuccess(JSONObject response) {
                onGetUploadUrlStateListener.onSuccess(new B2GetUploadUrlResponse(b2CreateBucketResponse, response.getString("uploadUrl"), response.getString("authorizationToken")));
            }
            @Override
            public void onFailed(JSONObject response) {
                onGetUploadUrlStateListener.onFailed(response.getInt("status"), response.getString("code"), response.getString("message"));
            }
            @Override
            public void onError(Exception e) {
                onGetUploadUrlStateListener.onFailed(0, "ERROR", e.getMessage());
            }
            
           });

        parameters.put("bucketId", b2CreateBucketResponse.getID());
        httpRequest.call(b2AuthResponse.getAPIURL()+ "/b2api/v3/", "b2_get_upload_url", b2AuthResponse.getAuthToken(), parameters, "GET");
    }
}
