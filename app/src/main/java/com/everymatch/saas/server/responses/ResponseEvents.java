package com.everymatch.saas.server.responses;

import com.everymatch.saas.server.Data.DataEvent;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Dacid on 29/06/2015.
 */
public class ResponseEvents extends BaseResponse implements Serializable {
    private ArrayList<DataEvent> events;


    public ArrayList<DataEvent> getEvents() {

        if (events == null) {
            return new ArrayList<>();
        }

        return events;
    }
}
