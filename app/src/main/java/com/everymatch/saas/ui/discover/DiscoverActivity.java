package com.everymatch.saas.ui.discover;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EventType;
import com.everymatch.saas.server.Data.DataActivity;
import com.everymatch.saas.server.Data.DataEvent;
import com.everymatch.saas.server.Data.DataEventHolder;
import com.everymatch.saas.server.Data.DataPeople;
import com.everymatch.saas.server.Data.DataPeopleHolder;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.singeltones.EventListener;
import com.everymatch.saas.singeltones.PeopleListener;
import com.everymatch.saas.ui.BaseActivity;
import com.everymatch.saas.ui.NotificationFragment;
import com.everymatch.saas.ui.PeopleViewPagerFragment;
import com.everymatch.saas.ui.event.EventActivity;
import com.everymatch.saas.ui.event.EventFragment;
import com.everymatch.saas.ui.event.MyEventsFragment;
import com.everymatch.saas.ui.user.UserActivity;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.view.DiscoverMenuItem;
import com.everymatch.saas.view.EventHeader;
import com.google.gson.Gson;


/**
 * Created by dors on 7/20/15.
 */
public class DiscoverActivity extends BaseActivity implements EventListener, PeopleListener, DiscoverFragment.DiscoverCallbacks {

    private static final String TAG = DiscoverActivity.class.getSimpleName();
    public static final String EXTRA_ACTIVITY_ID = TAG + ".extra.activity.id";
    public static final String EXTRA_EVENT_ID = TAG + ".extra.event.id";
    private static final int REQUEST_CODE_ME = 13;
    private Intent mCurrentIntent;
    public DataActivity currentActivity;
    public String currentActivityId;

    public enum DISCOVER_MENU_ITEMS {DISCOVER, EVENTS, PEOPLE, NOTIFICATIONS, MORE}

    //Views
    DiscoverMenuItem dmiDiscover, dmiEvents, dmiPeople, dmiNotifications, dmiMore;
    EventHeader mHeader;

    public static void startActivity(Activity activity, String extra, String id) {
        Intent intent = new Intent(activity, DiscoverActivity.class);
        intent.putExtra(extra, id);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover);

