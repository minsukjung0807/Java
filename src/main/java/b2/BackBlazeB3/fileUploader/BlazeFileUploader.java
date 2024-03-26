package b2.BackBlazeB3.fileUploader;

import b2.BackBlazeB3.uploadModel.UploadResponse;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.concurrent.Callable;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BlazeFileUploader {

    private String bucketId;
    private UploadListener uploadingListener;
    private boolean isAuthed = false;
    private String accountAuthorizationToken, apiUrl;
    private String contentType = "";

    // private ArrayList<MultiFile> files;

    //Upload Auth
    private String uploadUrl;
    private String uploadAuthorizationToken;

    private boolean isMultiUpload = false;

    public BlazeFileUploader(String accountAuthorizationToken, String apiUrl, String uploadUrl, String uploadAuthorizationToken, String bucketId) {
        this.accountAuthorizationToken = accountAuthorizationToken;
        this.apiUrl = apiUrl;
        this.uploadUrl = uploadUrl;
        this.uploadAuthorizationToken = uploadAuthorizationToken;
        this.bucketId = bucketId;
        isAuthed = false;

    }


    // public void startUploadingMultipleFiles(ArrayList<MultiFile> files) {

    //     this.files = files;
    //     isMultiUpload = true;


    //     try {
    //         uploadMultiImages(new ArrayList<>());
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //     }

    // }

    // private void uploadMultiImages(ArrayList<MultiFile> file) throws IOException {
    //     MultiFile fileModel = files.get(file.size());
    //     if (!isAuthed) {


    //         if (fileModel.getFile() != null)
    //             getClientData(fileModel.getFile(), fileModel.getFileName(), fileModel.getContentType());
    //         else
    //             getClientData(fileModel.getFileBytes(), fileModel.getFileName(), fileModel.getContentType());

    //     } else {
    //         byte[] inputData =null;
    //         if (fileModel.getFileUri() != null){

    //             InputStream iStream = context.getContentResolver().openInputStream(fileModel.getFileUri());
    //       inputData = getBytes(iStream);
    //             uploadFile(fileModel.getFileUri(), fileModel.getFileName(),
    //                     inputData, fileModel.getContentType(), () -> {

    //                         file.add(fileModel);
    //                         if (file.size() == files.size()) {

    //                             UploadResponse uploadResponse = new UploadResponse();

    //                             if (uploadingListener != null)
    //                                 uploadingListener.onUploadFinished(uploadResponse, true);
    //                         } else {
    //                             uploadMultiImages(file);
    //                         }

    //                         return null;
    //                     });
    //         }else{
    //             uploadFile(fileModel.getFileBytes(), fileModel.getFileName(), fileModel.getContentType(), () -> {

    //                         file.add(fileModel);
    //                         if (file.size() == files.size()) {

    //                             UploadResponse uploadResponse = new UploadResponse();

    //                             if (uploadingListener != null)
    //                                 uploadingListener.onUploadFinished(uploadResponse, true);
    //                         } else {
    //                             uploadMultiImages(file);
    //                         }

    //                         return null;
    //                     });
    //         }


    //     }
    // }


    public void startUploading(File file, String fileName, String contentType) {
        this.contentType = contentType;
        isMultiUpload = false;
        try {

            InputStream iStream = FileUtils.openInputStream(file);
            byte[] inputData = getBytes(iStream);

            if (uploadingListener != null)
                uploadingListener.onUploadStarted();

            checkIfAuthed(file, fileName, inputData);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void startUploading(byte[] fileBytes, String fileName, String contentType) {
        this.contentType = contentType;
        isMultiUpload = false;


        if (uploadingListener != null)
            uploadingListener.onUploadStarted();


        checkIfAuthed(fileBytes, fileName);

    }

    public void startUploading(byte[] fileBytes, String fileName) {
        isMultiUpload = false;


        if (uploadingListener != null)
            uploadingListener.onUploadStarted();


        checkIfAuthed(fileBytes, fileName);

    }

    public void startUploading(File file, String fileName) {
        isMultiUpload = false;
        try {

            InputStream iStream = FileUtils.openInputStream(file);
            byte[] inputData = getBytes(iStream);
            if (uploadingListener != null)
                uploadingListener.onUploadStarted();


            checkIfAuthed(file, fileName, inputData);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkIfAuthed(byte[] filebytes, String fileName) {
        uploadFile(filebytes, fileName, contentType, null);  
    }

    private void checkIfAuthed(File file, String fileName, byte[] inputData) {
            uploadFile(file, fileName, inputData, contentType, null);
    }

    private void uploadFile(File file, String fileName, byte[] inputData, String contentType, Callable<Void> onFinish) {
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
                new UploadProgressRequestBody.UploadInfo(file, inputData.length),
                (progress, total) -> {


                    int percentage = (int) ((progress * 100.0f) / total);

                    if (uploadingListener != null)
                        uploadingListener.onUploadProgress(percentage, progress, total);

                }
        );
        requestBody.setContentType(contentType);

// Upload
        Call<UploadResponse> call = uploadInterface.uploadFile(path, requestBody, uploadAuthorizationToken,
                SHAsum(inputData), fileName);
        call.enqueue(new Callback<UploadResponse>() {
            @Override
            public void onResponse(Call<UploadResponse> call, Response<UploadResponse> response) {


                if (uploadingListener != null) {
                    uploadingListener.onUploadFinished(response.body(), !isMultiUpload);
                    call.cancel();


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
                System.out.println("whynull" + response.body().toString());



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

    // private void getClientData(byte[] fileBytes, String fileName, String contentType) {

    //     blazeClient.setClientListener(new ClientListener() {
    //         @Override
    //         public void onConnectionStarted() {

    //         }

    //         @Override
    //         public void onRetrievingData(ClientModel data, JSONObject response) {

    //             Log.v("whynull" , response.toString());
    //             accountAuthorizationToken = data.getAuthorizationToken();
    //             apiUrl = data.getApiUrl();
    //             UploadAuth uploadAuth = new UploadAuth(context, bucketId, accountAuthorizationToken, apiUrl);
    //             uploadAuth.getUploadAuthData();
    //             uploadAuth.setUploadAuthListener(uploadAuthModel -> {

    //                 uploadUrl = uploadAuthModel.getUploadUrl();
    //                 uploadAuthorizationToken = uploadAuthModel.getAuthorizationToken();
    //                 isAuthed = true;
    //                 if (!isMultiUpload)
    //                     startUploading(fileBytes, fileName, contentType);
    //                 else
    //                     startUploadingMultipleFiles(files);
    //             });

    //         }

    //         @Override
    //         public void onFailure(Exception e) {
    //             if (uploadingListener != null)
    //                 uploadingListener.onUploadFailed(e);
    //         }
    //     });
    // }


    // private void getClientData(Uri fileUri, String fileName, String contentType) {

    //     blazeClient.setClientListener(new ClientListener() {
    //         @Override
    //         public void onConnectionStarted() {

    //         }

    //         @Override
    //         public void onRetrievingData(ClientModel data, JSONObject response) {

    //             accountAuthorizationToken = data.getAuthorizationToken();
    //             apiUrl = data.getApiUrl();
    //             UploadAuth uploadAuth = new UploadAuth(context, bucketId, accountAuthorizationToken, apiUrl);

    //             uploadAuth.getUploadAuthData();
    //             uploadAuth.setUploadAuthListener(uploadAuthModel -> {
    //                 uploadUrl = uploadAuthModel.getUploadUrl();
    //                 uploadAuthorizationToken = uploadAuthModel.getAuthorizationToken();
    //                 isAuthed = true;
    //                 if (!isMultiUpload)
    //                     startUploading(fileUri, fileName, contentType);
    //                 else
    //                     startUploadingMultipleFiles(files);
    //             });

    //         }

    //         @Override
    //         public void onFailure(Exception e) {
    //             if (uploadingListener != null)
    //                 uploadingListener.onUploadFailed(e);
    //         }
    //     });
    // }


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
