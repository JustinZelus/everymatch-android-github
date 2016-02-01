package com.everymatch.saas.ui.event;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.everymatch.saas.R;
import com.everymatch.saas.server.Data.DataEvent;
import com.everymatch.saas.server.Data.DataPeople;
import com.everymatch.saas.server.Data.DataPeopleHolder;
import com.everymatch.saas.singeltones.PeopleListener;
import com.everymatch.saas.ui.BaseActivity;
import com.everymatch.saas.ui.user.UserActivity;
import com.google.gson.Gson;

public class EventActivity extends BaseActivity implements PeopleListener {

    private static final String TAG = EventActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        getSupportActionBar().hide();

        String event = getIntent().getExtras().getString(EventFragment.EVENT);
        DataEvent mEvent = new Gson().fromJson(event, DataEvent.class);

        EventFragment eventFragment = EventFragment.getInstance(mEvent);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.event_layout, eventFragment)
                .commitAllowingStateLoss();
    }

    public static void startActivity(Activity activity, DataEvent event) {

        Intent intent = new Intent(activity, EventActivity.class);
        intent.putExtra(EventFragment.EVENT, new Gson().toJson(event).toString());
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_up_animation, R.anim.slide_down_animation);
        //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");

        final int backStuckFragmentsNumber = getSupportFragmentManager().getBackStackEntryCount();
        if (backStuckFragmentsNumber > 0) {
            getSupportFragmentManager().popBackStackImmediate();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onUserClick(DataPeople user) {
        UserActivity.openOtherUserFragment(this, user);
    }

    @Override
    public void onViewAllUsersClick(DataPeopleHolder holder) {
        //View All is not visible aby more :)

        /*
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        transaction
                .addToBackStack("myFragment")
                .replace(R.id.event_layout, new PeopleViewPagerFragment())
                .commit();
        */
    }
}
