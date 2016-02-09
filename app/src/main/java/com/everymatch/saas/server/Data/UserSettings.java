package com.everymatch.saas.server.Data;

import com.everymatch.saas.client.data.DataStore;

import java.io.Serializable;

public class UserSettings implements Serializable {
    public String application_default_activity;
    public String user_activity_profile_id_list;
    public String last_activity_id;
    public String currency = "";
    public boolean is_user_uploaded_profile_image;
    private String distance;
    public String weight;
    public String default_culture;
    private DataTimeZone time_zone;

    public DataTimeZone getTime_zone() {
        if (time_zone == null) {
            time_zone = DataStore.getInstance().getApplicationData().getSettings().default_timezone;
            // time_zone = new DataTimeZone();
        }
        return time_zone;
    }


    public String getDistance() {
        if (distance == null || distance.trim().length() == 0)
            distance = DataStore.getInstance().getApplicationData().getSettings().getDefault_units().distance;
        return distance.toLowerCase();
    }

    /**
     * sets default data like unit's and weight
     */
    public void setDefaultData() {
        if (weight == null || weight.trim().length() == 0)
            weight = DataStore.getInstance().getApplicationData().getSettings().getDefault_units().weight;
        if (distance == null || distance.trim().length() == 0)
            distance = DataStore.getInstance().getApplicationData().getSettings().getDefault_units().distance;
    }

    public String getUnitsPromo() {
        return distance + "," + weight;
    }

    public void setDistance(String value) {
        this.distance = value;
    }

    public void setTime_zone(DataTimeZone dataTimeZone) {
        time_zone = dataTimeZone;
    }
}