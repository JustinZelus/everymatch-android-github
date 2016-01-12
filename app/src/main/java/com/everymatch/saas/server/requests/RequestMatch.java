package com.everymatch.saas.server.requests;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.singeltones.Preferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PopApp_laptop on 20/12/2015.
 */
public class RequestMatch extends BaseRequest {

    private String action;
    private String object_id;
    private String activity_client_id;


    public RequestMatch(String action, String object_id, String activity_client_id) {
        this.action = action;
        this.object_id = object_id;
        this.activity_client_id = activity_client_id;
    }

    @Override
    public boolean parseResponseAsJson() {
        return false;
    }
/*
    * https://api.everymatch.me/api/match?
    * hl=en-US
    * &app_id=20150208103953
    * &activity_client_id=100001
    * &action=user_to_event
    * &object_id=563ee7f14e49702c90029c2a
    * &_=1450600001672
    * */

    @Override
    public String getServiceUrl() {
        return Constants.API_SERVICE_URL;
    }

    @Override
    public String getUrlFunction() {
        return "api/match?app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id)
                + "&hl=" + DataStore.getInstance().getCulture()
                + "&activity_client_id=" + activity_client_id
                + "&action=" + action
                + "&object_id=" + object_id
                ;
    }

    @Override
    public Class getResponseClass() {
        return null;
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
