package com.everymatch.saas.server.responses;

import com.everymatch.saas.server.Data.DataEventHolder;
import com.everymatch.saas.server.Data.DataPeopleHolder;

public class ResponseDiscover extends BaseResponse {

    private DataEventHolder events;
    private DataPeopleHolder users;

    public DataEventHolder getEventHolder() {
        if (events == null){
            events = new DataEventHolder();
        }
        return events;
    }

    public DataPeopleHolder getPeopleHolder() {
        if (users == null)
            users = new DataPeopleHolder();
        return users;
    }
}
