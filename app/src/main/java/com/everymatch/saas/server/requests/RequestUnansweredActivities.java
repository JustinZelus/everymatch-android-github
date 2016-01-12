package com.everymatch.saas.server.requests;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.responses.ResponseUnansweredActivities;

public class RequestUnansweredActivities extends BaseRequest {

    // doc at: https://everymatch.atlassian.net/wiki/display/DOC/Activities+Api

/*
    parameters:
    search_pahrase	The activity's text_title
    count	How many activities to get
    start	start index of the collection
    hl	host language
    app_id	the identity of the application
    is_new	true (optional for unanswered activities)
*/

    String search_pahrase;
    int count;
    int start;
    boolean is_new;

    public RequestUnansweredActivities(String search_pahrase, int count,
                                       int start) {
        this(search_pahrase, count, start, false);
    }

    public RequestUnansweredActivities(String search_pahrase, int count,
                                       int start, boolean is_new) {
        this.search_pahrase = search_pahrase;
        this.count = count;
        this.start = start;
        this.is_new = is_new;
    }

    @Override
    public String getServiceUrl() {
        return Constants.API_SERVICE_URL;
    }

    @Override
    public String getUrlFunction() {
        return "api/applicationactivities?hl=" + DataStore.getInstance().getCulture() +
                "&app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id) +
                "&is_new=" + is_new +
                "&search_pahrase=" + search_pahrase +
                "&count=" + count +
                "&start=" + start;
    }

    @Override
    public Class getResponseClass() {
        return ResponseUnansweredActivities.class;
    }

    @Override
    public int getType() {
        return Request.Method.GET;
    }
}
