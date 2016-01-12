package com.everymatch.saas.server.Data;

import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.Data.units.DataDistance;
import com.everymatch.saas.server.Data.units.DataWeight;
import com.everymatch.saas.server.responses.ResponseApplication;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dors on 10/26/15.
 */
public class ApplicationSettings implements Serializable {

    @SerializedName("default_design")
    public HashMap<String, String> defaultDesign;
    public String application_name;
    public String version;
    public String default_currency;
    private UnitsHolder units;
    private DataDefaultUnits default_units;
    public ResponseApplication.DataCulture default_culture;


    public DataDefaultUnits getDefault_units() {
        if (default_units == null)
            default_units = new DataDefaultUnits();
        return default_units;
    }

    public UnitsHolder getUnits() {
        if (units == null)
            units = new UnitsHolder();
        return units;
    }

    public class UnitsHolder {
        private ArrayList<DataDistance> distance;
        private ArrayList<DataWeight> weight;

        public String getUnitListNameByPosition(int pos) {
            if (pos == 0)
                return "Distance";
            if (pos == 1) {
                return "Weight";
            }
            return "";
        }

        public String getUnitValueByPosition(int unitListPosition, int itemPosition) {
            if (unitListPosition == 0)
                return distance.get(itemPosition).name;
            if (unitListPosition == 1) {
                return weight.get(itemPosition).name;
            }
            return "";
        }

        public int getCount() {
            return 2;
        }

        public ArrayList<DataDistance> getDistance() {
            if (distance == null)
                distance = new ArrayList<>();
            return distance;
        }

        public ArrayList<DataWeight> getWeight() {
            if (weight == null)
                weight = new ArrayList<>();
            return weight;
        }

        public Object getUnitListByPosition(int pos) {
            if (pos == 0)
                return DataStore.getInstance().getApplicationData().getSettings().getUnits().getDistance();
            if (pos == 1) {
                return DataStore.getInstance().getApplicationData().getSettings().getUnits().getWeight();
            }
            return DataStore.getInstance().getApplicationData().getSettings().getUnits().getDistance();

        }

        public String getUserUnitByPosition(int pos) {
            if (pos == 0)
                return DataStore.getInstance().getUser().user_settings.distance;
            if (pos == 1) {
                return DataStore.getInstance().getUser().user_settings.weight;
            }
            return "";

        }

        public void updateUserUnitByPositionAndValue(int unitListPosition, String value) {
            if (unitListPosition == 0)
                DataStore.getInstance().getUser().user_settings.distance = value;
            if (unitListPosition == 1) {
                DataStore.getInstance().getUser().user_settings.weight = value;
            }
        }

        /*this method returns the currnet selected position of unit to display in the units list*/
        public int getCurrentSelectedPositionByUserSettings(int unitPosition) {
            if (unitPosition == 0) {
                /*distance list*/
                for (int i = 0; i < getDistance().size(); ++i)
                    if (getDistance().get(i).name.equals(DataStore.getInstance().getUser().user_settings.distance))
                        return i;
            }
            if (unitPosition == 1) {
                /*weight list*/
                for (int i = 0; i < getWeight().size(); ++i)
                    if (getWeight().get(i).name.equals(DataStore.getInstance().getUser().user_settings.weight))
                        return i;
            }
            return 0;
        }
    }

    public class DataDefaultUnits {
        public String weight;
        public String distance;
    }
}
