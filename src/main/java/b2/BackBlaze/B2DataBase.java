package b2.BackBlaze;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

import b2.BackBlaze.models.B2Bucket;
import b2.BackBlaze.models.B2Session;
import b2.BackBlaze.models.B2UploadRequest;
import b2.BackBlaze.models.BucketType;

public class B2DataBase {

    private String USER_AGENT = "Mozilla/5.0 (Linux; Android 8.0.0; SAMSUNG-SM-G950N/KSU3CRJ1 Build/R16NW) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/8.2 Chrome/63.0.3239.111 Mobile Safari/537.36";

    public interface OnCreateBucketStateListener { 
        abstract void onSuccess(String message);
        abstract void onFailed(String message);
    }

    public OnCreateBucketStateListener onCreateBucketStateListener;

    /* setOnBucketStateListener 설정 */
    public void setOnBucketStateListener(OnCreateBucketStateListener onCreateBucketStateListener){
        this.onCreateBucketStateListener = onCreateBucketStateListener;
    }

     public B2Bucket createBucket(B2Session session, String bucketName, BucketType bucketType){
        JSONObject parameters = new JSONObject();
        parameters.put("accountId", session.getAccountID());
        parameters.put("bucketName", bucketName);
        parameters.put("bucketType", bucketType.getIdentifier());
        JSONObject requestResult = call(session.getAPIURL(), "b2_create_bucket", session.getAuthToken(), parameters);
        return new B2Bucket(bucketName, requestResult.getString("bucketId"), bucketType);
    }

     public B2UploadRequest getUploadURL(B2Session session, B2Bucket bucket){
        JSONObject parameters = new JSONObject();
        parameters.put("bucketId", bucket.getID());
        JSONObject result = call(session.getAPIURL(), "b2_get_upload_url", session.getAuthToken(), parameters);
        return new B2UploadRequest(bucket, result.getString("uploadUrl"), result.getString("authorizationToken"));
    }

    private JSONObject call(String URL, String method, String authorization, JSONObject body) {
        try {
            URL url = new URL(URL + "/b2api/v3/" + method);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setRequestProperty("Authorization", authorization);
            

            connection.setDoOutput(true);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(body.toString());
            outputStream.flush();
            outputStream.close();

            JSONObject requestResult;

            if(connection.getResponseCode() < 400){
                this.onCreateBucketStateListener.onSuccess("성공!!");
                InputStream inputStream =  connection.getInputStream();
                requestResult = inputToJSON(inputStream);
            }else{
                InputStream errorStream =  connection.getErrorStream();
                requestResult = inputToJSON(errorStream);
                this.onCreateBucketStateListener.onFailed("실패!: " + requestResult.getString("message") +
                requestResult.getInt("status") + requestResult.getString("code"));
                
                // B2APIException exception = new B2APIException(requestResult.getString("message"));
                // exception.setStatusCode(requestResult.getInt("status"));
                // exception.setIdentifier(requestResult.getString("code"));
                // throw exception;
                
            }

            connection.disconnect();
            return requestResult;
        } catch (IOException ex) {
            return new JSONObject();
        }
    }

      private JSONObject inputToJSON(InputStream inputStream) throws IOException {
        StringBuilder JSON = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        while(reader.ready()) JSON.append(reader.readLine());
        return new JSONObject(JSON.toString().trim());
    }
}
