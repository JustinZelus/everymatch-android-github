package com.everymatch.saas.server.requests;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.Data.DataProfile;
import com.everymatch.saas.singeltones.Preferences;

import java.util.HashMap;
import java.util.Map;

public class RequestActivityProfile extends BaseRequest {

    private transient String client_id;
    private String content;

    public RequestActivityProfile(String client_id, String content) {
        this.client_id = client_id;
        this.content = content;
    }

    @Override
    public String getServiceUrl() {
        return Constants.API_SERVICE_URL;
    }

    @Override
    public String getUrlFunction() {
        return "api/activityprofiles?hl=" + DataStore.getInstance().getCulture() +
                "&app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id) +
                "&client_id=" + client_id;
    }

    @Override
    public boolean parseResponseAsJson() {
        return false;
    }

    @Override
    public String getEncodedBody() {
        return content;
    }

    @Override
    public Class getResponseClass() {
        return DataProfile.class;
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
        headers.put("Authorization", "Bearer " + Preferences.getInstance().getTokenType());
        return headers;
    }

}
