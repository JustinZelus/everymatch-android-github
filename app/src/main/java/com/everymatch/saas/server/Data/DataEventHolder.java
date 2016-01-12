package com.everymatch.saas.server.Data;

import java.io.Serializable;
import java.util.ArrayList;

public class DataEventHolder implements Serializable{
    public int count;
    public String filter;
    public String text_title;
    public DataIcon icon;

    private ArrayList<DataEvent> events;

    public DataEventHolder() {
        events = new ArrayList<>();
    }

    public DataEventHolder(ArrayList<DataEvent> events) {
        this.events = events;
    }

    public ArrayList<DataEvent> getEvents() {
        if (events == null) {
            events = new ArrayList<>();
        }

        return events;
    }

    public void addEvent(DataEvent dataEvent){
        getEvents().add(dataEvent);
        count++;
    }

}