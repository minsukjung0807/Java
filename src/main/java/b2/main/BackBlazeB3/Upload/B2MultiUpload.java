package b2.main.BackBlazeB3.Upload;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import org.json.JSONObject;

import b2.main.BackBlazeB3.fileUploader.ApiClient;
import b2.main.BackBlazeB3.fileUploader.MultiFile;
import b2.main.BackBlazeB3.uploadModel.UploadResponse;
import retrofit2.Call;
import retrofit2.Response;

public class B2MultiUpload {

    private boolean isMultiUpload = false;
    private ArrayList<MultiFile> files;
    private UploadListener uploadingListener;

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
            if (!isAuthed) {
    
            } 
            else{
                    uploadFile(fileModel.getFileBytes(), fileModel.getFileName(), fileModel.getContentType(), () -> {
    
                                file.add(fileModel);
                                if (file.size() == files.size()) {
    
                                    UploadResponse uploadResponse = new UploadResponse();
    
                                    if (uploadingListener != null)
                                        uploadingListener.onUploadFinished(uploadResponse, true);
                                } else {
                                    uploadMultiImages(file);
                                }
    
                                return null;
                            });
                }
    
    
            }
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


        UploadInterface uploadInterface = ApiClient.getClient(baseUrl).create(UploadInterface.class);

        UploadProgressRequestBody requestBody = new UploadProgressRequestBody(
                new UploadProgressRequestBody.UploadInfo(fileBytes, fileBytes.length),
                (progress, total) -> {


                    int percentage = (int) ((progress * 100.0f) / total);

                    if (uploadingListener != null)
                        uploadingListener.onUploadProgress(percentage, progress, total);

                }
        );
        requestBody.setContentType(contentType);


// Upload
        Call<UploadResponse> call = uploadInterface.uploadFile(path, requestBody, uploadAuthorizationToken,
                SHAsum(fileBytes), fileName);
        call.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {
                



                if (uploadingListener != null) {
                    uploadingListener.onUploadFinished(response.body(), !isMultiUpload);


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
            public void onFailure(Call<UploadResponse> call, Throwable t) {
                if (uploadingListener != null)
                    uploadingListener.onUploadFailed((Exception) t);

            }
        });

    }


}
