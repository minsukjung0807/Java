package b2;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.commons.io.FileUtils;

import b2.BackBlaze.api.BackBlazeB2;
import b2.BackBlaze.authorize_account.response.B2AuthResponse;
import b2.BackBlaze.create_bucket.model.BucketType;
import b2.BackBlaze.create_bucket.response.B2CreateBucketResponse;
import b2.BackBlaze.get_upload_url.response.B2GetUploadUrlResponse;
import b2.BackBlaze.upload_file.B2MultiUpload;
import b2.BackBlaze.upload_file.model.B2UploadUtils;
import b2.BackBlaze.upload_file.model.MultiFile;
import b2.BackBlaze.upload_file.model.UploadListener;
import b2.BackBlaze.upload_file.response.B2UploadFileResponse;

import java.util.*;

public class Main {

  private static String uploadUrl, uploadAuthorizationToken;
  
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

    BackBlazeB2 backBlazeB2 = new BackBlazeB2();
    backBlazeB2.setOnAuthStateListener(new BackBlazeB2.OnAuthStateListener() {
      @Override
      public void onCompleted(B2AuthResponse b2AuthResponse) {

          System.out.println("Download URL: " + b2AuthResponse.getDownloadURL());
          System.out.println("Authentication Token: " + b2AuthResponse.getAuthToken());
          System.out.println("API URL: " + b2AuthResponse.getAPIURL());
          System.out.println("Account Id: " + b2AuthResponse.getAccountID());

          // createBucket(b2AuthResponse);
          B2CreateBucketResponse b2CreateBucketResponse = new B2CreateBucketResponse("mcpehub0403", "2e862fa05f0f830885e8081b", BucketType.ALL_PRIVATE);
          
          getUploadUrl(b2AuthResponse, b2CreateBucketResponse);

          // deleteFile(b2AuthResponse, "MAP/Image1.jpg","4_z2e862fa05f0f830885e8081b_f1085e5a603837f65_d20240329_m101818_c005_v0501017_t0012_u01711707498686");
        }
      @Override
      public void onFailed(String message) {
        System.out.println("으음: " + message);
      }
    });

