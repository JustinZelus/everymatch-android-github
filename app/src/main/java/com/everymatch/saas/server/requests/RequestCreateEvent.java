package com.everymatch.saas.server.requests;

import android.util.Log;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.responses.ResponseCreateEvent;
import com.everymatch.saas.singeltones.Preferences;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PopApp_laptop on 19/10/2015.
 */
public class RequestCreateEvent extends BaseRequest {

    public enum REQUEST_TYPE {POST_EVENT, UPDATE_EVENT, PUBLISH_EVENT, SEND_CROP_DATA}

    public REQUEST_TYPE request_type;
    String activity_client_id;
    String event_client_id;
    String eventId;
    String content;

    public RequestCreateEvent(String activity_client_id, String event_client_id, String content, REQUEST_TYPE request_type, String eventId) {
        Log.d(getClass().getName(), "calling Create Event stage: " + request_type);
        this.activity_client_id = activity_client_id;
        this.event_client_id = event_client_id;
        this.content = content;
        this.request_type = request_type;
        this.eventId = eventId;
    }

    @Override
    public String getServiceUrl() {
        return Constants.getAPI_SERVICE_URL();
    }

    @Override
    public String getEncodedBody() {
        return content;
    }

    @Override
    public String getBodyContentType() {
        return GsonRequest.CONTENT_TYPE_X_URL_ENCODED;
    }

    @Override
    public String getUrlFunction() {
        switch (request_type) {
            case POST_EVENT:
                return "api/eventprofiles?app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id)
                        + "&hl=" + DataStore.getInstance().getCulture()
                        + "&activity_client_id=" + activity_client_id
                        + "&event_client_id=" + event_client_id;

            case UPDATE_EVENT:
                return "api/eventprofiles?app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id)
                        + "&hl=" + DataStore.getInstance().getCulture()
                        + "&id=" + eventId
                        + "&activity_client_id=" + activity_client_id
                        + "&event_client_id=" + event_client_id;

            case PUBLISH_EVENT:
                return "api/eventprofiles?app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id)
                        + "&hl=" + DataStore.getInstance().getCulture()
                        + "&id=" + eventId;

            case SEND_CROP_DATA:
                return "api/events?app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id);

        }
        return "";
    }

    @Override
    public Class getResponseClass()/**/ {
        /*not realy metter -> we ask for string*/
        return ResponseCreateEvent.class;
    }

    @Override
    public boolean parseResponseAsJson() {
        return false;
    }

    @Override
    public int getType() {
        switch (request_type) {
            case POST_EVENT:
                return Request.Method.POST;

            case UPDATE_EVENT:
                return Request.Method.PUT;

            case PUBLISH_EVENT:
            case SEND_CROP_DATA:
                return Request.Method.PUT;
        }
        return Request.Method.POST;
    }

    @Override
    public Map<String, String> addExtraHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + Preferences.getInstance().getTokenType());
        return headers;
    }

}
