package com.everymatch.saas.server.responses;

import com.everymatch.saas.server.Data.DataPeople;

import java.util.ArrayList;

/**
 * Created by dors on 11/17/15.
 */
public class ResponseInvitees extends BaseResponse {
    public int count;
    private ArrayList<DataPeople> users;

    public ArrayList<DataPeople> getUsers() {

        if (users == null) {
            users = new ArrayList<>();
        }

        return users;
    }
}
