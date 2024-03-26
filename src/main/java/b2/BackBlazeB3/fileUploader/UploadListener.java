package b2.BackBlazeB3.fileUploader;

import b2.BackBlazeB3.uploadModel.UploadResponse;

public interface UploadListener {
    void onUploadStarted();

    void onUploadProgress(int percentage, long progress, long total);
    void onUploadFinished(UploadResponse response, boolean allFilesUploaded);

    void onUploadFailed(Exception e);
}
