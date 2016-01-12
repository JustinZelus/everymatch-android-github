package com.everymatch.saas.server.requests;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.Data.DataEventActions;
import com.everymatch.saas.server.responses.ResponseEvent;
import com.everymatch.saas.singeltones.Preferences;
import com.everymatch.saas.util.EMLog;
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

    public String other_user_id;
    public String object_id;
    public String action;
    /* added value for request */
    public String data;
    public TYPE type;
    private DataEventActions dataEventActions;


    public RequestEventActions(DataEventActions dataEventActions) {
        //this(null, null, null, null);
        this.dataEventActions = dataEventActions;
    }

    /*
        public RequestEventActions(String otherUserId, String eventsId, String action) {
            this(otherUserId, eventsId, action, null);
        }

        public RequestEventActions(String otherUserId, String eventsId, String action, String data) {
            if (otherUserId != null) {
                this.other_user_id = otherUserId;
            }
            this.object_id = eventsId;
            this.action = action;
            this.data = data;
            type = getTypeFromAction();
        }
    */
    private TYPE getTypeFromAction() {
        if (action.equals("accept_invitation"))
            return TYPE.Action_accept_invitation;
        if (action.equals("accept_request"))
            return TYPE.Action_accept_request;
        if (action.equals("cancel_event"))
            return TYPE.Action_cancel_event;
        if (action.equals("leave"))
            return TYPE.Action_leave;
        if (action.equals("maybe"))
            return TYPE.Action_maybe;
        if (action.equals("reject"))
            return TYPE.Action_reject;
        if (action.equals("join"))
            return TYPE.Action_join;
        if (action.equals("duplicate"))
            return TYPE.Action_duplicate;
        if (action.equals("save"))
            return TYPE.Action_save;
        if (action.equals("unsave"))
            return TYPE.Action_unsave;
        if (action.equals("cancel_request"))
            return TYPE.Action_cancel_event;
        if (action.equals("remove"))
            return TYPE.Action_remove;
        if (action.equals("remove_maybe"))
            return TYPE.Action_remove_maybe;
        if (action.equals("cancel_invitation"))
            return TYPE.Action_cancel_invitation;
        if (action.equals("add_participant"))
            return TYPE.Action_add_participant;
        if (action.equals("invite"))
            return TYPE.Action_invite;

        EMLog.e(Tag, "getTypeFromAction-> no Type for action: " + action);
        //default
        return TYPE.Action_accept_invitation;
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
        return "api/" + dataEventActions.api_access + "?app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id)
                + "&hl=" + DataStore.getInstance().getCulture();
    }

    @Override
    public String getEncodedBody() {
        // { "other_user_id": "<other_user_id>" , "object_id": "<object_id>" , "action": "reject" }

        //return dataEventActions.parameters;
        String j = new Gson().toJson(dataEventActions.parameters);
        return j;
        /*try {
            JSONObject output = new JSONObject();
            if (action.equals("save") || action.equals("unsave")) {
                output.put("collection_name", "events");
            } else if (action.equals("invite") || action.equals("add_participant")) {
                output.put("other_user_id", data);
                output.put("invitation_note", "");
                output.put("action", "add_participant");
            } else {
                output.put("other_user_id", other_user_id);
                output.put("action", action);
            }

            output.put("object_id", object_id);
            String outStr = output.toString();

            return outStr;
        } catch (Exception ex) {
            return super.getEncodedBody();
        }*/
    }

    @Override
    public Class getResponseClass() {
        return ResponseEvent.class;
    }

    @Override
    public int getType() {

        switch (dataEventActions.http_method.toLowerCase()) {
            //case Action_Reject_Join_Event:
            //case Action_save:
            //case Action_join:
            //return Request.Method.POST;
            case "get":
                return Request.Method.GET;
            case "delete":
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
