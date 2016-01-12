package com.everymatch.saas.server.requests;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.responses.ResponseDataModel;

/**
 * Created by PopApp_laptop on 15/11/2015.
 */
public class RequestDataModel extends BaseRequest {

    @Override
    public String getServiceUrl() {
        return Constants.API_SERVICE_URL;
    }

    @Override
    public String getUrlFunction() {

        return "api/components?app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id) +
                "&hl=" + DataStore.getInstance().getCulture() +
                "&name=content";
        // "https://api.everymatch.me/api/components?hl=en-US&app_id=20150208103953&name=content";
    }

    @Override
    public Class getResponseClass() {
        return ResponseDataModel.class;
    }

    @Override
    public int getType() {
        return Request.Method.GET;
    }
}
