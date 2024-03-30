package b2.BackBlaze.upload_file.model;

import b2.BackBlaze.upload_file.response.B2UploadFileResponse;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;

import retrofit2.http.POST;
import retrofit2.http.Url;

public interface UploadInterface {
    @POST
    Call<B2UploadFileResponse> uploadFile(@Url String url, @Body RequestBody file, @Header("Authorization") String authorization
            , @Header("X-Bz-Content-Sha1") String sha1, @Header("X-Bz-File-Name") String fileName );

}
