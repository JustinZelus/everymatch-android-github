package com.everymatch.saas.server.Data;

import java.io.Serializable;

/**
 * Created by PopApp_laptop on 13/10/2015.
 */
public class DataEvent_Activity implements Serializable {
    public String event_id;
    public String text_title;
    public DataIcon icon;
    public String status;
    public String text_description;
    public DataQuestion questions[];
    DataSpots spots;
    public String client_id;
}
