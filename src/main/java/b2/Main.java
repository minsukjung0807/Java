package b2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import b2.BackBlaze.BackBlazeB2;
import b2.BackBlaze.authorize_account.response.B2AuthResponse;
import b2.BackBlaze.create_bucket.model.BucketType;
import b2.BackBlaze.create_bucket.response.B2CreateBucketResponse;
import b2.BackBlaze.delete_file.item.DeleteFileItem;
import b2.BackBlaze.get_upload_url.response.B2GetUploadUrlResponse;
import b2.BackBlaze.upload_file.response.B2UploadFileResponse;
import b2.BackBlaze.upload_file.model.MultiFile;

import java.util.*;

public class Main {
  
    public static void main(String[] args) {
      authenticate();
    }

  // 인증 작업
  private static void authenticate() {

    BackBlazeB2 backBlazeB2 = new BackBlazeB2();
    backBlazeB2.setOnAuthStateListener(new BackBlazeB2.OnAuthStateListener() {
      @Override
      public void onCompleted(B2AuthResponse b2AuthResponse) {

          System.out.println("Download URL: " + b2AuthResponse.getDownloadURL());
          System.out.println("Authentication Token: " + b2AuthResponse.getAuthToken());
          System.out.println("API URL: " + b2AuthResponse.getAPIURL());
          System.out.println("Account Id: " + b2AuthResponse.getAccountID());

          // createBucket(b2AuthResponse);
          // B2CreateBucketResponse b2CreateBucketResponse = new B2CreateBucketResponse("mcpehub0403", "2e862fa05f0f830885e8081b", BucketType.ALL_PRIVATE);
          
          // getUploadUrl(b2AuthResponse, b2CreateBucketResponse);
          deleteMultipleFiles(b2AuthResponse);
          // deleteFile(b2AuthResponse, "MAP/Image2.jpg","4_z2e862fa05f0f830885e8081b_f1085e5a60383817f_d20240329_m101822_c005_v0501017_t0033_u01711707502593");
        }
      @Override
      public void onFailed(int status, String code, String message) {
        
        System.out.println("실패 11: " + status);
      }
    });

    backBlazeB2.authorize();

    
  }

  // // 버킷 생성
  // private static void createBucket(B2AuthResponse b2Session) {

  //   BackBlazeB2 backBlazeB2 = new BackBlazeB2();

  //   backBlazeB2.setOnCreateBucketStateListener(new BackBlazeB2.OnCreateBucketStateListener() {
  //     @Override
  //     public void onCompleted(B2CreateBucketResponse b2CreateBucketResponse) {

  //       System.out.println(b2CreateBucketResponse.getID());
  //       System.out.println(b2CreateBucketResponse.getName());
    

  //     }
  //     @Override
  //     public void onFailed(int status, String code, String message) {
  //       System.out.println("실패: " + message);
  //     }
  //   });

  //   backBlazeB2.createBucket(b2Session, "MCBedrock0807", BucketType.ALL_PRIVATE);
  // }

  // private static void deleteFile(B2AuthResponse b2AuthResponse, String fileName, String fileId) {

  //   BackBlazeB2 backBlazeB2 = new BackBlazeB2();

  //   backBlazeB2.setOnDeleteSingleFileListener(new BackBlazeB2.OnDeleteSingleFileStateListener() {
  //     @Override
  //     public void onSuccess() {
  //       System.out.println("삭제 성공!!");
  //     }
  //     @Override
  //     public void onFailed(int status, String code, String message) {
  //       System.out.println("삭제 실패: " + message);
  //     }
  //   });

  //   backBlazeB2.deleteSingleFile(b2AuthResponse, fileName, fileId);

  // }

  private static void deleteMultipleFiles(B2AuthResponse b2AuthResponse) {
    
    BackBlazeB2 backBlazeB2 = new BackBlazeB2();

    ArrayList<DeleteFileItem> deleteFileItems = new ArrayList<>();
    
    // deleteFileItems.add(new DeleteFileItem("hello1.txt", "4_z2e862fa05f0f830885e8081b_f1071e0a9edeafa02_d20240331_m060143_c005_v0501010_t0010_u01711864903665"));
    // deleteFileItems.add(new DeleteFileItem("hello2.txt", "4_z2e862fa05f0f830885e8081b_f1071e0a9edeafa76_d20240331_m060146_c005_v0501010_t0027_u01711864906781"));
    
    // deleteFileItems.add(new DeleteFileItem("hello3.txt", "4_z2e862fa05f0f830885e8081b_f1071e0a9edeafaf6_d20240331_m060150_c005_v0501010_t0037_u01711864910455"));
    
    backBlazeB2.setOnDeleteMultipleFileListener(new BackBlazeB2.OnDeleteMultipleFileStateListener() {
      @Override
      public void onSuccess(int nTh) {
        System.out.println(nTh+"번째 파일 삭제 성공!");
      }
      @Override
      public void onFailed(int status, String code, String message, int nThItem) {
        System.out.println(nThItem+"삭제 실패 ddd: "+message);
      }

      @Override
      public void onFinish() {
        System.out.println("모든 파일 삭제 완료!!");
      }
    });

    backBlazeB2.deleteMultipleFiles(b2AuthResponse, deleteFileItems);

  }

