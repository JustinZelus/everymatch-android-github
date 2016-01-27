package com.everymatch.saas.singeltones;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.Data.DataChannelName;
import com.everymatch.saas.server.Data.DataChatMessage;
import com.everymatch.saas.server.Data.DataEvent;
import com.everymatch.saas.server.Data.DataPeopleHolder;
import com.everymatch.saas.server.responses.ResponseGetUser;
import com.everymatch.saas.util.EMLog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pusher.client.Pusher;
import com.pusher.client.channel.Channel;
import com.pusher.client.channel.ChannelEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by PopApp_laptop on 23/08/2015.
 */
public class PusherManager implements ConnectionEventListener {
    public final String TAG = getClass().getName();

    public static final String ACTION_PUSHER_EVENT = "action.pusher.event";
    public static final String EXTRA_PUSHER_EVENT_DATA = "extra.pusher.event.data";
    public static final String EXTRA_PUSHER_EVENT_NAME = "extra.pusher.event.name";

    //minor change
    private static final String tag = "PusherManager";
    public static final String PUSHER_EVENT_PROFILES = "profiles"; // ResponseGetUser.Profiles
    public static final String PUSHER_EVENT_PEOPLE = "people"; // ResponseGetUser.People
    public static final String PUSHER_EVENT_MY_EVENT = "my_event";
    public static final String PUSHER_EVENT_USER_SETTINGS = "user_settings";
    public static final String PUSHER_EVENT_USER_ACCOUNT = "user_account";
    public static final String PUSHER_EVENT_CHANNELS_ADD = "channels_add"; //DataChannelName.class
    public static final String PUSHER_EVENT_CHANNELS_REMOVE = "channels_remove"; //DataChannelName.class
    public static final String PUSHER_EVENT_MY_EVENT_UPDATE = "my_event_update";
    public static final String PUSHER_EVENT_MY_EVENT_REMOVE = "my_event_remove";
    public static final String PUSHER_EVENT_EVENT_ADD = "event_add";
    public static final String PUSHER_EVENT_EVENT_NEW_MESSAGE = "new_message";
    public static final String PUSHER_EVENT_EVENT_INBOX_UNREAD = "conversation_unread_changed"; //ResponseGetUser.DataInbox

    private static PusherManager instance;
    private Pusher pusher;
    public Map<String, Channel> channelMap;
    DataStore ds = DataStore.getInstance();

    public static PusherManager getInstance() {
        if (instance == null)
            instance = new PusherManager();
        return instance;
    }

    private PusherManager() {
        channelMap = new HashMap<String, Channel>();
        pusher = new Pusher(Constants.PUSHER_APP_KEY);

        pusher.connect(this, ConnectionState.ALL);
    }

    public Channel subscribe(String ch) {
        if (getChannel(ch) != null) {

            return getChannel(ch);
        }

        EMLog.i(tag, "Subscribing to: " + ch);
        Channel channel = pusher.subscribe(ch);
        channelMap.put(ch, channel);
        return channel;
    }

    public Channel getChannel(String ch) {
        Channel channel = channelMap.get(ch);
        return channel;
    }

    public void registerAll() {
        for (DataChannelName channelName : DataStore.getInstance().getUser().getChannels()) {

            if (getChannel(channelName.name) != null) {
                /* we already registered this channel
                 * can happen when we adding a channel
                 */
                return;
            }

            Channel channel = subscribe(channelName.name);

            channel.bind(PUSHER_EVENT_PROFILES, pusherListener);
            channel.bind(PUSHER_EVENT_PEOPLE, pusherListener);
            channel.bind(PUSHER_EVENT_MY_EVENT, pusherListener);
            channel.bind(PUSHER_EVENT_USER_SETTINGS, pusherListener);
            channel.bind(PUSHER_EVENT_USER_ACCOUNT, pusherListener);
            channel.bind(PUSHER_EVENT_CHANNELS_ADD, pusherListener);
            channel.bind(PUSHER_EVENT_CHANNELS_REMOVE, pusherListener);
            channel.bind(PUSHER_EVENT_MY_EVENT_UPDATE, pusherListener);
            channel.bind(PUSHER_EVENT_MY_EVENT_REMOVE, pusherListener);
            channel.bind(PUSHER_EVENT_EVENT_ADD, pusherListener);
            channel.bind(PUSHER_EVENT_EVENT_NEW_MESSAGE, pusherListener);
            channel.bind(PUSHER_EVENT_EVENT_INBOX_UNREAD, pusherListener);
        }
    }

    private void unRegisterChannel(DataChannelName ch) {
        Channel channel = subscribe(ch.name);

        channel.unbind(PUSHER_EVENT_PROFILES, pusherListener);
        channel.unbind(PUSHER_EVENT_PEOPLE, pusherListener);
        channel.unbind(PUSHER_EVENT_MY_EVENT, pusherListener);
        channel.unbind(PUSHER_EVENT_USER_SETTINGS, pusherListener);
        channel.unbind(PUSHER_EVENT_USER_ACCOUNT, pusherListener);
        channel.unbind(PUSHER_EVENT_CHANNELS_ADD, pusherListener);
        channel.unbind(PUSHER_EVENT_CHANNELS_REMOVE, pusherListener);
        channel.unbind(PUSHER_EVENT_MY_EVENT_UPDATE, pusherListener);
        channel.unbind(PUSHER_EVENT_MY_EVENT_REMOVE, pusherListener);
        channel.unbind(PUSHER_EVENT_EVENT_ADD, pusherListener);
        channel.unbind(PUSHER_EVENT_EVENT_NEW_MESSAGE, pusherListener);
        channel.unbind(PUSHER_EVENT_EVENT_INBOX_UNREAD, pusherListener);

    }

