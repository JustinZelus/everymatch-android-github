package com.everymatch.saas.server.requests;

import android.graphics.Bitmap;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.responses.ResponseUploadImage;
import com.everymatch.saas.singeltones.Preferences;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class RequestUploadImage extends BaseRequest {
    String content;

    public RequestUploadImage(Bitmap bitmap) {
//        this.AppID = EverymatchApplication.getContext().getResources().getString(R.string.app_id);
//        hl = DataStore.getInstance().getCulture();

        ByteArrayOutputStream baos = new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100, baos);
        byte [] b = baos.toByteArray();
        content = new String(b);
//        content = Base64.encodeToString(b, Base64.DEFAULT);
    }

    @Override
    public String getServiceUrl(){
        return Constants.API_SERVICE_URL;
    }

    @Override
    public String getUrlFunction() {
        return "API/uploads/upload?hl=" + DataStore.getInstance().getCulture() +
                "&app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id);
    }

    @Override
    public Class getResponseClass() {
        return ResponseUploadImage.class;
    }

    @Override
    public int getType() {
        return Request.Method.POST;
    }

    @Override
    public String getEncodedBody() {
        return content;
    }

    @Override
    public Map<String, String> addExtraHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + Preferences.getInstance().getTokenType());
        return headers;
    }

    @Override
    public String getBodyContentType() {
        return "multipart/form-data"; //"image/jpg"; //"image/png";
    }
}
