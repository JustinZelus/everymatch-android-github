package com.everymatch.saas.ui.event;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.everymatch.saas.adapter.PeopleUsersAdapter;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.Data.DataPeople;
import com.everymatch.saas.server.Data.DataPeopleHolder;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.server.requests.RequestAddFriend;
import com.everymatch.saas.server.requests.RequestDeleteFriend;
import com.everymatch.saas.server.requests.RequestPeople;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.server.responses.ResponsePeople;
import com.everymatch.saas.ui.base.BasePeopleListFragment;
import com.everymatch.saas.util.EMLog;

/**
 * Created by PopApp_laptop on 28/10/2015.
 */
public class MyPeopleListFragment extends BasePeopleListFragment {

    public static final String TAG = MyPeopleListFragment.class.getSimpleName();

    /*we neeed to know to witch api we call (My Friends, Recently Viewed)*/
    private static final String EXTRA_PEOPLE_TYPE = "extra.people.type";

    // participants types
    public static final String TYPE_MY_FRIENDS = "My Friends";
    public static final String TYPE_RECENTLY_VIEWED = "Recently Viewed";

    /*(My Friends, Recently Viewed)*/
    private String mPeopleType;

    public static String positionToType(int position) {
        switch (position) {
            case 0:
                return TYPE_RECENTLY_VIEWED;
            default:
                return TYPE_MY_FRIENDS;
        }
    }

    /**
     * New instance
     */
    public static BasePeopleListFragment getInstance(DataPeopleHolder holder, String peopleType) {
        MyPeopleListFragment myPeopleListFragment = new MyPeopleListFragment();
        Bundle args = new Bundle(2);
        args.putSerializable(EXTRA_PEOPLE_HOLDER, holder);
        args.putString(EXTRA_PEOPLE_TYPE, peopleType);
        myPeopleListFragment.setArguments(args);
        return myPeopleListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*the mode for our adapter*/
        mMode = DataStore.ADAPTER_MODE_LIKE;
        /*for the api call*/
        mPeopleType = getArguments().getString(EXTRA_PEOPLE_TYPE);
        /*first data*/
        mPeopleHolder = (DataPeopleHolder) getArguments().getSerializable(EXTRA_PEOPLE_HOLDER);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //mAbsListView.setEmptyView();
    }

    @Override
    protected void setEmptyView() {
        if (mAdapter.getCount() == 0) {
            View v = new View(getActivity());
            mEmptyViewContainer.addView(v);
            mEmptyViewContainer.setVisibility(View.VISIBLE);
        } else {
            mEmptyViewContainer.removeAllViews();
        }
    }

    @Override
    protected void initAdapter() {
        super.initAdapter();

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        /*here we register ourself to the feart click event
        * relevant only to this kind of fragment*/
        // Handle favorite / Unfavorites
        mAdapter.setIconListener(new PeopleUsersAdapter.IconListener() {
            @Override
            public void onIconClick(DataPeople user, int position) {
                addOrCancelFriendship(user);
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * Make a request for cancel or add friendship with this user
     */
    private void addOrCancelFriendship(final DataPeople user) {

        if (user.is_friend) {

            user.is_friend = false;

            ServerConnector.getInstance().processRequest(new RequestDeleteFriend(user.users_id), new ServerConnector.OnResultListener() {
                @Override
                public void onSuccess(BaseResponse baseResponse) {
                    EMLog.i(TAG, "addOrCancelFriendship::RequestDeleteFriend - onSuccess");
                }

                @Override
                public void onFailure(ErrorResponse errorResponse) {
                    EMLog.i(TAG, "addOrCancelFriendship::RequestDeleteFriend - onFailure");
                    user.is_friend = true;
                    refreshAdapterAfterFriendshipRequest();
                }
            }, TAG + RequestDeleteFriend.class.getName());
        } else {

            user.is_friend = true;

            ServerConnector.getInstance().processRequest(new RequestAddFriend(user.users_id), new ServerConnector.OnResultListener() {

                @Override
                public void onSuccess(BaseResponse baseResponse) {
                    EMLog.i(TAG, "addOrCancelFriendship::RequestAddFriend - onSuccess");
                }

                @Override
                public void onFailure(ErrorResponse errorResponse) {
                    EMLog.i(TAG, "addOrCancelFriendship::RequestAddFriend - onFailure");
                    user.is_friend = false;
                    refreshAdapterAfterFriendshipRequest();
                }
            }, TAG + RequestAddFriend.class.getName());
        }
    }

    /**
     * Safe call for adapter refresh
     * called only when addOrCancelFriendship failed (update UI)
     */
    private void refreshAdapterAfterFriendshipRequest() {
        if (isAdded() && mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void setHeader() {
        mEventHeader.setVisibility(View.GONE);
    }

    /*comes from base list*/
    @Override
    protected void setActionButtons() {
        // Nothing in here
    }

    @Override
    public PeopleUsersAdapter createAdapter() {
        return new PeopleUsersAdapter(getActivity(), mPeopleHolder.getUsers(), mMode);
    }

    @Override
    protected void fetchNextPage() {
        RequestPeople requestPeople = new RequestPeople(mPeopleType, mAdapter.getCount(), PAGE_COUNT);
        ServerConnector.getInstance().processRequest(requestPeople, new ServerConnector.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                EMLog.i(TAG, "onSuccess");

                ResponsePeople responsePeople = (ResponsePeople) baseResponse;

                if (responsePeople == null) {
                    setNoMoreResults();
                } else {
                    MyPeopleListFragment.super.addUsersToAdapter(responsePeople.users);
                }

                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(ErrorResponse errorResponse) {
                EMLog.i(TAG, "onFailure");
            }
        }, TAG + RequestPeople.class.getSimpleName());
    }

    @Override
    protected boolean shouldFetchMoreData() {
        if (!mIsSearching && mPeopleHolder.count > mAdapter.getCount()) {
            return true;
        } else {
            return false;
        }
    }
}
