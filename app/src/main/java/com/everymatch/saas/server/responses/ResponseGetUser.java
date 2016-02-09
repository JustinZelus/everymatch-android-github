package com.everymatch.saas.server.responses;

import android.text.TextUtils;

import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.Data.DataActivity;
import com.everymatch.saas.server.Data.DataChannelName;
import com.everymatch.saas.server.Data.DataDate;
import com.everymatch.saas.server.Data.DataEvent;
import com.everymatch.saas.server.Data.DataEventHolder;
import com.everymatch.saas.server.Data.DataNotifications;
import com.everymatch.saas.server.Data.DataPeopleHolder;
import com.everymatch.saas.server.Data.DataProfile;
import com.everymatch.saas.server.Data.DataUserProfile;
import com.everymatch.saas.server.Data.UserSettings;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Dor on 7/27/15.
 */
public class ResponseGetUser extends BaseResponse {
    public final String TAG = getClass().getName();

    //public DataNotifications notifications;

    public String users_id;
    public DataDate created_date;
    public String first_name;
    public String last_name;
    public String image_url;
    public String age;
    public String city;
    public String country;
    public String about;
    public String email;
    public String phone;
    public String registered_app_id;
    //public int time_zone;
    //public String country_code;

    private ArrayList<DataChannelName> channels;

    public ArrayList<DataChannelName> getChannels() {
        if (channels == null)
            channels = new ArrayList<>();
        return channels;
    }

    public void addChannel(DataChannelName dataChannelName) {
        for (DataChannelName ch : getChannels()) {
            if (ch.name.equals(dataChannelName.name)) {
                EMLog.e(TAG, "Atempt to add an already added channel");
                return;
            }
        }
        getChannels().add(dataChannelName);
    }

    public void removeChannel(DataChannelName dataChannelName) {
        getChannels().remove(dataChannelName);
    }

    //hold user profiles
    public Profiles profiles;

    // hold my notifications
    public UserSettings user_settings;

    // hold my notifications
    public Notifications notifications;

    // holds my events
    private HashMap<String, DataEventHolder> my_events;

    // holds my friends and recently viewed
    private HashMap<String, DataPeopleHolder> people;

    public DataInbox getInbox() {
        if (inbox == null)
            inbox = new DataInbox();
        return inbox;
    }

    public void setInbox(DataInbox inbox) {
        this.inbox = inbox;
    }

    // holds my inbox data
    private DataInbox inbox;

    public HashMap<String, DataPeopleHolder> getPeople() {
        if (people == null) {
            people = new HashMap<>();
        }
        return people;
    }

    public boolean hasPeople() {
        if (people != null) {
            for (DataPeopleHolder holder : people.values()) {
                if (holder != null && !Utils.isArrayListEmpty(holder.getUsers())) {
                    return true;
                }
            }
        }

        return false;
    }

    public void setPeople(HashMap<String, DataPeopleHolder> people) {
        this.people = people;
    }

    public class Profiles implements Serializable {

        public DataUserProfile user_profile;
        public DataProfile activity_profiles[];
    }

    public class Notifications {
        public ArrayList<DataNotifications> list;

        public int unread;
        public int unseen;
        public int count;

        public ArrayList<DataNotifications> getNotifications() {
            if (list == null)
                list = new ArrayList<>();
            return list;
        }
    }

    public String getName() {
        return first_name + " " + last_name;
    }

    public String[] getAnswerActivityProfile() {
        if (user_settings.user_activity_profile_id_list != null && !TextUtils.isEmpty(user_settings.user_activity_profile_id_list)) {
            String[] answer = user_settings.user_activity_profile_id_list.split(",");
            return answer;
        }
        return new String[0];
    }

    public class DataInbox implements Serializable {
        //public HashMap<String,String> unread;
        public int unread;

        public int getUnread() {
            /*if(unread==null)
                return 0;

            return  0;*/
            return unread;
        }
        /*public DataInbox() {
            this.unread = 0;
        }*/
    }

    public HashMap<String, DataEventHolder> getAllEvents() {
        if (my_events == null) {
            my_events = new HashMap<>();
        }

        return my_events;
    }

    public int getTotalEventsCount() {
        int answer = 0;
        for (Map.Entry<String, DataEventHolder> entry : getAllEvents().entrySet()) {
            if (entry.getValue().count > 0) {
                answer += entry.getValue().count;
                break;
            }
        }
        return answer;
    }

    public DataEventHolder getEventHolderByKey(String key) {
        DataEventHolder dataEventHolder = my_events.get(key);

        if (dataEventHolder == null) {
            dataEventHolder = new DataEventHolder();
        }

        return dataEventHolder;
    }

    public boolean hasActivity(String clientId) {
        for (DataActivity dataActivity : getUserActivities()) {
            if (dataActivity.client_id.equals(clientId))
                return true;
        }
        return false;
    }

    /* this method get and */
    public void addOrUpdateEventIntoMap(DataEvent newDataEvent, String intoKey) {
        DataEventHolder dataEventHolder = my_events.get(intoKey);

        if (dataEventHolder == null) {
            dataEventHolder = new DataEventHolder();
            /*add to hashMap*/
            dataEventHolder.addEvent(newDataEvent);
            my_events.put(intoKey, dataEventHolder);
            return;
        }
        int i = 0;
        for (DataEvent dataEvent : dataEventHolder.getEvents()) {
            if (dataEvent._id.equals(newDataEvent._id)) {
                /* here we updating the old event */
                dataEventHolder.getEvents().set(i, newDataEvent);
                return;
            }
            i++;
        }

        /*this method knows to increment the count filed*/
        dataEventHolder.addEvent(newDataEvent);
    }

    public void removeEventFromMap(DataEvent newDataEvent, String fromKey) {
        DataEventHolder dataEventHolder = my_events.get(fromKey);

        if (dataEventHolder != null) {

            int i = 0;
            for (DataEvent dataEvent : dataEventHolder.getEvents()) {
                if (dataEvent._id.equals(newDataEvent._id)) {
                /* here we updating the old event */
                    dataEventHolder.getEvents().remove(i);
                    return;
                }
                i++;
            }
        }
    }


    /*this method return's only user answered activities*/
    public ArrayList<DataActivity> getUserActivities() {
        ArrayList<DataActivity> answer = new ArrayList<>();

        // Get activities from local store
        DataActivity[] activities = DataStore.getInstance().getApplicationData().getActivities();

        // Filter only the ones that the user attached to
        String[] activityIds = DataStore.getInstance().getUser().getAnswerActivityProfile();

        for (DataActivity dataActivity : activities) {
            for (String activityId : activityIds) {
                if (activityId.equals(dataActivity.client_id)) {
                    answer.add(dataActivity);
                }
            }
        }

        return answer;
    }

}
