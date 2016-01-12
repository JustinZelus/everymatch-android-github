package com.everymatch.saas.server.Data;

import com.everymatch.saas.util.EMLog;

import java.io.Serializable;

/**
 * Created by PopApp_laptop on 22/11/2015.
 */
public class DataTimeZone implements Serializable {
    public String country_code;
    public int coordinates;
    public String title;
    public String comments;
    public String utc;
    public String utc_dst;

    public int getGmt() {

        String gmtStr = utc.substring(0, 3);
        try {
            int gmt = Integer.parseInt(gmtStr);
            return gmt;
        } catch (Exception ex) {
            EMLog.e("DataTimeZone", "Parse error: " + ex.getMessage());
            return 0;
        }
    }
}
