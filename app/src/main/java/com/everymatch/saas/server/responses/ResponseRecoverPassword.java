package com.everymatch.saas.server.responses;


import com.google.gson.annotations.SerializedName;

public class ResponseRecoverPassword extends BaseResponse {

    @SerializedName("message")
    String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


}
