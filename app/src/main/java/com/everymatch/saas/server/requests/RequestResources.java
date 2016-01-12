package com.everymatch.saas.server.requests;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.responses.ResponseResources;
import com.everymatch.saas.server.serialization.ResourcesDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Created by dors on 8/2/15.
 */
public class RequestResources extends BaseRequest {
    @Override
    public String getServiceUrl() {
        return Constants.API_SERVICE_URL;
    }

    @Override
    public String getUrlFunction() {
        return "api/resources?hl=" + DataStore.getInstance().getCulture() +
                "&app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id);
    }

    @Override
    public Class getResponseClass() {
        return ResponseResources.class;
    }

    @Override
    public int getType() {
        return Request.Method.GET;
    }

    @Override
    public Gson getGsonDeserializer() {
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(ResponseResources.class, new ResourcesDeserializer());
        return gsonBuilder.create();
    }
}
