package com.everymatch.saas.server.requests;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.responses.ResponseEvents;
import com.everymatch.saas.singeltones.Preferences;

import java.util.HashMap;
import java.util.Map;

public class RequestEvents extends BaseRequest {

    private transient String filter;
    private transient int start;
    private transient int count;
    private transient String sort_by = "";
    private transient String search_phrase = "";
    private transient String activity_client_id = "";
    private transient String status = "";

    public RequestEvents(String filter, int start, int count) {
        this.filter = filter;
        this.start = start;
        this.count = count;
    }

    @Override
    public String getServiceUrl() {
        return Constants.API_SERVICE_URL;
    }

    @Override
    public String getUrlFunction() {
            return "api/events?hl=" + DataStore.getInstance().getCulture()
                    + "&app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id)+
                    "&filter=" + filter +"&start=" + start + "&count=" + count + "&sort_by=" + sort_by + "&search_phrase=" + search_phrase
                    + "&activity_client_id=" + activity_client_id + "&status=" + status;
    }

    @Override
    public Class getResponseClass() {
        return ResponseEvents.class;
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
