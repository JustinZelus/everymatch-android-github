package com.everymatch.saas.server.responses;


import com.everymatch.saas.server.Data.DataIcon;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class ResponseLoadProviders extends BaseResponse {

    private ArrayList<Provider> array;

    public ArrayList<Provider> getProviders() {
        if (array == null)
            array = new ArrayList<>();
        return array;
    }

    public void setArray(ArrayList<Provider> array) {
        this.array = array;
    }

    public class Provider {
        @SerializedName("Icon")
        public DataIcon Icon;
        public String name;
        public String gateway_url;
        public String type;
        public String text_title;
        public String background_color;
        public String auth_type;
        public boolean is_manages_identities;
        public String client_url;
        public UserLoginObject user_login;

    }

    public class UserLoginObject implements Serializable {
        public String LoginProvider;
        public String ProviderKey;
    }
}
