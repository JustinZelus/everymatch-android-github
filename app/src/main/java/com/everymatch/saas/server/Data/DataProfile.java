package com.everymatch.saas.server.Data;

import java.io.Serializable;

/**
 * Created by PopApp_laptop on 31/08/2015.
 */
public class DataProfile implements Serializable{
    public String _id;
    public String type;
    public DataAnswer answers[];
    public String client_id;
    public DataDate updated_date;
    public String users_id;
    public DataDate created_date;
}
