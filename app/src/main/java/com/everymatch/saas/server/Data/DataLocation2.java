package com.everymatch.saas.server.Data;

import java.util.HashMap;

/**
 * Created by sergata on 13/07/15.
 */
public class DataLocation2 {

    public DataCoordinate coordinates;
    public int distance_value;
    public String distance_units;
    public String country_code;
    public String country;
    public String city_code;
    public HashMap<String, String> text_address;

    public static class DataCoordinate {
        public String type;
        public float value[][];
    }

}




