package com.everymatch.saas.ui.event;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.adapter.PeopleUsersAdapter;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.Participation_Type;
import com.everymatch.saas.client.data.RoleType;
import com.everymatch.saas.client.data.UserEventStatus;
import com.everymatch.saas.server.Data.DataEvent;
import com.everymatch.saas.server.Data.DataPeople;
import com.everymatch.saas.server.Data.DataPeopleHolder;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.server.requests.BaseRequest;
import com.everymatch.saas.server.requests.GsonRequest;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.server.responses.ResponseEvent;
import com.everymatch.saas.singeltones.PeopleListener;
import com.everymatch.saas.singeltones.Preferences;
import com.everymatch.saas.ui.PeopleViewPagerFragment;
import com.everymatch.saas.ui.base.BasePeopleListFragment;
import com.everymatch.saas.ui.user.UserActivity;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.NotifierPopup;
import com.everymatch.saas.util.Utils;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by dors on 10/28/15.
 */
public class ParticipantsListFragment extends BasePeopleListFragment implements View.OnClickListener, PeopleListener, PeopleUsersAdapter.PeopleUsersAdapterCallback {
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        onSelectionMade(0);
    }

    @Override
    protected void setHeader() {
        super.setHeader();
        mEventHeader.setVisibility(View.GONE);
    }

    private void setUsers() {

        switch (mParticipantType) {
            case Participation_Type.PARTICIPATING:
            case Participation_Type.MAYBE:
            case Participation_Type.INVITED:
            case Participation_Type.PENDING:

                mPeopleHolder = mDataEvent.dataPublicEvent.getParticipants().get(mParticipantType);
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
            return new PeopleUsersAdapter(getActivity(), mPeopleHolder.getUsers(), DataStore.ADAPTER_MODE_COUNTER, mDataEvent, this);
        else
            return new PeopleUsersAdapter(getActivity(), mPeopleHolder.getUsers(), DataStore.ADAPTER_MODE_NONE, this, mDataEvent, this);

    }

    @Override
    protected void setActionButtons() {

        mActionButtonPrimary.setOnClickListener(this);
        mActionButtonSecondary.setOnClickListener(this);

        String status = mDataEvent.dataPublicEvent.user_event_status.status;

        if (UserEventStatus.TYPE_HOSTING.equals(status) || UserEventStatus.TYPE_MANAGER.equals(status)) {

            //we want to show emptyFooterView at the bottom so we can select the last user
            mShowEmptyFooterView = true;

            mActionButtonPrimary.setVisibility(View.VISIBLE);

            // Indicates the type of the column
            switch (mParticipantType) {
                case Participation_Type.PARTICIPATING:
                    mActionButtonPrimary.setText(dm.getResourceText(R.string.Remove));
                    break;
                case Participation_Type.MAYBE:
                    mActionButtonPrimary.setText(dm.getResourceText(R.string.Remove));
                    break;
                case Participation_Type.INVITED:
                    mActionButtonPrimary.setText(dm.getResourceText(R.string.Cancel));
                    break;
                case Participation_Type.PENDING:
                    mActionButtonPrimary.setText(dm.getResourceText(R.string.Accept_Invitation));
                    mActionButtonSecondary.setText(dm.getResourceText(R.string.Reject_Invitation));
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
                if (mAdapter.getSelectedCount() == 0) {
                    Toast.makeText(getActivity(), "No user was selected", Toast.LENGTH_SHORT).show();
                    return;
                }
                action = getAction(true);
                callServer(action);
                break;
            case R.id.fragment_list_action_button_secondary:
                if (mAdapter.getSelectedCount() == 0) {
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
            case Participation_Type.PARTICIPATING:
                return "remove";
            case Participation_Type.MAYBE:
                // TODO - ask michel about it.
                return "remove_maybe";
            // break;
            case Participation_Type.INVITED:
                return "cancel_invitation";
            case Participation_Type.PENDING:
                return isPrimaryButton ? "accept_request" : "reject_request";
        }
        return null;
    }

    private void callServer(final String action) {
        final String ids = mAdapter.getSelectedIds();

        ServerConnector.getInstance().processRequest(new BaseRequest() {
            @Override
            public String getServiceUrl() {
                return Constants.getAPI_SERVICE_URL();
            }

            @Override
            public String getUrlFunction() {
                return "api/eventactions?app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id)
                        + "&hl=" + DataStore.getInstance().getCulture();
                       /* + "&object_id=" + mDataEvent._id
                        + "&participant_id=" + ids
                        + "&invitation_note=";*/
            }

            @Override
            public Class getResponseClass() {
                return ResponseEvent.class;
            }

            @Override
            public int getType() {
                return Request.Method.PUT;
            }

            @Override
            public String getBodyContentType() {
                return GsonRequest.CONTENT_TYPE_X_URL_ENCODED;
            }

            @Override
            public String getEncodedBody() {
                Map m = new HashMap<>();
                m.put("action", action);
                m.put("object_id", mDataEvent._id);
                m.put("other_user_id", ids);
                JSONObject obj = new JSONObject(m);
                String str = obj.toString();
                return str;
            }

            @Override
            public Map<String, String> addExtraHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                String token = Preferences.getInstance().getTokenType();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        }, new ServerConnector.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                ResponseEvent responseEvent = (ResponseEvent) baseResponse;
                if (responseEvent != null) {
                    //delete removed users
                    for (String id : ids.split(",")) {
                        if (mAdapter.mSelectedIds.contains(id))
                            mAdapter.mSelectedIds.remove(id);
                    }
                    mDataEvent.setEvent(responseEvent);
                    onSelectionMade(mAdapter.getSelectedCount());
                    mAdapter.refreshData(mDataEvent.dataPublicEvent.getAllUsers(mParticipantType));

                    ((PeopleViewPagerFragment) getParentFragment()).mDataEvent = mDataEvent;
                    ((PeopleViewPagerFragment) getParentFragment()).update(mDataEvent);
                }
            }

            @Override
            public void onFailure(ErrorResponse errorResponse) {

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

    /**
     * called when user selects more users then spots
     */
    @Override
    public void onLimitExeeded() {
        NotifierPopup.Builder builder = new NotifierPopup.Builder(getActivity());
        builder.setDuration(3000);
        builder.setMessage(dm.getResourceText(R.string.MaxNumberOfPlaces));
        builder.setGravity(Gravity.TOP);
        builder.setType(NotifierPopup.TYPE_ERROR);
        builder.setView(getView());
        builder.setTopOffset(Utils.dpToPx(24));
        builder.show();
    }

    @Override
    public void onSelectionMade(int selectionCount) {
       //we don't need to show upper title when we are not hosting
        try {
            String status = mDataEvent.dataPublicEvent.user_event_status.status;
            if (!UserEventStatus.TYPE_HOSTING.equals(status) && !UserEventStatus.TYPE_MANAGER.equals(status)) {
                titleHolder.setVisibility(View.GONE);
                return;
            }
        } catch (Exception ex) {
            EMLog.e(TAG, ex.getMessage());
        }


        numOfSelectedPeople = selectionCount;
        setTitle("" + numOfSelectedPeople + "/" + mDataEvent.dataPublicEvent.getAllUsers(mParticipantType).size() + " " + dm.getResourceText("Match.Selected"));
        enableButtons(selectionCount != 0);
    }

    @Override
    public boolean canProceed() {
        return true;
    }

    private void enableButtons(boolean enabled) {
        ObjectAnimator.ofFloat(mActionButtonPrimary, View.ALPHA.getName(), enabled ? 1.0f : 0.5f).start();
        ObjectAnimator.ofFloat(mActionButtonSecondary, View.ALPHA.getName(), enabled ? 1.0f : 0.5f).start();

        mActionButtonPrimary.setOnClickListener(enabled ? this : null);
        mActionButtonSecondary.setOnClickListener(enabled ? this : null);


    }

}
