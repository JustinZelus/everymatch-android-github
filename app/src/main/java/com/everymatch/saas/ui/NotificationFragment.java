package com.everymatch.saas.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;

import com.everymatch.saas.R;
import com.everymatch.saas.adapter.AdapterNotification;
import com.everymatch.saas.adapter.EmBaseAdapter;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.server.Data.DataEvent;
import com.everymatch.saas.server.Data.DataNotifications;
import com.everymatch.saas.server.request_manager.NotificationManager;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.singeltones.GenericCallback;
import com.everymatch.saas.ui.base.BaseListFragment;
import com.everymatch.saas.ui.chat.ConversationsFragment;
import com.everymatch.saas.ui.discover.DiscoverActivity;
import com.everymatch.saas.ui.event.EventFragment;
import com.everymatch.saas.util.EmptyViewFactory;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.EventDataRow;
import com.everymatch.saas.view.EventHeader;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationFragment extends BaseListFragment implements EventHeader.OnEventHeader, AdapterView.OnItemClickListener {
    public static final String TAG = NotificationFragment.class.getSimpleName();

    public static final String NOTIFICATION_TYPE_Invitation_Canceled = "invitation_canceled";
    public static final String NOTIFICATION_TYPE_Request_Canceled = "request_canceled";
    public static final String NOTIFICATION_TYPE_Event_Invite = "event_invite";
    public static final String NOTIFICATION_TYPE_Group_Invite = "group_invite";
    public static final String NOTIFICATION_TYPE_Join_Request = "join_request";
    public static final String NOTIFICATION_TYPE_Request_Accepted = "request_accepted";
    public static final String NOTIFICATION_TYPE_Request_Rejected = "request_rejected";
    public static final String NOTIFICATION_TYPE_Saved_Event_Removed = "saved_event_removed";
    public static final String NOTIFICATION_TYPE_User_Joined = "user_joined";
    public static final String NOTIFICATION_TYPE_User_Left_Event = "user_left_event";
    public static final String NOTIFICATION_TYPE_Event_Canceled = "event_canceled";
    public static final String NOTIFICATION_TYPE_Event_Undeleted = "event_undeleted";
    public static final String NOTIFICATION_TYPE_Removed_From_Event = "removed_from_event";
    public static final String NOTIFICATION_TYPE_INVITATION_REJECTED = "invitation_rejected";

    private boolean isClicked = false;
    public ArrayList<DataNotifications> notifications;
    AdapterNotification adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        notifications = ds.getUser().notifications.getNotifications();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((DiscoverActivity) getActivity()).setSelectedMenuItem(DiscoverActivity.DISCOVER_MENU_ITEMS.NOTIFICATIONS);

        adapter = new AdapterNotification(notifications, getActivity());
        View emptyView = EmptyViewFactory.createEmptyView(EmptyViewFactory.TYPE_NOTIFICATIONS);
        ((ViewGroup) mAbsListView.getParent()).addView(emptyView);
        mAbsListView.setEmptyView(emptyView);
        mAbsListView.setAdapter(adapter);
        mAbsListView.setOnItemClickListener(this);
        mAbsListView.setDividerHeight(Utils.dpToPx(1));
        mAbsListView.setPadding(0, 0, 0, 0);

        setInBox();
    }

    private void setInBox() {
        mTopContainer.removeAllViews();
        mTopContainer.setVisibility(View.VISIBLE);
        EventDataRow edr = new EventDataRow(getActivity());
        edr.setLeftIconVisibility(true);
        edr.getLeftIcon().setText(Consts.Icons.icon_Mail);
        edr.setDetails(null);
        edr.setTitle(dm.getResourceText(R.string.Inbox_title));
        edr.getTitleView().setTextColor(ds.getIntColor(EMColor.MOON));
        edr.setRightIconText(Consts.Icons.icon_Arrowright);
        edr.getWrapperLayout().setBackgroundColor(ds.getIntColor(EMColor.WHITE));

        edr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((BaseActivity) getActivity()).replaceFragment(R.id.fragment_container, new ConversationsFragment(),
                        ConversationsFragment.TAG, true, null, R.anim.enter_from_right, R.anim.exit_to_left,
                        R.anim.enter_from_left, R.anim.exit_to_right);
            }
        });
        mTopContainer.addView(edr);
    }

    protected void setHeader() {
        mEventHeader.setVisibility(View.GONE);

        //get header from discover activity
        EventHeader mHeader = (EventHeader) getActivity().findViewById(R.id.eventHeader);
        mHeader.setListener(this);
        mHeader.getBackButton().setVisibility(View.GONE);
        mHeader.getIconOne().setVisibility(View.GONE);
        mHeader.getIconTwo().setVisibility(View.GONE);
        mHeader.getIconThree().setVisibility(View.GONE);
        mHeader.setTitle(dm.getResourceText(R.string.Notification));
        mHeader.getTitle().setOnClickListener(null);
        mHeader.setArrowDownVisibility(false);
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
    public void onThreeIconClicked() {
        if (!isClicked) {
            mEventHeader.getTitle().setVisibility(View.GONE);
            mEventHeader.getEditTitle().setVisibility(View.VISIBLE);
            mEventHeader.getEditTitle().setFocusable(true);

            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInputFromWindow(mEventHeader.getEditTitle().getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

            isClicked = true;
        } else {
            mEventHeader.getTitle().setVisibility(View.VISIBLE);
            mEventHeader.setTitle("Notifications");
            mEventHeader.getEditTitle().setVisibility(View.GONE);

            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);

            isClicked = false;
        }

    }

    @Override
    public EmBaseAdapter getAdapter() {
        return null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DataNotifications dataNotification = notifications.get(position);
        dataNotification.read = true;
        adapter.notifyDataSetChanged();
        NotificationManager.markNotificationsAsReadOrSeen("read", dataNotification._id, new GenericCallback() {
            @Override
            public void onDone(boolean success, Object data) {

            }
        });
        switch (dataNotification.notification_type) {
            case NOTIFICATION_TYPE_INVITATION_REJECTED:
            case NOTIFICATION_TYPE_Join_Request:
            case NOTIFICATION_TYPE_Event_Invite:
            case NOTIFICATION_TYPE_Invitation_Canceled:
            case NOTIFICATION_TYPE_User_Joined:
            case NOTIFICATION_TYPE_User_Left_Event:
            case NOTIFICATION_TYPE_Event_Undeleted:
            case NOTIFICATION_TYPE_Removed_From_Event:
            case NOTIFICATION_TYPE_Request_Accepted:
            case NOTIFICATION_TYPE_Request_Canceled:
            case NOTIFICATION_TYPE_Group_Invite:
            case NOTIFICATION_TYPE_Request_Rejected:

                /* go to event page */
                /*lets create an event and set it's id to sent it to event fragment*/
                DataEvent dataEvent = new DataEvent();
                dataEvent._id = dataNotification.object_id;
                ((BaseActivity) getActivity())
                        .replaceFragment(R.id.fragment_container,
                                EventFragment.getInstance(dataEvent), NotificationFragment.TAG, true, null,
                                R.anim.enter_from_right, R.anim.exit_to_left,
                                R.anim.enter_from_left, R.anim.exit_to_right);
                break;
        }
    }

    @Override
    protected void fetchNextPage() {

    }
}
