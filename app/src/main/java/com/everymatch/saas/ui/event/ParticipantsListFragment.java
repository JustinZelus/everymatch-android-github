package com.everymatch.saas.ui.event;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.adapter.PeopleUsersAdapter;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EventPeopleStatus;
import com.everymatch.saas.client.data.RoleType;
import com.everymatch.saas.client.data.UserEventStatus;
import com.everymatch.saas.server.Data.DataEvent;
import com.everymatch.saas.server.Data.DataPeople;
import com.everymatch.saas.server.Data.DataPeopleHolder;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.server.requests.BaseRequest;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.server.responses.ResponseEvent;
import com.everymatch.saas.singeltones.PeopleListener;
import com.everymatch.saas.ui.base.BasePeopleListFragment;
import com.everymatch.saas.ui.user.UserActivity;
import com.everymatch.saas.util.EMLog;

/**
 * Created by dors on 10/28/15.
 */
public class ParticipantsListFragment extends BasePeopleListFragment implements View.OnClickListener, PeopleListener {
    public final String TAG = getClass().getName();

    private static final String EXTRA_EVENT = "extra.event";
    private static final String EXTRA_PARTICIPANT_TYPE = "extra.participant.type";

    private DataEvent mDataEvent;
    private String mParticipantType;

    /**
     * Discover instance
     */
    public static BasePeopleListFragment getInstance(String participantType, DataEvent dataEvent) {
        ParticipantsListFragment participantsListFragment = new ParticipantsListFragment();
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
        setUsers();
    }

    @Override
    protected void setHeader() {
        super.setHeader();
        mEventHeader.setVisibility(View.GONE);
    }

    private void setUsers() {

        switch (mParticipantType) {
            case EventPeopleStatus.TYPE_PARTICIPATING:
            case EventPeopleStatus.TYPE_MAYBE:
            case EventPeopleStatus.TYPE_INVITED:
            case EventPeopleStatus.TYPE_PENDING:

                mPeopleHolder = mDataEvent.dataPublicEvent.participants.get(mParticipantType);
                break;

            default:
                mPeopleHolder = new DataPeopleHolder();

        }

    }

    @Override
    protected void fetchNextPage() {
        //addUsersToAdapter();
    }

    @Override
    public PeopleUsersAdapter createAdapter() {
        /**
         *   all roles are:
         *   participating -> i'm a participant
         *   host          -> i'v created this event
         *   guest         -> just a guest
         *   manager       -> host set me as a manager
         */

        String role = mDataEvent.getRole_name();
        EMLog.d(TAG, "Role is: " + role);

        if (RoleType.TYPE_HOST.equals(role) || RoleType.TYPE_MANAGER.equals(role) || RoleType.TYPE_OWNER.equals(role))
            return new PeopleUsersAdapter(getActivity(), mPeopleHolder.getUsers(), DataStore.ADAPTER_MODE_COUNTER);
        else
            return new PeopleUsersAdapter(getActivity(), mPeopleHolder.getUsers(), DataStore.ADAPTER_MODE_NONE, this);

    }

    @Override
    protected void setActionButtons() {

        mActionButtonPrimary.setOnClickListener(this);
        mActionButtonSecondary.setOnClickListener(this);

        String status = mDataEvent.dataPublicEvent.user_event_status.status;

        if (UserEventStatus.TYPE_HOSTING.equals(status) || UserEventStatus.TYPE_MANAGER.equals(status)) {

            mActionButtonPrimary.setVisibility(View.VISIBLE);

            // Indicates the type of the column
            switch (mParticipantType) {
                case EventPeopleStatus.TYPE_PARTICIPATING:
                    mActionButtonPrimary.setText("REJECT");
                    break;
                case EventPeopleStatus.TYPE_MAYBE:
                    mActionButtonPrimary.setText("REMOVE");
                    break;
                case EventPeopleStatus.TYPE_INVITED:
                    mActionButtonPrimary.setText("CANCEL");
                    break;
                case EventPeopleStatus.TYPE_PENDING:
                    mActionButtonPrimary.setText("ACCEPT");
                    mActionButtonSecondary.setText("CANCEL");
                    mActionButtonSecondary.setVisibility(View.VISIBLE);
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        String action = "";
        switch (v.getId()) {
            case R.id.fragment_list_action_button_primary:
                if (mAdapter.mSelectedIds.size() == 0) {
                    Toast.makeText(getActivity(), "No user was selected", Toast.LENGTH_SHORT).show();
                    return;
                }
                action = getAction(true);
                callServer(action);
                break;
            case R.id.fragment_list_action_button_secondary:
                if (mAdapter.mSelectedIds.size() == 0) {
                    Toast.makeText(getActivity(), "No user was selected", Toast.LENGTH_SHORT).show();
                    return;
                }
                action = getAction(false);
                callServer(action);
                break;
        }
    }

    /**
     * @param isPrimaryButton
     * @return the right action for there call
     */
    private String getAction(boolean isPrimaryButton) {
        switch (mParticipantType) {
            case EventPeopleStatus.TYPE_PARTICIPATING:
                return "remove";
            case EventPeopleStatus.TYPE_MAYBE:
                // TODO - ask michel about it.
                return "remove_maybe";
            // break;
            case EventPeopleStatus.TYPE_INVITED:
                return "cancel_invitation";
            case EventPeopleStatus.TYPE_PENDING:
                // TODO - ask michel/stephan
                return isPrimaryButton ? "" : "";
        }
        return null;
    }

    private void callServer(String action) {
        final String ids = mAdapter.getSelectedIds();
        /*
        ServerConnector.getInstance().processRequest(new RequestEventActions(ids, mDataEvent._id, action), new ServerConnector.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                Toast.makeText(getActivity(), "RequestLeaveEvent success", Toast.LENGTH_SHORT).show();

                ResponseEvent responseEvent = (ResponseEvent) baseResponse;
                if (responseEvent != null) {
                    Toast.makeText(getActivity(), "reject Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "events = null", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(ErrorResponse errorResponse) {
                Toast.makeText(getActivity(), errorResponse.getServerRawResponse(), Toast.LENGTH_SHORT).show();
            }
        });
*/
        ServerConnector.getInstance().processRequest(new BaseRequest() {
            @Override
            public String getServiceUrl() {
                return Constants.API_SERVICE_URL;
            }

            @Override
            public String getUrlFunction() {
                return "api/eventactions?app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id)
                        + "&hl=" + DataStore.getInstance().getCulture()
                        + "&object_id=" + mDataEvent._id
                        + "&participant_id=" + ids
                        + "&invitation_note=";
            }

            @Override
            public Class getResponseClass() {
                return ResponseEvent.class;
            }

            @Override
            public int getType() {
                return Request.Method.PUT;
            }
        }, new ServerConnector.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                Toast.makeText(getActivity(), "RequestLeaveEvent success", Toast.LENGTH_SHORT).show();

                ResponseEvent responseEvent = (ResponseEvent) baseResponse;
                if (responseEvent != null) {
                    Toast.makeText(getActivity(), "reject Success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "events = null", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(ErrorResponse errorResponse) {
                Toast.makeText(getActivity(), errorResponse.getServerRawResponse(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onUserClick(DataPeople user) {
        UserActivity.openOtherUserFragment(getActivity(), user);
    }

    @Override
    public void onViewAllUsersClick(DataPeopleHolder holder) {

    }
}
