package com.everymatch.saas.server.requests;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.Data.DataEventActions;
import com.everymatch.saas.server.responses.ResponseEvent;
import com.everymatch.saas.singeltones.Preferences;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class RequestEventActions extends BaseRequest {
    public static final String Tag = "RequestEventActions";

    public enum TYPE {
        Action_Reject_Join_Event,
        Action_accept_invitation,
        Action_accept_request,
        Action_cancel_event,
        Action_leave,
        Action_maybe,
        Action_reject,
        Action_join,
        Action_duplicate,
        Action_save,
        Action_unsave,
        Action_cancel_request,
        Action_remove,
        Action_remove_maybe,
        Action_cancel_invitation,
        Action_add_participant,
        Action_invite,
    }

    public String action;
    /* added value for request */
    public String data;
    public TYPE type;
    private DataEventActions dataEventActions;

    public RequestEventActions(DataEventActions dataEventActions) {
        this.dataEventActions = dataEventActions;
    }

    @Override
    public String getServiceUrl() {
        return Constants.API_SERVICE_URL;
    }

    @Override
    public String getUrlFunction() {
        /*if (action.equals("save") || action.equals("unsave"))
            return "api/watchlater?app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id)
                    + "&hl=" + DataStore.getInstance().getCulture() +
                    "&collection_name=events" +
                    "&object_id=" + object_id;
        */
       /* if (action.equals("join")) {
            return "api/eventactions/join?app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id)
                   + "&hl=" + DataStore.getInstance().getCulture();
        }*/
        /*if (action.equals("add_participant") || action.equals("invite"))
            return "api/eventactions?app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id)
                    + "&hl=" + DataStore.getInstance().getCulture();
        //+ "&object_id=" + object_id
        //+ "&participant_id=" + data.trim()
        //+"&invitation_note=";
        */
        if (dataEventActions.action.equals("unsave")) {

        }
        return "api/" + dataEventActions.api_access + "?app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id)
                + "&hl=" + DataStore.getInstance().getCulture();
    }

    @Override
    public String getEncodedBody() {
        String j = new Gson().toJson(dataEventActions.parameters);
        return j;
    }

    @Override
    public Class getResponseClass() {
        return ResponseEvent.class;
    }

    @Override
    public int getType() {

        switch (dataEventActions.http_method.toLowerCase()) {
            case "get":
                return Request.Method.GET;
            case "delete":
                if (dataEventActions.action.equals("unsave")) {
                    return Request.Method.PUT;
                }
                return Request.Method.DELETE;
            case "post":
                return Request.Method.POST;
            default:
                return Request.Method.PUT;
        }
    }

    @Override
    public Map<String, String> addExtraHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + Preferences.getInstance().getTokenType());
        return headers;
    }

    @Override
    public String getBodyContentType() {
        return GsonRequest.CONTENT_TYPE_X_URL_ENCODED;
    }
}
