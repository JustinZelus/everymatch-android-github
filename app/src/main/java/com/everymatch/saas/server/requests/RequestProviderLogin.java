package com.everymatch.saas.server.requests;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.responses.ResponseSignIn;

public class RequestProviderLogin extends BaseRequest {

    private String AppID;
    private String hl;
    private String key;
    private String grant_type = "password";

    public RequestProviderLogin(String key) {
        this.AppID = EverymatchApplication.getContext().getResources().getString(R.string.app_id);
        hl = DataStore.getInstance().getCulture();
        this.key = key;
    }

    @Override
    public String getServiceUrl(){
        return Constants.getOAUTH2_SERVICE_URL();
    }

    @Override
    public String getUrlFunction() {
//        return "Account/Register?hl=en-US&rand=0.0032386346720159054";
        return "Token?hl=" + DataStore.getInstance().getCulture() +
                "&key=" + key +
                "&AppID=" + AppID;
    }

    @Override
    public Class getResponseClass() {
        return ResponseSignIn.class;
    }

    @Override
    public int getType() {
        return Request.Method.POST;
    }

    @Override
    public String getEncodedBody() {
        return "grant_type=password";
    }
}