        initViews();
        if (savedInstanceState == null) {
            mCurrentIntent = getIntent();
            //fetchData();
            goToWantedFragment();
        }
    }

    private void initViews() {
        dmiDiscover = (DiscoverMenuItem) findViewById(R.id.dmiDiscover);
        dmiEvents = (DiscoverMenuItem) findViewById(R.id.dmiEvents);
        dmiPeople = (DiscoverMenuItem) findViewById(R.id.dmiPeople);
        dmiNotifications = (DiscoverMenuItem) findViewById(R.id.dmiNotification);
        dmiMore = (DiscoverMenuItem) findViewById(R.id.dmiMore);

        mHeader = (EventHeader) findViewById(R.id.eventHeader);

        dmiDiscover.setOnClickListener(onDmiClickListener);
        dmiEvents.setOnClickListener(onDmiClickListener);
        dmiPeople.setOnClickListener(onDmiClickListener);
        dmiNotifications.setOnClickListener(onDmiClickListener);
        dmiMore.setOnClickListener(onDmiClickListener);
    }

    public EventHeader getmHeader() {
        return mHeader;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mCurrentIntent = intent;
        goToWantedFragment();
    }

    @Override
    public void onEventClick(DataEvent event) {
        EventActivity.startActivity(this, event);
    }

    @Override
    public void onViewAllEventsClick(DataEventHolder dataEventHolder) {
        EMLog.i(TAG, "onViewAllEventsClick");

        DiscoverFragment discoverFragment = (DiscoverFragment) findFragment(DiscoverFragment.TAG);

        if (discoverFragment != null && discoverFragment.getCurrentActivityId() != null) {
            replaceFragment(R.id.fragment_container, DiscoverEventListFragment.getInstance(dataEventHolder, discoverFragment.getCurrentActivityId()),
                    DiscoverEventListFragment.TAG, true, null, R.anim.enter_from_right, R.anim.exit_to_left,
                    R.anim.enter_from_left, R.anim.exit_to_right);
        }
    }

    @Override
    public void onUserClick(DataPeople user) {
        UserActivity.openOtherUserFragment(this, user);
    }

    @Override
    public void onViewAllUsersClick(DataPeopleHolder holder) {
        EMLog.i(TAG, "onViewAllUsersClick");

        DiscoverFragment discoverFragment = (DiscoverFragment) findFragment(DiscoverFragment.TAG);

        if (discoverFragment != null && discoverFragment.getCurrentActivityId() != null) {
            replaceFragment(R.id.fragment_container, DiscoverPeopleListFragment.getInstance(holder, discoverFragment.getCurrentActivityId()),
                    DiscoverPeopleListFragment.TAG, true, null, R.anim.enter_from_right, R.anim.exit_to_left,
                    R.anim.enter_from_left, R.anim.exit_to_right);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ServerConnector.getInstance().cancelPendingRequests(TAG);
    }

    private void goToWantedFragment() {
        String id;
        if (mCurrentIntent != null) {

            id = mCurrentIntent.getStringExtra(EXTRA_ACTIVITY_ID);
            if (id != null) {
                replaceFragment(R.id.fragment_container, DiscoverFragment.newInstance(id), DiscoverFragment.TAG);
                return;
            }
            id = mCurrentIntent.getStringExtra(EXTRA_EVENT_ID);
            if (id != null) {
                for (DataEvent dataEvent : DataStore.getInstance().getUser().getEventHolderByKey(EventType.UPCOMING).getEvents())
                    if (dataEvent._id.equals(id)) {
                        Intent intent = new Intent(this, EventActivity.class);
                        intent.putExtra(EventFragment.EVENT, new Gson().toJson(dataEvent).toString());
                        startActivity(intent);
                        break;
                    }
                return;
            }
        } else
            Toast.makeText(DiscoverActivity.this, "There was no id to go to!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onMeClick() {
        UserActivity.openMeFragment(this, REQUEST_CODE_ME);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // In case user edited an activity, we need to refresh it in the DiscoverFragment
        if (requestCode == REQUEST_CODE_ME && resultCode == Activity.RESULT_OK) {
            DiscoverFragment discoverFragment = (DiscoverFragment) findFragment(DiscoverFragment.TAG);
            if (discoverFragment != null) {
                discoverFragment.refreshCurrentActivity();
            }
        }
    }


    public void setSelectedMenuItem(DISCOVER_MENU_ITEMS selectedMenuItem) {
        dmiDiscover.setSelected(false);
        dmiEvents.setSelected(false);
        dmiPeople.setSelected(false);
        dmiNotifications.setSelected(false);
        dmiMore.setSelected(false);

        switch (selectedMenuItem) {
            case DISCOVER:
                dmiDiscover.setSelected(true);
                break;
            case EVENTS:
                dmiEvents.setSelected(true);
                break;
            case PEOPLE:
                dmiPeople.setSelected(true);
                break;
            case NOTIFICATIONS:
                dmiNotifications.setSelected(true);
                break;
            case MORE:
                dmiMore.setSelected(true);
                break;
        }
    }

    View.OnClickListener onDmiClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.dmiDiscover:
                    replaceFragment(R.id.fragment_container, DiscoverFragment.newInstance(currentActivityId),
                            NotificationFragment.TAG, false, null, R.anim.enter_from_right, R.anim.exit_to_left,
                            R.anim.enter_from_left, R.anim.exit_to_right);
                    break;
                case R.id.dmiEvents:
                    replaceFragment(R.id.fragment_container, MyEventsFragment.getInstance(),
                            MyEventsFragment.TAG, false, null, R.anim.enter_from_right, R.anim.exit_to_left,
                            R.anim.enter_from_left, R.anim.exit_to_right);
                    break;
                case R.id.dmiPeople:
                    replaceFragment(R.id.fragment_container, PeopleViewPagerFragment.getInstance(DataStore.SCREEN_TYPE_FRIENDS, null),
                            PeopleViewPagerFragment.TAG, false, null, R.anim.enter_from_right, R.anim.exit_to_left,
                            R.anim.enter_from_left, R.anim.exit_to_right);
                    break;
                case R.id.dmiNotification:
                    replaceFragment(R.id.fragment_container, new NotificationFragment(),
                            NotificationFragment.TAG, false, null, R.anim.enter_from_right, R.anim.exit_to_left,
                            R.anim.enter_from_left, R.anim.exit_to_right);
                    break;
                case R.id.dmiMore:
                    UserActivity.openMeFragment(DiscoverActivity.this, 9);
                    break;
            }
        }
    };
}
