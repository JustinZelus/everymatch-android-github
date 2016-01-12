package com.everymatch.saas.server.requests;

import com.android.volley.Request;
import com.everymatch.saas.Constants;

/**
 * Created by dors on 12/1/15.
 */
public class RequestTNC extends BaseRequest {

    private String relativePath;

    public RequestTNC(String relativePath){
        this.relativePath = relativePath;
    }

    @Override
    public String getServiceUrl() {
        return Constants.API_SERVICE_URL;
    }

    @Override
    public String getUrlFunction() {
        return relativePath;
    }

    @Override
    public Class getResponseClass() {
        return null;
    }

    @Override
    public int getType() {
        return Request.Method.GET;
    }
}
