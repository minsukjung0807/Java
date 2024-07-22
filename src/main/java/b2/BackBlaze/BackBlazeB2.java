package b2.BackBlaze;

import java.io.File;
import java.util.ArrayList;

import b2.BackBlaze.authorize_account.B2Auth;
import b2.BackBlaze.authorize_account.response.B2AuthResponse;
import b2.BackBlaze.create_bucket.B2CreateBucket;
import b2.BackBlaze.create_bucket.model.BucketType;
import b2.BackBlaze.create_bucket.response.B2CreateBucketResponse;
import b2.BackBlaze.delete_file.B2DeleteMultipleFile;
import b2.BackBlaze.delete_file.B2DeleteSingleFile;
import b2.BackBlaze.delete_file.item.DeleteFileItem;
import b2.BackBlaze.get_upload_url.B2GetUploadUrl;
import b2.BackBlaze.get_upload_url.response.B2GetUploadUrlResponse;
import b2.BackBlaze.upload_file.B2MultiUpload;
import b2.BackBlaze.upload_file.B2SingleUpload;
import b2.BackBlaze.upload_file.model.MultiFile;
import b2.BackBlaze.upload_file.model.UploadListener;
import b2.BackBlaze.upload_file.response.B2UploadFileResponse;

public class BackBlazeB2 {

    /* 
     * 인증시에 사용
     */

    public OnAuthStateListener onAuthStateListener;

    public interface OnAuthStateListener { 
        abstract void onCompleted(B2AuthResponse b2AuthResponse);
        abstract void onFailed(int status, String code, String message);
    }
 
     public BackBlazeB2 setOnAuthStateListener(OnAuthStateListener onAuthStateListener){
        this.onAuthStateListener = onAuthStateListener;
        return this;
     }
 
     public void authorize() {
         
         B2Auth b2Auth = new B2Auth();
 
         if(onAuthStateListener != null) {
            b2Auth.setOnAuthStateListener(new B2Auth.OnAuthStateListener() {
                @Override
                 public void onSuccess(B2AuthResponse b2AuthResponse) {
                    onAuthStateListener.onCompleted(b2AuthResponse);
                 }
     
                 @Override
                 public void onFailed(int status, String code, String message) {
                    onAuthStateListener.onFailed(status, code, message);
                 }
            });
         }
         b2Auth.startAuthenticating();
     }

    /* 
     * 버킷 생성 시에 사용
     */

    public OnCreateBucketStateListener onCreateBucketStateListener;

    public interface OnCreateBucketStateListener { 
        abstract void onCompleted(B2CreateBucketResponse b2CreateBucketResponse);
        abstract void onFailed(int status, String code, String message);
    }

    public BackBlazeB2 setOnCreateBucketStateListener(OnCreateBucketStateListener onCreateBucketStateListener){
        this.onCreateBucketStateListener = onCreateBucketStateListener;
        return this;
    }

    public void createBucket(B2AuthResponse b2AuthResponse, String bucketName, BucketType bucketType) {
        
        B2CreateBucket b2CreateBucket = new B2CreateBucket();

        if(onCreateBucketStateListener != null) {
            b2CreateBucket.setOnCreateBucketStateListener(new B2CreateBucket.OnCreateBucketStateListener() {
            
                @Override
                public void onSuccess(B2CreateBucketResponse b2CreateBucketResponse) {
                    onCreateBucketStateListener.onCompleted(b2CreateBucketResponse);
                }
    
                @Override
                public void onFailed(int status, String code, String message) {
                    onCreateBucketStateListener.onFailed(status, code, message);
                }
            } );
        }

        b2CreateBucket.startCreatingBucket(b2AuthResponse, bucketName, bucketType);

    }

    /* 
     * 업로드 URL 가져올때 사용
     */

    public OnGetUploadUrlStateListener onGetUploadUrlStateListener;

    public interface OnGetUploadUrlStateListener { 
        abstract void onCompleted(B2GetUploadUrlResponse b2GetUploadUrlResponse);
        abstract void onFailed(int status, String code, String message);
    }

