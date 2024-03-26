package b2.BackBlaze.models;

public interface ProgressListener {
    void onProgress(long bytesWritten, long contentLength, boolean done);
}
