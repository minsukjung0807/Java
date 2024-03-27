package b2.main.BackBlazeB3.Upload;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

import b2.main.BackBlazeB3.fileUploader.UploadInterface;
import b2.main.BackBlazeB3.fileUploader.UploadListener;
import b2.main.BackBlazeB3.fileUploader.UploadProgressRequestBody;
import b2.main.BackBlazeB3.uploadModel.UploadResponse;
import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class B2UploadAbs {

    private UploadListener uploadingListener;
    private String uploadUrl;
    private String accountAuthorizationToken;
    private String uploadAuthorizationToken;
    private boolean isMultiUpload = false;
    private String apiUrl;
    private String contentType = "";

    public B2UploadAbs(String accountAuthorizationToken, String apiUrl, String uploadUrl, String uploadAuthorizationToken, String bucketId) {
        this.accountAuthorizationToken = accountAuthorizationToken;
        this.apiUrl = apiUrl;
        this.uploadUrl = uploadUrl;
        this.uploadAuthorizationToken = uploadAuthorizationToken;
    }

    public void setOnUploadingListener(UploadListener uploadingListener) {
        this.uploadingListener = uploadingListener;
    }

    public void startUploading(File file, String fileName) {
        
        isMultiUpload = false;

        if(file.exists()) {
            InputStream iStream = null;
            try {
                iStream = FileUtils.openInputStream(file);
                byte[] inputData = B2UploadUtils.getBytes(iStream);

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

                    if (uploadingListener != null) {
                        uploadingListener.onUploadProgress(percentage, progress, total);
                    }
                        
                }
        );

        requestBody.setContentType(contentType);
                   
        Call<UploadResponse> uploadCall = uploadInterface.uploadFile(path, requestBody, uploadAuthorizationToken,
                B2UploadUtils.SHAsum(fileBytes), fileName);
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
                    } 
                    
                    catch (InterruptedException e) {
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

}
