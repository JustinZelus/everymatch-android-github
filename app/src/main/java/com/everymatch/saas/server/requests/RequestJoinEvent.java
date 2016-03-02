package com.everymatch.saas.server.requests;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.responses.ResponseEvent;
import com.everymatch.saas.singeltones.Preferences;

import java.util.HashMap;
import java.util.Map;

public class RequestJoinEvent extends BaseRequest {

    public String object_id;

    public RequestJoinEvent(String eventsId) {
        this.object_id = eventsId;
    }

    @Override
    public String getServiceUrl() {
        return Constants.getAPI_SERVICE_URL();
    }

    @Override
    public String getUrlFunction() {

        return "api/eventactions/join?app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id)
                + "&hl=" + DataStore.getInstance().getCulture();

    }

    @Override
    public Class getResponseClass() {
        return ResponseEvent.class;
    }

    @Override
    public int getType() {
        return Request.Method.POST;
    }

    @Override
    public Map<String, String> addExtraHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + Preferences.getInstance().getTokenType());
        return headers;
    }

    @Override
    public String getBodyContentType() {
        return GsonRequest.CONTENT_TYPE_X_URL_ENCODED;
    }
}
