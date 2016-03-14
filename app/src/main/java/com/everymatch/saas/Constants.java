package com.everymatch.saas;

import com.everymatch.saas.client.data.BUILD_MODE;
import com.everymatch.saas.client.data.DataStore;

/**
 * Created by dor on 21/01/2015.
 */
public class Constants {


    //public static String mode = BuildConfig.DEBUG ? "me" : "com"; // me // local // com
    public static BUILD_MODE mode = BUILD_MODE.ME;

    public static String getAPI_SERVICE_URL() {
        switch (mode) {
            case COM:
                return "https://api.everymatch.com/";
            case ME:
                return "https://api.everymatch.me/";
            default:
                return "http://192.168.1.101:4434/";
        }

    }

    public static String getOAUTH2_SERVICE_URL() {
        switch (mode) {
            case COM:
                return "https://oauth2.everymatch.com/";
            case ME:
                return "https://oauth2.everymatch.me/";
            default: //local
                return "http://192.168.1.101:4432/";
        }


    }

    public static String getImageUploadUrl() {
        switch (mode) {
            case COM:
                return "https://api.everymatch.com/api/uploads?" +
                        "hl=" + DataStore.getInstance().getCulture() +
                        "&app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id);
            case ME:
                return "https://api.everymatch.me/api/uploads?" +
                        "hl=" + DataStore.getInstance().getCulture() +
                        "&app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id);

            default:
                return "http://192.168.1.101:4434/api/uploads?" +
                        "hl=" + DataStore.getInstance().getCulture() +
                        "&app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id);
        }


    }

    public static final int ACTION_TEXT_SIZE_SP = 22;
    public static final String PUSHER_APP_KEY = "3686be310b7f401be2ec";

    // SERVICES
    public static final String DEFAULT_AVATAR_IMAGE = "https://cdn.everymatch.com/static/man.png";
    public static final String DEFAULT_EVENT_IMAGE = "http://cdn2.everymatch.com/remote/emprod.everymatchintern.netdna-cdn.com/apps/icons/event.png";

    // Terms and condition path
    public static final String PRIVACY_SETTINGS_URL = "api/tnc?type=privacy_settings";
    public static final String TERMS_AND_CONDITIONS_URL = "api/tnc?type=terms_and_conditions";

}
