package com.everymatch.saas.server.Data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by PopApp_laptop on 11/08/2015.
 */
public class DataChatMessage implements Serializable {
    public String id;
    public String sender;
    public String message;

    public DataDate getUpdated_date() {
        return updated_date;
    }

    private DataDate updated_date;


    public ArrayList<DataReadBy> read_by;
    public String status;
    public String first_name;
    public String last_name;
    public String conversation_id;

    public ArrayList<DataReadBy> getRead_by() {
        if (read_by == null)
            read_by = new ArrayList<>();
        return read_by;
    }
}
