package com.everymatch.saas.ui.match;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.everymatch.saas.R;
import com.everymatch.saas.server.Data.DataEvent;
import com.everymatch.saas.server.Data.DataMatchResults;
import com.everymatch.saas.server.responses.ResponseOtherUser;
import com.everymatch.saas.ui.BaseActivity;

/**
 * Created by PopApp_laptop on 18/01/2016.
 */
public class MatchActivity extends BaseActivity {
    public static String EXTRA_MATCH_RESULTS = "extra.match.results";
    public static String EXTRA_DATA_PEOPLE = "extra.data.people";
    public static String EXTRA_DATA_EVENT = "extra.data.event";
    public static String EXTRA_MATCH_TYPE = "extra.match.type";

    //DATA
    public DataMatchResults mMatchResults;
    public ResponseOtherUser mDataPeople;
    public DataEvent mDataEvent;
    public String matchType;

    public static void start(Activity activity, DataMatchResults matchResults, String matchType, ResponseOtherUser otherUser, DataEvent dataEvent) {
        Intent starter = new Intent(activity, MatchActivity.class);
        starter.putExtra(EXTRA_MATCH_RESULTS, matchResults);
        starter.putExtra(EXTRA_DATA_PEOPLE, otherUser);
        starter.putExtra(EXTRA_DATA_EVENT, dataEvent);
        starter.putExtra(EXTRA_MATCH_TYPE, matchType);
        activity.startActivity(starter);
        //activity.overridePendingTransition(R.anim.fade_in_fast, R.anim.fade_out_fast);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.abstract_activity);
        mMatchResults = (DataMatchResults) getIntent().getSerializableExtra(EXTRA_MATCH_RESULTS);
        mDataPeople = (ResponseOtherUser) getIntent().getSerializableExtra(EXTRA_DATA_PEOPLE);
        mDataEvent = (DataEvent) getIntent().getSerializableExtra(EXTRA_DATA_EVENT);
        matchType = getIntent().getStringExtra(EXTRA_MATCH_TYPE);

        replaceFragment(R.id.fragment_container, new MatchFragment(), "");
    }
}
