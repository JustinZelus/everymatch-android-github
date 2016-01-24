package com.everymatch.saas.ui;


import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.everymatch.saas.R;
import com.everymatch.saas.adapter.PeopleTabsPagerAdapter;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.server.Data.DataEvent;
import com.everymatch.saas.server.Data.DataPeopleHolder;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.ui.base.BaseFragment;
import com.everymatch.saas.ui.base.BasePeopleListFragment;
import com.everymatch.saas.ui.event.InviteParticipantsListFragment;
import com.everymatch.saas.ui.user.UserActivity;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.EmptyViewFactory;
import com.everymatch.saas.view.EventHeader;

import java.io.Serializable;
import java.util.HashMap;


/**
 * A simple {@link Fragment} subclass.
 */
public class PeopleViewPagerFragment extends BaseFragment implements EventHeader.OnEventHeader, TextWatcher, EmptyViewFactory.ButtonListener {
    public static final String TAG = PeopleViewPagerFragment.class.getSimpleName();

    public static String ARG_SCREEN_TYPE = "arg_screen_type";
    public static String ARG_EVENT = "arg_event";

    DataStore ds = DataStore.getInstance();
    /* tells me what type of screen to show (my friend / participants / invite participants) */
    private int pagerScreenType;

    /*data for participants mode*/
    private DataEvent mDataEvent;

    /*Views*/
    private ViewPager viewPager;
    private EventHeader mHeader;
    public PeopleTabsPagerAdapter mAdapter;

    private TabLayout mTabLayout;
    private boolean isClicked = false;

    private View mEmptyView;

    public static PeopleViewPagerFragment getInstance(int pagerScreenType, DataEvent event) {
        PeopleViewPagerFragment answer = new PeopleViewPagerFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SCREEN_TYPE, pagerScreenType);
        args.putSerializable(ARG_EVENT, event);
        answer.setArguments(args);
        return answer;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.pagerScreenType = getArguments().getInt(ARG_SCREEN_TYPE);
        this.mDataEvent = (DataEvent) getArguments().getSerializable(ARG_EVENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people_view_pager, container, false);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mEmptyView = EmptyViewFactory.createEmptyView(EmptyViewFactory.TYPE_PEOPLE, this);
        ((FrameLayout) view.findViewById(R.id.fragment_people_view_page_empty_view_container)).addView(mEmptyView);
        mEmptyView.setVisibility(View.GONE);
        mTabLayout = (TabLayout) view.findViewById(R.id.sliding_tabs);
        mTabLayout.setBackgroundColor(DataStore.getInstance().getIntColor(EMColor.PRIMARY));
        mTabLayout.setSelectedTabIndicatorColor(DataStore.getInstance().getIntColor(EMColor.WHITE));
        /*how many fragment to save in memory*/
        viewPager.setOffscreenPageLimit(4);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mHeader = (EventHeader) view.findViewById(R.id.participants_eventHeader);
        mHeader.setListener(this);
        mHeader.getBackButton().setText(Consts.Icons.icon_ArrowBack);
        mHeader.getIconOne().setVisibility(View.GONE);
        mHeader.getIconTwo().setVisibility(View.GONE);
        mHeader.getIconThree().setText(Consts.Icons.icon_Search);
        mHeader.getEditTitle().addTextChangedListener(this);
        if (pagerScreenType == DataStore.SCREEN_TYPE_FRIENDS)
            mHeader.setTitle(DataManager.getInstance().getResourceText(R.string.People));
        else if (pagerScreenType == DataStore.SCREEN_TYPE_EVENT_PARTICIPANTS)
            mHeader.setTitle(DataManager.getInstance().getResourceText(R.string.Participants));
        else if (pagerScreenType == DataStore.SCREEN_TYPE_INVITE_PARTICIPANTS)
            mHeader.setTitle(DataManager.getInstance().getResourceText(R.string.Invite_Participants));

