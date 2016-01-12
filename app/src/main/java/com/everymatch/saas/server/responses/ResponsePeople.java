package com.everymatch.saas.server.responses;

import com.everymatch.saas.server.Data.DataPeople;

import java.util.ArrayList;

/**
 * Created by Dacid on 29/06/2015.
 */
public class ResponsePeople extends BaseResponse {

    public ArrayList<DataPeople> users;
    public int count;
}
