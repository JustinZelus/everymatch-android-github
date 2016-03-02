package com.everymatch.saas.server.Data;

import java.io.Serializable;

/**
 * Created by Dacid on 29/06/2015.
 */
public class DataIcon implements Serializable {
    public String type;
    public String value;

    public DataIcon() {
        type = " ";
        value = " ";
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
