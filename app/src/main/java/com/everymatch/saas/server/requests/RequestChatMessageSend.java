package com.everymatch.saas.server.requests;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.responses.ResponseConversation;
import com.everymatch.saas.singeltones.Preferences;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PopApp_laptop on 12/08/2015.
 */
public class RequestChatMessageSend extends BaseRequest {
    String id;
    String message;
    boolean isFirstCall;

    public RequestChatMessageSend(String id, String message, boolean isFirstCall) {
        this.id = id;
        this.message = message;
        this.isFirstCall = isFirstCall;
    }

    @Override
    public boolean parseResponseAsJson() {
        return false;
        //return super.parseResponseAsJson();
    }

    @Override
    public String getEncodedBody() {
        Map m = new HashMap<>();
        m.put("message", message);
        if (isFirstCall)
            m.put("channel_name", id);
        else {
            m.put("id", id.replace("users_", ""));
        }
        JSONObject obj = new JSONObject(m);
        String str = obj.toString();
        return str;
    }

    @Override
    public String getServiceUrl() {
        return Constants.getAPI_SERVICE_URL();
    }

    @Override
    public String getUrlFunction() {
        if (isFirstCall)
            return "api/conversations?app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id) + "&hl=" + DataStore.getInstance().getCulture();

        return "api/messages?app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id) + "&hl=" + DataStore.getInstance().getCulture();
    }

    @Override
    public Class getResponseClass() {
        /*not really matter*/
        return ResponseConversation.class;
    }

    @Override
    public int getType() {
        return Request.Method.POST;
    }

    @Override
    public String getBodyContentType() {
        return GsonRequest.CONTENT_TYPE_X_URL_ENCODED;
    }

    @Override
    public Map<String, String> addExtraHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        String token = Preferences.getInstance().getTokenType();
        headers.put("Authorization", "Bearer " + token);
        return headers;
    }
}
