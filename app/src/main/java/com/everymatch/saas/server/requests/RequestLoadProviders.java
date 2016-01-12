package com.everymatch.saas.server.requests;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.responses.ResponseLoadProviders;
import com.everymatch.saas.singeltones.Preferences;

import java.util.HashMap;
import java.util.Map;

public class RequestLoadProviders extends BaseRequest {

    private boolean withToken = false;

    public RequestLoadProviders(boolean withToken) {
        this.withToken = withToken;
    }
    public RequestLoadProviders( ) {

    }

    @Override
    public String getServiceUrl() {
        return Constants.AUTH2_SERVICE_URL;
    }

    @Override
    public String getUrlFunction() {
        return "api/providers?hl=" + DataStore.getInstance().getCulture() +
                "&app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id);
    }

    @Override
    public Class getResponseClass() {
        return ResponseLoadProviders.class;
    }

    @Override
    public int getType() {
        return Request.Method.GET;
    }

    @Override
    public Map<String, String> addExtraHeaders() {
        if(withToken) {
            Map<String, String> headers = new HashMap<String, String>();
            headers.put("Authorization", "Bearer " + Preferences.getInstance().getTokenType());
            return headers;
        }
        return super.addExtraHeaders();
    }
}
