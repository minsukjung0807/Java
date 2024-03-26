package b2.BackBlazeB3.fileUploader;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class UploadProgressRequestBody extends RequestBody {
    private static final String LOG_TAG = UploadProgressRequestBody.class.getSimpleName();

    private String contentType ="application/octet-stream";

    public interface ProgressCallback {
        public void onProgress(long progress, long total);
    }

    public static class UploadInfo {
        //Content uri for the file
        public Uri contentUri;

        public byte[] fileBytes;
        // File size in bytes
        public long contentLength;

        public UploadInfo(Uri contentUri, long contentLength) {
            this.contentUri = contentUri;
            this.contentLength = contentLength;
        }

        public UploadInfo(byte[] fileBytes, long contentLength) {
            this.fileBytes = fileBytes;
            this.contentLength = contentLength;
        }
    }

    private UploadInfo mUploadInfo;
    private ProgressCallback mListener;

    private static final int UPLOAD_PROGRESS_BUFFER_SIZE = 8192;

    public UploadProgressRequestBody(UploadInfo uploadInfo, ProgressCallback listener) {
        mUploadInfo =  uploadInfo;
        mListener = listener;
    }

    @Override
    public MediaType contentType() {
        // NOTE: We are posting the upload as binary data so we don't need the true mimeType
        return MediaType.parse(contentType.isEmpty()?"application/octet-stream":contentType);
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        long fileLength = mUploadInfo.contentLength;
        byte[] buffer = new byte[UPLOAD_PROGRESS_BUFFER_SIZE];
        InputStream in = in();
        long uploaded = 0;

        try {
            int read;
            while ((read = in.read(buffer)) != -1) {
                mListener.onProgress(uploaded, fileLength);

                
                uploaded += read;

                sink.write(buffer, 0, read);
            }
        } finally {
            in.close();
        }
    }


    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * WARNING: You must override this function and return the file size or you will get errors
     */
    @Override
    public long contentLength() throws IOException {
        return mUploadInfo.contentLength;
    }

    private InputStream in() throws IOException {
        InputStream stream = null;
        try {
            if(mUploadInfo.contentUri!=null)
            
            stream = getContentResolver().openInputStream(mUploadInfo.contentUri);
            else
                stream = new ByteArrayInputStream(mUploadInfo.fileBytes);

        } catch (Exception ex) {
            Log.e(LOG_TAG, "Error getting input stream for upload", ex);
        }

        return stream;
    }

    private ContentResolver getContentResolver() {
        if (mContextRef.get() != null) {
            return mContextRef.get().getContentResolver();
        }
        return null;
    }
}