  // private static void getUploadUrl(B2AuthResponse b2AuthResponse, B2CreateBucketResponse b2CreateBucketResponse) {


  //   new BackBlazeB2().setOnGetUploadUrlStateListener(new BackBlazeB2.OnGetUploadUrlStateListener() {
  //     @Override
  //     public void onCompleted(B2GetUploadUrlResponse b2GetUploadUrlResponse) {

  //       System.out.println(b2GetUploadUrlResponse.getUploadURL());
  //       System.out.println(b2GetUploadUrlResponse.getUploadAuthorizationToken());
    

  //       uploadMultipleFiles(b2GetUploadUrlResponse);
  //       // uploadSingleFile(b2GetUploadUrlResponse);
  //     }
  //     @Override
  //     public void onFailed(int status, String code, String message) {
  //       System.out.println("실패: " + message);
  //     }
  //   }).getUploadUrl(b2AuthResponse, b2CreateBucketResponse);
  // }

  // private static void uploadMultipleFiles(B2GetUploadUrlResponse b2GetUploadUrlResponse) {
  //   BackBlazeB2 backBlazeB2 = new BackBlazeB2();

  //   File path = new File("");

  //   File file = new File(path.getAbsolutePath()+"/src/file/5MB.txt");

  //   File file2 = new File(path.getAbsolutePath()+"/src/file/10MB.txt");

  //   ArrayList<MultiFile> arrayList = new ArrayList<>();
  //   arrayList.add(new MultiFile(file, "hello1.txt"));
  //   arrayList.add(new MultiFile(file2, "hello2.txt"));
  //   arrayList.add(new MultiFile(file, "hello3.txt"));

  //   backBlazeB2.setOnUploadMultipleFilesStateListener(new BackBlazeB2.OnUploadMultipleFileStateListener() {
  //     @Override
  //     public void onStarted() {
  //       System.out.println("시작중!!");
  //     }

  //     @Override
  //     public void onProgress(int percentage, long progress, long total) {
  //       System.out.println("업로드 중" + percentage);
  //     }
  //     @Override
  //     public void onCompleted(B2UploadFileResponse response, boolean allFilesUploaded) {
  //       System.out.println("업로드 완료!!");
  //     }
  //     @Override
  //     public void onFailed(int status, String code, String message) {
  //       System.out.println("오류!!"+  "상태:"+ status + "코드: " +code + "메시지: " + message);
  //     }
  //   });

  //   backBlazeB2.uploadMultipleFiles(arrayList, b2GetUploadUrlResponse);
  // }
  // private static void uploadSingleFile(B2GetUploadUrlResponse b2GetUploadUrlResponse) {


  //   File path = new File("");
  //   File file = new File(path.getAbsolutePath()+"/src/file/test.zip");

  //   BackBlazeB2 backBlazeB2 = new BackBlazeB2();
  //   backBlazeB2.setOnUploadSingleFileStateListener(new BackBlazeB2.OnUploadSingleFileStateListener() {
  //     @Override
  //     public void onStarted() {
  //       System.out.println("시작중!!");
  //     }

  //     @Override
  //     public void onProgress(int percentage, long progress, long total) {
  //       System.out.println("업로드 중" + percentage);
  //     }
  //     @Override
  //     public void onCompleted(B2UploadFileResponse response, boolean allFilesUploaded) {
  //       System.out.println("업로드 완료!!");
  //     }
  //     @Override
  //     public void onFailed(int status, String code, String message) {
  //       System.out.println("오류!!"+  "상태:"+ status + "코드: " +code + "메시지: " + message);
  //     }

  //   }).uploadSingleFile(file, "MAP/test.mcworld", b2GetUploadUrlResponse);
  // }



   
}