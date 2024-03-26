package b2.BackBlaze2.models;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Response;

import java.io.*;

public interface Callback {
 
    void onFailure(Call call, IOException e);
 
    void onResponse(Call call, Response response) throws IOException;
}

