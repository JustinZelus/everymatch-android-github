package com.everymatch.saas.server.requests;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.responses.ResponseSettings;

public class RequestSettings extends BaseRequest {

    @Override
    public String getServiceUrl() {
        return Constants.API_SERVICE_URL;
    }

    @Override
    public String getUrlFunction() {
        return "api/settings?hl=" + DataStore.getInstance().getCulture() +
                "&app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id);
    }

    @Override
    public Class getResponseClass() {
        return ResponseSettings.class;
    }

    @Override
    public int getType() {
        return Request.Method.GET;
    }
}
