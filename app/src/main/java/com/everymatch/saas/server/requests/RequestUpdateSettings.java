package com.everymatch.saas.server.requests;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.responses.ResponseGetUser;
import com.everymatch.saas.singeltones.Preferences;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PopApp_laptop on 07/10/2015.
 */
public class RequestUpdateSettings extends BaseRequest {

    private ResponseGetUser user;

    public RequestUpdateSettings(ResponseGetUser user) {
        this.user = user;
    }

    @Override
    public String getServiceUrl() {
        return Constants.API_SERVICE_URL;
    }

    @Override
    public String getBodyContentType() {
        return GsonRequest.CONTENT_TYPE_X_URL_ENCODED;
    }

    @Override
    public String getUrlFunction() {
        //https://oauth2.everymatch.me/api/Account/UpdateUser?hl=en-US
        //return "api/Account/UpdateUser?hl=" + DataStore.getInstance().getCulture();
        return "api/usersettings?hl=" + DataStore.getInstance().getCulture()
                + "&app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id);
    }

    @Override
    public String getEncodedBody() {
        try {
            DataStore ds = DataStore.getInstance();
            JSONObject out = new JSONObject();
            out.put("currency", ds.getUser().user_settings.currency);
            out.put("default_culture", ds.getCulture());

            //add  units
            JSONObject units = new JSONObject();
            units.put("distance", ds.getUser().user_settings.getDistance());
            units.put("weight", ds.getUser().user_settings.weight);
            out.put("units", units);

            //add time zone
            JSONObject time_zone = new JSONObject();
            units.put("utc", ds.getUser().user_settings.getTime_zone().utc);
            units.put("country_code", "");//TODO add country code here
            out.put("title", ds.getUser().user_settings.getTime_zone().title);
            out.put("index", "1");
            out.put("time_zone", time_zone);

            String str = out.toString();
            return str;


        } catch (Exception ex) {
            return "{}";
        }


        /*Map m = new HashMap<>();
        m.put("app_id", EverymatchApplication.getContext().getResources().getString(R.string.app_id));
        m.put("day_of_birth", user.age);
        m.put("email", user.email);
        m.put("first_name", user.first_name);
        m.put("gender", "male");
        m.put("id", user.users_id);
        m.put("last_name", user.last_name);
        m.put("month_of_birth", "3");
        m.put("phone_number", user.phone);
        m.put("user_name", user.email);
        m.put("year_of_birth", "1970");
        m.put("country_code", user.user_settings.getTime_zone().country_code);
        m.put("time_zone", user.user_settings.getTime_zone().utc);
        m.put("currency", user.user_settings.currency);
        m.put("distance", user.user_settings.getDistance());
        m.put("default_culture", DataStore.getInstance().getCulture());
        m.put("weight", user.user_settings.weight);
        //m.put("Image eUrl", "");
        //m.put("Password", "");
        //m.put("ConfirmPassword", "");

        JSONObject obj = new JSONObject(m);
        String str = obj.toString();*/
    }

    @Override
    public Class getResponseClass() {
        // not really metter - because we get a string back from server
        return ResponseGetUser.class;
    }


    @Override
    public int getType() {
        return Request.Method.PUT;
    }

    @Override
    public Map<String, String> addExtraHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization", "Bearer " + Preferences.getInstance().getTokenType());
        return headers;
    }

    @Override
    public boolean parseResponseAsJson() {
        return false;
    }
}
