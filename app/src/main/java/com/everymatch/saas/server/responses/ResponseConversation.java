package com.everymatch.saas.server.responses;

import com.everymatch.saas.server.Data.DataChannelName;
import com.everymatch.saas.server.Data.DataDate;
import com.everymatch.saas.server.Data.DataChatBlock;
import com.everymatch.saas.server.Data.DataParticipant;

/**
 * Created by PopApp_laptop on 10/08/2015.
 */
public class ResponseConversation extends BaseResponse {
    public String _id;
    public DataChannelName[] channel_names;
    public DataChatBlock[] messages;
    public DataDate updated_date;
    public String created_by;
    public DataParticipant[] participants;
    public String updated_by;
    public String status;
}
