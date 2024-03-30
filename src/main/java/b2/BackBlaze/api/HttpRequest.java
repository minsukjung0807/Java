package b2.BackBlaze.api;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONObject;

public class HttpRequest {

    // private String USER_AGENT = "Mozilla/5.0 (Linux; Android 8.0.0; SAMSUNG-SM-G950N/KSU3CRJ1 Build/R16NW) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/8.2 Chrome/63.0.3239.111 Mobile Safari/537.36";

    public interface onHttpRequestListener { 
        abstract void onSuccess(JSONObject requestResult);
        abstract void onFailed(JSONObject requestResult);
    }

    public onHttpRequestListener onHttpRequestListener;

    public void setOnHttpRequestListener(onHttpRequestListener onHttpRequestListener){
        this.onHttpRequestListener = onHttpRequestListener;
    }

    public JSONObject call(String URL, String method, String authorization, JSONObject body, String requestMethod) {

        HttpsURLConnection connection = null; 

        try {

            URL url = new URL(URL + method);
            connection = (HttpsURLConnection) url.openConnection();
            connection.setRequestMethod(requestMethod);
            // connection.setRequestProperty("User-Agent", USER_AGENT);
            connection.setRequestProperty("Authorization", authorization);
            
            connection.setDoOutput(true);
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(body.toString());
            outputStream.flush();
            outputStream.close();

            JSONObject requestResult;

            if(connection.getResponseCode() < 400){
                InputStream inputStream =  connection.getInputStream();
                requestResult = inputToJSON(inputStream);
                onHttpRequestListener.onSuccess(requestResult);
            }else{
                InputStream errorStream =  connection.getErrorStream();
                requestResult = inputToJSON(errorStream);
                onHttpRequestListener.onFailed(requestResult);
            }
            return requestResult;
        } 
        
        catch (IOException ex) {
            return new JSONObject();
        } finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
    }

    private JSONObject inputToJSON(InputStream inputStream) throws IOException {
        StringBuilder JSON = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        while(reader.ready()) JSON.append(reader.readLine());
        return new JSONObject(JSON.toString().trim());
    }
}
