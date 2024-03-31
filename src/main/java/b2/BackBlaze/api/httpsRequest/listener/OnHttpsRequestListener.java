package b2.BackBlaze.api.httpsRequest.listener;

import org.json.JSONObject;

public interface OnHttpsRequestListener { 
    abstract void onSuccess(JSONObject requestResult);
    abstract void onFailed(JSONObject requestResult);
}
