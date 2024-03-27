package b2.temp.BackBlaze2.listeners;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import okio.*;
import java.io.*;

public class ProgressRequestBody extends RequestBody {

    private final RequestBody requestBody;
    private final ProgressListener progressListener;
    private BufferedSink bufferedSink;

    public ProgressRequestBody(RequestBody requestBody, ProgressListener progressListener) {
        this.requestBody = requestBody;
        this.progressListener = progressListener;
    }

    @Override
    public long contentLength() throws IOException {
        return requestBody.contentLength();
    }

    @Override
    public MediaType contentType() {
        return requestBody.contentType();
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        // if (bufferedSink == null) {
        //     bufferedSink = Okio.buffer(sink(sink));
        // }
        

        byte[] buffer = new byte[8192];
        InputStream in = null;
        long uploaded = 0;

        try {
            int read;
            while ((read = in.read(buffer)) != -1) {
                System.out.println("업로드 중.." + uploaded);
                // mListener.onProgress(uploaded, contentLength());

                uploaded += read;

                sink.write(buffer, 0, read);
            }
        } finally {
            in.close();
        }

        // requestBody.writeTo(bufferedSink);
        // System.out.println("writeTo실행");
        // bufferedSink.flush();

    }

    private Sink sink(Sink sink) {
        return new ForwardingSink(sink) {
            long bytesWritten = 0L;
            long contentLength = 0L;

            @Override
            public void write(Buffer source, long byteCount) throws IOException {
                super.write(source, byteCount);
                if (contentLength == 0) {
                    contentLength = contentLength();
                }
                bytesWritten += byteCount;
                
                if (progressListener != null) {
                    progressListener.onProgress(bytesWritten, contentLength, bytesWritten == contentLength);
                }
            }
        };
    }

    public interface ProgressListener {
        void onProgress(long bytesWritten, long contentLength, boolean done);
    }

    //  private InputStream in() throws IOException {
    //     InputStream stream = null;
    //     try {
    //         if(mUploadInfo.contentUri!=null)
    //         stream = getContentResolver().openInputStream(mUploadInfo.contentUri);
    //         else
    //             stream = new ByteArrayInputStream(mUploadInfo.fileBytes);

    //     } catch (Exception ex) {
    //         Log.e(LOG_TAG, "Error getting input stream for upload", ex);
    //     }

        // return stream;
    // }


}