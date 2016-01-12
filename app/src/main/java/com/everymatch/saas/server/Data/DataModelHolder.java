package com.everymatch.saas.server.Data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by PopApp_laptop on 15/11/2015.
 */
public class DataModelHolder implements Serializable {
    private ArrayList<DataModel> model;
    public String name;

    public ArrayList<DataModel> getModel() {
        if (model == null)
            model = new ArrayList<>();
        return model;
    }

    public class DataModel {
        public String text_title;
        public String text_subtitle;
        public String text_description;
        public String background_image;
        public String icon_font;
        public String icon_image_url;
        public String text_url;
    }
}
