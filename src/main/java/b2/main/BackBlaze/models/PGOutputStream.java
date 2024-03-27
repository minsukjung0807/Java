package b2.main.BackBlaze.models;

import java.io.IOException;
import java.io.OutputStream;

public class PGOutputStream extends OutputStream {
    private final OutputStream outputStream;
    private final long totalSize;
    private long bytesWritten;
    private static final int UPDATE_INTERVAL = 8192; // Update progress every 8192 bytes

    public PGOutputStream(OutputStream outputStream, long totalSize) {
        this.outputStream = outputStream;
        this.totalSize = totalSize;
        this.bytesWritten = 0;
    }

    @Override
    public void write(int b) throws IOException {
        outputStream.write(b);
        bytesWritten++;
        if (bytesWritten % UPDATE_INTERVAL == 0 || bytesWritten == totalSize) {
            // Notify progress here
            double progress = (double) bytesWritten / totalSize * 100.0;
            System.out.printf("Progress: %.2f%%\n", progress);
        }
    }

    @Override
    public void write(byte[] b) throws IOException {
        outputStream.write(b);
        bytesWritten += b.length;
        if (bytesWritten % UPDATE_INTERVAL == 0 || bytesWritten == totalSize) {
            // Notify progress here
            double progress = (double) bytesWritten / totalSize * 100.0;
            System.out.printf("Progress: %.2f%%\n", progress);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        outputStream.write(b, off, len);
        bytesWritten += len;
        if (bytesWritten % UPDATE_INTERVAL == 0 || bytesWritten == totalSize) {
            // Notify progress here
            double progress = (double) bytesWritten / totalSize * 100.0;
            System.out.printf("Progress: %.2f%%\n", progress);
        }
    }

    @Override
    public void flush() throws IOException {
        outputStream.flush();
    }

    @Override
    public void close() throws IOException {
        outputStream.close();
    }
}
