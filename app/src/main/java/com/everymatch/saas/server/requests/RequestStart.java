package com.everymatch.saas.server.requests;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.responses.ResponseStart;

public class RequestStart extends BaseRequest {

    @Override
    public String getServiceUrl() {
        return Constants.API_SERVICE_URL;
    }

    @Override
    public String getUrlFunction() {
        return "api/Components/start?hl=" + DataStore.getInstance().getCulture() +
                "&app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id);
        //10000000000000"; //20150518212134
    }

    @Override
    public Class getResponseClass() {
        return ResponseStart.class;
    }

    @Override
    public int getType() {
        return Request.Method.GET;
    }
}
