package com.everymatch.saas.server.responses;

import com.everymatch.saas.server.Data.DataActivity;
import com.everymatch.saas.server.Data.DataEvent;
import com.everymatch.saas.server.Data.DataLocation;

import java.util.ArrayList;

/**
 * Created by PopApp_laptop on 31/08/2015.
 */
public class ResponseOtherUser extends BaseResponse {
    public DataActivity activities[];
    public ArrayList<DataEvent> events;
    public DataLocation location;
    public String groups;
    public String searches;
    public String users_id;
    public String first_name;
    public String last_name;
    public String age;
    public String about;
    public boolean is_friend;
    public String image_url;
}
