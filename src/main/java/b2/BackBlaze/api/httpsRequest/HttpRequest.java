package b2.BackBlaze.api.httpsRequest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;

import b2.BackBlaze.api.httpsRequest.listener.OnHttpsRequestListener;

    /**
     * 이 클래스는 B2 API 사용때 공통으로 이용할 HttpsURL 통신 함수를 제공하고 있습니다. 
     * 
     * TO-DO: 이 클래스는 USER_AGENT 값을 디바이스에서 가져오는 함수를 추가적으로 제공해야 합니다.
     */

public class HttpRequest {

    /**
     * private String USER_AGENT = "Mozilla/5.0 (Linux; Android 8.0.0; SAMSUNG-SM-G950N/KSU3CRJ1 Build/R16NW) AppleWebKit/537.36 (KHTML, like Gecko) SamsungBrowser/8.2 Chrome/63.0.3239.111 Mobile Safari/537.36";
     * 
     * httpsURLConnection.setRequestProperty("User-Agent", USER_AGENT);
     */
    
    public OnHttpsRequestListener onHttpRequestListener; 

    public void setOnHttpsRequestListener(OnHttpsRequestListener onHttpRequestListener){
        this.onHttpRequestListener = onHttpRequestListener;
    }

    public JSONObject call(String URL, String method, String authorization, JSONObject body, String requestMethod) {
        
        HttpsURLConnection httpsURLConnection = null;
        DataOutputStream dataOutputStream;

        try {

            httpsURLConnection = getHttpsURLConnection(URL, method);
            httpsURLConnection.setRequestMethod(requestMethod);
            httpsURLConnection.setRequestProperty("Authorization", authorization);
            httpsURLConnection.setDoOutput(true);

            dataOutputStream = new DataOutputStream(httpsURLConnection.getOutputStream());
            dataOutputStream.writeBytes(body.toString());
            dataOutputStream.flush();
            dataOutputStream.close();

            if(httpsURLConnection.getResponseCode() < 400){
                return connectionSuccess(httpsURLConnection);
            } else {
                return connectionFailed(httpsURLConnection);
            }
            
        } catch (IOException ioException) {
            return new JSONObject();
        } finally {
            if(httpsURLConnection != null)
                httpsURLConnection.disconnect();
        }
    }

    private JSONObject inputToJSON(InputStream inputStream) throws IOException {
        StringBuilder JSON = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while(reader.ready()) JSON.append(reader.readLine());
        return new JSONObject(JSON.toString().trim());
    }

    private JSONObject connectionSuccess(HttpsURLConnection httpsURLConnection) throws IOException {
            JSONObject requestResult = inputToJSON(httpsURLConnection.getInputStream());
            onHttpRequestListener.onSuccess(requestResult);
            return requestResult;
    }

    private JSONObject connectionFailed(HttpsURLConnection httpsURLConnection) throws IOException {
            JSONObject requestResult = inputToJSON(httpsURLConnection.getErrorStream());
            onHttpRequestListener.onFailed(requestResult);
            return requestResult;
    }

    private HttpsURLConnection getHttpsURLConnection(String URL, String method) throws IOException {
        URL url = new URL(URL + method);

        if(url.openConnection() instanceof HttpsURLConnection) 
            return (HttpsURLConnection) url.openConnection();
            
        return null;
    }
}
