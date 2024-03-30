package b2.BackBlaze.upload_file;

import java.util.concurrent.*;

import org.json.JSONException;
import org.json.JSONObject;

import b2.BackBlaze.get_upload_url.response.B2GetUploadUrlResponse;
import b2.BackBlaze.upload_file.model.MultiFile;
import b2.BackBlaze.upload_file.model.UploadInterface;
import b2.BackBlaze.upload_file.model.UploadListener;
import b2.BackBlaze.upload_file.model.UploadProgressRequestBody;
import b2.BackBlaze.upload_file.response.B2UploadFileResponse;
import okhttp3.OkHttpClient;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.HttpException;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class B2MultiUpload {
    
    private boolean isMultiUpload = false;
    private ArrayList<MultiFile> files;
    private UploadListener uploadingListener;
    private int prev_percentage = 0;
    private String uploadUrl;
    private Call<B2UploadFileResponse> uploadCall;
    private OkHttpClient client; 
    private String uploadAuthorizationToken;

    public B2MultiUpload (B2GetUploadUrlResponse b2UploadRequest) {
        this. uploadUrl = b2UploadRequest.getUploadURL();
        this.uploadAuthorizationToken = b2UploadRequest.getUploadAuthorizationToken();
    }

    public void startUploadingMultipleFiles(ArrayList<MultiFile> files) {
        this.files = files;
        isMultiUpload = true;
        try {
            uploadMultiImages(new ArrayList<>());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void uploadMultiImages(ArrayList<MultiFile> file) throws IOException {
            
        MultiFile fileModel = files.get(file.size());
            
        uploadFile(fileModel.getFileBytes(), fileModel.getFileName(), fileModel.getContentType(), () -> {
                        file.add(fileModel);

                            if (file.size() == files.size()) {
    
                                B2UploadFileResponse uploadResponse = new B2UploadFileResponse();
    
                                if (uploadingListener != null) {
                                    System.out.println("업로드 중 ㅎㅎㅎ");
                                    uploadingListener.onUploadFinished(uploadResponse, true);
                                }
                            } 
                                
                                else {
                                    uploadMultiImages(file);
                                }
    
                                return null;
            });
                
        }

        private void uploadFile(byte[] fileBytes, String fileName, String contentType, Callable<Void> onFinish) {
            
            URL url = getUploadUrl(uploadUrl);

            if(url != null) {
                String path = getPath(url);
                String baseUrl = getBaseUrl(url);
        
                client = new OkHttpClient.Builder().build();
                
                Retrofit retrofit = buildRetrofit(baseUrl);
        
                UploadInterface uploadInterface =  retrofit.create(UploadInterface.class);
        
                UploadProgressRequestBody requestBody = getUploadProgressRequestBody(fileBytes);
        
                requestBody.setContentType(contentType);


                uploadCall = uploadInterface.uploadFile(path, requestBody, uploadAuthorizationToken,
                SHAsum(fileBytes), fileName);

                uploadCall.enqueue(new Callback<B2UploadFileResponse>() {
            @Override
            public void onResponse(Call<B2UploadFileResponse> call, Response<B2UploadFileResponse> response) {
                
                if (uploadingListener != null) {

                    if(response.code() < 400){
                        uploadingListener.onUploadFinished(response.body(), !isMultiUpload);
                    } else {
                        uploadingListener.onUploadFailed(response.body().getStatus(), response.body().getCode(), response.body().getMessage());  
                    }
                
                    closeHttpClient();
                }

                if (onFinish != null) {
                    try {
                        onFinish.call();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<B2UploadFileResponse> call, Throwable throwable) {
                if(uploadingListener!=null) {
                    uploadingListener.onUploadFailed(0, "", "");  
                }
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
