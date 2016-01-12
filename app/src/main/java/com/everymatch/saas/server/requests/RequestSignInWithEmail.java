package com.everymatch.saas.server.requests;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.responses.ResponseSignIn;

/**
 * Created by anastasia on 3/8/15.
 */
public class RequestSignInWithEmail extends BaseRequest {

    private String AppID;
    private String hl;
    private String grant_type = "password";
    private String username;
    private String password;

    public RequestSignInWithEmail(String username, String password) {
        this.AppID = EverymatchApplication.getContext().getResources().getString(R.string.app_id);
        hl = DataStore.getInstance().getCulture();
        this.username = username;
        this.password = password;
    }

    @Override
    public String getServiceUrl(){
        return Constants.AUTH2_SERVICE_URL;
    }

    @Override
    public String getUrlFunction() {
//        return "Account/Register?hl=en-US&rand=0.0032386346720159054";
        return "Token?hl=" + DataStore.getInstance().getCulture();
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
        return "username="+username + "&password="+password + "&grant_type=password";
    }
}
