package com.everymatch.saas.server.requests;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.responses.ResponseUpdateProfile;
import com.everymatch.saas.singeltones.Preferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dors on 11/1/15.
 */
public class RequestUpdateUserProfile extends BaseRequest {

    private String content;

    public RequestUpdateUserProfile(String jsonProfile) {
        this.content = jsonProfile;
    }

    @Override
    public String getServiceUrl() {
        return Constants.getAPI_SERVICE_URL();
    }

    @Override
    public String getUrlFunction() {
        return "api/userprofiles?app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id) +
                "&hl=" + DataStore.getInstance().getCulture();
    }

    @Override
    public Class getResponseClass() {
        return ResponseUpdateProfile.class;
    }

    @Override
    public String getEncodedBody() {
        return content;
    }

    @Override
    public int getType() {
        return Request.Method.PUT;
    }

    @Override
    public Map<String, String> addExtraHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + Preferences.getInstance().getTokenType());
        return headers;
    }

    @Override
    public String getBodyContentType() {
        return GsonRequest.CONTENT_TYPE_X_URL_ENCODED;
    }
}
