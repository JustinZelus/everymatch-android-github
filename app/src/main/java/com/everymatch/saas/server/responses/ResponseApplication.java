package com.everymatch.saas.server.responses;

import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.server.Data.DataActivity;
import com.everymatch.saas.server.Data.DataQuestion;
import com.everymatch.saas.server.Data.ApplicationSettings;
import com.everymatch.saas.server.Data.DataTimeZone;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.GregorianCalendar;


/**
 * Created by PopApp_laptop on 20/09/2015.
 */
public class ResponseApplication extends BaseResponse {
    public String culture_name;
    public String app_id;
    public String timestamp;
    private DataActivity activities[];
    private DataQuestion user_profile_questions[];
    private ApplicationSettings settings;
    private ArrayList<DataTimeZone> time_zone;
    private ArrayList<DataCurrency> currencies;
    private ArrayList<DataCulture> cultures;

    public ArrayList<DataCulture> getCultures() {
        if (cultures == null)
            cultures = new ArrayList<>();
        return cultures;
    }

    public ArrayList<DataCurrency> getCurrencies() {
        if (currencies == null)
            currencies = new ArrayList<>();
        return currencies;
    }

    public ArrayList<DataTimeZone> getTime_zone() {
        if (time_zone == null)
            time_zone = new ArrayList<>();
        return time_zone;
    }

    /**
     * @param countryCode
     * @param gmt
     * @return this method returns the first timeZone that fits the parameters
     */
    public DataTimeZone getTimeZoneByCountryCodeAndGmt(String countryCode, String gmt) {
        for (DataTimeZone dtz : getTime_zone()) {
            if (dtz.country_code.equals(countryCode) && gmt.equals(dtz.utc))
                return dtz;
        }
        return null;
    }

    public DataTimeZone getTimeZoneBySystem() {
        String countryCode = EverymatchApplication.getContext().getResources().getConfiguration().locale.getCountry();
        String gmt = "" + (new GregorianCalendar().getTimeZone().getRawOffset() / 60 / 60 / 1000);
        for (DataTimeZone dtz : getTime_zone()) {
            if (dtz.country_code.toLowerCase().equals(countryCode.toLowerCase())) {
                if (gmt.equals(dtz.getGmt())) {
                    return dtz;
                }
            }
        }
        return null;
    }

    public Start start;

    public Event event;


    public DataQuestion[] getUser_profile_questions() {
        if (user_profile_questions == null)
            user_profile_questions = new DataQuestion[0];
        return user_profile_questions;
    }

    public boolean doesHaveDefaultActivity() {
        for (DataActivity dataActivity : getActivities()) {
            if (dataActivity.is_default)
                return true;
        }

        return false;
    }

    public DataActivity[] getActivities() {
        if (activities == null)
            activities = new DataActivity[0];
        return activities;
    }

    public DataActivity getActivityById(String id) {
        for (DataActivity activity : getActivities()) {
            if (("" + activity.client_id).equals(id))
                return activity;
        }
        return null;
    }

    public ApplicationSettings getSettings() {
        if (settings == null)
            settings = new ApplicationSettings();
        return settings;
    }

    public class Start {
        public ArrayList<DataModelApplication> getModel() {
            if (model == null)
                model = new ArrayList<>();
            return model;
        }

        private ArrayList<DataModelApplication> model;

        public class DataModelApplication implements Serializable {
            public String text_title;
            public String text_subtitle;
            public String text_description;
            public String background_image;
            public String icon_image_url;
            public String icon_font;
        }

    }

    public class Event {
        public ArrayList<DataQuestion> publish_questions;
    }

    public class DataCurrency implements Serializable {
        public String symbol = "";
        public String symbol_native = "";
        public int decimal_digits;
        public int rounding;
        public String code = "";
        public boolean is_enabled;
    }

    public class DataCulture {
        public String culture_name;
        public boolean is_enabled;
        public boolean is_right_to_left;
        public String name;
        public String text_title;
    }

}