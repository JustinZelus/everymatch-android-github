package com.everymatch.saas.server.Data;

import com.everymatch.saas.util.EMLog;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by PopApp_laptop on 09/08/2015.
 */
public class DataChannelName implements Serializable {
    public final String TAG = getClass().getName();

    public String name;
    public String title;

    public DataChannelName(JSONObject obj) {
        try {
            if (obj.has("name"))
                this.name = obj.getString("name");
            if (obj.has("title"))
                this.title = obj.getString("title");
        } catch (Exception ex) {
            EMLog.e(TAG, ex.getMessage());
        }

    }
}
