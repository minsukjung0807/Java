package b2.BackBlazeHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.json.Json;
import javax.json.JsonObject;

import b2.BackBlazeHelper.models.B2Session;



public class Authentication {

    private static String appKeyId = "005e6f0ff38588b000000000a";
    private static String appKey = "K005k2tpcpfoqMY525/C9Pj5kHbDWXY";

    private B2Session b2Session;

    public interface OnStateListener { 
        abstract void onSuccess(B2Session b2Session);
        abstract void onFailed(String message);
    }

    public OnStateListener onStateListener;

    public void authenticate() {

        try {
                URL url = new URL("https://api.backblazeb2.com" + "/b2api/v2/" + "b2_authorize_account");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                
                String encodedAuth = encodeAuthorization(appKeyId + ":" + appKey);
                connection.setRequestProperty("Authorization", encodedAuth);
                connection.setConnectTimeout(5000);   
                connection.setReadTimeout(1000);
          try {
                try (InputStream inputStream = connection.getInputStream()) {
                        String respStr = readInputStream(inputStream);
                        JsonObject response = Json.createReader(new StringReader(respStr)).readObject();
            
                        String authToken = response.getString("authorizationToken");
                        String apiUrl = response.getString("apiUrl");
                        String accountId = response.getString("accountId");
                        String downloadUrl = response.getString("downloadUrl");

                        b2Session = new B2Session(authToken, accountId, apiUrl, downloadUrl);

                        this.onStateListener.onSuccess(b2Session);
                    }
            } 

            catch (Exception e) {
                this.onStateListener.onFailed(e.getMessage());
                // System.out.println("에러: " + e.getMessage());
            }
    
            finally {
                connection.disconnect();
            }
          
    
        } 
          
        catch (Exception e) {
            this.onStateListener.onFailed(e.getMessage());
            // System.out.println("에러: " + e.getMessage());
        }

        
    }

    /* OnStateListener 설정 */
    public void setOnStateListener(OnStateListener onStateListener){
        this.onStateListener = onStateListener;
    }
    
    private String encodeAuthorization(String input){
        byte[] authorizationBytes = input.getBytes(StandardCharsets.UTF_8);
        String encodedAuthorization = Base64.getEncoder().encodeToString(authorizationBytes);
        return "Basic " + encodedAuthorization;
    }
    
      public String readInputStream(InputStream inputStream) {
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("utf-8")))) {
            while (true) {
                line = reader.readLine();
                if (line == null) break;
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            // Handle IOException as needed
            System.out.println("에러: " + e.getMessage());
        }

        return stringBuilder.toString();
    }
      
}
