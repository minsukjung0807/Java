package b2.main.BackBlazeB3.Upload;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import java.util.concurrent.*;

import b2.main.BackBlazeB3.Upload.UploadInterface;
import b2.main.BackBlazeB3.Upload.UploadListener;
import b2.main.BackBlazeB3.Upload.UploadProgressRequestBody;
import b2.main.BackBlazeB3.uploadModel.UploadResponse;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URL;
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
    private boolean isAuthed = false;
    private String  apiUrl;
    private String contentType = "";
    Call<UploadResponse> uploadCall;
    private int prev_percentage = 0;

    private String uploadUrl;
    private String uploadAuthorizationToken;

    private boolean isMultiUpload = false;

    public B2SingleUpload(String apiUrl, String uploadUrl, String uploadAuthorizationToken, String bucketId) {
        this.apiUrl = apiUrl;
        this.uploadUrl = uploadUrl;
        this.uploadAuthorizationToken = uploadAuthorizationToken;
        isAuthed = false;
    }

    public void startUploading(File file, String fileName) {
        isMultiUpload = false;

        if(file.exists()) {
            InputStream iStream = null;
            try {

                iStream = FileUtils.openInputStream(file);
                byte[] inputData = getBytes(iStream);

                checkIfAuthed(inputData, fileName);

                if (uploadingListener != null) {
                    uploadingListener.onUploadStarted();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
          } else {
            System.out.println("파일이 없습니다!");
          }
        }

    private void checkIfAuthed(byte[] filebytes, String fileName) {

        Callable<Void> onFinish = () -> {
            System.out.println("파일 업로드가 완료되었습니다.");
            return null;
        };

        uploadFile(filebytes, fileName, contentType, onFinish);  
    }

    private void uploadFile(byte[] fileBytes, String fileName, String contentType, Callable<Void> onFinish) {
        
        URL url = null;
        String path = null;
        
        try {
            url = new URL(uploadUrl);
            path = url.getPath();
            path = path.replaceFirst("/", "");

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        // ConnectionPool connectionPool = new ConnectionPool(1, 1, TimeUnit.MINUTES);

        String baseUrl = url.getProtocol() + "://" + url.getHost();

        OkHttpClient client = 
        new OkHttpClient.Builder().build();
        
        Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

        UploadInterface uploadInterface =  retrofit.create(UploadInterface.class);

        
        
        UploadProgressRequestBody requestBody = new UploadProgressRequestBody(
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

        requestBody.setContentType(contentType);
                   
       uploadCall = uploadInterface.uploadFile(path, requestBody, uploadAuthorizationToken,
                SHAsum(fileBytes), fileName);
                uploadCall.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call1, Response<UploadResponse> response) {

                if (uploadingListener != null) {
                    uploadingListener.onUploadFinished(response.body(), !isMultiUpload);

                    // 네트워크 닫기 (Okio watch dog 닫기)
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

                // if (onFinish != null) {
                //     try {
                //         onFinish.call();
                //     } catch (Exception e) {
                //         e.printStackTrace();
                //     }
                // }
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                if (uploadingListener != null)
                    uploadingListener.onUploadFailed((Exception) t);
                    
            }
        });



    }

    public void setOnUploadingListener(UploadListener uploadingListener) {
        this.uploadingListener = uploadingListener;
    }


    private static String SHAsum(byte[] convertme) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return byteArray2Hex(md.digest(convertme));
    }

    private static String byteArray2Hex(final byte[] hash) {
        Formatter formatter = new Formatter();
        for (byte b : hash) {
            formatter.format("%02x", b);
        }
        return formatter.toString();
    }


    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }


}
