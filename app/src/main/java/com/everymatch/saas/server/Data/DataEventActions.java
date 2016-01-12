package com.everymatch.saas.server.Data;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by sergata on 13/07/15.
 */
public class DataEventActions implements Serializable {
    public String action;
    public String http_method;
    public String api_access;
    /* ALL PARAMETERS */
    public HashMap<String, String> parameters;
    //public String parameters;
    public String text;
    public String icon;
    public String icon_color;
    //public String target_id;
}
