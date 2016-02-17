package com.everymatch.saas.server.Data;

import com.everymatch.saas.server.responses.BaseResponse;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by PopApp_laptop on 10/08/2015.
 */
public class DataConversation extends BaseResponse implements Serializable {
    public String _id;
    public ArrayList<DataChannelName> channel_names;
    //public String status;
    public ArrayList<DataParticipant> participants;

    public ArrayList<DataChatMessage> getMessages() {
        if (messages == null)
            messages = new ArrayList<>();
        return messages;
    }

    //private ArrayList<DataChatBlock> messages;
    private ArrayList<DataChatMessage> messages;

    public DataChatMessage getLast_message() {
        if (last_message == null)
            last_message = new DataChatMessage();
        return last_message;
    }

    private DataChatMessage last_message;
    public DataDate updated_date;
    public String created_by;

    public String getConversation_title() {
        if (conversation_title == null)
            conversation_title = "";
        return conversation_title;
    }

    private String conversation_title;
    public String updated_by;

}