    public BackBlazeB2 setOnGetUploadUrlStateListener(OnGetUploadUrlStateListener onGetUploadUrlStateListener){
        this.onGetUploadUrlStateListener = onGetUploadUrlStateListener;
        return this;
    }

    public void getUploadUrl(B2AuthResponse b2AuthResponse, B2CreateBucketResponse b2CreateBucketResponse) {
        
        B2GetUploadUrl b2GetUploadUrl = new B2GetUploadUrl();

        if(onGetUploadUrlStateListener != null) {
            b2GetUploadUrl.setOnGetUploadUrlStateListener(new B2GetUploadUrl.OnGetUploadUrlStateListener() {
                @Override
                public void onSuccess(B2GetUploadUrlResponse b2GetUploadUrlResponse) {
                    onGetUploadUrlStateListener.onCompleted(b2GetUploadUrlResponse);
                }
    
                @Override
                public void onFailed(int status, String code, String message) {
                    onGetUploadUrlStateListener.onFailed(status, code, message);
                }
            });
            
        }

        b2GetUploadUrl.startGettingUploadUrl(b2AuthResponse, b2CreateBucketResponse);

    }


    /* 
     * 하나의(단일) 파일 업로드 시 사용
     */

    public OnUploadSingleFileStateListener onUploadSingleFileStateListener;

    public interface OnUploadSingleFileStateListener { 
        abstract void onStarted();
        abstract void onProgress(int percentage, long progress, long total);
        abstract void onCompleted(B2UploadFileResponse response, boolean allFilesUploaded);
        abstract void onFailed(int status, String code, String message);
    }

    public BackBlazeB2 setOnUploadSingleFileStateListener(OnUploadSingleFileStateListener onUploadSingleFileStateListener){
        this.onUploadSingleFileStateListener = onUploadSingleFileStateListener;
        return this;
    }

    public void uploadSingleFile(File file, String b2FileName, B2GetUploadUrlResponse b2GetUploadUrlResponse) {
        
        B2SingleUpload b2SingleUpload = new B2SingleUpload(b2GetUploadUrlResponse);

        if(onUploadSingleFileStateListener != null) {
            b2SingleUpload.setOnUploadingListener(new UploadListener() {
                @Override
                public void onUploadStarted() {
                    onUploadSingleFileStateListener.onStarted();
                }
          
                @Override
                public void onUploadProgress(int percentage, long progress, long total) {
                    onUploadSingleFileStateListener.onProgress(percentage, progress, total);
                }
          
                @Override
                public void onUploadFinished(B2UploadFileResponse response, boolean allFilesUploaded) {
                    onUploadSingleFileStateListener.onCompleted(response, allFilesUploaded);
                }
          
                @Override
                public void onUploadFailed(int status, String code, String message) {
                    onUploadSingleFileStateListener.onFailed(status, code, message);
                }
            }); 
        }

        b2SingleUpload.startUploading(file, b2FileName);

    }

    /* @remove
     * 하나의(단일) 파일 삭제 시 사용
     */

     public OnDeleteSingleFileStateListener onDeleteSingleFileStateListener;

     public interface OnDeleteSingleFileStateListener { 
         abstract void onSuccess();
         abstract void onFailed(int status, String code, String message);
     }
 
     public BackBlazeB2 setOnDeleteSingleFileListener(OnDeleteSingleFileStateListener onDeleteSingleFileStateListener){
         this.onDeleteSingleFileStateListener = onDeleteSingleFileStateListener;
         return this;
     }
 
     public void deleteSingleFile(B2AuthResponse b2AuthResponse, String fileName, String fileId) {
         
         B2DeleteSingleFile b2DeleteSingleFile = new B2DeleteSingleFile();
        
         if(onDeleteSingleFileStateListener != null) {
            b2DeleteSingleFile.setOnDeleteFileStateListener(new B2DeleteSingleFile.OnDeleteFileStateListener() {
                @Override
                public void onSuccess() {
                    onDeleteSingleFileStateListener.onSuccess();
                }

                @Override
                public void onFailed(int status, String code, String message) {
                    onDeleteSingleFileStateListener.onFailed(status, code, message);
                }
             });
            
         }

         b2DeleteSingleFile.startDeletingFile(b2AuthResponse, fileName, fileId);
     }


