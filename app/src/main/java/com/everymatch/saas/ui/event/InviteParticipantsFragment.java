package com.everymatch.saas.ui.event;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.everymatch.saas.R;
import com.everymatch.saas.adapter.PeopleTabsPagerAdapter;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.Data.DataEvent;
import com.everymatch.saas.server.Data.DataEventActions;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.server.requests.RequestEventActions;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.server.responses.ResponseEvent;
import com.everymatch.saas.ui.BaseActivity;
import com.everymatch.saas.ui.PeopleViewPagerFragment;
import com.everymatch.saas.ui.base.BaseFragment;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.ShapeDrawableUtils;

/**
 * Created by dors on 11/17/15.
 */
public class InviteParticipantsFragment extends BaseFragment implements View.OnClickListener {

    public static final String TAG = InviteParticipantsFragment.class.getSimpleName();
    private static final String EVENT_EXTRA = "event_extra";
    private static final String EXTRA_MAKE_ACTION_INSIDE = "extra.make.action.inside";
    private static final String EXTRA_ACTION = "extra.action";
    private static final String EXTRA_ACTION_POSITION = "extra.action.position";
    public static final String ACTION_INVITEES_SELECTED = "inviteesSelected";

    //Data
    private boolean makeActionInside;
    private String action;
    private DataEvent mEvent;
    private int dataActionPosition;

    //VIEWS
    private Button mButtonDone;
   // private EventHeader mHeader;


    public static Fragment getInstance(DataEvent mGeneratedEvent, int dataActionPosition, boolean makeActionInside, String action) {
        InviteParticipantsFragment inviteParticipantsFragment = new InviteParticipantsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EVENT_EXTRA, mGeneratedEvent);
        bundle.putBoolean(EXTRA_MAKE_ACTION_INSIDE, makeActionInside);
        bundle.putString(EXTRA_ACTION, action);
        inviteParticipantsFragment.setArguments(bundle);
        return inviteParticipantsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEvent = (DataEvent) getArguments().getSerializable(EVENT_EXTRA);
        action = getArguments().getString(EXTRA_ACTION);
        dataActionPosition = getArguments().getInt(EXTRA_ACTION_POSITION);
        makeActionInside = getArguments().containsKey(EXTRA_MAKE_ACTION_INSIDE) ? getArguments().getBoolean(EXTRA_MAKE_ACTION_INSIDE) : false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_invite_participants, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //mHeader = (EventHeader) view.findViewById(R.id.eventHeader);
        //setHeader();
        mButtonDone = (Button) view.findViewById(R.id.fragment_invite_participants_button_done);
        mButtonDone.setOnClickListener(this);
        mButtonDone.setBackgroundDrawable(ShapeDrawableUtils.getRoundendButton());
        if (makeActionInside) {
            /*we pass SCREEN_TYPE_EVENT_ACTION_INVITE so that the people viewPager
            * wont show already participating people*/
            ((BaseActivity) getActivity()).replaceFragment(R.id.fragment_container,
                    PeopleViewPagerFragment.getInstance(DataStore.SCREEN_TYPE_EVENT_ACTION_INVITE, mEvent),
                    PeopleViewPagerFragment.TAG, false, null, R.anim.enter_from_right, R.anim.exit_to_left,
                    R.anim.enter_from_left, R.anim.exit_to_right);
        } else {
            ((BaseActivity) getActivity()).replaceFragment(R.id.fragment_container,
                    PeopleViewPagerFragment.getInstance(DataStore.SCREEN_TYPE_INVITE_PARTICIPANTS, mEvent),
                    PeopleViewPagerFragment.TAG, false, null, R.anim.enter_from_right, R.anim.exit_to_left,
                    R.anim.enter_from_left, R.anim.exit_to_right);
        }
    }

    private void setHeader() {

    }

    @Override
    public void onClick(View v) {
        PeopleViewPagerFragment fragment = (PeopleViewPagerFragment) (getActivity()).getSupportFragmentManager().findFragmentByTag(PeopleViewPagerFragment.TAG);

        if (fragment != null) {
            PeopleTabsPagerAdapter adapter = fragment.mAdapter;
            InviteParticipantsListFragment tmp = (InviteParticipantsListFragment) adapter.fragments.get(0);
            String allInvitees = tmp.getSelectedIds();

            if (allInvitees != null && allInvitees.trim().equals("")) {
                allInvitees = ((InviteParticipantsListFragment) fragment.mAdapter.fragments.get(1)).getSelectedIds();
            } else {
                allInvitees += " ," + ((InviteParticipantsListFragment) fragment.mAdapter.fragments.get(1)).getSelectedIds();
            }
            allInvitees = allInvitees.trim();
            if (allInvitees.endsWith(","))
                allInvitees = allInvitees.substring(0, allInvitees.length() - 1);

            if (makeActionInside) {
                invite(allInvitees);
            } else {
            /*
            * here we broadcasting the selected people to the publish fragment.
            */
                Intent intent = new Intent(ACTION_INVITEES_SELECTED);
                intent.putExtra("selectedInvitees", allInvitees);
                LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                getActivity().getSupportFragmentManager().popBackStackImmediate();
            }
        }
    }

    private void invite(String allInvitees) {
        DataEventActions actions = mEvent.getEvent_actions().get(dataActionPosition);
        //if (Utils.isEmpty(actions.parameters)) actions.parameters = "{}";
        try {
            actions.parameters.put("other_user_id", allInvitees);

            //JSONObject jsonObject = new JSONObject(actions.parameters);
            //jsonObject.put("other_user_id", allInvitees);
            ServerConnector.getInstance().processRequest(new RequestEventActions(mEvent.getEvent_actions().get(dataActionPosition)), new ServerConnector.OnResultListener() {
                @Override
                public void onSuccess(BaseResponse baseResponse) {
                    ResponseEvent responseEvent = (ResponseEvent) baseResponse;
                    if (responseEvent != null) {
                    /*here we update the events in the events fragment and close ourself*/
                        mEvent.setEvent(responseEvent);
                        ((EventFragment) getTargetFragment()).setEvent(mEvent);
                        getActivity().getSupportFragmentManager().popBackStackImmediate();
                        return;
                    }
                    EMLog.e(TAG, "invite participants from event action - got event null response");
                }

                @Override
                public void onFailure(ErrorResponse errorResponse) {
                }
            });

        } catch (Exception ex) {
            EMLog.e(TAG, ex.getMessage());
        }
    }

}
