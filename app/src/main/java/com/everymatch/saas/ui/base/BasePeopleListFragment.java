package com.everymatch.saas.ui.base;

import android.content.Context;
import android.view.View;

import com.everymatch.saas.adapter.EmBaseAdapter;
import com.everymatch.saas.adapter.PeopleUsersAdapter;
import com.everymatch.saas.server.Data.DataPeople;
import com.everymatch.saas.server.Data.DataPeopleHolder;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.singeltones.PeopleListener;

import java.util.ArrayList;

/**
 * Created by dors on 10/25/15.
 */
public abstract class BasePeopleListFragment extends BaseListFragment {

    public static final String TAG = BasePeopleListFragment.class.getSimpleName();

    // Constants
    protected static final String EXTRA_PEOPLE_HOLDER = "extra.people.holder";

    // Views


    // Data
    protected int totalCount;
    public int numOfSelectedPeople;
    protected PeopleUsersAdapter mAdapter;
    protected DataPeopleHolder mPeopleHolder;
    protected PeopleListener mPeopleListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof PeopleListener) {
            mPeopleListener = (PeopleListener) context;
        } else {
            throw new IllegalStateException("Context must implement PeopleListener");
        }
    }

    /**
     * Set header for this page
     */
    @Override
    protected void setHeader() {
        mEventHeader.setListener(this);
        mEventHeader.getBackButton().setText(Consts.Icons.icon_ArrowBack);
        mEventHeader.getIconOne().setVisibility(View.GONE);
        mEventHeader.getIconTwo().setVisibility(View.GONE);
        mEventHeader.getIconThree().setVisibility(View.GONE);
        mEventHeader.setTitle("please add title");
    }

    /**
     * Set the first data we already have in cache
     */
    @Override
    protected void initAdapter() {
        mAdapter = createAdapter();
        mAdapter.setListener(mPeopleListener);
        mAbsListView.setAdapter(mAdapter);
    }

    protected void addUsersToAdapter(ArrayList<DataPeople> users) {

        int currentCount = mPeopleHolder.getUsers().size();

        mPeopleHolder.getUsers().addAll(users);

        if (users.size() < PAGE_COUNT) {
            setNoMoreResults();
        }

        refreshAdapter();

        if (currentCount > 0) {
            mAdapter.setAnimatedPosition(currentCount);
        }
    }

    public abstract PeopleUsersAdapter createAdapter();

    protected void refreshAdapter() {
        mAdapter.notifyDataSetChanged();
        mLoading = false;
    }

    @Override
    public void onBackButtonClicked() {
        getActivity().onBackPressed();
    }

    @Override
    public void performSearch(String s) {
        if (mAdapter != null) {
            mAdapter.getFilter().filter(s);
        }
    }

    @Override
    public EmBaseAdapter getAdapter() {
        return mAdapter;
    }
}
