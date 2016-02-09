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
        Map m = new HashMap<>();
        m.put("AppID", EverymatchApplication.getContext().getResources().getString(R.string.app_id));
        m.put("DayOfBirth", user.age);
        m.put("Email", user.email);
        m.put("FirstName", user.first_name);
        m.put("Gender", "male");
        m.put("Id", user.users_id);
        m.put("LastName", user.last_name);
        m.put("MonthOfBirth", "3");
        m.put("PhoneNumber", user.phone);
        m.put("UserName", user.email);
        m.put("YearOfBirth", "1970");
        m.put("CountryCode", user.user_settings.getTime_zone().country_code);
        m.put("Time Zone", user.user_settings.getTime_zone().utc);
        m.put("Currency", user.user_settings.currency);
        m.put("Distance", user.user_settings.getDistance());
        m.put("default_culture", DataStore.getInstance().getCulture());
        m.put("Weight", user.user_settings.weight);
        //m.put("Image eUrl", "");
        //m.put("Password", "");
        //m.put("ConfirmPassword", "");

        JSONObject obj = new JSONObject(m);
        String str = obj.toString();
        return str;
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