    /* 
     * 많은 파일을 업로드 시 사용 (이미지 여러장 업로드 시)
     */

    public OnUploadMultipleFileStateListener onUploadMultipleFileStateListener;

    public interface OnUploadMultipleFileStateListener { 
        abstract void onStarted();
        abstract void onProgress(int percentage, long progress, long total);
        abstract void onCompleted(B2UploadFileResponse response, boolean allFilesUploaded);
        abstract void onFailed(int status, String code, String message);
    }

    public BackBlazeB2 setOnUploadMultipleFilesStateListener(OnUploadMultipleFileStateListener onUploadMultipleFileStateListener){
        this.onUploadMultipleFileStateListener = onUploadMultipleFileStateListener;
        return this;
    }

    public void uploadMultipleFiles(ArrayList<MultiFile> multiFilesArrayList, B2GetUploadUrlResponse b2GetUploadUrlResponse) {
        
        B2MultiUpload b2MultiUpload = new B2MultiUpload(b2GetUploadUrlResponse);

        if(b2MultiUpload != null) {
            b2MultiUpload.setOnUploadingListener(new UploadListener() {
                @Override
                public void onUploadStarted() {
                    onUploadMultipleFileStateListener.onStarted();
                }
          
                @Override
                public void onUploadProgress(int percentage, long progress, long total) {
                    onUploadMultipleFileStateListener.onProgress(percentage, progress, total);
                }
          
                @Override
                public void onUploadFinished(B2UploadFileResponse response, boolean allFilesUploaded) {
                    onUploadMultipleFileStateListener.onCompleted(response, allFilesUploaded);
                }
          
                @Override
                public void onUploadFailed(int status, String code, String message) {
                    onUploadMultipleFileStateListener.onFailed(status, code, message);
                }
            });
        }


        b2MultiUpload.startUploadingMultipleFiles(multiFilesArrayList);
    }

    /* 
     * 많은 파일 삭제 시 사용
     */

     public OnDeleteMultipleFileStateListener onDeleteMultipleFileStateListener;

     public interface OnDeleteMultipleFileStateListener { 
         abstract void onFinish();
         abstract void onSuccess(int nThItem);
         abstract void onFailed(int status, String code, String message, int nThItem);
     }
 
     public BackBlazeB2 setOnDeleteMultipleFileListener(OnDeleteMultipleFileStateListener onDeleteMultipleFileStateListener){
        this.onDeleteMultipleFileStateListener = onDeleteMultipleFileStateListener;
        return this;
     }
 
     public void deleteMultipleFiles(B2AuthResponse b2AuthResponse, ArrayList<DeleteFileItem> filesToDelete) {
         
         B2DeleteMultipleFile b2DeleteMultipleFile = new B2DeleteMultipleFile();
        
         if(onDeleteMultipleFileStateListener != null) {
            
            b2DeleteMultipleFile.setOnDeleteMultipleFilesStateListener(new B2DeleteMultipleFile.OnDeleteMultipleFileStateListener() {
                @Override
                public void onSuccess(int nThItem) {
                    onDeleteMultipleFileStateListener.onSuccess(nThItem);
                }

                @Override
                public void onFailed(int status, String code, String message, int nThItem) {
                    onDeleteMultipleFileStateListener.onFailed(status, code, message, nThItem);
                }

                @Override
                public void onFinish() {
                    onDeleteMultipleFileStateListener.onFinish();
                }
             });
         }

         b2DeleteMultipleFile.setFilesToDelete(filesToDelete);
         b2DeleteMultipleFile.startDeletingFiles(b2AuthResponse);
     }


    
}
