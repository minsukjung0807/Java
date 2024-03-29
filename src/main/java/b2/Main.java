package b2;


import b2.main.BackBlaze.BackblazeB2Auth;

import java.io.ByteArrayOutputStream;
import java.io.File;

import java.io.IOException;
import java.io.InputStream;
import b2.main.BackBlaze.BackblazeB2;
import b2.main.BackBlaze.models.B2Bucket1;
import b2.main.BackBlaze.models.B2Session1;
import b2.main.BackBlaze.models.B2UploadRequest1;
import b2.main.BackBlaze.models.BucketType1;
import b2.main.BackBlazeB3.Upload.B2SingleUpload;
import b2.main.BackBlazeB3.Upload.B2UploadUtils;
import b2.main.BackBlazeB3.Upload.UploadListener;
import b2.main.BackBlazeB3.uploadModel.UploadResponse;

public class Main {
  
  private static String appKeyId = "005e6f0ff38588b000000000a";
  private static String appKey = "K005k2tpcpfoqMY525/C9Pj5kHbDWXY";

  private static String authorizationToken, apiUrl, uploadUrl, uploadAuthorizationToken, bucketId;
  
    public static void main(String[] args) {
      authenticate();
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
      ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
      int bufferSize = 1024;
      byte[] buffer = new byte[bufferSize];

      int len = 0;
      while ((len = inputStream.read(buffer)) != -1) {
          byteBuffer.write(buffer, 0, len);
      }
      return byteBuffer.toByteArray();
  }
  
  // 인증 작업
  private static void authenticate() {
    
    BackblazeB2Auth authentication = new BackblazeB2Auth();

    authentication.setOnAuthStateListener(new BackblazeB2Auth.OnAuthStateListener() {

      @Override
      public void onSuccess(B2Session1 b2Session) {

          System.out.println("Download URL: " + b2Session.getDownloadURL());
          System.out.println("Authentication Token: " + b2Session.getAuthToken());
          System.out.println("API URL: " + b2Session.getAPIURL());
          System.out.println("Account Id: " + b2Session.getAccountID());

          BackblazeB2 backblazeB2 = new BackblazeB2();
          backblazeB2.setOnBucketStateListener(new BackblazeB2.OnCreateBucketStateListener() {
            @Override
            public void onSuccess(String message) {
              System.out.println(message);
            }
      
            @Override
            public void onFailed(String message) {
              System.out.println(message);
            }
          });

          // createBucket(b2Session);
          B2Bucket1 b2Bucket1 = new B2Bucket1(b2Session.getAPIURL(), "2e862fa05f0f830885e8081b", BucketType1.ALL_PUBLIC);
          
          getUploadUrl(backblazeB2, b2Session, b2Bucket1);
      }

      @Override
      public void onFailed(String message) {

      }
    });

    authentication.authenticate();
  }

  // 버킷 생성
  private static void createBucket(B2Session1 b2Session) {

    BackblazeB2 bucketCreation = new BackblazeB2();
    bucketCreation.setOnBucketStateListener(new BackblazeB2.OnCreateBucketStateListener() {
      @Override
      public void onSuccess(String message) {
        System.out.println(message);
      }

      @Override
      public void onFailed(String message) {
        System.out.println(message);
      }
    });

    B2Bucket1 b2Bucket = bucketCreation.createBucket(b2Session, "Mc", BucketType1.ALL_PUBLIC);
    System.out.println(b2Bucket.getID());
    System.out.println(b2Bucket.getName());

    getUploadUrl(bucketCreation, b2Session, b2Bucket);
  }


  private static void getUploadUrl(BackblazeB2 backblazeB2, B2Session1 b2Session, B2Bucket1 b2Bucket) {

    B2UploadRequest1 b2UploadRequest = backblazeB2.getUploadURL(b2Session, b2Bucket);

    apiUrl = b2Session.getAPIURL();
    authorizationToken = b2Session.getAuthToken();
    uploadUrl = b2UploadRequest.getUploadURL();
    bucketId = b2Bucket.getID();
    uploadAuthorizationToken = b2UploadRequest.getAuthorizationToken();

    System.out.println("업로드 URL: " + b2UploadRequest.getUploadURL());

    File path = new File("");
    File file = new File(path.getAbsolutePath()+"/src/file/10MB.txt");

    String contentType = B2UploadUtils.getContentType(file);

   if(file.exists()) {

      System.out.println("콘텐츠의 타입2: " + contentType);
      System.out.println("파일이 존재합니다!");
    
    B2SingleUpload b2SingleUpload = new B2SingleUpload(uploadUrl, uploadAuthorizationToken, bucketId);
    
    b2SingleUpload.setOnUploadingListener(new UploadListener() {
      @Override
      public void onUploadStarted() {
        System.out.println("파일 업로드 시작...");
      }

      @Override
      public void onUploadProgress(int percentage, long progress, long total) {
          System.out.println("파일 업로드 중... "+ percentage + "%");
      }

      @Override
      public void onUploadFinished(UploadResponse response, boolean allFilesUploaded) {
        System.out.println("파일 업로드 완료!!");
      }

      @Override
      public void onUploadFailed(Exception e) {
        System.out.println("업로드 실패: " + e.getMessage());
      }
  });

  b2SingleUpload.startUploading(file, "MAP/12MB.txt");

} else{
  System.out.println("파일이 없습니다!");
}
  }
}