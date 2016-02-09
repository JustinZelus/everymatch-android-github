package com.everymatch.saas.ui.discover;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.everymatch.saas.R;
import com.everymatch.saas.adapter.DiscoverMoreAdapter;
import com.everymatch.saas.client.data.DataHelper;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.client.data.PopupMenuItem;
import com.everymatch.saas.server.Data.DataActivity;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.server.ServerConnector.OnResultListener;
import com.everymatch.saas.server.requests.RequestDiscover;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.server.responses.ResponseDiscover;
import com.everymatch.saas.server.responses.ResponseGetUser;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.singeltones.Preferences;
import com.everymatch.saas.singeltones.PusherManager;
import com.everymatch.saas.ui.BaseActivity;
import com.everymatch.saas.ui.NotificationFragment;
import com.everymatch.saas.ui.PeopleViewPagerFragment;
import com.everymatch.saas.ui.base.BaseFragment;
import com.everymatch.saas.ui.chat.ConversationsFragment;
import com.everymatch.saas.ui.common.EventCarouselFragment;
import com.everymatch.saas.ui.common.PeopleCarouselFragment;
import com.everymatch.saas.ui.dialog.menus.MenuChangeActivity;
import com.everymatch.saas.ui.event.MyEventsFragment;
import com.everymatch.saas.ui.questionnaire.QuestionnaireActivity;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.EmptyViewFactory;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.BaseIconTextView;
import com.everymatch.saas.view.EventHeader;
import com.everymatch.saas.view.IconImageView;
import com.everymatch.saas.view_controller.DiscoverActivitiesViewController;
import com.everymatch.saas.view_controller.DiscoverActivitiesViewController.DiscoverActivitiesListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dors on 7/19/15.
 */
public class DiscoverFragment extends BaseFragment implements DiscoverActivitiesListener, View.OnClickListener, EmptyViewFactory.ButtonListener {

    public static final String TAG = DiscoverFragment.class.getSimpleName();

    private static final int REQUEST_CODE_EDIT_ACTIVITY = 101;

    private static final String ACTIVITY_ID = TAG + "ACTIVITY_ID";
    public static final int MORE_MENU_WIDTH = 190;
    private ResponseGetUser user = DataStore.getInstance().getUser();

    // Views
    private LinearLayout mFragmentContainer;
    private View mHeaderTmp, mActivityPopup;
    private EventHeader mHeader;

    private BaseIconTextView mButtonMore;
    private TextView mHeaderTitle;
    private IconImageView mIcon;
    private DiscoverActivitiesViewController mDiscoverActivitiesViewController;
    private ListPopupWindow mMorePopup;
    private FrameLayout mViewNoDataContainer;
    private int mNoDataVisibility = View.GONE;

    // Data
    public static DataActivity mCurrentActivity;
    private ArrayList<DataActivity> mActivities;
    private List<PopupMenuItem> mMoreData;
    private DiscoverCallbacks mCallbacks;
    private DiscoverMoreAdapter moreAdapter;
    private BaseIconTextView mButtonNotifications;

    public DiscoverFragment() {
    }

