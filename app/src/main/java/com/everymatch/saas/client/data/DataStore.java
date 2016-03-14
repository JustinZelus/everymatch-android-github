package com.everymatch.saas.client.data;

import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.server.Data.ApplicationSettings;
import com.everymatch.saas.server.responses.ResponseApplication;
import com.everymatch.saas.server.responses.ResponseGetUser;
import com.everymatch.saas.server.responses.ResponseLoadProviders;
import com.everymatch.saas.singeltones.Preferences;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.Utils;

import java.util.Locale;

/**
 * Created by PopApp_laptop on 06/09/2015.
 */
public class DataStore {
    public final String TAG = getClass().getName();

    private static DataStore instance;

    private ResponseGetUser user;
    private ResponseApplication application;
    public ResponseLoadProviders responseLoadProviders;


    // people adapter types
    public static final int ADAPTER_MODE_TEXT = 10;
    public static final int ADAPTER_MODE_LIKE = 11;
    public static final int ADAPTER_MODE_COUNTER = 12;
    public static final int ADAPTER_MODE_NONE = 13;
    public static final int ADAPTER_MODE_MATCH = 14;
    public static final int ADAPTER_MODE_COUNTER_WITH_PERCENT = 15;

    // people (pager) screen type
    public static final int SCREEN_TYPE_INVITE_PARTICIPANTS = 15;
    public static final int SCREEN_TYPE_EVENT_PARTICIPANTS = 16;
    public static final int SCREEN_TYPE_FRIENDS = 17;
    public static final int SCREEN_TYPE_EVENT_ACTION_INVITE = 18;

    public static DataStore getInstance() {
        if (instance == null)
            instance = new DataStore();

        return instance;
    }

    private DataStore() {
        this.application = Preferences.getInstance().getApplicationData();
    }

    public void setUserData(ResponseGetUser responseGetUser) {
        this.user = responseGetUser;
    }

    public void setApplicationData(ResponseApplication application) {
        this.application = application;

        if (application != null) {
            Preferences.getInstance().setApplicationData(application);
        }
    }

    public ResponseGetUser getUser() {
        return user;
    }

    public ResponseApplication getApplicationData() {
        return application;
    }

    // ============================== Culture ================================ //

    public String getLocal() {
        String country = Locale.getDefault().getCountry().toUpperCase();
        switch (country) {
            case "US":
                return "en-US";
            case "IL":
                return "he-IL";
            case "FR":
                return "fr-FR";
            case "SA":
                return "ar-SA";
            case "BR":
                return "pt-BR";
            case "UK":
                return "en-UK";
            default:
                return "en-US";
        }
    }

    public String getCultureTest() {
        try {
            String local = getLocal();
            String savedLocal = Preferences.getInstance().getLanguage();
            if (Utils.isEmpty(savedLocal))
                return local;
            return savedLocal;
        } catch (Exception ex) {
            EMLog.e(TAG, ex.getMessage());
            return EverymatchApplication.getContext().getResources().getString(R.string.default_culture);
        }
    }

    public String getCulture() {
        return getCultureTest();
        /*
        String answer = "";

        String local = getLocal();

        try {
            if (Preferences.getInstance().getTokenType() == null) {
                // no logged user
                if (Preferences.getInstance().getTimestamp() == null) {
                    // no application -> get from build
                    answer = EverymatchApplication.getContext().getResources().getString(R.string.default_culture);
                } else {
                    //we have application -> return default culture
                    answer = getApplicationData().getSettings().default_culture.culture_name;
                }
            } else {
                // we have user

                // get from user object (but on splash it still null. in that case we take from preferences)
                if (getUser() != null && getUser().user_settings != null && getUser().user_settings.default_culture != null)
                    answer = getUser().user_settings.default_culture;
                else {
                    if (Preferences.getInstance().getLanguage() != null) {
                        answer = Preferences.getInstance().getLanguage();
                        return answer;
                    }
                }
            }
        } catch (Exception ex) {
            EMLog.e(TAG, "Error in get culture : " + ex.getMessage());
            answer = EverymatchApplication.getContext().getResources().getString(R.string.default_culture);
        }

        return answer;*/
    }

    public String getColor(int color) {
        ApplicationSettings dataSettings = application.getSettings();
        return dataSettings.defaultDesign.get(Utils.intToStringColor(color));
    }

    public int getColorByName(String colorName) {
        ApplicationSettings dataSettings = application.getSettings();
        String hexColor = dataSettings.defaultDesign.get(colorName);
        return android.graphics.Color.parseColor(hexColor);
    }

    public int getIntColor(int color) {
        ApplicationSettings dataSettings = application.getSettings();
        String hexColor = dataSettings.defaultDesign.get(Utils.intToStringColor(color));
        return android.graphics.Color.parseColor(hexColor);
    }

}
