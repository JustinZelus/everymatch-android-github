package com.everymatch.saas.server.Data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by PopApp_laptop on 20/12/2015.
 */
public class DataMatchResults implements Serializable {
    public ArrayList<DataMatchResultsValueHolder> results;

    public class DataMatchResultsValueHolder implements Serializable {
        public DataMatchResultsValue value;
        public String _id;
    }

    public class DataMatchResultsValue implements Serializable {
        public double match;
        public double real_match;
        public int my_default_raduis;
        public int other_default_radius;
        public ArrayList<DataQuestion> questions_results;
    }

}
