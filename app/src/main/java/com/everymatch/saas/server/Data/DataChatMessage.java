package com.everymatch.saas.server.Data;

import java.io.Serializable;

/**
 * Created by PopApp_laptop on 11/08/2015.
 */
public class DataChatMessage implements Serializable{
    public String id;
    public String sender;
    public String message;
    public DataDate updated_date;
    public DataReadBy[] read_by;
    public String status;
    public String first_name;
    public String last_name;
    public String conversation_id;
}
