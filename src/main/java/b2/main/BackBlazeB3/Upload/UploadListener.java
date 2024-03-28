package b2.main.BackBlazeB3.Upload;

import b2.main.BackBlazeB3.uploadModel.UploadResponse;

public interface UploadListener {
    void onUploadStarted();

    void onUploadProgress(int percentage, long progress, long total);
    
    void onUploadFinished(UploadResponse response, boolean allFilesUploaded);

    void onUploadFailed(Exception e);
}
