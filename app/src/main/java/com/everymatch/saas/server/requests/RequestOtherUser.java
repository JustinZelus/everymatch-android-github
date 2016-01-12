package com.everymatch.saas.server.requests;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.responses.ResponseOtherUser;
import com.everymatch.saas.singeltones.Preferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PopApp_laptop on 31/08/2015.
 */
public class RequestOtherUser extends BaseRequest {

    private String otherUserId;


    public RequestOtherUser(String otherUserId) {
        this.otherUserId = otherUserId;
    }


    @Override
    public String getServiceUrl() {
        return Constants.API_SERVICE_URL;
    }

    @Override
    public String getUrlFunction() {

        //api / profiles ? app_id =<app_id > & hl =<hl > & other_user_id =<other_user_id >
        return "/api/profiles?app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id) +
                "&hl=" + DataStore.getInstance().getCulture() +
                "&other_user_id=" + otherUserId;
    }

    @Override
    public Class getResponseClass() {
        return ResponseOtherUser.class;
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

