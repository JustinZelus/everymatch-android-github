package com.everymatch.saas.server.Data;

import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.server.requests.RequestConversations;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.server.responses.ResponseConversations;
import com.everymatch.saas.singeltones.GenericCallback;

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
    public String conversation_title;
    public String updated_by;


    public static void getConversationById(String id, final GenericCallback callback) {
        ServerConnector.getInstance().processRequest(new RequestConversations(0, 10, id), new ServerConnector.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                ResponseConversations responseConversations = (ResponseConversations) baseResponse;

                if (responseConversations != null) {
                    callback.onDone(true, responseConversations);
                } else {
                    callback.onDone(false, "responseConversations is null");
                }
            }

            @Override
            public void onFailure(ErrorResponse errorResponse) {
                callback.onDone(false, errorResponse.getServerRawResponse());
            }
        });

    }

    public static void getAllConversations(int from, int to, final GenericCallback callback) {
        ServerConnector.getInstance().processRequest(new RequestConversations(from, to), new ServerConnector.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                ResponseConversations responseConversations = (ResponseConversations) baseResponse;
                if (responseConversations != null)
                    callback.onDone(true, responseConversations);
                else
                    callback.onDone(false, "responseConversations is null");
            }
            @Override
            public void onFailure(ErrorResponse errorResponse) {
                callback.onDone(false, errorResponse.getServerRawResponse());
            }
        });
    }
}
