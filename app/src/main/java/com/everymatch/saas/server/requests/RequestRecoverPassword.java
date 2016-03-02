package com.everymatch.saas.server.requests;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.responses.ResponseRecoverPassword;

/**
 * Created by anastasia on 3/8/15.
 */
public class RequestRecoverPassword extends BaseRequest {

    private String AppID;

    private String email;

    public RequestRecoverPassword(String email) {
        this.AppID = EverymatchApplication.getContext().getResources().getString(R.string.app_id);
        this.email = email;
    }

    @Override
    public String getServiceUrl(){
        return Constants.getOAUTH2_SERVICE_URL();
    }

    @Override
    public String getUrlFunction() {
        return "api/Account/ResetPassword?hl=" + DataStore.getInstance().getCulture();
    }

    @Override
    public Class getResponseClass() {
        return ResponseRecoverPassword.class;
    }

    @Override
    public int getType() {
        return Request.Method.POST;
    }

//    @Override
//    public String getEncodedBody() {
//        return "username="+username + "&password="+password + "&grant_type=password";
//    }
}
