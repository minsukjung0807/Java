package b2.main.BackBlaze.models;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import javax.net.ssl.HttpsURLConnection;

public class ProgressHttpsRequestBody {

    private final HttpsURLConnection connection;
    private final ProgressListener progressListener;
    private final byte[] postData;

    public ProgressHttpsRequestBody(HttpsURLConnection connection, byte[] postData, ProgressListener progressListener) {
        this.connection = connection;
        this.postData = postData;
        this.progressListener = progressListener;
    }

    public void writeRequestBody() throws IOException {
        // Set necessary headers
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("Content-Length", String.valueOf(postData.length));
        connection.setDoOutput(true); // This indicates that this request will send a body

        // Write the request body to the output stream
        try (OutputStream outputStream = connection.getOutputStream()) {
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
            bufferedOutputStream.write(postData);
            bufferedOutputStream.flush();
        }

        // Get the response
        int responseCode = connection.getResponseCode();
        if (responseCode == HttpsURLConnection.HTTP_OK) {
            // Read the response
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                // Process the response
                System.out.println("Response: " + response.toString());
            }
        } else {
            // Handle error responses
            try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()))) {
                String line;
                StringBuilder errorResponse = new StringBuilder();
                while ((line = errorReader.readLine()) != null) {
                    errorResponse.append(line);
                }
                // Process the error response
                System.out.println("Error Response: " + errorResponse.toString());
            }
        }
        // Disconnect the connection
        connection.disconnect();
    }
}

