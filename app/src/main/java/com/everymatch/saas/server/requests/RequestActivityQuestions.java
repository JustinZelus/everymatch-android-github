package com.everymatch.saas.server.requests;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.responses.ResponseActivityQuestions;

public class RequestActivityQuestions extends BaseRequest {
    // doc at: https://everymatch.atlassian.net/wiki/display/DOC/Activity+Questions+api

    String client_id;

    public RequestActivityQuestions(String client_id) {
        this.client_id = client_id;
    }

    @Override
    public String getServiceUrl() {
        return Constants.API_SERVICE_URL;
    }

    @Override
    public String getUrlFunction() {
        return "api/activityquestions?hl=" + DataStore.getInstance().getCulture() +
                "&app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id) +
                "&client_id=" + client_id;
    }

    @Override
    public Class getResponseClass() {
        return ResponseActivityQuestions.class;
    }

    @Override
    public int getType() {
        return Request.Method.GET;
    }
}
