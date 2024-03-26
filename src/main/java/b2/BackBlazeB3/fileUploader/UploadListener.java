package b2.BackBlazeB3.fileUploader;

public interface UploadListener {
    void onUploadStarted();

    void onUploadProgress(int percentage, long progress, long total);
    void onUploadFinished(UploadResponse response, boolean allFilesUploaded);

    void onUploadFailed(Exception e);
}
