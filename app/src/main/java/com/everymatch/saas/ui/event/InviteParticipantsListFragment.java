package com.everymatch.saas.ui.event;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.everymatch.saas.adapter.PeopleUsersAdapter;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.Data.DataEvent;
import com.everymatch.saas.server.Data.DataPeopleHolder;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.server.requests.RequestDiscover;
import com.everymatch.saas.server.requests.RequestGetInvitees;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.server.responses.ResponseBestMatches;
import com.everymatch.saas.server.responses.ResponseInvitees;
import com.everymatch.saas.ui.base.BasePeopleListFragment;
import com.everymatch.saas.util.EMLog;

/**
 * Created by dors on 11/17/15.
 */
public class InviteParticipantsListFragment extends BasePeopleListFragment {

    public final String TAG = getClass().getName();

    private static final String EXTRA_EVENT = "extra.event";
    private static final String EXTRA_PARTICIPANT_TYPE = "extra.participant.type";

    // participants types
    public static final String TYPE_BEST_MATCH = "Best Match";
    public static final String TYPE_FRIENDS = "Friends";

    private DataEvent mDataEvent;
    private String mParticipantType;

    public static String positionToType(int position) {
        switch (position) {
            case 0:
                return TYPE_BEST_MATCH;
            default:
                return TYPE_FRIENDS;
        }
    }

    /**
     * InviteParticipantsListFragment instance
     */
    public static InviteParticipantsListFragment getInstance(String participantType, DataEvent dataEvent) {
        InviteParticipantsListFragment participantsListFragment = new InviteParticipantsListFragment();
        Bundle args = new Bundle(2);
        args.putSerializable(EXTRA_EVENT, dataEvent);
        args.putString(EXTRA_PARTICIPANT_TYPE, participantType);
        participantsListFragment.setArguments(args);
        return participantsListFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMode = DataStore.ADAPTER_MODE_COUNTER;
        mDataEvent = (DataEvent) getArguments().getSerializable(EXTRA_EVENT);
        mParticipantType = getArguments().getString(EXTRA_PARTICIPANT_TYPE);
    }

    @Override
    protected void setHeader() {
        super.setHeader();
        mEventHeader.setVisibility(View.GONE);
    }

    @Override
    protected void setActionButtons() {
        mActionButtonPrimary.setVisibility(View.GONE);
        mActionButtonSecondary.setVisibility(View.GONE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setUsers();
    }

    private void setUsers() {
        fetchNextPage();
    }

    @Override
    protected void fetchNextPage() {

        RequestGetInvitees baseRequest = new RequestGetInvitees(mDataEvent._id, mParticipantType, mAdapter.getCount(), PAGE_COUNT);

        ServerConnector.getInstance().processRequest(baseRequest, new ServerConnector.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                EMLog.i(TAG, "onSuccess");
                if (mParticipantType.equals(TYPE_BEST_MATCH)) {
                    ResponseBestMatches responseBestMatches = (ResponseBestMatches) baseResponse;

                    if (responseBestMatches == null) {
                        setNoMoreResults();
                    } else {
                        InviteParticipantsListFragment.super.addUsersToAdapter(responseBestMatches.users.getUsers());
                    }
                } else {
                    ResponseInvitees responseInvitees = (ResponseInvitees) baseResponse;

                    if (responseInvitees == null) {
                        setNoMoreResults();
                    } else {
                        InviteParticipantsListFragment.super.addUsersToAdapter(responseInvitees.getUsers());
                    }
                }

                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(ErrorResponse errorResponse) {
                EMLog.i(TAG, "onFailure");
            }
        }, TAG + RequestDiscover.class.getSimpleName());
    }

    @Override
    public PeopleUsersAdapter createAdapter() {
        mPeopleHolder = new DataPeopleHolder();
        return new PeopleUsersAdapter(getActivity(), mPeopleHolder.getUsers(), DataStore.ADAPTER_MODE_COUNTER);
    }

    public String getSelectedIds() {
        return mAdapter.getSelectedIds();
    }
}

