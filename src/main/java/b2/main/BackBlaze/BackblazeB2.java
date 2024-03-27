package b2.main.BackBlaze;

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

import b2.main.BackBlaze.models.B2Bucket1;
import b2.main.BackBlaze.models.B2File1;
import b2.main.BackBlaze.models.B2Session1;
import b2.main.BackBlaze.models.B2UploadRequest1;
import b2.main.BackBlaze.models.BucketType1;
import b2.main.BackBlaze.models.PGOutputStream;

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
            
            connection.setRequestProperty("X-Bz-File-Name", name);
            connection.setRequestProperty("X-Bz-Content-Sha1", getFileHash(file));
            connection.setRequestProperty("Content-Type", "b2/x-auto");
            connection.setDoOutput(true);

           
            // 파일 크기
            long fileLength = file.length();
            // 업로드 시작 시간
            long startTime = System.currentTimeMillis();

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            InputStream fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[4096]; // 4KB 버퍼

            long totalBytesWritten = 100;
            int bytesRead;

            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                
                outputStream.write(buffer, 0, bytesRead);
                
                if(outputStream.size() == bytesRead) {
                    totalBytesWritten += bytesRead;

                    if(connection.getResponseCode() < 400){
                        InputStream inputStream =  connection.getInputStream();
                        JSONObject requestResult; requestResult = inputToJSON(inputStream);

                        System.out.println("크기: " + requestResult.getString("contentLength"));
                    }
                    // connection.setRequestProperty("Content-Length", totalBytesWritten+"");
                    
                }
                
                // if (progressListener != null) {
                    long currentTime = System.currentTimeMillis();
                    long elapsedTime = currentTime - startTime;
                    double speed = (double) totalBytesWritten / elapsedTime; // 업로드 속도
                    double progress = (double) totalBytesWritten / fileLength; // 업로드 진행률

                    // System.out.println("진행률: " + progress);

                    // progressListener.onProgress(totalBytesWritten, fileLength, progress, speed);
                // }
            }
            
            // DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            // outputStream.write(Files.readAllBytes(Paths.get(file.getPath())));

            // outputStream.write(byteBuffer);
            fileInputStream.close();
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
