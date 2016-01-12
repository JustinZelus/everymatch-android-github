package com.everymatch.saas.server.Data;

import android.support.v4.app.Fragment;

import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.ui.me.settings.DistanceSelectFragment;
import com.everymatch.saas.ui.me.settings.UnitFragment;

import java.io.Serializable;

public class UserSettings implements Serializable {
    public String application_default_activity;
    public String user_activity_profile_id_list;
    public String last_activity_id;
    public String currency = "";
    public boolean is_user_uploaded_profile_image;
    public String distance;
    public String weight;
    public String default_culture;

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


    public Fragment getUnitFragmentByPosition(int position) {
        if (position == 1)
            return new UnitFragment();
        if (position == 2)
            return new DistanceSelectFragment();

        return new UnitFragment();
    }
}