    public static DiscoverFragment newInstance(String activityId) {
        DiscoverFragment discoverFragment = new DiscoverFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ACTIVITY_ID, activityId);
        discoverFragment.setArguments(bundle);
        return discoverFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof DiscoverCallbacks) {
            mCallbacks = (DiscoverCallbacks) context;
        } else {
            throw new IllegalArgumentException(context + " must implement DiscoverCallbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discover, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((DiscoverActivity) getActivity()).setSelectedMenuItem(DiscoverActivity.DISCOVER_MENU_ITEMS.DISCOVER);
        // Bind views
        mProgressBar.setVisibility(View.GONE);
        mFragmentContainer = (LinearLayout) view.findViewById(R.id.fragment_discover_container);
        //here we get the header from the activity
        mHeaderTmp = (getActivity()).findViewById(R.id.fragment_discover_header);
        mHeaderTmp.setVisibility(View.GONE);
        mHeader = (EventHeader) getActivity().findViewById(R.id.eventHeader);
        setHeader();

        //mHeaderTmp = view.findViewById(R.id.fragment_discover_header);
        mHeaderTitle = (TextView) mHeaderTmp.findViewById(R.id.view_discover_header_title);
        mIcon = (IconImageView) mHeaderTmp.findViewById(R.id.view_discover_header_icon);
        mActivityPopup = view.findViewById(R.id.fragment_discover_activities_popup);
        mButtonMore = (BaseIconTextView) mHeaderTmp.findViewById(R.id.view_discover_header_menu_more);
        mButtonNotifications = (BaseIconTextView) mHeaderTmp.findViewById(R.id.tv_view_notification_icon);

        // click listeners
        mHeaderTmp.findViewById(R.id.tv_view_people_icon).setOnClickListener(this);
        mHeaderTmp.findViewById(R.id.tv_view_my_event_icon).setOnClickListener(this);
        mHeaderTmp.findViewById(R.id.tv_view_notification_icon);

        //view.findViewById(R.id.tv_view_people_icon).setOnClickListener(this);
        //view.findViewById(R.id.tv_view_my_event_icon).setOnClickListener(this);
        //view.findViewById(R.id.tv_view_notification_icon);

        mButtonNotifications.setOnClickListener(this);

        mHeaderTmp.findViewById(R.id.view_discover_header_title_container).setOnClickListener(this);
        mButtonMore.setOnClickListener(this);

        mViewNoDataContainer = (FrameLayout) view.findViewById(R.id.fragment_discover_view_no_data_container);
        mViewNoDataContainer.removeAllViews();
        mViewNoDataContainer.addView(EmptyViewFactory.createEmptyView(EmptyViewFactory.TYPE_DISCOVER, this));
        mViewNoDataContainer.setVisibility(mNoDataVisibility);

        moreAdapter = new DiscoverMoreAdapter(new ArrayList<PopupMenuItem>());
        handleActivities(savedInstanceState);
        fetchUnReadMessages();
    }

    private void setHeader() {
        mHeader.setListener(new EventHeader.OnEventHeader() {
            @Override
            public void onBackButtonClicked() {

            }

            @Override
            public void onOneIconClicked() {

            }

            @Override
            public void onTwoIconClicked() {

            }

            @Override
            public void onThreeIconClicked() {

            }
        });

        mHeader.getBackButton().setVisibility(View.GONE);
        //mHeader.getBackButton().setText(Consts.Icons.icon_ArrowBack);
        mHeader.getIconOne().setVisibility(View.GONE);
        mHeader.getIconTwo().setVisibility(View.GONE);
        mHeader.getIconThree().setVisibility(View.GONE);
        mHeader.setArrowDownVisibility(true);
        mHeader.getTvArrowDown().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHeader.getTitle().performClick();
            }
        });
        mHeader.getTitle().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MenuChangeActivity menuChangeActivity = new MenuChangeActivity();
                menuChangeActivity.setTargetFragment(DiscoverFragment.this, MenuChangeActivity.REQUEST_CODE_CHANGE_ACTIVITY);
                menuChangeActivity.show(getActivity().getSupportFragmentManager(), "");
            }
        });

        //mHeader.setTitle();
    }

    private void handleActivities(Bundle savedInstanceState) {
        if (mActivities == null) {
            setUserActivities();
            // Check if we have saved state
            if (savedInstanceState == null) {
                if (getArguments() != null && getArguments().getString(ACTIVITY_ID) != null) {
                    setCurrentActivity(getArguments().getString(ACTIVITY_ID));
                } else {
                    mCurrentActivity = mActivities.get(0);
                }

                fetchActivityInfo(mCurrentActivity.client_id);
            } else {
                setCurrentActivity(savedInstanceState.getString(ACTIVITY_ID));
            }
            ((DiscoverActivity) (getActivity())).currentActivityId = mCurrentActivity.client_id;

            fetchNotifications();
            fetchUnReadMessages();
        }


        mDiscoverActivitiesViewController = new DiscoverActivitiesViewController(mActivityPopup, mActivities);
        mDiscoverActivitiesViewController.setActivitiesListener(this);
        mDiscoverActivitiesViewController.setSelectedActivity(mCurrentActivity.client_id);
        updateUiByActivity();
    }

    /**
     * init user activities to mActivities variable
     */
    private void setUserActivities() {
        if (mActivities == null) {
            mActivities = new ArrayList<>();
        }

        mActivities.clear();
        mActivities = ds.getUser().getUserActivities();
    }

    /**
     * Update the ui after new activity selected
     */
    private void updateUiByActivity() {
        mIcon.setIconImage(mCurrentActivity.icon);
        mHeaderTitle.setText(mCurrentActivity.text_title);


        mHeader.setTitle(mCurrentActivity.text_title);
    }

    /**
     * Fetch all the information related to a specific activity
     *
     * @param activityId the id of the activity
     */
    private void fetchActivityInfo(String activityId) {

        // Cancel previous request
        ServerConnector.getInstance().cancelPendingRequests(TAG + "RequestDiscover");

        // Hide error views
        mViewNoDataContainer.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.VISIBLE);

        ServerConnector.getInstance().processRequest(new RequestDiscover(activityId), new OnResultListener() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {

                mProgressBar.setVisibility(View.GONE);

                ResponseDiscover responseDiscover = (ResponseDiscover) baseResponse;

                if (responseDiscover == null) {
                    return;
                }

                addFragments(responseDiscover);
            }

            @Override
            public void onFailure(ErrorResponse errorResponse) {
                mProgressBar.setVisibility(View.GONE);
            }
        }, TAG + "RequestDiscover");
    }

    /**
     * Fetch notifications, in order to show notification count - > locally :)
     */
    private void fetchNotifications() {
        //here we get the notifications locally insted of make a web call
        if (user.notifications.unread > 0) {
            updateNotificationCount(user.notifications.unread);
        }
    }

    private void fetchUnReadMessages() {
        //update more popUp
        mMoreData = DataHelper.createDiscoverMenuItems();

        //if (moreAdapter != null) {
        moreAdapter.setItems(mMoreData);
        moreAdapter.notifyDataSetChanged();
        //}

        //update more icon
        if (ds.getUser().getInbox().getUnread() <= 0) {
            mButtonMore.setTextColor(ds.getIntColor(EMColor.WHITE));
        } else {
            mButtonMore.setTextColor(ds.getIntColor(EMColor.ERROR));
        }
    }

    /**
     * Add related fragment by discover object
     */
    private void addFragments(ResponseDiscover responseDiscover) {

        if (getActivity() == null || getActivity().isFinishing()) {
            return;
        }

        // =================== EVENTS =================== //
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        EventCarouselFragment eventCarouselFragment = (EventCarouselFragment) findFragment(EventCarouselFragment.TAG);


        String leftText = Utils.makeTextCamelCase(dm.getResourceText(R.string.Suggested_Events)
                + " (" + responseDiscover.getEventHolder().count + ")");
        String rightText = Utils.makeTextCamelCase(dm.getResourceText(R.string.View_All));

        if (eventCarouselFragment != null) {
            eventCarouselFragment.refreshData(responseDiscover.getEventHolder(), leftText, rightText);
        } else {
            eventCarouselFragment = EventCarouselFragment.getInstance(responseDiscover.getEventHolder(), leftText, rightText);
            fragmentTransaction.add(mFragmentContainer.getId(), eventCarouselFragment, EventCarouselFragment.TAG);
        }

        // =================== PEOPLE =================== //
        PeopleCarouselFragment peopleCarouselFragment = (PeopleCarouselFragment) findFragment(PeopleCarouselFragment.TAG);
        leftText = Utils.makeTextCamelCase(dm.getResourceText(R.string.People_Matches) + "" +
                " (" + responseDiscover.getPeopleHolder().count + ")");
        rightText = Utils.makeTextCamelCase(dm.getResourceText(R.string.View_All));

        if (peopleCarouselFragment != null) {
            peopleCarouselFragment.refreshData(responseDiscover.getPeopleHolder(), leftText, rightText);
        } else {
            peopleCarouselFragment = PeopleCarouselFragment.getInstance(responseDiscover.getPeopleHolder(), leftText, rightText);
            fragmentTransaction.add(mFragmentContainer.getId(), peopleCarouselFragment, PeopleCarouselFragment.TAG);
        }

        // =================== COMMERCIAL =================== //
        FragmentDiscoverAd fragmentDiscoverAd = (FragmentDiscoverAd) findFragment(FragmentDiscoverAd.TAG);

        if (fragmentDiscoverAd != null) {
            fragmentDiscoverAd.refreshData();
        } else {
            fragmentDiscoverAd = new FragmentDiscoverAd();
            fragmentTransaction.add(mFragmentContainer.getId(), fragmentDiscoverAd, FragmentDiscoverAd.TAG);
        }

        if (!fragmentTransaction.isEmpty()) {
            fragmentTransaction.commitAllowingStateLoss();
        }

        // Show empty view if this activity has no events
        if (Utils.isArrayListEmpty(responseDiscover.getEventHolder().getEvents())) {
            mViewNoDataContainer.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onAddActivityButtonClick() {
        mDiscoverActivitiesViewController.toggleActivitySelectionPopup();
        // set the question flag to activity creation
        if (ds.getUser().profiles.activity_profiles.length == ds.getApplicationData().getActivities().length) {
            Toast.makeText(getActivity(), "You already filled all profiles!", Toast.LENGTH_SHORT).show();
            return;
        }
        QuestionnaireActivity.create_mode = QuestionnaireActivity.CREATE_MODE.CREATE_ACTIVITY;
        getActivity().startActivity(new Intent(getActivity(), QuestionnaireActivity.class));
    }

    @Override
    public void onActivitySettingsButtonClick(DataActivity activity) {
        EMLog.i(TAG, "onActivitySettingsButtonClick " + activity.text_title);
        QuestionnaireActivity.create_mode = QuestionnaireActivity.CREATE_MODE.EDIT_ACTIVITY;
        QuestionnaireActivity.editActivity(DiscoverFragment.this, Integer.valueOf(activity.client_id), REQUEST_CODE_EDIT_ACTIVITY);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_EDIT_ACTIVITY && resultCode == Activity.RESULT_OK) {
            refreshCurrentActivity();
        }
    }

    /**
     * A call to refresh the current activity
     */
    public void refreshCurrentActivity() {
        if (mCurrentActivity != null) {
            fetchActivityInfo(mCurrentActivity.client_id);
        }
    }

    @Override
    public void onActivityClick(final DataActivity activity) {

        if (activity.client_id.equals(mCurrentActivity.client_id)) {
            return;
        }

        mCurrentActivity = activity;
        mDiscoverActivitiesViewController.setSelectedActivity(mCurrentActivity.client_id);
        mDiscoverActivitiesViewController.refreshAdapter();
        updateUiByActivity();
        mDiscoverActivitiesViewController.toggleActivitySelectionPopup();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fetchActivityInfo(mCurrentActivity.client_id);
            }
        }, DiscoverActivitiesViewController.ANIMATION_DURATION);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.view_discover_header_menu_more:
                if (mMorePopup != null && mMorePopup.isShowing()) {
                    mMorePopup.dismiss();
                    return;
                }

                showMoreMenu();
                break;

            case R.id.view_discover_header_title_container:
                mDiscoverActivitiesViewController.toggleActivitySelectionPopup();
                break;
            case R.id.tv_view_people_icon:
                ((BaseActivity) getActivity()).replaceFragment(R.id.fragment_container, PeopleViewPagerFragment.getInstance(DataStore.SCREEN_TYPE_FRIENDS, null),
                        PeopleViewPagerFragment.TAG, true, null, R.anim.enter_from_right, R.anim.exit_to_left,
                        R.anim.enter_from_left, R.anim.exit_to_right);
                break;
            case R.id.tv_view_my_event_icon:
                ((BaseActivity) getActivity()).replaceFragment(R.id.fragment_container, MyEventsFragment.getInstance(),
                        MyEventsFragment.TAG, true, null, R.anim.enter_from_right, R.anim.exit_to_left,
                        R.anim.enter_from_left, R.anim.exit_to_right);
                break;
            case R.id.tv_view_notification_icon:
                ((BaseActivity) getActivity()).replaceFragment(R.id.fragment_container, new NotificationFragment(),
                        NotificationFragment.TAG, true, null, R.anim.enter_from_right, R.anim.exit_to_left,
                        R.anim.enter_from_left, R.anim.exit_to_right);
                break;
        }
    }

    private void showMoreMenu() {
        if (mMoreData == null) {
        }

        mMoreData = DataHelper.createDiscoverMenuItems();

        if (mMorePopup == null) {
            mMorePopup = new ListPopupWindow(getActivity());
        }
        mMorePopup.setAnchorView(mButtonMore);
        //moreAdapter = new DiscoverMoreAdapter(mMoreData);
        moreAdapter.setItems(mMoreData);
        mMorePopup.setAdapter(moreAdapter);
        mMorePopup.setWidth(Utils.dpToPx(MORE_MENU_WIDTH));
        mMorePopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    //here we showing messages fragment
                    ((BaseActivity) getActivity()).replaceFragment(R.id.fragment_container, new ConversationsFragment(),
                            ConversationsFragment.TAG, true, null, R.anim.enter_from_right, R.anim.exit_to_left,
                            R.anim.enter_from_left, R.anim.exit_to_right);
                } else if (position == 1) {
                    mCallbacks.onMeClick();
                }
                /*else {
                    Toast.makeText(getActivity(), "2", Toast.LENGTH_SHORT).show();
                }*/

                mMorePopup.dismiss();
                hideDimView(false);
            }
        });


        showDimView();
        mMorePopup.show();
    }

    @Override
    protected void onDimViewClick() {
        mMorePopup.dismiss();
        hideDimView(false);
    }

    private void updateNotificationCount(int notificationCount) {
        if (notificationCount == 0) {
            mButtonNotifications.setTextColor(ds.getIntColor(EMColor.WHITE));
            mButtonNotifications.setText(Consts.Icons.icon_NotificationFull);
        } else {
            mButtonNotifications.setTextColor(ds.getIntColor(EMColor.ERROR));
            mButtonNotifications.setText(Consts.Icons.NotificationOn);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mCurrentActivity != null) {
            outState.putString(ACTIVITY_ID, mCurrentActivity.client_id);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mDiscoverActivitiesViewController.closeActivityPopup();

        if (mMorePopup != null) {
            mMorePopup.dismiss();
        }
    }

    @Override
    public void onDestroyView() {
        mNoDataVisibility = mViewNoDataContainer.getVisibility();
        super.onDestroyView();
    }

    @Override
    protected void cancelRequests() {
        super.cancelPendingRequests(TAG + "RequestDiscover", TAG + "RequestGetUser");
    }

    @Override
    public void onTryAgainClick() {
        reloadData();
    }

    /**
     * Reload data once connection is back
     */
    public void reloadData() {
        fetchActivityInfo(mCurrentActivity.client_id);
    }

    private void setCurrentActivity(String activityId) {
        for (DataActivity activity : mActivities) {
            if (activity.client_id.equals(activityId)) {
                mCurrentActivity = activity;
                Preferences.getInstance().setLastActivityId(activityId);
            }
        }
    }

    public String getCurrentActivityId() {

        if (mCurrentActivity != null) {
            return mCurrentActivity.client_id;
        }

        return null;
    }

    @Override
    public void onEmptyViewFirstButtonClick() {
        QuestionnaireActivity.create_mode = QuestionnaireActivity.CREATE_MODE.EDIT_ACTIVITY;
        QuestionnaireActivity.editActivity(DiscoverFragment.this, Integer.valueOf(mCurrentActivity.client_id), REQUEST_CODE_EDIT_ACTIVITY);
    }

    @Override
    public void onEmptyViewSecondButtonClick() {
        ((BaseActivity) getActivity()).replaceFragment(R.id.fragment_container, MyEventsFragment.getInstance(true),
                MyEventsFragment.TAG, true, null, R.anim.enter_from_right, R.anim.exit_to_left,
                R.anim.enter_from_left, R.anim.exit_to_right);

        /*QuestionnaireActivity.create_mode = QuestionnaireActivity.CREATE_MODE.CREATE_EVENT;
        final Intent intent = new Intent(getActivity(), QuestionnaireActivity.class);
        getActivity().start(intent);*/
    }

    public interface DiscoverCallbacks {
        void onMeClick();
    }

    /* menu click responses  ******************************/
    public void onEditActivityClick(DataActivity activity) {
        onActivitySettingsButtonClick(activity);
    }

    public void onActivitySelected(DataActivity activity) {
        //save last activity id to prefs
        Preferences.getInstance().setLastActivityId(activity.client_id);
        //****************************************

        mCurrentActivity = activity;
        fetchActivityInfo(mCurrentActivity.client_id);
        fetchNotifications();
        fetchUnReadMessages();
        updateUiByActivity();
    }

    public void onAddActivityClick() {
        onAddActivityButtonClick();
    }

    /*********************************************************/


    @Override
    protected void handleBroadcast(Serializable eventObject, String eventName) {
        if (eventName.equals(PusherManager.PUSHER_EVENT_EVENT_INBOX_UNREAD)) {
            fetchUnReadMessages();
        }

        //update unread messages
        if (eventName.equals(PusherManager.PUSHER_EVENT_EVENT_NEW_MESSAGE)) {
            fetchUnReadMessages();
        }
    }
}
