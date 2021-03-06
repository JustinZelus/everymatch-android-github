package com.everymatch.saas.ui;


import android.app.Activity;
import android.content.Intent;
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
import com.everymatch.saas.ui.discover.DiscoverActivity;
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
public class PeopleViewPagerFragment extends BaseFragment implements EventHeader.OnEventHeader, TextWatcher, EmptyViewFactory.ButtonListener, InviteParticipantsListFragment.InviteParticipantCallBack {
    public static final String TAG = PeopleViewPagerFragment.class.getSimpleName();

    public static String ARG_SCREEN_TYPE = "arg_screen_type";
    public static String ARG_EVENT = "arg_event";
    public static final String ACTION_CHECK_PARTICIPANTS = "action.check.participants";
    public static final String EXTRA_EVENT = "extra.event";

    DataStore ds = DataStore.getInstance();
    /* tells me what type of screen to show (my friend / participants / invite participants) */
    private int pagerScreenType;

    /*data for participants mode*/
    public DataEvent mDataEvent;

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
        mTabLayout.setBackgroundColor(ds.getIntColor(EMColor.WHITE));
        mTabLayout.setSelectedTabIndicatorColor(ds.getIntColor(EMColor.PRIMARY));
        mTabLayout.setTabTextColors(ds.getIntColor(EMColor.NIGHT), ds.getIntColor(EMColor.PRIMARY));

        /*how many fragment to save in memory*/
        viewPager.setOffscreenPageLimit(4);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getActivity() instanceof DiscoverActivity) {
            ((DiscoverActivity) getActivity()).setSelectedMenuItem(DiscoverActivity.DISCOVER_MENU_ITEMS.PEOPLE);
            mHeader = (EventHeader) getActivity().findViewById(R.id.eventHeader);
        } else {
            mHeader = (EventHeader) view.findViewById(R.id.participants_eventHeader);
        }

        setHeader();
        /*lets load some content into our adapter*/
        getPeople(0);
    }

    private void setHeader() {
        mHeader.setVisibility(View.VISIBLE);
        mHeader.setListener(this);
        mHeader.getBackButton().setText(Consts.Icons.icon_New_Close);
        mHeader.getIconOne().setVisibility(View.GONE);
        mHeader.getIconTwo().setVisibility(View.GONE);
        mHeader.getIconThree().setText(Consts.Icons.icon_Search);
        mHeader.getTitle().setOnClickListener(null);
        mHeader.setArrowDownVisibility(false);
        mHeader.getEditTitle().addTextChangedListener(this);
        if (pagerScreenType == DataStore.SCREEN_TYPE_FRIENDS)
            mHeader.setTitle(DataManager.getInstance().getResourceText(R.string.People));
        else if (pagerScreenType == DataStore.SCREEN_TYPE_EVENT_PARTICIPANTS)
            mHeader.setTitle(DataManager.getInstance().getResourceText(R.string.Participants));
        else if (pagerScreenType == DataStore.SCREEN_TYPE_INVITE_PARTICIPANTS)
            mHeader.setTitle(DataManager.getInstance().getResourceText(R.string.Invite_Participants));

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
                mAdapter = new PeopleTabsPagerAdapter(pagerScreenType, getChildFragmentManager(), mDataEvent, mDataEvent.dataPublicEvent.getParticipants());
            }

        } else if (pagerScreenType == DataStore.SCREEN_TYPE_INVITE_PARTICIPANTS) {
            HashMap hashMap = new HashMap();
            hashMap.put(InviteParticipantsListFragment.TYPE_BEST_MATCH, new DataPeopleHolder());
            hashMap.put(InviteParticipantsListFragment.TYPE_FRIENDS, new DataPeopleHolder());
            mAdapter = new PeopleTabsPagerAdapter(DataStore.SCREEN_TYPE_INVITE_PARTICIPANTS, getChildFragmentManager(), mDataEvent, hashMap);
        } else if (pagerScreenType == DataStore.SCREEN_TYPE_EVENT_ACTION_INVITE) {
            /*don't show */
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
        //getActivity().onBackPressed();
        getActivity().getSupportFragmentManager().popBackStackImmediate();
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
            mHeader.setTitle(dm.getResourceText(R.string.People));
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
        if (getActivity() instanceof DiscoverActivity) {
            ((DiscoverActivity) getActivity()).dmiDiscover.performClick();
            return;
        }
        getActivity().onBackPressed();
    }

    @Override
    public void onEmptyViewSecondButtonClick() {
        UserActivity.openMyProfileFragment(getActivity());
    }

    //when inviteParticipantsListFragment informs a click, we need to know
    //about it and tell it if it's the max selection
    @Override
    public int giveMeTotalSelection() {
        int bestMatchSelection = mAdapter.getItem(0).numOfSelectedPeople;
        int likedSelection = mAdapter.getItem(1).numOfSelectedPeople;
        return bestMatchSelection + likedSelection;
    }

    public void update(DataEvent dataEvent) {
        //notify event fragment about new change
        if (getTargetFragment() != null)
            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent(ACTION_CHECK_PARTICIPANTS).putExtra(EXTRA_EVENT, mDataEvent));

    }

    public interface ParticipantsCallback {
        void onParticipantsChanged(DataEvent dataEvent);
    }
}
