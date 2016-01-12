package com.everymatch.saas.server.responses;


import com.google.gson.annotations.SerializedName;

public class ResponseRegisterWithEmail extends BaseResponse {

    @SerializedName("Succeeded")
    boolean Succeeded;

    public boolean isSucceeded() {
        return Succeeded;
    }

    public void setSucceeded(boolean succeeded) {
        Succeeded = succeeded;
    }
}
