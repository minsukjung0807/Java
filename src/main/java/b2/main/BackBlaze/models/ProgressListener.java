package b2.main.BackBlaze.models;

public interface ProgressListener {
    void onProgress(long bytesWritten, long contentLength, boolean done);
}