        /*lets load some content into our adapter*/
        getPeople(0);
    }

    private void getPeople(final int pagePosition) {
        if (pagerScreenType == DataStore.SCREEN_TYPE_FRIENDS) {

            mAdapter = new PeopleTabsPagerAdapter(pagerScreenType, getChildFragmentManager(), null/*Event here*/, ds.getUser().getPeople());

            // Show the people empty view above the tabs if no people at all
            if (!ds.getUser().hasPeople()) {
                mEmptyView.setVisibility(View.VISIBLE);
            }

        } else if (pagerScreenType == DataStore.SCREEN_TYPE_EVENT_PARTICIPANTS) {
            //participating, invited, maybe, pending, bestmatch
            String status = mDataEvent.dataPublicEvent.user_event_status.status;
            if (status != null) {
                mAdapter = new PeopleTabsPagerAdapter(pagerScreenType, getChildFragmentManager(), mDataEvent, mDataEvent.dataPublicEvent.participants);
            }

        } else if (pagerScreenType == DataStore.SCREEN_TYPE_INVITE_PARTICIPANTS) {
            HashMap hashMap = new HashMap();
            hashMap.put(InviteParticipantsListFragment.TYPE_BEST_MATCH, new DataPeopleHolder());
            hashMap.put(InviteParticipantsListFragment.TYPE_FRIENDS, new DataPeopleHolder());
            mAdapter = new PeopleTabsPagerAdapter(DataStore.SCREEN_TYPE_INVITE_PARTICIPANTS, getChildFragmentManager(), mDataEvent, hashMap);
        } else if (pagerScreenType == DataStore.SCREEN_TYPE_EVENT_ACTION_INVITE) {
            /*dont show */
            HashMap hashMap = new HashMap();
            hashMap.put(InviteParticipantsListFragment.TYPE_BEST_MATCH, new DataPeopleHolder());
            hashMap.put(InviteParticipantsListFragment.TYPE_FRIENDS, new DataPeopleHolder());
            mAdapter = new PeopleTabsPagerAdapter(DataStore.SCREEN_TYPE_INVITE_PARTICIPANTS, getChildFragmentManager(), mDataEvent, hashMap);
        }

        viewPager.setAdapter(mAdapter);
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.setupWithViewPager(viewPager);
        viewPager.setCurrentItem(pagePosition);
    }

    @Override
    public void onPause() {
        super.onPause();
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
    }

    @Override
    public void onBackButtonClicked() {
        getActivity().onBackPressed();
    }

    @Override
    public void onOneIconClicked() {

    }

    @Override
    public void onTwoIconClicked() {

    }

    @Override
    public void onThreeIconClicked() {

        if (!isClicked) {
            mHeader.getTitle().setVisibility(View.GONE);
            mHeader.getEditTitle().setVisibility(View.VISIBLE);
            mHeader.getEditTitle().requestFocus();
            mHeader.getEditTitle().setFocusable(true);

            isClicked = true;
        } else {
            mHeader.getTitle().setVisibility(View.VISIBLE);
            mHeader.setTitle("People");
            mHeader.getEditTitle().setVisibility(View.GONE);


            isClicked = false;
        }

        for (int i = 0; i < mAdapter.getCount(); i++) {
            BasePeopleListFragment fragment = mAdapter.getItem(i);
            fragment.onSearchIconClick();
        }
    }

    @Override
    protected void handleBroadcast(Serializable eventObject, String eventName) {
        try {
            ds.getUser().setPeople((HashMap<String, DataPeopleHolder>) eventObject);
            getPeople(viewPager.getCurrentItem());
        } catch (Exception ex) {
            EMLog.e(TAG, ex.getMessage());
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    @Override
    public void afterTextChanged(Editable s) {

        for (int i = 0; i < mAdapter.getCount(); i++) {
            BasePeopleListFragment fragment = mAdapter.getItem(i);
            fragment.performSearch(s.toString());
        }
    }

    @Override
    public void onEmptyViewFirstButtonClick() {
        getActivity().onBackPressed();
    }

    @Override
    public void onEmptyViewSecondButtonClick() {
        UserActivity.openMyProfileFragment(getActivity());
    }
}
