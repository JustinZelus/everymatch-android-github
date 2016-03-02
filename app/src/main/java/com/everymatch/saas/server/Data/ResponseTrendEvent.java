package com.everymatch.saas.server.Data;

import com.everymatch.saas.server.responses.BaseResponse;

import java.util.ArrayList;

/**
 * Created by PopApp_laptop on 01/03/2016.
 */
public class ResponseTrendEvent extends BaseResponse {
    public ArrayList<DataEvent> events;
    public int count;

    public ArrayList<DataEvent> getEvents() {
        if (events == null)
            events = new ArrayList<>();
        return events;
    }

}
