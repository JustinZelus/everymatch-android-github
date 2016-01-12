package com.everymatch.saas.server.requests;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.Data.DataConversation;
import com.everymatch.saas.server.responses.ResponseConversations;
import com.everymatch.saas.singeltones.Preferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PopApp_laptop on 10/08/2015.
 */
public class RequestConversations extends BaseRequest {
    public int start;
    public int count;
    String id = null;

    public RequestConversations(int start, int count) {
        this.start = start;
        this.count = count;
    }

    public RequestConversations(int start, int count, String id) {
        this(start, count);
        this.id = id;
    }


    @Override
    public String getServiceUrl() {
        return Constants.API_SERVICE_URL;
    }

    @Override
    public String getUrlFunction() {
        if (id == null)
            return "api/conversations?app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id) +
                    "&hl=" + DataStore.getInstance().getCulture() +
                    "&start=" + start +
                    "&count=" + count;
        else
            return "api/conversations?app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id) +
                    "&hl=" + DataStore.getInstance().getCulture() +
                    "&start=" + start +
                    "&count=" + count +
                    "&id=" + id;
    }

    @Override
    public Class getResponseClass() {
        if (id == null)
            return ResponseConversations.class;
        else
            return DataConversation.class;
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
