package com.everymatch.saas.server.Data;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by PopApp_laptop on 09/08/2015.
 */
public class DataChatBlock implements Serializable {
    public String _id;
    public ArrayList<DataChannelName> channel_names;
    public ArrayList<DataChatMessage> messages;
    public DataDate updated_date;
    public String created_by;
    public ArrayList<DataParticipant> participants;
    public String updated_by;
    public String status;

    public DataChatBlock() {
        channel_names = new ArrayList<DataChannelName>();
        messages = new ArrayList<DataChatMessage>();
        participants = new ArrayList<DataParticipant>();
    }

    public DataChatBlock(JSONObject obj) {
        this();

        try {
            if (obj.has("_id"))
                _id = obj.getString("_id");
            if (obj.has("created_by"))
                created_by = obj.getString("created_by");
            if (obj.has("status"))
                status = obj.getString("status");
            if (obj.has("updated_by"))
                updated_by = obj.getString("updated_by");
            if (obj.has("channel_names"))
                channel_names = (ArrayList<DataChannelName>) new Gson().fromJson(obj.getJSONArray("channel_names").toString(),new TypeToken<List<DataChannelName>>(){}.getType());

            if (obj.has("participants"))
                participants = (ArrayList<DataParticipant>) new Gson().fromJson(obj.getJSONArray("participants").toString(), new TypeToken<List<DataParticipant>>(){}.getType());

            if (obj.has("updated_date"))
                updated_date = new Gson().fromJson(obj.getJSONObject("updated_date").toString(),new TypeToken<DataDate>(){}.getType());

            if (obj.has("messages"))
                messages = (ArrayList<DataChatMessage>) new Gson().fromJson(obj.getJSONArray("messages").toString(), new TypeToken<List<DataChatMessage>>(){}.getType());

        } catch (Exception ex) {
        }


    }

    public void appendMessages() {

    }
}
