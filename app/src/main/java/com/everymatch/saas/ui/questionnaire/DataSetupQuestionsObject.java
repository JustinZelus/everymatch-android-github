package com.everymatch.saas.ui.questionnaire;

import android.text.TextUtils;

import com.everymatch.saas.client.data.JoinType;
import com.everymatch.saas.util.EMLog;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by PopApp_laptop on 18/10/2015.
 * <p/>
 * this class hold the entity object that will be sent to server on event's creation/update
 */

public class DataSetupQuestionsObject {
    public int numberOfSpots = -2;
    public String joinType = JoinType.FREE;
    public String privacy = "public";
    public boolean isParticipating = true;
    /* the invited users */
    public String participants;

    public DataSetupQuestionsObject() {
        this.numberOfSpots = -2;
    }

    public JSONObject getEntityObject() {
        JSONObject entity = new JSONObject();
        try {
            entity.put("status", "draft");
            entity.put("is_participating", isParticipating);
            entity.put("spots", numberOfSpots == -2 ? -1 : numberOfSpots);
            entity.put("join_type", joinType);
            entity.put("privacy_settings", new JSONObject().put("type", privacy));
            entity.put("display_settings", new JSONObject().put("type", "public"));


            /*create invited users jsonObject */
            if (!TextUtils.isEmpty(participants)) {
                JSONArray users = new JSONArray();
                String[] invitees = participants.split(",");

                for (int i = 0; i < invitees.length; ++i) {
                    users.put(i, new JSONObject().put("users_id", invitees[i].trim()));
                }

                entity.put("users", users);
            }

        } catch (Exception ex) {
            EMLog.e(getClass().getName(), ex.getMessage());
        }
        return entity;
    }

}
