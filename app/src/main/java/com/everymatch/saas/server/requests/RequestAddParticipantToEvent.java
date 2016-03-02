package com.everymatch.saas.server.requests;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.responses.ResponseEvent;
import com.everymatch.saas.singeltones.Preferences;

import java.util.HashMap;
import java.util.Map;

public class RequestAddParticipantToEvent extends BaseRequest {

    public String mId;
    public String mInvitationNote;
    public String mParticipantUsername;
    public String mParticipantFirstName;
    public String mParticipantLastName;

    public RequestAddParticipantToEvent(String id, String invitationNote, String participantUsername, String participantFirstName, String participantLastName) {
        mId = id;
        mInvitationNote = invitationNote;
        mParticipantUsername = participantUsername;
        mParticipantFirstName = participantFirstName;
        mParticipantLastName = participantLastName;
    }

    @Override
    public String getServiceUrl() {
        return Constants.getAPI_SERVICE_URL();
    }

    @Override
    public String getUrlFunction() {
        return "api/events/addexternalparticipant?app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id) + "&events?id=" + mId
                + "&hl=" + DataStore.getInstance().getCulture() +"&invitation_note=" + mInvitationNote
                + "&participant_username=" + mParticipantUsername + "&participant_first_name=" + mParticipantFirstName + "&participant_last_name=" + mParticipantLastName;
    }

    @Override
    public Class getResponseClass() {
        return ResponseEvent.class;
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
