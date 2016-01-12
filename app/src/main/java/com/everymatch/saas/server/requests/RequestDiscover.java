package com.everymatch.saas.server.requests;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.responses.ResponseDiscover;
import com.everymatch.saas.singeltones.Preferences;

import java.util.HashMap;
import java.util.Map;

public class RequestDiscover extends BaseRequest {

    private final String activityID;

    // For pagination
    private transient boolean paginationRequest;
    private transient String type;
    private transient int start;
    private transient int count;

    @Override
    public String getServiceUrl() {
        return Constants.API_SERVICE_URL;
    }

    public RequestDiscover(String activityId) {
        this.activityID = activityId;
    }

    public RequestDiscover(String activityId, String type, int start, int count) {
        this.activityID = activityId;
        this.type = type;
        this.start = start;
        this.count = count;
        paginationRequest = true;
    }

    @Override
    public String getUrlFunction() {
        StringBuilder request = new StringBuilder();
        request.append("api/discover?hl=" + DataStore.getInstance().getCulture() +
                "&app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id) + "&client_id=" + activityID);

        if (paginationRequest){
            request.append("&type=" + type + "&start=" + start + "&count=" + count);
        }

        return request.toString();
    }

    @Override
    public Class getResponseClass() {
        return ResponseDiscover.class;
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
