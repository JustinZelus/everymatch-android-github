package com.everymatch.saas.server.requests;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.Data.ResponseTrendEvent;
import com.everymatch.saas.singeltones.Preferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PopApp_laptop on 01/03/2016.
 */
public class RequestTrends extends BaseRequest {

    private int start, count;
    private String collectionName;
    private String activityClientId;

    public RequestTrends() {
    }

    public RequestTrends(int start, int count, String collectionName, String activityClientId) {
        this.start = start;
        this.count = count;
        this.collectionName = collectionName;
        this.activityClientId = activityClientId;
    }

    @Override
    public String getServiceUrl() {
        return Constants.getAPI_SERVICE_URL();
    }

    @Override
    public String getUrlFunction() {
        ///api/trends?hl=<hl>&app_id=<app_id>&collection_name=<collection_name>&activity_client_id=<activity_client_id>&start=<start>&count=<count>
        return "api/trends?app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id) +
                "&hl=" + DataStore.getInstance().getCulture() +
                "&collection_name=" + collectionName +
                "&activity_client_id=" + activityClientId +
                "&start=" + start +
                "&count=" + count;

    }

    @Override
    public Class getResponseClass() {
        return ResponseTrendEvent.class;
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
