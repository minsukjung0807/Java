package b2.BackBlaze.api.httpsRequest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;
import org.json.JSONObject;


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
    
    public OnHttpsRequestListener onHttpsRequestListener; 

    /**
     * HTTPS 요청의 리스너 객체를 전달받아 리스너를 등록합니다
     * 
     * @param onHttpsRequestListener HTTPS 요청의 리스너 객체입니다
     */
    public void setOnHttpsRequestListener(OnHttpsRequestListener onHttpsRequestListener){
        this.onHttpsRequestListener = onHttpsRequestListener;
    }

    public interface OnHttpsRequestListener { 
        abstract void onSuccess(JSONObject requestResult);
        abstract void onFailed(JSONObject requestResult);
        abstract void onError(Exception e);
    }

    /**
     * parameter들을 이용하여 HTTP 통신을 요청하고 성공/실패 여부를 호출하며, JSONObject를 반환합니다.
     * 
     * @param URL B2 API와 통신하기 위한 URL 주소입니다
     * @param method B2 API 통신에 사용될 요청 메서드입니다
     * @param authToken b2_authorize_account로 얻은 인증 키입니다
     * @param body HTTP 통신에 같이 첨부할 body 구조입니다
     * @param requestMethod GET, POST, DELETE, PUT 등과 같은 HTTP 요청 메서드입니다
     * 
     * @return B2 통신으로 전달받은 결과를 JSONObject로 반환합니다 
     */
    public JSONObject call(String URL, String method, String authToken, JSONObject body, String requestMethod) {
        
        HttpsURLConnection httpsURLConnection = null;
        DataOutputStream dataOutputStream;

        try {
            httpsURLConnection = buildHttpsURLConnection(URL, method);
            httpsURLConnection.setRequestMethod(requestMethod);
            httpsURLConnection.setRequestProperty("Authorization", authToken);
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
            
        } catch (Exception e) {
            onHttpsRequestListener.onError(e);
            return new JSONObject();
        } finally {
            if(httpsURLConnection != null)
                httpsURLConnection.disconnect();
        }
    }

    /**
     * @param inputStream HTTPS 통신 결과로 가져온 InputStream입니다
     * @return B2 API 통신으로 가져온 결과를 JSONObject로 반환합니다
     * @throws IOException
     */
    private JSONObject inputToJSON(InputStream inputStream) throws IOException {
        StringBuilder JSON = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        while(reader.ready()) JSON.append(reader.readLine());
        return new JSONObject(JSON.toString().trim());
    }

    /**
     * @param httpsURLConnection HTTPS통신을 하기 위한 객체입니다 
     * @return B2 API 호출을 성공하였을때 전달받은 결과를 JSONObject로 반환합니다 
     * @throws IOException
     */
    private JSONObject connectionSuccess(HttpsURLConnection httpsURLConnection) throws IOException {
            JSONObject requestResult = inputToJSON(httpsURLConnection.getInputStream());
            onHttpsRequestListener.onSuccess(requestResult);
            return requestResult;
    }

    /**
     * @param httpsURLConnection HTTPS통신을 하기 위한 객체입니다 
     * @return B2 API 호출을 실패하였을때 전달받은 결과를 JSONObject로 반환합니다 
     * @throws IOException
     */
    private JSONObject connectionFailed(HttpsURLConnection httpsURLConnection) throws IOException {
            JSONObject requestResult = inputToJSON(httpsURLConnection.getErrorStream());
            onHttpsRequestListener.onFailed(requestResult);
            return requestResult;
    }

    /**
     * parameter들을 받아서 HttpsURLConnection을 생성합니다
     * 
     * @param URL B2 API와 통신하기 위한 URL 주소입니다
     * @param method B2 API 통신에 사용될 요청 메서드입니다
     * @return B2 통신으로 전달받은 결과를 JSONObject로 반환합니다 
     * @throws IOException
     */
    private HttpsURLConnection buildHttpsURLConnection(String URL, String method) throws IOException {
        URL url = new URL(URL + method);

        if(url.openConnection() instanceof HttpsURLConnection) 
            return (HttpsURLConnection) url.openConnection();

        return null;
    }
}
