package com.everymatch.saas.server.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sergata on 13/07/15.
 */
public class DataPublicEvent implements Serializable {

    public String event_id;
    public String event_title;
    public String event_description;
    public String title;
    public int match;
    public Schedule schedule;
    private DataLocation location;
    public String join_type;
    public DataPeople host;
    public TagData tags[];
    public String recurring_event_id;
    public String image_url;
    public int user_count;
    public DataUserEventStatus user_event_status;
    private ArrayList<DataPeople> users;
    public int spots;
    public String activity_title;
    public String activity_description;
    public String event_icon;
    private DataIcon icon;
    public String status;
    public Stats stats;

    public DataIcon getIcon() {
        if (icon == null) icon = new DataIcon();
        return icon;
    }

    public DataLocation getLocation() {
        if (location == null)
            location = new DataLocation();
        return location;
    }

    public void setLocation(DataLocation location) {
        this.location = location;
    }

    public ArrayList<DataPeople> getAllUsers() {
        if (users == null)
            users = new ArrayList<>();
        return users;
    }

    public ArrayList<DataPeople> getAllUsers(String participationType) {
        ArrayList<DataPeople> answer = new ArrayList<>();

        DataPeopleHolder dataPeopleHolder = participants.get(participationType);
        if (dataPeopleHolder == null) {
            return answer;
        }
        answer = dataPeopleHolder.getUsers();
        return answer;
    }

    public void setUsers(ArrayList<DataPeople> users) {
        this.users = users;
    }


    public HashMap<String, DataPeopleHolder> getParticipants() {
        if (participants == null)
            participants = new HashMap<>();
        return participants;
    }

    private HashMap<String, DataPeopleHolder> participants;
    public boolean is_participating;

    public class Schedule implements Serializable {
        public DataDate from;
        public DataDate to;
        //public String to_date;

    }

    public class TagData implements Serializable {
        public String type;
        public String text;
        public String key;
    }

    public class Stats implements Serializable {
        public int coming;
        public int maybe;
        public int open;
    }

    public class DataUserEventStatus implements Serializable {
        public String status;
        public String icon;
        public String color;
    }
}