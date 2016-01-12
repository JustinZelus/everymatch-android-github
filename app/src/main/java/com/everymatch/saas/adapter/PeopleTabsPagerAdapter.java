package com.everymatch.saas.adapter;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.UserEventStatus;
import com.everymatch.saas.server.Data.DataEvent;
import com.everymatch.saas.server.Data.DataPeopleHolder;
import com.everymatch.saas.ui.base.BasePeopleListFragment;
import com.everymatch.saas.ui.event.InviteParticipantsListFragment;
import com.everymatch.saas.ui.event.MyPeopleListFragment;
import com.everymatch.saas.ui.event.ParticipantsListFragment;
import com.everymatch.saas.util.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Yossi on 09/07/2015.
 */
public class PeopleTabsPagerAdapter extends FragmentStatePagerAdapter {
    private ArrayList<String> tabTitles;
    private ArrayList<DataPeopleHolder> mPeopleHolderArray;
    /*so we could know what fragment to create*/
    private int screenType;
    /*for participants type*/
    private DataEvent mEvent;

    public ArrayList<BasePeopleListFragment> fragments;

    public PeopleTabsPagerAdapter(int screenType, FragmentManager fm, DataEvent event, HashMap<String, DataPeopleHolder> people) {
        super(fm);
        this.screenType = screenType;
        tabTitles = new ArrayList();
        mPeopleHolderArray = new ArrayList<>(people.size());
        buildData(event, people);
    }

    private void buildData(DataEvent event, HashMap<String, DataPeopleHolder> people) {

        /* Participants screen should be build in a specific ordering, hence iterate over mPeopleHolderArray*/
        if (screenType == DataStore.SCREEN_TYPE_EVENT_PARTICIPANTS) {
            mPeopleHolderArray = new ArrayList<>();

            for (int i = 0; i < 4; i++) {

                /* ONLY If User Event status is HOSTING - we can preceed to next iteration with "pending" */
                if (i == 3 && !UserEventStatus.TYPE_HOSTING.equals(event.dataPublicEvent.user_event_status.status)) {
                    continue;
                }

                String participantsType = Utils.participantsTabPositionToType(i);
                mPeopleHolderArray.add(people.get(participantsType));
                tabTitles.add(Utils.participantsTypeToTabTitle(participantsType));
            }

        } else {
            /*My People or Invite Participants*/
            for (Map.Entry<String, DataPeopleHolder> entry : people.entrySet()) {
                tabTitles.add(DataManager.getInstance().getResourceText(entry.getKey()));
                if (entry.getValue() == null)
                    entry.setValue(new DataPeopleHolder());
                mPeopleHolderArray.add(entry.getValue());
            }
        }

        this.mEvent = event;

        makeFragments();
    }

    /**
     * this method generates the right fragment from the begining so we can
     * get to them later...
     */
    private void makeFragments() {
        fragments = new ArrayList<>();

        for (int i = 0; i < tabTitles.size(); ++i) {
            BasePeopleListFragment fragment = null;

            if (screenType == DataStore.SCREEN_TYPE_EVENT_PARTICIPANTS) {
                fragment = ParticipantsListFragment.getInstance(Utils.participantsTabPositionToType(i), mEvent);
            } else if (screenType == DataStore.SCREEN_TYPE_FRIENDS) {
                fragment = MyPeopleListFragment.getInstance(mPeopleHolderArray.get(i), MyPeopleListFragment.positionToType(i));
            } else if (screenType == DataStore.SCREEN_TYPE_INVITE_PARTICIPANTS) {
                fragment = InviteParticipantsListFragment.getInstance(InviteParticipantsListFragment.positionToType(i), mEvent);
            }

            fragments.add(fragment);
        }
    }

    @Override
    public int getCount() {
        return tabTitles.size();
    }

    @Override
    public BasePeopleListFragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles.get(position).toUpperCase();
    }
}
