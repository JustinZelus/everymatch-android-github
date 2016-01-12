package com.everymatch.saas.util;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * Created by Yossi on 27/07/2015.
 */
public class IconManager {

    Context mContext;
    private JSONArray iconsArray;

    private static IconManager mInstance;

    public static IconManager getInstance(Context context) {
        if(mInstance == null) {
            mInstance = new IconManager(context);
        }
        return mInstance;
    }

    public IconManager(Context context) {
        mContext = context;

        try {
            InputStream is = mContext.getAssets().open("selection.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, "UTF-8");
            JSONObject jsonObject = new JSONObject(jsonString);
            iconsArray = jsonObject.getJSONArray("icons");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getIconString(String iconName) {

        if (iconName == null){
            return "";
        }

        if (iconName.startsWith("icon-")){
            iconName = iconName.replace("icon-", "");
        }

        int iconCode = 0;
        try {
            for (int i = 0; i < iconsArray.length(); i++) {
                JSONObject iconObj = iconsArray.getJSONObject(i);
                JSONObject propertiesObj = iconObj.getJSONObject("properties");
                String name = propertiesObj.getString("name");

                if (name.equals(iconName)) {
                    iconCode = propertiesObj.getInt("code");
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return String.valueOf((char) iconCode);
    }
}