    ChannelEventListener pusherListener = new ChannelEventListener() {
        @Override
        public void onSubscriptionSucceeded(String channelName) {
            EMLog.i(tag, "onSubscriptionSucceeded: " + channelName);
        }

        @Override
        public void onEvent(String channelName, String eventName, String data) {

            data = handleEventData(data);

            Serializable serializable = pusherDataToObject(data, eventName);

            if (serializable != null) {
                /* Events that should only update data.*/

                /*add a channels*/
                if (PUSHER_EVENT_CHANNELS_ADD.equals(eventName)) {
                    ArrayList<DataChannelName> channelNames = (ArrayList<DataChannelName>) serializable;
                    if (channelNames != null) {
                        for (DataChannelName dataChannelName : channelNames) {
                            ds.getUser().addChannel(dataChannelName);
                        }
                        registerAll();
                    }
                    return;
                }
                /*remove a channel */
                if (PUSHER_EVENT_CHANNELS_REMOVE.equals(eventName)) {
                    ArrayList<DataChannelName> channelNames = (ArrayList<DataChannelName>) serializable;
                    if (channelName != null) {
                        for (DataChannelName dataChannelName : channelNames) {
                            /*remove this channel from user's channels*/
                            ds.getUser().removeChannel((DataChannelName) serializable);
                            /*unregister channel*/
                            unRegisterChannel((DataChannelName) serializable);
                        }
                    }
                    return;
                }

                /*update inbox*/
                if (PUSHER_EVENT_EVENT_INBOX_UNREAD.equals(eventName)) {
                    ds.getUser().setInbox((ResponseGetUser.DataInbox) serializable);
                    //return;
                }

                /*update user profile*/
                if (PUSHER_EVENT_PROFILES.equals(eventName)) {
                    ds.getUser().profiles = (ResponseGetUser.Profiles) serializable;
                    //return;
                }

                /*update user unread messages*/
                if (PUSHER_EVENT_EVENT_NEW_MESSAGE.equals(eventName)) {
                    ds.getUser().getInbox().unread++;
                }
                if (PUSHER_EVENT_MY_EVENT_UPDATE.equals(eventName)) {
                    try {
                        JSONObject jsonObject = new JSONObject(data);
                        ArrayList<String> filters = new Gson().fromJson(jsonObject.getJSONArray("filters").toString(), new TypeToken<ArrayList<String>>() {
                        }.getType());
                        DataEvent dataEvent = new Gson().fromJson(jsonObject.getJSONObject("data").toString(), DataEvent.class);
                        String action = jsonObject.getString("action");

                        if (action.equals("create") || action.equals("update") || action.equals("add")) {
                            if (filters != null && dataEvent != null) {
                                for (String filter : filters) {
                                    ds.getUser().addOrUpdateEventIntoMap(dataEvent, filter);
                                }
                            }
                        } else if (action.equals("remove")) {
                            if (filters != null && dataEvent != null) {
                                for (String filter : filters) {
                                    ds.getUser().removeEventFromMap(dataEvent, filter);
                                }
                            }
                        }

                    } catch (Exception ex) {
                        EMLog.e(TAG, ex.getMessage());
                    }
                }

                Intent intent = new Intent(ACTION_PUSHER_EVENT);
                intent.putExtra(EXTRA_PUSHER_EVENT_DATA, serializable);
                intent.putExtra(EXTRA_PUSHER_EVENT_NAME, eventName);
                LocalBroadcastManager.getInstance(EverymatchApplication.getContext()).sendBroadcast(intent);
            }
        }
    };

    @Override
    public void onConnectionStateChange(ConnectionStateChange change) {
        EMLog.i(tag, "State changed to " + change.getCurrentState() + " from " + change.getPreviousState());
        if (change.getCurrentState().toString().toUpperCase().equals("DISCONNECTING")) {
            EMLog.d(tag, "RECONECTING TO PUSHER...");
            pusher.connect(this, ConnectionState.ALL);
        }
    }

    @Override
    public void onError(String message, String code, Exception e) {
        EMLog.i(tag, "There was a problem connecting!");
    }

    private String handleEventData(String data) {
        data = data.substring(1, data.length() - 1);
        data = data.replaceAll("\\\\", "");
        return data;
    }

    private Serializable pusherDataToObject(String eventData, String eventName) {

        Serializable serializable = null;

        Gson gson = new Gson();

        switch (eventName) {
            case PUSHER_EVENT_PROFILES:
                serializable = gson.fromJson(eventData, ResponseGetUser.Profiles.class);
                break;

            case PUSHER_EVENT_PEOPLE:
                serializable = gson.fromJson(eventData, new TypeToken<HashMap<String, DataPeopleHolder>>() {
                }.getType());
                break;

            case PUSHER_EVENT_EVENT_NEW_MESSAGE:
                serializable = gson.fromJson(eventData, DataChatMessage.class);
                break;

            case PUSHER_EVENT_EVENT_INBOX_UNREAD:
                serializable = gson.fromJson(eventData, ResponseGetUser.DataInbox.class);
                break;
            case PUSHER_EVENT_CHANNELS_ADD:
            case PUSHER_EVENT_CHANNELS_REMOVE:
                try {
                    ArrayList<DataChannelName> channelNames = gson.fromJson(eventData, new TypeToken<ArrayList<DataChannelName>>() {
                    }.getType());
                    serializable = channelNames;
                } catch (Exception ex) {
                    EMLog.e(TAG, ex.getMessage());
                }

                break;

            case PUSHER_EVENT_MY_EVENT_UPDATE:
                // no need to use it: we will take the Json
                serializable = new Serializable() {
                };
                break;


        }

        return serializable;
    }
}
