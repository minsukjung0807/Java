package b2.BackBlaze;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.net.URL;
import com.google.gson.JsonObject;
import com.google.gson.Gson;
import javax.net.ssl.HttpsURLConnection;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import okio.*;

import org.json.JSONObject;

import b2.BackBlaze.models.B2UploadRequest1;

// File path = new File("");
      
// System.out.println("####경로: "+path.getAbsolutePath()+"###");

public class FileUploader {
    //  private JSONObject uploadFile(File file, String name, B2UploadRequest upload)  {
        
    //     try {
    //         URL url = new URL(upload.getUploadURL());
    //         HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
    //         connection.setRequestMethod("POST");
    //         connection.setRequestProperty("User-Agent", USER_AGENT);
    //         connection.setRequestProperty("Authorization", upload.getAuthorizationToken());
    //         connection.setRequestProperty("Content-Type", "b2/x-auto");
    //         connection.setRequestProperty("X-Bz-File-Name", name);
    //         connection.setRequestProperty("X-Bz-Content-Sha1", getFileHash(file));

    //         connection.setDoOutput(true);
    //         DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
    //         outputStream.write(Files.readAllBytes(Paths.get(file.getPath())));
    //         outputStream.flush();
    //         outputStream.close();

    //         JSONObject requestResult;

    //         if(connection.getResponseCode() < 400){
    //             InputStream inputStream =  connection.getInputStream();
    //             requestResult = inputToJSON(inputStream);
    //         }else{
    //             // InputStream errorStream =  connection.getErrorStream();
    //             // requestResult = inputToJSON(errorStream);

    //             // B2APIException exception = new B2APIException(requestResult.getString("message"));
    //             // exception.setStatusCode(requestResult.getInt("status"));
    //             // exception.setIdentifier(requestResult.getString("code"));
    //             // throw exception;
    //         }

    //         connection.disconnect();
    //         return requestResult;
    //     } catch (IOException | NoSuchAlgorithmException ex) {
    //         return new JSONObject();
    //     }
    // }

    // private JSONObject inputToJSON(InputStream inputStream) throws IOException {
    //     StringBuilder JSON = new StringBuilder();
    //     BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

    //     while(reader.ready()) JSON.append(reader.readLine());
    //     return new JSONObject(JSON.toString().trim());
    // }

    
    // private String getFileHash(File file) throws NoSuchAlgorithmException, IOException {
    //     MessageDigest md = MessageDigest.getInstance("SHA1");
    //     FileInputStream fis = new FileInputStream(file);
    //     byte[] dataBytes = new byte[1024 * 1024];
    //     // byte[] dataBytes = Files.readAllBytes(file.toPath()); 테스트
    //     int nread = 0;

    //     while ((nread = fis.read(dataBytes)) != -1) md.update(dataBytes, 0, nread);

    //     byte[] mdBytes = md.digest();
    //     StringBuffer sb = new StringBuffer("");
    //     for (int i = 0; i < mdBytes.length; i++) sb.append(Integer.toString((mdBytes[i] & 0xff) + 0x100, 16).substring(1));

    //     return sb.toString();
    // }
}
