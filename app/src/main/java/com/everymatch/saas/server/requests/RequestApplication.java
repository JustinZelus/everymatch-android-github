package com.everymatch.saas.server.requests;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.responses.ResponseApplication;
import com.everymatch.saas.singeltones.Preferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PopApp_laptop on 20/09/2015.
 */
public class RequestApplication extends BaseRequest {
    @Override
    public String getServiceUrl() {
        return Constants.getAPI_SERVICE_URL();
    }

    @Override
    public String getUrlFunction() {

        //https://api.everymatch.me/api/applications?hl=en-US&app_id=20150518212134&timestamp=20150920095735&culture_name=en-US&_=1442733821109
        String timeStamp = Preferences.getInstance().getTimestamp();
        if(timeStamp == null) timeStamp = "";
        return "api/applications?hl=" + DataStore.getInstance().getCulture() +
                "&app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id) +
                "&culture_name=en-US"+  // TODO - talk with michel about langs...
                "&timestamp=" + timeStamp;
    }

    @Override
    public Class getResponseClass() {
        return ResponseApplication.class;
    }

    @Override
    public int getType() {
        return Request.Method.GET;
    }

    @Override
    public Map<String, String> addExtraHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + Preferences.getInstance().getTokenType());
        return headers;
    }
}
