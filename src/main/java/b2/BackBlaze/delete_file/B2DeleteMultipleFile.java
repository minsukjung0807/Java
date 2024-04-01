package b2.BackBlaze.delete_file;

import b2.BackBlaze.delete_file.item.DeleteFileItem;
import b2.BackBlaze.api.httpsRequest.HttpRequest;
import b2.BackBlaze.api.httpsRequest.HttpRequest.OnHttpsRequestListener;
import b2.BackBlaze.authorize_account.response.B2AuthResponse;

import java.util.ArrayList;

import org.json.JSONObject;

public class B2DeleteMultipleFile {

    private ArrayList<DeleteFileItem> filesToDelete;

    public B2DeleteMultipleFile() {
        
        filesToDelete = new ArrayList<>();
    }

    /* 성공 또는 실패시 몇번째의 아이템(nThItem)인지 데이터를 같이 넘겨줌 */
    public interface OnDeleteMultipleFileStateListener {
        void onFinish();
        void onSuccess(int nThItem);
        void onFailed(int status, String code, String message, int nThItem);
    }

    public OnDeleteMultipleFileStateListener onDeleteMultipleFileStateListener;

    public void setOnDeleteMultipleFilesStateListener(OnDeleteMultipleFileStateListener onDeleteMultipleFileStateListener){
        this.onDeleteMultipleFileStateListener = onDeleteMultipleFileStateListener;
    }

    public void setFilesToDelete(ArrayList<DeleteFileItem> filesToDelete) {
        this.filesToDelete = filesToDelete;
    }

    public void startDeletingFiles(B2AuthResponse b2AuthResponse) {
        
        HttpRequest httpRequest = new HttpRequest();

        if(filesToDelete != null && filesToDelete.size() > 0) {

            for (int i = 0; i < filesToDelete.size(); i++) {

                JSONObject parameters = new JSONObject();

                final int j = i;
    
                parameters.put("fileName", filesToDelete.get(j).getFileName());
                parameters.put("fileId", filesToDelete.get(j).getFileId());
    
                    httpRequest.setOnHttpsRequestListener(new OnHttpsRequestListener() {
                            
                        @Override
                        public void onSuccess(JSONObject response) {
                            
                            onDeleteMultipleFileStateListener.onSuccess(j);

                            if(j == filesToDelete.size() - 1) {
                                onDeleteMultipleFileStateListener.onFinish();
                            }
                        }
            
                        @Override
                        public void onFailed(JSONObject response) {
                            onDeleteMultipleFileStateListener.onFailed(response.getInt("status"), response.getString("code"), response.getString("message"), j);
                        }

                        @Override
                        public void onError(Exception e) {
                            onDeleteMultipleFileStateListener.onFailed(0, "ERROR", e.getMessage(), 0);
                        }
                    });
    
                httpRequest.call(b2AuthResponse.getAPIURL() + "/b2api/v3/", "b2_delete_file_version", b2AuthResponse.getAuthToken(), parameters, "POST");
            }
        } else {
            onDeleteMultipleFileStateListener.onFailed(0, "ERROR", "삭제할 파일이 존재하지 않습니다", 0);
        }
       
    }
}
