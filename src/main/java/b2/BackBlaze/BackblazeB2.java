package b2.BackBlaze;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

import b2.BackBlaze.models.B2Bucket1;
import b2.BackBlaze.models.B2File1;
import b2.BackBlaze.models.B2Session1;
import b2.BackBlaze.models.B2UploadRequest1;
import b2.BackBlaze.models.BucketType1;
import b2.BackBlaze.models.PGOutputStream;

public class BackblazeB2 {

    private String USER_AGENT = "Mozilla/5.0 (Linux; Android 8.0.0; SAMSUNG-SM-G950N/KSU3CRJ1 Build/R16NW) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/8.2 Chrome/63.0.3239.111 Mobile Safari/537.36";

    public interface OnCreateBucketStateListener { 
        abstract void onSuccess(String message);
        abstract void onFailed(String message);
    }

    public interface OnUploadFileStateListener { 
        abstract void onSuccess(String message);
        abstract void onFailed(String message);
    }

    public OnCreateBucketStateListener onCreateBucketStateListener;

    /* setOnBucketStateListener 설정 */
    public void setOnBucketStateListener(OnCreateBucketStateListener onCreateBucketStateListener){
        this.onCreateBucketStateListener = onCreateBucketStateListener;
    }

    public OnUploadFileStateListener onUploadFileStateListener;

    /* setOnBucketStateListener 설정 */
    public void setOnUploadFileStateListener(OnUploadFileStateListener onUploadFileStateListener){
        this.onUploadFileStateListener = onUploadFileStateListener;
    }

     public B2Bucket1 createBucket(B2Session1 session, String bucketName, BucketType1 bucketType){
        JSONObject parameters = new JSONObject();
        parameters.put("accountId", session.getAccountID());
        parameters.put("bucketName", bucketName);
        parameters.put("bucketType", bucketType.getIdentifier());
        JSONObject requestResult = call(session.getAPIURL(), "b2_create_bucket", session.getAuthToken(), parameters);
        return new B2Bucket1(bucketName, requestResult.getString("bucketId"), bucketType);
    }

     public B2UploadRequest1 getUploadURL(B2Session1 session, B2Bucket1 bucket){
        JSONObject parameters = new JSONObject();
        parameters.put("bucketId", bucket.getID());
        JSONObject result = call(session.getAPIURL(), "b2_get_upload_url", session.getAuthToken(), parameters);
        return new B2UploadRequest1(bucket, result.getString("uploadUrl"), result.getString("authorizationToken"));
    }

    public B2File1 uploadFile(B2UploadRequest1 upload, File file, String name){
        JSONObject result = uploadFile(file, name, upload);
        return new B2File1(name, result.getString("contentType"), result.getString("fileId"), file.length(), System.currentTimeMillis());
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

    private JSONObject uploadFile(File file, String name, B2UploadRequest1 upload) {
        try {
            URL url = new URL(upload.getUploadURL());
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setRequestProperty("Authorization", upload.getAuthorizationToken());
            connection.setRequestProperty("Content-Type", "b2/x-auto");
            connection.setRequestProperty("X-Bz-File-Name", name);
            connection.setRequestProperty("X-Bz-Content-Sha1", getFileHash(file));

            connection.setDoOutput(true);
        
            
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.write(Files.readAllBytes(Paths.get(file.getPath())));

         // Get server response
         BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
         String line = "";
         StringBuilder builder = new StringBuilder();
         while ((line = reader.readLine()) != null) {
             builder.append(line);
             System.out.println("연결결과: " + line);
         }

            // outputStream.write(byteBuffer);
            outputStream.flush();
            outputStream.close();

            JSONObject requestResult;

            if(connection.getResponseCode() < 400){
                InputStream inputStream =  connection.getInputStream();
                requestResult = inputToJSON(inputStream);
                this.onCreateBucketStateListener.onSuccess("성공!!");
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
        } catch (IOException | NoSuchAlgorithmException ex) {
            return new JSONObject();
        }
    }

    private String getFileHash(File file) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        FileInputStream fis = new FileInputStream(file);
        byte[] dataBytes = new byte[1024];
        int nread = 0;

        while ((nread = fis.read(dataBytes)) != -1) md.update(dataBytes, 0, nread);

        byte[] mdBytes = md.digest();
        StringBuffer sb = new StringBuffer("");
        for (int i = 0; i < mdBytes.length; i++) sb.append(Integer.toString((mdBytes[i] & 0xff) + 0x100, 16).substring(1));

        return sb.toString();
    }

}
