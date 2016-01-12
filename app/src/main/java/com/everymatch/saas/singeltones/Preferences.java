package com.everymatch.saas.singeltones;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.server.Data.Resources;
import com.everymatch.saas.server.responses.ResponseApplication;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * Created by Dor on 18/12/2014.
 */
public class Preferences {

    private static final String TAG = Preferences.class.getSimpleName();

    private static Preferences sInstance = null;
    private SharedPreferences mPrefs = null;

    private final String ACCESS_TOKEN = "access_token";
    private final String TOKEN_TYPE = "token_type";
    private final String EXPIRES_IN = "expires_in";
    private final String USERNAME = "userName";
    private final String EXPIRES = "expires";
    private final String APPLICATION_DATA = "application data";
    private final String RESOURCES = "resources";
    private final String TIMESTAMP = "timestamp";
    private final String LANGUAGE = "myLanguage";

    private Preferences() {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(EverymatchApplication.getContext());
    }

    public static Preferences getInstance() {
        if (sInstance == null) {
            synchronized (Preferences.class) {
                if (sInstance == null) {
                    sInstance = new Preferences();
                }
            }
        }

        return sInstance;
    }


    public boolean isRegistered() {
        return !TextUtils.isEmpty(getTokenType());
    }

    public String getTokenType() {
        return mPrefs.getString(ACCESS_TOKEN, null);
    }

    public String getLanguage() {
        return mPrefs.getString(LANGUAGE, null);
    }


    public String getUsername() {
        return mPrefs.getString(USERNAME, null);
    }

    public Resources getResources() {
        String resourcesJson = mPrefs.getString(RESOURCES, null);

        if (resourcesJson == null) {
            return null;
        }

        return new Gson().fromJson(resourcesJson, Resources.class);
    }

    public void setAccessToken(String accessToken) {
        mPrefs.edit().putString(ACCESS_TOKEN, accessToken).commit();
    }

    public void setLanguage(String language) {
        mPrefs.edit().putString(LANGUAGE, language).commit();
    }

    public void setTokenType(String tokenType) {
        mPrefs.edit().putString(TOKEN_TYPE, tokenType).commit();
    }

    public void setExpireIn(int expireIn) {
        mPrefs.edit().putInt(EXPIRES_IN, expireIn).commit();
    }

    public void setUsername(String username) {
        mPrefs.edit().putString(USERNAME, username).commit();
    }

    public void setExpires(String expires) {
        mPrefs.edit().putString(EXPIRES, expires).commit();
    }

    public void setResources(Resources dataResources) {
        mPrefs.edit().putString(RESOURCES, new Gson().toJson(dataResources)).commit();
    }

    public void setTimestamp(String timestamp) {
        mPrefs.edit().putString(TIMESTAMP, timestamp).commit();
    }

    public String getTimestamp() {
        return mPrefs.getString(TIMESTAMP, null);
    }

    public void setApplicationData(ResponseApplication applicationData) {
        mPrefs.edit().putString(APPLICATION_DATA, new Gson().toJson(applicationData)).commit();
    }

    public ResponseApplication getApplicationData() {
        String applicationDataJson = mPrefs.getString(APPLICATION_DATA, null);
        if (applicationDataJson == null)
            return null;

        ResponseApplication answer = new Gson().fromJson(applicationDataJson, new TypeToken<ResponseApplication>() {
        }.getType());
        return answer;
    }
}
