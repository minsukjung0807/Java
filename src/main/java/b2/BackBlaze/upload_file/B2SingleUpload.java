package b2.BackBlaze.upload_file;

import b2.BackBlaze.get_upload_url.response.B2GetUploadUrlResponse;
import b2.BackBlaze.upload_file.model.UploadInterface;
import b2.BackBlaze.upload_file.model.UploadListener;
import b2.BackBlaze.upload_file.model.UploadProgressRequestBody;
import b2.BackBlaze.upload_file.response.B2UploadFileResponse;

import java.util.concurrent.*;

import okhttp3.OkHttpClient;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class B2SingleUpload {

    private UploadListener uploadingListener;

    private String contentType = "";
    private Call<B2UploadFileResponse> uploadCall;
    private OkHttpClient client; 
    private String uploadUrl;
    private String uploadAuthorizationToken;
    private int prev_percentage = 0;


    public B2SingleUpload(B2GetUploadUrlResponse b2GetUploadUrlResponse) {
        this.uploadUrl = b2GetUploadUrlResponse.getUploadURL();
        this.uploadAuthorizationToken = b2GetUploadUrlResponse.getUploadAuthorizationToken();
    }

    public void startUploading(File file, String fileName) {

        if(file.exists()) {
            try {
                byte[] inputData = readFileToBytesWithProgress(file.getPath());

                checkIfAuthed(inputData, fileName);

                if (uploadingListener != null) {
                    uploadingListener.onUploadStarted();
                }

            } catch (Exception e) {
                uploadingListener.onUploadFailed(0, "ERROR", e.getMessage());
            }
          } else {
            uploadingListener.onUploadFailed(0, "ERROR", "File Not Found");
          }
        }
        
    private void checkIfAuthed(byte[] filebytes, String fileName) {

        uploadFile(filebytes, fileName, contentType);  
    }

    private void uploadFile(byte[] fileBytes, String fileName, String contentType) {
        
        URL url = getUploadUrl(uploadUrl);

        if(url != null) {
            String path = getPath(url);
            String baseUrl = getBaseUrl(url);
    
            client = new OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.MINUTES)
            .readTimeout(2, TimeUnit.MINUTES)
            .writeTimeout(2, TimeUnit.MINUTES).build();
            
            Retrofit retrofit = buildRetrofit(baseUrl);
    
            UploadInterface uploadInterface =  retrofit.create(UploadInterface.class);
    
            UploadProgressRequestBody requestBody = getUploadProgressRequestBody(fileBytes);
    
            requestBody.setContentType(contentType);
                       
            uploadCall = uploadInterface.uploadFile(path, requestBody, uploadAuthorizationToken,
                    SHAsum(fileBytes), fileName);
                    
            uploadCall.enqueue(new Callback<B2UploadFileResponse>() {
                
                @Override
                public void onResponse(Call<B2UploadFileResponse> call1, Response<B2UploadFileResponse> response) {
    
                    
                    if (uploadingListener != null) {

                        if(response.code() < 400){
                            uploadingListener.onUploadFinished(response.body(), false);
                        } else {
                            uploadingListener.onUploadFailed(response.body().getStatus(), response.body().getCode(), response.body().getMessage());  
                        }
                    
                        closeHttpClient();
                    }
    
                }
    
                @Override
                public void onFailure(Call<B2UploadFileResponse> call, Throwable throwable) {

                    if(uploadingListener!=null) {
                        uploadingListener.onUploadFailed(0, "ERROR", throwable.getMessage());  
                    }

                    closeHttpClient();
            }
        });
                        
            
        }
    }


    public void setOnUploadingListener(UploadListener uploadingListener) {
        this.uploadingListener = uploadingListener;
    }

    private UploadProgressRequestBody getUploadProgressRequestBody(byte[] fileBytes) {
        return new UploadProgressRequestBody(
            new UploadProgressRequestBody.UploadInfo(fileBytes, fileBytes.length),
            (progress, total) -> {

                int percentage = (int) ((progress * 100.0f) / total);
                
                if(percentage != prev_percentage) {
                        if (uploadingListener != null) {
                            uploadingListener.onUploadProgress(percentage, progress, total);
                        }
                        prev_percentage = percentage;
                    } 
                    
            }
        );
    }


    private static String SHAsum(byte[] convertme) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return byteArrayToHex(md.digest(convertme));
    }

    private static String byteArrayToHex(final byte[] hash) {
        Formatter formatter = new Formatter();
        
        for (byte b : hash) {
            formatter.format("%02x", b);
        }

        String Hex = formatter.toString();
        formatter.close();
        return Hex;
    }

    public static byte[] readFileToBytesWithProgress(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        long fileSize = Files.size(path);
        
        try (FileInputStream fis = new FileInputStream(filePath)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            long totalBytesRead = 0;
            byte[] result = new byte[(int) fileSize];
            int offset = 0;
            
            int y = 0;

            while ((bytesRead = fis.read(buffer)) != -1) {
                totalBytesRead += bytesRead;

                double progress = (double) totalBytesRead / fileSize * 100;
                
                int x = (int)(progress);

                if(y != x) {
                    System.out.println("파일 크기 읽는 중: " +  x + "%");
                    y = x;
                }
                
                System.arraycopy(buffer, 0, result, offset, bytesRead);
                offset += bytesRead;
            }
            
            return result;
        }
    }

    // HttpClient 네트워크 닫기 (Okio watch dog 닫기)
    private void closeHttpClient() {
        client.connectionPool().evictAll();
        ExecutorService executorService = client.dispatcher().executorService();
        executorService.shutdown();
        try {
            executorService.awaitTermination(0, TimeUnit.SECONDS);
            System.out.println("시스템 종료 완료!");
        } catch (InterruptedException e) {
            System.out.println("시스템 종료 실패!"+ e);
        }
    }

    private Retrofit buildRetrofit(String baseUrl) {
        return new Retrofit.Builder().baseUrl(baseUrl).client(client)
        .addConverterFactory(GsonConverterFactory.create()).build();
    }

    private String getBaseUrl(URL url) {
        return url.getProtocol() + "://" + url.getHost();
    }

    private String getPath(URL url) {
        return url.getPath().replaceFirst("/", "");
    }

    private URL getUploadUrl(String uploadUrl) {
        try {
            return new URL(uploadUrl);
        } catch (MalformedURLException e) {
            System.out.println("잘못된 URL: " + e.getMessage());
            return null;
        }
    }

}
