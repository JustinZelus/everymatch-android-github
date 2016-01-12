package com.everymatch.saas.server.requests;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.responses.ResponseConversation;
import com.everymatch.saas.singeltones.Preferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PopApp_laptop on 10/08/2015.
 */
/*this Request has no response!!! we get the response as strign and parse it in the callback*/
public class RequestChatMessages extends BaseRequest {
    public String id;
    public int start;
    public int count;

    public RequestChatMessages(String id, int start, int count) {
        this.id = id;
        this.start = start;
        this.count = count;
    }

    @Override
    public boolean parseResponseAsJson() {
        return  false;
        //return super.parseResponseAsJson();
    }

    @Override
    public String getServiceUrl() {
        return Constants.API_SERVICE_URL;
    }

    @Override
    public String getUrlFunction() {
        return "api/conversations?app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id) +
                "&hl=" + DataStore.getInstance().getCulture() +
                "&id=" + id +
                "&start=" + start +
                "&count=" + count;

    }

    @Override
    public Class getResponseClass() {
        return ResponseConversation.class;
    }

    @Override
    public int getType() {
        return Request.Method.GET;
    }

    @Override
    public Map<String, String> addExtraHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        String token = Preferences.getInstance().getTokenType();
        headers.put("Authorization", "Bearer " + token);
        return headers;
    }
}
