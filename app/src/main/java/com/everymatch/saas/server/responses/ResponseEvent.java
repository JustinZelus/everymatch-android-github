package com.everymatch.saas.server.responses;

import com.everymatch.saas.server.Data.DataDisplaySettings;
import com.everymatch.saas.server.Data.DataEventActions;
import com.everymatch.saas.server.Data.DataPrivacySettings;
import com.everymatch.saas.server.Data.DataProfile;
import com.everymatch.saas.server.Data.DataPublicEvent;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Dacid on 29/06/2015.
 */
public class ResponseEvent extends BaseResponse implements Serializable {

    public String _id;
    @SerializedName("public")
    DataPublicEvent dataPublicEvent;
    DataProfile profile;
    ArrayList<DataEventActions> event_actions;
    public String activity_client_id;
    public String client_id;

    public String role_name;
    public String owner_user_id;
    public DataDisplaySettings display_settings;
    public DataPrivacySettings privacy_settings;

    public String get_id() {
        return _id;
    }

    public DataPublicEvent getDataPublicEvent() {
        return dataPublicEvent;
    }

    public DataProfile getProfile() {
        return profile;
    }

    public ArrayList<DataEventActions>  getEvent_actions() {
        return event_actions;
    }
}
