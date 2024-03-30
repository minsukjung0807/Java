package b2.BackBlaze.upload_file.model;

import b2.BackBlaze.upload_file.response.B2UploadFileResponse;

public interface UploadListener {
    
    void onUploadStarted();

    void onUploadProgress(int percentage, long progress, long total);
    
    void onUploadFinished(B2UploadFileResponse response, boolean allFilesUploaded);

    void onUploadFailed(int status, String code, String message);
}
