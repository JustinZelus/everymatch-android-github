package com.everymatch.saas.server.requests;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.responses.ResponseString;
import com.everymatch.saas.singeltones.Preferences;

import java.util.HashMap;
import java.util.Map;

public class RequestSaveUploadedImage extends BaseRequest {

    private String content;

    public RequestSaveUploadedImage(String crop) {
        content = crop;
    }

    @Override
    public String getServiceUrl() {
        return Constants.getAPI_SERVICE_URL();
    }

    @Override
    public String getUrlFunction() {
        return "API/users/UploadImage?hl=" +
                DataStore.getInstance().getCulture() +
                "&app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id);
    }

    @Override
    public Class getResponseClass() {
        return ResponseString.class;
    }

    @Override
    public String getEncodedBody() {
        return content;
    }

    @Override
    public int getType() {
        return Request.Method.PUT;
    }

    @Override
    public int getConnectionTimeout() {
        return 1000 * 40;
    }

    @Override
    public int getNumberOfRetries() {
        return super.getNumberOfRetries();
    }

    @Override
    public String getBodyContentType() {
        return GsonRequest.CONTENT_TYPE_X_URL_ENCODED;
    }

    @Override
    public Map<String, String> addExtraHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + Preferences.getInstance().getTokenType());
        return headers;
    }

    public boolean parseResponseAsJson() {
        return false;
    }
}
