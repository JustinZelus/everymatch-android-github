package com.everymatch.saas.ui.event;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import com.everymatch.saas.adapter.PeopleUsersAdapter;
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
import com.everymatch.saas.util.Utils;

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
    public static final int REQUEST_CODE_INVITE = 107;
    public static final String EXTRA_EVENT = "extra.event";
    public static final String ACTION_INVITE = "action.invite";

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

        //register receiver
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiverSelectionChanged, new IntentFilter(PeopleUsersAdapter.ACTION_COUNTER_CLICK));
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

            // do nothing on done click when nothing selected
            if (Utils.isEmpty(allInvitees)) return;

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
        try {
            actions.parameters.put("other_user_id", allInvitees);
            showDialog(dm.getResourceText(R.string.Loading), false);
            ServerConnector.getInstance().processRequest(new RequestEventActions(mEvent.getEvent_actions().get(dataActionPosition)), new ServerConnector.OnResultListener() {
                @Override
                public void onSuccess(BaseResponse baseResponse) {
                    stopDialog();
                    ResponseEvent responseEvent = (ResponseEvent) baseResponse;
                    if (responseEvent != null) {
                    /*here we update the events in the events fragment and close ourself*/
                        /*mEvent.setEvent(responseEvent);
                        if (getTargetFragment() != null) {
                            getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent(ACTION_INVITE).putExtra(EXTRA_EVENT, mEvent));
                        }
                        ((EventFragment) getTargetFragment()).setEvent(mEvent);
                        getActivity().getSupportFragmentManager().popBackStackImmediate();
                        return;*/
                    }
                    EMLog.e(TAG, "invite participants from event action - got event null response");
                }

                @Override
                public void onFailure(ErrorResponse errorResponse) {
                    stopDialog();
                }
            });

        } catch (Exception ex) {
            EMLog.e(TAG, ex.getMessage());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiverSelectionChanged);
    }

    private BroadcastReceiver receiverSelectionChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (PeopleUsersAdapter.ACTION_COUNTER_CLICK.equals(intent.getAction())) {
                int selectionCount = intent.getIntExtra(PeopleUsersAdapter.EXTRA_SELECTION_COUNT, 0);
                mButtonDone.setAlpha(selectionCount == 0 ? 0.5f : 1f);
                mButtonDone.setOnClickListener(selectionCount == 0 ? null : InviteParticipantsFragment.this);
            }
        }
    };
}
