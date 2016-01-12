package com.everymatch.saas.server.Data;

import java.io.Serializable;

/**
 * Created by sergata on 13/07/15.
 */
public class DataLocation implements Serializable {

    public DataCoordinate coordinates;
    public int distance_value;
    public String distance_units;
    public String country_code;
    public String city;
    public String country;
    public String city_code;
    public String text_address;
    public String place_name;

    public DataLocation() {
        city = "NA";
        country = "NA";
        city_code = "NA";
        text_address = "NA";
    }

    public static class DataCoordinate implements Serializable {
        public String type;
        public Float value[][];
    }

}




