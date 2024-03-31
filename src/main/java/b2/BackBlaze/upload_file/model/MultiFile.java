package b2.BackBlaze.upload_file.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.FileUtils;


public class MultiFile {
    public byte[] fileBytes;
    public String fileName;
    public String contentType;

    public MultiFile(File file, String fileName) {

         InputStream iStream = null;
                try {
                  String fileType = B2UploadUtils.getContentType(file);

                  iStream = FileUtils.openInputStream(file);
                  
                  byte[] inputData = B2UploadUtils.getBytes(iStream);

                  init(inputData, fileName, fileType);

            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("에러!");
            }
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