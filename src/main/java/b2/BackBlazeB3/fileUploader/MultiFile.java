package b2.BackBlazeB3.fileUploader;


public class MultiFile {

    public Uri fileUri;
    public byte[] fileBytes;
    public String fileName;
    public String contentType;
    public MultiFile() {
    }

    public MultiFile(Uri fileUri, String fileName, String contentType) {
        this.fileUri = fileUri;
        this.fileName = fileName;
        this.contentType = contentType;
    }

    public MultiFile(byte[] fileBytes, String fileName, String contentType) {
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

    public Uri getFileUri() {
        return fileUri;
    }

    public void setFileUri(Uri fileUri) {
        this.fileUri = fileUri;
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


    public void init(Uri fileUri, String fileName, String contentType) {
        this.fileUri = fileUri;
        this.fileName = fileName;
        this.contentType = contentType;
    }
    public void init(byte[] fileBytes, String fileName, String contentType) {
        this.fileBytes = fileBytes;
        this.fileName = fileName;
        this.contentType = contentType;
    }
}