    backBlazeB2.authorize();
  }

  // 버킷 생성
  private static void createBucket(B2AuthResponse b2Session) {

    BackBlazeB2 backBlazeB2 = new BackBlazeB2();

    backBlazeB2.setOnCreateBucketStateListener(new BackBlazeB2.OnCreateBucketStateListener() {
      @Override
      public void onCompleted(B2CreateBucketResponse b2CreateBucketResponse) {

        System.out.println(b2CreateBucketResponse.getID());
        System.out.println(b2CreateBucketResponse.getName());
    

      }
      @Override
      public void onFailed(String message) {
        System.out.println("실패: " + message);
      }
    });

    backBlazeB2.createBucket(b2Session, "MCBedrock0807", BucketType.ALL_PRIVATE);
  }

  private static void deleteFile(B2AuthResponse b2AuthResponse, String fileName, String fileId) {

    BackBlazeB2 backBlazeB2 = new BackBlazeB2();

    backBlazeB2.setOnDeleteSingleFileListener(new BackBlazeB2.OnDeleteSingleFileStateListener() {
      @Override
      public void onSuccess() {
        System.out.println("삭제 성공!!");
      }
      @Override
      public void onFailed(String message) {
        System.out.println("삭제 실패: " + message);
      }
    });

    backBlazeB2.deleteSingleFile(b2AuthResponse, fileName, fileId);

  }

  private static void getUploadUrl(B2AuthResponse b2AuthResponse, B2CreateBucketResponse b2CreateBucketResponse) {


    new BackBlazeB2().setOnGetUploadUrlStateListener(new BackBlazeB2.OnGetUploadUrlStateListener() {
      @Override
      public void onCompleted(B2GetUploadUrlResponse b2GetUploadUrlResponse) {

        System.out.println(b2GetUploadUrlResponse.getUploadURL());
        System.out.println(b2GetUploadUrlResponse.getUploadAuthorizationToken());
    

        uploadSingleFile(b2GetUploadUrlResponse);
      }
      @Override
      public void onFailed(String message) {
        System.out.println("실패: " + message);
      }
    }).getUploadUrl(b2AuthResponse, b2CreateBucketResponse);
  }


  // private static void getUploadUrl(BackblazeB2 backblazeB2, B2AuthResponse b2Session, B2CreateBucketResponse b2Bucket) {

  //   B2GetUploadUrlResponse b2UploadRequest = backblazeB2.getUploadURL(b2Session, b2Bucket);

  //   uploadUrl = b2UploadRequest.getUploadURL();
  //   uploadAuthorizationToken = b2UploadRequest.getUploadAuthorizationToken();

  //   System.out.println("업로드 URL: " + b2UploadRequest.getUploadURL());

    
  //   B2MultiUpload b2MultiUpload = new B2MultiUpload();

  //   ArrayList<MultiFile> arrayList = new ArrayList<>();

  //   for (int i = 0; i < 6; i++) {

  //       File path = new File("");
  //       File file = new File(path.getAbsolutePath()+"/src/image/image"+i+".jpg");

  //           MultiFile multiFile = new MultiFile();
        
  //           if(file.exists()) {
  //             InputStream iStream = null;
  //               try {
  //                 String fileType = B2UploadUtils.getContentType(file);
  //                 iStream = FileUtils.openInputStream(file);
  //                 byte[] inputData = getBytes(iStream);
  //                 multiFile.init(inputData, "MAP/Image" + i+".jpg", fileType);
  //                 arrayList.add(multiFile);
  //           } catch (IOException e) {
  //               e.printStackTrace();
  //               System.out.println("에러!");
  //           }
  //         } else {
  //           System.out.println("파일이 없습니다!");
  //         }

  //   }
    
  //   b2MultiUpload.setOnUploadingListener(new UploadListener() {
  //           @Override
  //           public void onUploadStarted() {
  //             System.out.println("파일 업로드 시작...");
  //           }
      
  //           @Override
  //           public void onUploadProgress(int percentage, long progress, long total) {
  //               System.out.println("파일 업로드 중... "+ percentage + "%");
  //           }
      
  //           @Override
  //           public void onUploadFinished(B2UploadFileResponse response, boolean allFilesUploaded) {
  //             System.out.println("파일 업로드 완료!!");
  //           }
      
  //           @Override
  //           public void onUploadFailed(Exception e) {
  //             System.out.println("업로드 실패: " + e.getMessage());
  //           }
  //       });

        
  //   if (arrayList.size() > 0) {
  //     b2MultiUpload.startUploadingMultipleFiles(arrayList, b2UploadRequest);
  //   }

  private static void uploadSingleFile(B2GetUploadUrlResponse b2GetUploadUrlResponse) {


    File path = new File("");
    File file = new File(path.getAbsolutePath()+"/src/file/10MB.txt");

    BackBlazeB2 backBlazeB2 = new BackBlazeB2();
    backBlazeB2.setOnUploadSingleFileStateListener(new BackBlazeB2.OnUploadSingleFileStateListener() {
      @Override
      public void onStarted() {
        System.out.println("시작중!!");
      }

      @Override
      public void onProgress(int percentage, long progress, long total) {
        System.out.println("업로드 중" + percentage);
      }
      @Override
      public void onCompleted(B2UploadFileResponse response, boolean allFilesUploaded) {
        System.out.println("업로드 완료!!");
      }
      @Override
      public void onFailed(int status, String code, String message) {
        System.out.println("오류!!"+  "상태:"+ status + "코드: " +code + "메시지: " + message);
      }

    }).uploadSingleFile(file, "MAP/xxx.jpg", b2GetUploadUrlResponse);
  }
    // String contentType = B2UploadUtils.getContentType(file);

//    if(file.exists()) {

//       System.out.println("콘텐츠의 타입2: " + contentType);
//       System.out.println("파일이 존재합니다!");
    
//     B2SingleUpload b2SingleUpload = new B2SingleUpload(uploadUrl, uploadAuthorizationToken);
    
//     b2SingleUpload.setOnUploadingListener(new UploadListener() {
//       @Override
//       public void onUploadStarted() {
//         System.out.println("파일 업로드 시작...");
//       }

//       @Override
//       public void onUploadProgress(int percentage, long progress, long total) {
//           System.out.println("파일 업로드 중... "+ percentage + "%");
//       }

//       @Override
//       public void onUploadFinished(UploadResponse response, boolean allFilesUploaded) {
//         System.out.println("파일 업로드 완료!!");
//       }

//       @Override
//       public void onUploadFailed(Exception e) {
//         System.out.println("업로드 실패: " + e.getMessage());
//       }
//   });

//   b2SingleUpload.startUploading(file, "MAP/12MB.txt");

// } else{
//   System.out.println("파일이 없습니다!");
// }
  // }
}