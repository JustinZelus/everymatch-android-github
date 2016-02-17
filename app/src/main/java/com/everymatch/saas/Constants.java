package com.everymatch.saas;

import com.everymatch.saas.client.data.DataStore;

/**
 * Created by dor on 21/01/2015.
 */
public class Constants {

    // public static final String CUSTOMER_SUPPORT_NUMBER = "1234";

   // public static  String API_SERVICE_URL = "http://192.168.1.101:4434/";
   // public static  String AUTH2_SERVICE_URL = "http://192.168.1.101:4432/";

    public static final int ACTION_TEXT_SIZE_SP = 22;

    public static String API_SERVICE_URL = "https://api.everymatch.me/";
    public static String AUTH2_SERVICE_URL = "https://oauth2.everymatch.me/";

    public static String getImageUploadUrl() {
        return "https://api.everymatch.me/api/uploads?" +
                "hl=" + DataStore.getInstance().getCulture() +
                "&app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id);
    }

    public static String getLocalImageUploadUrl() {
        return "http://192.168.1.101:4433/api/uploads?" +
                "hl=" + DataStore.getInstance().getCulture() +
                "&app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id);
    }

    //public static final String PUSHER_APP_KEY = "126101";
    public static final String PUSHER_APP_KEY = "3686be310b7f401be2ec";

    // SERVICES
    public static final String DEFAULT_AVATAR_IMAGE = "https://cdn.everymatch.com/static/man.png";
    public static final String DEFAULT_EVENT_IMAGE = "http://cdn2.everymatch.com/remote/emprod.everymatchintern.netdna-cdn.com/apps/icons/event.png";

    // Terms and condition path
    public static final String PRIVACY_SETTINGS_URL = "api/tnc?type=privacy_settings";
    public static final String TERMS_AND_CONDITIONS_URL = "api/tnc?type=terms_and_conditions";


//    public static final String GOOGLE_API_SERVICE_URL = "https://maps.googleapis.com/maps/api/place/";
//    public static final String GOOGLE_DIRECTIONS_API = "https://maps.googleapis.com/maps/api/directions/json?";

    // SERVER AUTHENTICATION
//    public static final String AUTH_CLIENT_ID = "";
//    public static final String AUTH_CLIENT_SECRET = "";

    // KEYS
//    public static final String BROWSER_APPLICATION_KEY = "";
//    public static final String STRIPE_PUBLISH_KEY = "";

//    Google API key for Android
//    AIzaSyDTmtWl6IrNikgakt87cp1CtRNWnCElLuk

}
