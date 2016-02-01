package com.everymatch.saas.server.Data;

import com.everymatch.saas.util.EMLog;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by sergata on 13/07/15.
 */
public class DataLocation implements Serializable {
    public static final String TAG = "DataLocation";

    public DataCoordinate coordinates;
    public double distance_value;
    public String distance_units;
    public String country_code;
    public String city;
    public String country_name;
    public String city_code;
    public String text_address;
    public String place_name;
    public String place_id;

    public LatLng getLatLng() {
        if (latLng == null) {
            latLng = new LatLng(coordinates.value[0][0], coordinates.value[0][1]);
        }
        return latLng;
    }

    public void setLatLon(LatLng latLon) {
        this.latLng = latLon;
        coordinates.value[0][0] = (float) latLon.latitude;
        coordinates.value[0][1] = (float) latLon.longitude;
    }

    private LatLng latLng;

    public DataLocation() {
        city = "";
        country_name = "";
        city_code = "";
        text_address = "";
        coordinates = new DataCoordinate();
    }

    public static DataLocation fromJsonObject(JSONObject value) {
        DataLocation answer = new DataLocation();
        try {
            if (value.has("distance_value"))
                answer.distance_value = Math.round((float) value.getDouble("distance_value"));

            if (value.has("city"))
                answer.city = value.getString("city");

            if (value.has("city_code"))
                answer.city_code = value.getString("city_code");

            if (value.has("country_code"))
                answer.country_code = value.getString("country_code");

            if (value.has("country_name"))
                answer.country_name = value.getString("country_name");

            if (value.has("text_address"))
                answer.text_address = value.getString("text_address");

            if (value.has("distance_units"))
                answer.distance_units = value.getString("distance_units");

            if (value.has("place_name"))
                answer.place_name = value.getString("place_name");

            if (value.has("place_id"))
                answer.place_id = value.getString("place_id");

            answer.coordinates = new DataCoordinate();
            if (value.has("coordinates")) {
                //get coordinates
                JSONObject coordinates = value.getJSONObject("coordinates");
                JSONArray v = coordinates.getJSONArray("value");
                JSONArray arr = v.getJSONArray(0);
                double lat = arr.getDouble(0);
                double lon = arr.getDouble(1);
                answer.latLng = new LatLng(lat, lon);

                answer.coordinates.value[0][0] = lat;
                answer.coordinates.value[0][1] = lon;
            }

        } catch (Exception ex) {
            EMLog.e(TAG, ex.getMessage());
        }


        return answer;
    }

    public static class DataCoordinate implements Serializable {
        public String type;
        public double value[][];

        public DataCoordinate() {
            value = new double[1][2];
        }
    }

    public String getTitle() {
        return text_address;
    }
}




