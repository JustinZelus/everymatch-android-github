package com.everymatch.saas.server.responses;


import com.everymatch.saas.server.Data.DataIcon;
import com.google.gson.annotations.SerializedName;

public class ResponseLoadProviders extends BaseResponse {

    private Provider[] array;

    public Provider[] getProviders() {
        return array;
    }

    public void setArray(Provider[] array) {
        this.array = array;
    }

    public class Provider {
        //public JSONObject icon;
        //public JSONObject Icon;
        //public Dictionary<String,Object> Icon;
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
        public DataUserLogin user_login;

    }

    public class DataUserLogin {
        public String LoginProvider;
        public String ProviderKey;
    }
}
