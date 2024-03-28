package b2.main.BackBlazeB3.Upload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

import b2.main.BackBlazeB3.fileUploader.UploadInterface;
import b2.main.BackBlazeB3.fileUploader.UploadListener;
import b2.main.BackBlazeB3.fileUploader.UploadProgressRequestBody;
import b2.main.BackBlazeB3.uploadModel.UploadResponse;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class B2SingleUpload {

    private UploadListener uploadingListener;
    private String uploadUrl;
    private String accountAuthorizationToken;
    private String uploadAuthorizationToken;
    private boolean isMultiUpload = false;
    private String apiUrl;
    private String contentType = "";
    private OkHttpClient okHttpClient;

    public B2SingleUpload(String accountAuthorizationToken, String apiUrl, String uploadUrl, String uploadAuthorizationToken, String bucketId) {
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
        uploadFile(filebytes, fileName, contentType, null);  
    }


    private void uploadFile(byte[] fileBytes, String fileName, String contentType, Callable<Void> onFinish) {
        
        okHttpClient = buildHttpClient();

        URL uploadURL = getURL(uploadUrl);

        String baseUrl =  getBaseUrl(uploadURL);

        Retrofit retrofit = buildRetrofit(baseUrl, okHttpClient);

        UploadInterface uploadInterface =  retrofit.create(UploadInterface.class);

        UploadProgressRequestBody requestBody = setUploadProgressRequestBody(fileBytes);

        requestBody.setContentType(contentType);
                   
        Call<UploadResponse> uploadCall = getUploadCall(uploadInterface, uploadURL, requestBody, uploadAuthorizationToken, fileBytes, fileName);
                
        uploadCall.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call1, Response<UploadResponse> response) {

                if (uploadingListener != null) {
                    uploadingListener.onUploadFinished(response.body(), !isMultiUpload);
                    closeHttpClient();
                }
            }

            @Override
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                if (uploadingListener != null)
                    uploadingListener.onUploadFailed((Exception) t);
                    
            }
        });
        
    }

    private Retrofit buildRetrofit(String baseUrl, OkHttpClient oHttpClient) {
        return new Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(oHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build();
    }

    private OkHttpClient buildHttpClient() {
        return new OkHttpClient.Builder().build();
    }

    // OkHttpClient 닫기 (Okio WATCH DOG 닫기)
    private void closeHttpClient() {
        ExecutorService executorService;
        okHttpClient.connectionPool().evictAll();
        executorService = okHttpClient.dispatcher().executorService();
        executorService.shutdown();
        
        try { executorService.awaitTermination(0, TimeUnit.SECONDS); } 
        catch (InterruptedException e) { System.out.println("시스템 종료 실패!"+ e); }
    }

    private URL getURL(String uploadUrl) {
        try {
            return new URL(uploadUrl);
        } catch (MalformedURLException e) {
            System.out.println("잘못된 URL: " + e.getMessage());
            return null;
        }
    }

    private String getPath(URL url) {
        return url.getPath().replaceFirst("/", "");
    }

    private String getBaseUrl(URL url) {
        return url.getProtocol() + "://" + url.getHost();
    }

    private UploadProgressRequestBody setUploadProgressRequestBody(byte[] fileBytes) {
        return new UploadProgressRequestBody(
            new UploadProgressRequestBody.UploadInfo(fileBytes, fileBytes.length),
            (progress, total) -> {
    
                int percentage = (int) ((progress * 100.0f) / total);
    
                if (uploadingListener != null) {
                    uploadingListener.onUploadProgress(percentage, progress, total);
                }
                    
            });
        }

    private Call<UploadResponse> getUploadCall(UploadInterface uploadInterface, URL uploadURL, UploadProgressRequestBody requestBody, String uploadAuthorizationToken, byte[] fileBytes, String fileName) {
        return uploadInterface.uploadFile(getPath(uploadURL), requestBody, uploadAuthorizationToken,
                B2UploadUtils.SHAsum(fileBytes), fileName);
    }
    
    

}
