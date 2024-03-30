package b2.BackBlaze.upload_file.model;

public class MultiFile {
    public byte[] fileBytes;
    public String fileName;
    public String contentType;

    public MultiFile() {
    }

    public MultiFile(byte[] fileBytes, String fileName, String contentType) {
        this.fileBytes = fileBytes;
        this.fileName = fileName;
        this.contentType = contentType;
    }

    public void init(byte[] fileBytes, String fileName, String contentType) {
        this.fileBytes = fileBytes;
        this.fileName = fileName;
        this.contentType = contentType;
    }

    public byte[] getFileBytes() {
        return fileBytes;
    }

    public void setFileBytes(byte[] fileBytes) {
        this.fileBytes = fileBytes;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
}