package b2.BackBlaze.delete_file.item;

public class DeleteFileItem {
    
    private String fileName;
    private String fileId;

    public DeleteFileItem(String fileName, String fileId) {
        this.fileName = fileName;
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }
}

