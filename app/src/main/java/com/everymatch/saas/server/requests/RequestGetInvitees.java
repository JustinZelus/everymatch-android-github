package com.everymatch.saas.server.requests;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.responses.ResponseBestMatches;
import com.everymatch.saas.server.responses.ResponseInvitees;
import com.everymatch.saas.singeltones.Preferences;
import com.everymatch.saas.ui.event.InviteParticipantsListFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dors on 11/17/15.
 */
public class RequestGetInvitees extends BaseRequest{

    // For pagination
    private transient String type;
    private transient int start;
    private transient int count;
    private String eventId;

    @Override
    public String getServiceUrl() {
        return Constants.getAPI_SERVICE_URL();
    }

    public RequestGetInvitees(String eventId, String type, int start, int count) {
        this.eventId = eventId;
        this.type = type;
        this.start = start;
        this.count = count;
    }

    @Override
    public String getUrlFunction() {
        StringBuilder request = new StringBuilder();

        if (type.equals(InviteParticipantsListFragment.TYPE_BEST_MATCH)){
            request.append("api/bestmatchevent?hl=" + DataStore.getInstance().getCulture() +
                    "&events_id=" + eventId + "&app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id));
        } else{
            request.append("api/friends?hl=" + DataStore.getInstance().getCulture() +
                    "&collection_name=events&object_id=" + eventId + "&app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id));
        }

        request.append("&start=" + start + "&count=" + count);

        return request.toString();
    }

    @Override
    public Class getResponseClass() {

        if (type.equals(InviteParticipantsListFragment.TYPE_BEST_MATCH)){
            return ResponseBestMatches.class;
        } else{
            return ResponseInvitees.class;
        }
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
