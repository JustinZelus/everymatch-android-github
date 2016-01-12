package com.everymatch.saas.server.requests;

import com.android.volley.Request;

import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.server.responses.ResponseRegisterWithEmail;

/**
 * Created by anastasia on 3/8/15.
 */
public class RequestRegisterWithEmail extends BaseRequest {

    private String AppID;
    private String FirstName;
    private String LastName;
    private String Email;

    public RequestRegisterWithEmail(String firstName, String lastName, String email) {
        this.AppID = EverymatchApplication.getContext().getResources().getString(R.string.app_id);
        this.FirstName = firstName;
        this.LastName = lastName;
        this.Email = email;
    }

    @Override
    public String getServiceUrl() {
        return Constants.AUTH2_SERVICE_URL;
    }

    @Override
    public String getUrlFunction() {
        return "api/Account/Register?hl=en-US&rand=0.0032386346720159054";
    }

    @Override
    public Class getResponseClass() {
        return ResponseRegisterWithEmail.class;
    }

    @Override
    public int getType() {
        return Request.Method.POST;
    }

}
