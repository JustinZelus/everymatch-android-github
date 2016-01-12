package com.everymatch.saas.ui.discover;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EventType;
import com.everymatch.saas.server.Data.DataEvent;
import com.everymatch.saas.server.Data.DataEventHolder;
import com.everymatch.saas.server.Data.DataPeople;
import com.everymatch.saas.server.Data.DataPeopleHolder;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.singeltones.EventListener;
import com.everymatch.saas.singeltones.PeopleListener;
import com.everymatch.saas.ui.BaseActivity;
import com.everymatch.saas.ui.event.EventActivity;
import com.everymatch.saas.ui.event.EventFragment;
import com.everymatch.saas.ui.user.UserActivity;
import com.everymatch.saas.util.EMLog;
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

    public static void startActivity(Activity activity, String extra, String id) {
        Intent intent = new Intent(activity, DiscoverActivity.class);
        intent.putExtra(extra, id);
        activity.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.abstract_activity);

        if (savedInstanceState == null) {
            mCurrentIntent = getIntent();
            //fetchData();
            goToWantedFragment();
        }
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
        if (requestCode == REQUEST_CODE_ME && resultCode == Activity.RESULT_OK){
            DiscoverFragment discoverFragment = (DiscoverFragment) findFragment(DiscoverFragment.TAG);
            if (discoverFragment != null){
                discoverFragment.refreshCurrentActivity();
            }
        }
    }
}
