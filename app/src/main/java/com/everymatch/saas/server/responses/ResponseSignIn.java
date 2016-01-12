package com.everymatch.saas.server.responses;


import com.google.gson.annotations.SerializedName;

public class ResponseSignIn extends BaseResponse {

    @SerializedName("access_token")
    String access_token;

    @SerializedName("token_type")
    String token_type;

    @SerializedName("expires_in")
    int expires_in;

    @SerializedName("userName")
    String userName;

    @SerializedName(".issued")
    String issued;

    @SerializedName(".expires")
    String expires;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(String access_token) {
        this.access_token = access_token;
    }

    public String getToken_type() {
        return token_type;
    }

    public void setToken_type(String token_type) {
        this.token_type = token_type;
    }

    public int getExpires_in() {
        return expires_in;
    }

    public void setExpires_in(int expires_in) {
        this.expires_in = expires_in;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIssued() {
        return issued;
    }

    public void setIssued(String issued) {
        this.issued = issued;
    }

    public String getExpires() {
        return expires;
    }

    public void setExpires(String expires) {
        this.expires = expires;
    }
}
