package b2.main.BackBlazeB3.Upload;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;

public class UploadProgressRequestBody extends RequestBody {

    private String contentType ="application/octet-stream";

    public interface ProgressCallback {
        public void onProgress(long progress, long total);
    }

    public static class UploadInfo {
        public File file;

        public byte[] fileBytes;
        // File size in bytes
        public long contentLength;

        public UploadInfo(File file, long contentLength) {
            this.file = file;
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
            if(mUploadInfo.file!=null)
                stream = FileUtils.openInputStream(mUploadInfo.file);
            else
                stream = new ByteArrayInputStream(mUploadInfo.fileBytes);

        } catch (Exception ex) {
            System.out.println("Error getting input stream for upload" + ex);
        }

        return stream;
    }
}
