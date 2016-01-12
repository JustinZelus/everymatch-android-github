package com.everymatch.saas.server.Data;

import java.io.Serializable;

/**
 * Created by Dacid on 29/06/2015.
 */
public class DataPeople implements Serializable{

    public String users_id;
    public String first_name;
    public String last_name;
    public int age;
    public String about;
    public String city;
    public String country_code;
    public String image_url;
    public String status;
    public DataDate approved_date;
    public boolean is_friend;
    public DataDate invitation_date;
    public String invitation_note;
    public String username;
    public int match;
}
