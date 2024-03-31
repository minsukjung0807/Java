package b2.BackBlaze.delete_file;

import org.json.JSONObject;

import b2.BackBlaze.api.HttpRequest;
import b2.BackBlaze.authorize_account.response.B2AuthResponse;


public class B2DeleteSingleFile {

 private HttpRequest httpRequest;

    public B2DeleteSingleFile() {
        httpRequest = new HttpRequest();
    }

    public interface OnDeleteFileStateListener { 
        abstract void onSuccess();
        abstract void onFailed(int status, String code, String message);
    }

    public OnDeleteFileStateListener onDeleteFileStateListener;

    public void setOnDeleteFileStateListener(OnDeleteFileStateListener onDeleteFileStateListener){
        this.onDeleteFileStateListener = onDeleteFileStateListener;
    }

    public void startDeletingFile(B2AuthResponse b2AuthResponse, String fileName, String fileId) {
        JSONObject parameters = new JSONObject();

        httpRequest.setOnHttpRequestListener(new HttpRequest.onHttpRequestListener() {
            @Override
            public void onSuccess(JSONObject response) {
                onDeleteFileStateListener.onSuccess();
            }
            @Override
            public void onFailed(JSONObject response) {
                onDeleteFileStateListener.onFailed(response.getInt("status"), response.getString("code"), response.getString("message"));
            }
           });

        parameters.put("fileName", fileName);
        parameters.put("fileId", fileId);

        httpRequest.call(b2AuthResponse.getAPIURL() + "/b2api/v3/", "b2_delete_file_version", b2AuthResponse.getAuthToken(), parameters, "POST");
    }

}
