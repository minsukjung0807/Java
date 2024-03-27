package b2.temp.BackBlaze2;

public class Execute {
    // B2 b2 = new B2(appKeyId, appKey);
      // B2Bucket bucket = new B2Bucket();
      // bucket.setBucketName("mus3453"); //bucket names are unique
      // bucket.setBucketType(B2.BUCKET_TYPE_PUBLIC); //public or private

      // B2Bucket bucket1 = b2.createBucket(bucket);

      // authorizationToken = b2.getInfo().getAuthorizationToken();
      // apiUrl = b2.getInfo().getApiUrl();
      // System.out.println("인증 토큰" + b2.getInfo().getAuthorizationToken());
      // System.out.println("API URL" + b2.getInfo().getApiUrl());

      // System.out.println("벗킷 ID: "+ bucket1.getBucketId());
      // System.out.println("버킷 이름: " + bucket1.getBucketName());
      // System.out.println("버킷 타입: " + bucket1.getBucketType());
      // System.out.println("아이디: " + bucket1.getAccountId());

      // B2UploadInfo uploadInfo = b2.getUploadInfo(bucket1.getBucketId()); //Get upload info for specific bucket

      // uploadAuthorizationToken = uploadInfo.getAuthorizationToken();
      // bucketId = uploadInfo.getBucketId();
      // uploadUrl = uploadInfo.getUploadUrl();

      // System.out.println("업로드 인증 토큰: " + uploadInfo.getAuthorizationToken());
      // System.out.println("버킷 ID: " + uploadInfo.getBucketId());
      // System.out.println("업로드 URL: "+ uploadInfo.getUploadUrl());

      // File path = new File("");
     
      // File file = new File(path.getAbsolutePath()+"/src/file/5MB.txt");

      // if(file.exists()) {
      //   System.out.println("파일 존재함!!");

      //   BlazeFileUploader blazeFileUploader = new BlazeFileUploader(authorizationToken, apiUrl, uploadUrl, uploadAuthorizationToken, bucketId);


      //   blazeFileUploader.setOnUploadingListener(new UploadListener() {
      //     @Override
      //     public void onUploadStarted() {
  
      //     }
  
  
      //     @Override
      //     public void onUploadProgress(int percentage, long progress, long total) {
  
      //         System.out.println("uplooooad: "+ percentage + "  " + progress + "   " + total);
  
      //     }
  
      //     @Override
      //     public void onUploadFinished(UploadResponse response, boolean allFilesUploaded) {
      //       System.out.println("끝");
      //     }
  
      //     @Override
      //     public void onUploadFailed(Exception e) {
  
      //     }
      // });

      //   InputStream iStream = null;
      //   try {
      //       iStream = FileUtils.openInputStream(file);
      //       byte[] inputData = getBytes(iStream);
      //       blazeFileUploader.startUploading(inputData, "4MB.txt");
      //   } catch (FileNotFoundException e) {
      //       e.printStackTrace();
      //   } catch (IOException e) {
      //       e.printStackTrace();
      //   }

        
    
      // } else {
      //   System.out.println("파일 없음!!");
      // }
    // if(file.exists()) {
    //   b2.uploadFile(file, uploadInfo);
    //   System.out.println("파일이 존재함!!");
    //   b2.uploadFile(file, uploadInfo , new ProgressRequestBody.ProgressListener() {
    //     @Override
    //     public void onProgress(long bytesWritten, long contentLength, boolean done) {
    //         System.out.println("업로드 중..." + bytesWritten);
    //     }
    // });

    // }
    // else {
    //   System.out.println("파일 없음");
    // }
//       b2.uploadFile(file, uploadInfo, new ProgressRequestBody.ProgressListener() {
//     @Override
//     public void onProgress(long bytesWritten, long contentLength, boolean done) {
//         System.out.println("업로드 중..." + bytesWritten);
//     }
// });
//     } else {
//       System.out.println("파일이 없음!!");
//     }
      // authenticate();
}
