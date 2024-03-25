package b2.BackBlaze2.listeners;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import okio.*;

import java.io.IOException;

public class ProgressRequestBody extends RequestBody {

    private final RequestBody requestBody;
    private final ProgressListener progressListener;
    private BufferedSink bufferedSink;

    private static final int DEFAULT_BUFFER_SIZE = 2048; // 버퍼 크기 조절

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
        if (bufferedSink == null) {
            bufferedSink = Okio.buffer(sink(sink));
        }
        requestBody.writeTo(bufferedSink);
        // 플러시하지 않음 - 마지막에 한 번에 플러시
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
}
