package b2.BackBlaze.authorize_account;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


import org.json.JSONObject;

import b2.BackBlaze.api.values.B2Keys;
import b2.BackBlaze.authorize_account.response.B2AuthResponse;

public class B2Auth {

    private String encodedAuth;
    private JSONObject requestResult;

     public interface OnAuthStateListener { 
        abstract void onSuccess(B2AuthResponse b2AuthResponse);
        abstract void onFailed(int status, String code, String message);
    }

    public OnAuthStateListener onAuthStateListener;

    public void setOnAuthStateListener(OnAuthStateListener onAuthStateListener){
        this.onAuthStateListener = onAuthStateListener;
    }


    public void startAuthenticating() {

        HttpURLConnection connection = null;
        try {
                URL url = new URL("https://api.backblazeb2.com/b2api/v2/b2_authorize_account");
                connection = (HttpURLConnection) url.openConnection();
                encodedAuth = encodeAuthorization(B2Keys.APP_KEY_ID + ":" + B2Keys.APP_KEY);
                connection.setRequestProperty("Authorization", encodedAuth);
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);   
                connection.setReadTimeout(1000);

                

            if(connection.getResponseCode() < 400){
                InputStream inputStream =  connection.getInputStream();
                requestResult = inputToJSON(inputStream);
                onAuthStateListener.onSuccess(new B2AuthResponse(requestResult.getString("authorizationToken"), requestResult.getString("accountId"), requestResult.getString("apiUrl"), requestResult.getString("downloadUrl")));
            } else {
                InputStream errorStream =  connection.getErrorStream();
                requestResult = inputToJSON(errorStream);
                onAuthStateListener.onFailed(requestResult.getInt("status"), requestResult.getString("code"), requestResult.getString("message"));
            } 
        }
        
        catch (Exception e) {
                onAuthStateListener.onFailed(0, "", "");
        }  finally {
            if(connection != null) {
                connection.disconnect();
            }
        }   
    }


    private String encodeAuthorization(String input){
        byte[] authorizationBytes = input.getBytes(StandardCharsets.UTF_8);
        String encodedAuthorization = Base64.getEncoder().encodeToString(authorizationBytes);
        return "Basic " + encodedAuthorization;
    }

    private JSONObject inputToJSON(InputStream inputStream) throws IOException {
        StringBuilder JSON = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        while(reader.ready()) JSON.append(reader.readLine());
        return new JSONObject(JSON.toString().trim());
    }
}
