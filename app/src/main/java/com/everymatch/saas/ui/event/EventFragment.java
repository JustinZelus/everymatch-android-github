package com.everymatch.saas.ui.event;


import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.adapter.DiscoverMoreAdapter;
import com.everymatch.saas.client.data.DataHelper;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.client.data.Participation_Type;
import com.everymatch.saas.client.data.PopupMenuItem;
import com.everymatch.saas.server.Data.DataDate;
import com.everymatch.saas.server.Data.DataEvent;
import com.everymatch.saas.server.Data.DataEventActions;
import com.everymatch.saas.server.Data.DataMatchResults;
import com.everymatch.saas.server.Data.DataPeopleHolder;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.server.request_manager.MatchManager;
import com.everymatch.saas.server.requests.RequestEvent;
import com.everymatch.saas.server.requests.RequestEventActions;
import com.everymatch.saas.server.requests.RequestMatch;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.server.responses.ResponseEvent;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.singeltones.GenericCallback;
import com.everymatch.saas.ui.PeopleViewPagerFragment;
import com.everymatch.saas.ui.base.BaseFragment;
import com.everymatch.saas.ui.chat.ChatFragment;
import com.everymatch.saas.ui.common.PeopleCarouselFragment;
import com.everymatch.saas.ui.inbox.InboxActivity;
import com.everymatch.saas.ui.match.MatchActivity;
import com.everymatch.saas.ui.questionnaire.QuestionnaireActivity;
import com.everymatch.saas.ui.user.UserActivity;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.IconManager;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.BaseIconTextView;
import com.everymatch.saas.view.BaseImageView;
import com.everymatch.saas.view.BaseTextView;
import com.everymatch.saas.view.EventDataRow;
import com.everymatch.saas.view.EventHeader;
import com.everymatch.saas.view.OverScrollView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventFragment extends BaseFragment implements EventHeader.OnEventHeader, View.OnClickListener, PeopleViewPagerFragment.ParticipantsCallback {

    public static final String TAG = EventFragment.class.getSimpleName();
    public static final String EVENT = "event";
    private static final int REQUEST_CODE_ADD_PARTICIPANTS = 100;
    private static final int REQUEST_CHECK_PARTICIPANTS = 101;

    private DataEvent mEvent;
    private EventHeader mHeader;
    private BaseImageView mImageTitle;
    private BaseIconTextView mPercentIcon;
    private BaseTextView mPercent;
    private BaseImageView mLogo;
    private EventDataRow mDetailsRow;
    private EventDataRow mLocationRow;
    private EventDataRow mDateRow;
    private EventDataRow mPeopleRow;
    private LinearLayout mPeopleList;
    private BaseIconTextView mToolbar1Button;
    private BaseTextView mToolbar1Text;
    private BaseIconTextView mToolbar2Button;
    private BaseTextView mToolbar2Text;
    private BaseIconTextView mToolbar3Button;
    private BaseTextView mToolbar3Text;
    private BaseTextView mAboutDetails;
    private RelativeLayout rlActionLoader;
    private LinearLayout llActions;

    private FragmentTransaction transaction;
    private ListPopupWindow mMorePopup;
    private List<PopupMenuItem> mMoreActions;
    private View mView;
    private OverScrollView mOverScrollView;
    private View mInvisibleHeader;
    private EventDataRow mPeopleListItem;
    private BaseTextView mAboutTitle;
    private DataMatchResults mDataMatchResults;

    public static EventFragment getInstance(DataEvent dataEvent) {
        EventFragment eventFragment = new EventFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(EventFragment.EVENT, dataEvent);
        eventFragment.setArguments(bundle);
        return eventFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            mEvent = (DataEvent) bundle.getSerializable(EVENT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event, container, false);

        transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);

        /*bundle = getArguments();
        if (bundle != null) {
            String eventJson = bundle.getString(EVENT);

            mEvent = new Gson().fromJson(eventJson, DataEvent.class);
        }*/

        mImageTitle = (BaseImageView) view.findViewById(R.id.event_image_title);

        mView = view;
        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mHeader = (EventHeader) view.findViewById(R.id.event_eventHeader);
        mHeader.setListener(this);
        mHeader.getBackButton().setText(Consts.Icons.icon_New_Close);
        mHeader.getIconOne().setVisibility(View.GONE);
        mHeader.getIconTwo().setVisibility(View.GONE);
        mHeader.getIconThree().setVisibility(View.GONE);

        if (mEvent != null) {
            getFullEvent(view);
        }
    }

    private void initViews(final View view) {
        mPercentIcon = (BaseIconTextView) view.findViewById(R.id.event_percent_icon);
        mPercent = (BaseTextView) view.findViewById(R.id.event_percent);

        mLogo = (BaseImageView) view.findViewById(R.id.event_logo);

        mOverScrollView = (OverScrollView) view.findViewById(R.id.event_scrollView);
        mOverScrollView.setMaxOverScrollDelta(0);
        mInvisibleHeader = view.findViewById(R.id.view_invisible_header);
        mPeopleListItem = (EventDataRow) view.findViewById(R.id.fragment_event_people_list_item);

        mDetailsRow = (EventDataRow) view.findViewById(R.id.event_row_details);
        mDetailsRow.setOnClickListener(this);
        mDetailsRow.getLeftIcon().setText(IconManager.getInstance(getActivity()).getIconString("ListView"));
        mDetailsRow.getRightIcon().setText(IconManager.getInstance(getActivity()).getIconString("Arrowright"));
        mDetailsRow.getTitleView().setSingleLine(true);
        mDetailsRow.getTitleView().setMaxLines(1);

        mLocationRow = (EventDataRow) view.findViewById(R.id.event_row_location);
        mLocationRow.setOnClickListener(this);
        mLocationRow.getLeftIcon().setText(IconManager.getInstance(getActivity()).getIconString("Location"));
        mLocationRow.getRightIcon().setText(IconManager.getInstance(getActivity()).getIconString("Arrowright"));
        mLocationRow.getTitleView().setSingleLine(true);
        mLocationRow.getTitleView().setMaxLines(1);

        mDateRow = (EventDataRow) view.findViewById(R.id.event_row_date);
        mDateRow.setOnClickListener(this);
        mDateRow.getLeftIcon().setText(IconManager.getInstance(getActivity()).getIconString("Clock"));
        mDateRow.getRightIcon().setText(IconManager.getInstance(getActivity()).getIconString("Add"));
        mDateRow.getTitleView().setSingleLine(true);
        mDateRow.getTitleView().setMaxLines(1);

        mPeopleRow = (EventDataRow) view.findViewById(R.id.event_row_people);
        mPeopleRow.setOnClickListener(this);
        mPeopleRow.getLeftIcon().setText(IconManager.getInstance(getActivity()).getIconString("People"));
        mPeopleRow.getRightIcon().setText(IconManager.getInstance(getActivity()).getIconString("Arrowright"));
        mPeopleRow.getTitleView().setSingleLine(true);
        mPeopleRow.getTitleView().setMaxLines(1);

        mToolbar1Button = (BaseIconTextView) view.findViewById(R.id.event_toolbar_1_icon);
        ((LinearLayout) mToolbar1Button.getParent()).setOnClickListener(this);
        mToolbar1Text = (BaseTextView) view.findViewById(R.id.event_toolbar_1_text);

        mToolbar2Button = (BaseIconTextView) view.findViewById(R.id.event_toolbar_2_icon);
        mToolbar2Button.setOnClickListener(this);
        ((LinearLayout) mToolbar2Button.getParent()).setOnClickListener(this);
        mToolbar2Text = (BaseTextView) view.findViewById(R.id.event_toolbar_2_text);

        mToolbar3Button = (BaseIconTextView) view.findViewById(R.id.event_toolbar_3_icon);
        mToolbar3Button.setOnClickListener(this);
        ((LinearLayout) mToolbar3Button.getParent()).setOnClickListener(this);
        mToolbar3Text = (BaseTextView) view.findViewById(R.id.event_toolbar_3_text);

        mPeopleList = (LinearLayout) view.findViewById(R.id.event_people_list);


        mAboutDetails = (BaseTextView) view.findViewById(R.id.event_about_details);
        mAboutTitle = (BaseTextView) view.findViewById(R.id.event_about_title);


        llActions = (LinearLayout) view.findViewById(R.id.actions);
        rlActionLoader = (RelativeLayout) view.findViewById(R.id.actionsLoader);
        llActions.measure(0, 0);
        rlActionLoader.getLayoutParams().height = llActions.getMeasuredHeight();
        rlActionLoader.requestLayout();
    }

    private void setData(View view) {
        //fix crash when fragment is no longer attached to activity
        if (!isAdded()) {
            return;
        }
        mHeader.setTitle(mEvent.dataPublicEvent.event_title);
        if (!TextUtils.isEmpty(mEvent.dataPublicEvent.image_url)) {
            Picasso.with(getContext()).load(mEvent.dataPublicEvent.image_url).into(mImageTitle, new Callback.EmptyCallback() {
                @Override
                public void onSuccess() {
                    if (isAdded()) {
                        if (mImageTitle.getDrawable() != null) {

                            if (mImageTitle.getMeasuredHeight() == 0) {
                                mImageTitle.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                                    @Override
                                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                                        setMaxScrolling();
                                    }
                                });
                            } else {
                                setMaxScrolling();
                            }
                        }
                    }
                }
            });
        }

        if (!TextUtils.isEmpty(mEvent.dataPublicEvent.event_icon)) {
            Picasso.with(getContext()).load(mEvent.dataPublicEvent.event_icon).into(mLogo);
        }

        //Title
        mHeader.setTitle(mEvent.dataPublicEvent.event_title);

        // DETAILS
        mDetailsRow.setTitle(dm.getResourceText(R.string.Details));
        mDetailsRow.setDetails(Utils.getEventImportantAnswersText(mEvent));

        // LOCATION
        mLocationRow.setTitle(mEvent.getLocationText());
        String city = Utils.isEmpty(mEvent.dataPublicEvent.getLocation().city) ? "" : mEvent.dataPublicEvent.getLocation().city;
        String country = Utils.isEmpty(mEvent.dataPublicEvent.getLocation().country_name) ? "" : mEvent.dataPublicEvent.getLocation().country_name;
        String details = city;
        if (!Utils.isEmpty(city) && !Utils.isEmpty(country))
            details += ", " + country;
        else if(!Utils.isEmpty(country))
            details = country;
        mLocationRow.setDetails(details);
        if (Utils.isEmpty(city) && Utils.isEmpty(country))
            mLocationRow.setDetails(null);

        // SCHEDULE
        if (mEvent.dataPublicEvent.schedule.from.isSameDay(mEvent.dataPublicEvent.schedule.to)) {
            mDateRow.setTitle(Utils.getDateStringFromDataDate(mEvent.dataPublicEvent.schedule.from, EditEventFragment.DATE_FORMAT_LONG));
            if (mEvent.dataPublicEvent.schedule.to.hasEndTime()) {
                mDateRow.setDetails(mEvent.dataPublicEvent.schedule.from.getHourString() + " - " + mEvent.dataPublicEvent.schedule.to.getHourString());
            } else {
                mDateRow.setDetails(mEvent.dataPublicEvent.schedule.from.getHourString());
            }
        } else {
            mDateRow.setTitle(Utils.getDateStringFromDataDate(mEvent.dataPublicEvent.schedule.from, EditEventFragment.DATE_FORMAT_LONG + " ") + " - " +
                    Utils.getDateStringFromDataDate(mEvent.dataPublicEvent.schedule.to, EditEventFragment.DATE_FORMAT_LONG));

            String at = dm.getResourceText(getString(R.string.At)) + " ";
            String from = Utils.getDateStringFromDataDate(mEvent.dataPublicEvent.schedule.from, EditEventFragment.DATE_FORMAT_SHORT) + at + mEvent.dataPublicEvent.schedule.from.getHourString();
            String to = Utils.getDateStringFromDataDate(mEvent.dataPublicEvent.schedule.to, EditEventFragment.DATE_FORMAT_SHORT) + at + mEvent.dataPublicEvent.schedule.to.getHourString();
            mDateRow.setDetails(from + " - " + to);
        }

        // PARTICIPANTS
        mPeopleRow.setTitle(String.valueOf(mEvent.dataPublicEvent.getAllUsers().size()) + " " + getString(R.string.Participants));
        if (mEvent.dataPublicEvent.spots == -1) {
            mPeopleRow.setDetails(dm.getResourceText(R.string.Unlimited));
        } else {
            try {
                mPeopleRow.setDetails(Utils.setFirstLetterUpperCase(new MessageFormat(dm.getResourceText(R.string.Event_open_spots)).format(new Object[]{mEvent.dataPublicEvent.spots})));
            } catch (Exception e) {
            }
        }

        // Host
        if (mEvent.dataPublicEvent.host != null) {
            mPeopleListItem.setTitle(Utils.setFirstLetterUpperCase(getString(R.string.Host)));
            mPeopleListItem.setDetails(mEvent.dataPublicEvent.host.first_name + " " + mEvent.dataPublicEvent.host.last_name);
            ImageView image = mPeopleListItem.getLeftImage();
            image.setVisibility(View.VISIBLE);
            String url = Utils.getImageUrl(mEvent.dataPublicEvent.host.image_url, image.getLayoutParams().width, image.getLayoutParams().width);
            Picasso.with(EverymatchApplication.getContext()).load(url).placeholder(DataManager.getInstance().getAvatarDrawable()).into(image);

            if (!Utils.isUserMe(mEvent.dataPublicEvent.host)) {

                mPeopleListItem.getRightIcon().setText(IconManager.getInstance(getActivity()).getIconString("Arrowright"));
                mPeopleListItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        UserActivity.openOtherUserFragment(getActivity(), mEvent.dataPublicEvent.host);
                    }
                });
            } else {
                mPeopleListItem.getRightIcon().setVisibility(View.GONE);
            }
        }

        setEventActions(view);
        String mStatus = mEvent.dataPublicEvent.user_event_status.status;

        if (mStatus.equals("participating")) {
            mPercent.setText(dm.getResourceText(R.string.Participating));
            mPercent.setTextColor(ds.getIntColor(EMColor.POSITIVE));
            mPercentIcon.setText(im.getIconString("StatusPositive"));
            mPercentIcon.setTextColor(ds.getIntColor(EMColor.POSITIVE));
        } else if (mStatus.equals("pending")) {
            mPercent.setText(dm.getResourceText(R.string.Pending));
            mPercent.setTextColor(ds.getIntColor(EMColor.MAYBE));
            mPercentIcon.setText(im.getIconString("StatusMaybe"));
            mPercentIcon.setTextColor(ds.getIntColor(EMColor.MAYBE));
        } else if (mStatus.equals("saved")) {
            mPercent.setText(dm.getResourceText(R.string.Saved));
            mPercent.setTextColor(ds.getIntColor(EMColor.MAYBE));
            mPercentIcon.setText(im.getIconString("StatusLater"));
            mPercentIcon.setTextColor(ds.getIntColor(EMColor.MAYBE));
        } else if (mStatus.equals("hosting")) {
            mPercent.setText("HOSTING");
            mPercent.setTextColor(ds.getIntColor(EMColor.WHITE));
            mPercentIcon.setText(im.getIconString("StatusHosting"));
            mPercentIcon.setTextColor(ds.getIntColor(EMColor.WHITE));
        } else if (mStatus.equals("invited")) {
            mPercent.setText("INVITED");
            mPercent.setTextColor(ds.getIntColor(EMColor.NEGATIVE));
            mPercentIcon.setText(im.getIconString("StatusInvited"));
            mPercentIcon.setTextColor(ds.getIntColor(EMColor.NEGATIVE));
        } else {
            /*here we need to set match percents*/
            mPercent.setText("");
            if (mDataMatchResults != null && mDataMatchResults.getQuestions_results().size() > 0) {
                try {
                    //mPercent.setText("" + mDataMatchResults.match + "%");
                    mPercent.setTextColor(ds.getIntColor(EMColor.WHITE));
                    mPercent.setOnClickListener(this);
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                }
            }
            /*mPercent.setText(mEvent.dataPublicEvent.match + "%");
            mPercent.setTextColor(ds.getIntColor(EMColor.WHITE));
            mPercentIcon.setText(IconManager.getInstance(getActivity()).getIconString("Match"));
            mPercentIcon.setTextColor(ds.getIntColor(EMColor.WHITE));*/
        }

        setPeopleCarousel();


        mAboutDetails.setText(mEvent.dataPublicEvent.event_description);

        if (TextUtils.isEmpty(mEvent.dataPublicEvent.event_description)) {
            mAboutDetails.setVisibility(View.GONE);
            mAboutTitle.setVisibility(View.GONE);
        }


    }

    private void setMaxScrolling() {
        final int actualHeight;
        final int imageViewHeight = mImageTitle.getHeight();
        final int imageViewWidth = mImageTitle.getWidth();
        final int bitmapHeight = mImageTitle.getDrawable().getIntrinsicHeight();
        final int bitmapWidth = mImageTitle.getDrawable().getIntrinsicWidth();
        if (imageViewHeight * bitmapWidth <= imageViewWidth * bitmapHeight) {
            actualHeight = imageViewHeight;
        } else {
            actualHeight = bitmapHeight * imageViewWidth / bitmapWidth;
        }

        mOverScrollView.setMaxOverScrollDelta(actualHeight - mInvisibleHeader.getMeasuredHeight());
    }

    private void setEventActions(View view) {
        ArrayList<DataEventActions> eventActions = mEvent.getEvent_actions();
        int size = eventActions.size();
        DataEventActions current;

        //First Item
        if (size >= 1) {
            current = eventActions.get(0);
            mToolbar1Text.setText(current.text);
            mToolbar1Button.setText(IconManager.getInstance(getActivity()).getIconString(current.icon));
        } else {
            view.findViewWithTag("action_1").setVisibility(View.INVISIBLE);
        }
        //Second Item
        if (size >= 2) {
            current = eventActions.get(1);
            mToolbar2Text.setText(current.text);
            mToolbar2Button.setText(IconManager.getInstance(getActivity()).getIconString(current.icon));
        } else {
            view.findViewWithTag("action_2").setVisibility(View.INVISIBLE);
        }
        // decide whether to show the "More" button
        if (size >= 3) {

            if (size == 3) {
                mToolbar3Text.setText(eventActions.get(2).text);
                mToolbar3Button.setText(IconManager.getInstance(getActivity()).getIconString(eventActions.get(2).icon));
            } else {
                mToolbar3Text.setText(dm.getResourceText(R.string.More));
                mToolbar3Button.setText(Consts.Icons.icon_MenuMore2);
            }

        } else {
            view.findViewWithTag("action_3").setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onBackButtonClicked() {
        getActivity().onBackPressed();
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

    @Override
    public void onClick(final View v) {
        final FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);

        switch (v.getId()) {
            case R.id.action_1:
            case R.id.event_toolbar_1_icon:
                eventToolbarAction(0);
                break;
            case R.id.action_2:
            case R.id.event_toolbar_2_icon:
                eventToolbarAction(1);
                break;
            case R.id.action_3:
            case R.id.event_toolbar_3_icon:
                if (mEvent.getEvent_actions().size() == 3) {
                    eventToolbarAction(2);
                    return;
                }

                if (mMorePopup != null && mMorePopup.isShowing()) {
                    mMorePopup.dismiss();
                    return;
                }
                List<PopupMenuItem> items = new ArrayList<>();
                for (int i = 2; i < mEvent.getEvent_actions().size(); i++) {
                    items.add(new PopupMenuItem(mEvent.getEvent_actions().get(i).text, mEvent.getEvent_actions().get(i).icon));
                }
                showMoreMenu(items);
                break;

            case R.id.event_row_details:
                ////*transaction.addToBackStack("myFragment")
                //       .add(R.id.event_layout, new EventDetailsFragment(/*TODO send details*/))
                //      .commit();*/
                QuestionnaireActivity.IS_VIEW_MODE = true;
                QuestionnaireActivity.editEvent(this, mEvent, null, QuestionnaireActivity.EDIT_EVENT_TYPE.PROFILE, EditEventFragment.CODE_EDIT_EVENT);

                break;
            case R.id.event_row_location:
                Nammu.askForPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION, new PermissionCallback() {
                    @Override
                    public void permissionGranted() {
                        EventLocationFragment eventLocationFragment = new EventLocationFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString(EventLocationFragment.EVENT_TITLE, mEvent.dataPublicEvent.event_title);
                        bundle.putDouble(EventLocationFragment.LAT, mEvent.dataPublicEvent.getLocation().coordinates.value[0][0]);
                        bundle.putDouble(EventLocationFragment.LON, mEvent.dataPublicEvent.getLocation().coordinates.value[0][1]);
                        bundle.putString(EventLocationFragment.ADDRESS, mEvent.dataPublicEvent.getLocation().text_address);
                        eventLocationFragment.setArguments(bundle);
                        fragmentTransaction
                                .addToBackStack("myFragment")
                                .add(R.id.event_layout, eventLocationFragment)
                                .commit();
                    }

                    @Override
                    public void permissionRefused() {
                        EMLog.d(TAG, " permissionRefused ACCESS_COARSE_LOCATION");
                        getActivity().getSupportFragmentManager().popBackStackImmediate();
                    }
                });

                break;
            case R.id.event_row_date:
                Calendar beginTime = Calendar.getInstance();
                DataDate from = mEvent.dataPublicEvent.schedule.from;
                beginTime.set(from.year, from.month, from.day, from.hour, from.minute);
                Calendar endTime = Calendar.getInstance();
                DataDate to = mEvent.dataPublicEvent.schedule.to;
                endTime.set(to.year, to.month, to.day, to.hour, to.minute);
                Intent intent = new Intent(Intent.ACTION_INSERT)
                        .setData(CalendarContract.Events.CONTENT_URI)
                        .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                        .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                        .putExtra(CalendarContract.Events.TITLE, mEvent.dataPublicEvent.event_title)
                        .putExtra(CalendarContract.Events.DESCRIPTION, mEvent.dataPublicEvent.event_description)
                        .putExtra(CalendarContract.Events.EVENT_LOCATION, mEvent.dataPublicEvent.getLocation().city)
                        .putExtra(CalendarContract.Events.AVAILABILITY, CalendarContract.Events.AVAILABILITY_FREE);
                //.putExtra(Intent.EXTRA_EMAIL, "rowan@example.com,trevor@example.com");
                startActivity(intent);
                break;
            case R.id.event_row_people:
                PeopleViewPagerFragment peopleViewPagerFragment = PeopleViewPagerFragment.getInstance(DataStore.SCREEN_TYPE_EVENT_PARTICIPANTS, mEvent);
                peopleViewPagerFragment.setTargetFragment(EventFragment.this, REQUEST_CHECK_PARTICIPANTS);

                fragmentTransaction.addToBackStack("myFragment")
                        .add(R.id.event_layout, peopleViewPagerFragment)
                        .commitAllowingStateLoss();
                break;

            case R.id.event_percent:
                if ((int) mDataMatchResults.match > 0)
                    MatchActivity.start(getActivity(), mDataMatchResults, RequestMatch.MATCH_TYPE_USER_TO_EVENT, null, mEvent);
                break;
        }
    }

    public void getFullEvent(final View view) {
        ServerConnector.getInstance().processRequest(new RequestEvent(mEvent._id), new ServerConnector.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {

                mProgressBar.setVisibility(View.GONE);
                ResponseEvent responseEvent = (ResponseEvent) baseResponse;
                if (responseEvent != null) {
                    mEvent = new DataEvent(responseEvent);
                    initViews(view);
                    setData(mView);
                    getEventMatch();
                } else {
                }
            }

            @Override
            public void onFailure(ErrorResponse errorResponse) {
                getActivity().onBackPressed();
            }
        }, TAG + RequestEvent.class.getSimpleName());
    }

    private void getEventMatch() {

        MatchManager.getMatch(RequestMatch.MATCH_TYPE_USER_TO_EVENT, mEvent._id, mEvent.activity_client_id, new GenericCallback() {
            @Override
            public void onDone(boolean success, Object data) {
                if (success) {
                    mDataMatchResults = (DataMatchResults) data;
                    if (mDataMatchResults.getQuestions_results().size() > 0) {
                        mPercent.setText("" + mDataMatchResults.match + "%");
                        setData(mView);
                    }
                } else {
                    Log.e(TAG, data.toString());
                }
            }
        });
       /*
        ServerConnector.getInstance().processRequest(new RequestMatch(RequestMatch.MATCH_TYPE_USER_TO_EVENT, mEvent._id, mEvent.activity_client_id), new ServerConnector.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                mProgressBar.setVisibility(View.GONE);
                try {
                    //String res = ((ResponseString) baseResponse).responseStr;
                    //mDataMatchResults = new Gson().fromJson(res, DataMatchResults.class);
                    mDataMatchResults = (DataMatchResults) baseResponse;
                    if (mDataMatchResults != null) {
                        if (mDataMatchResults.questions_results != null && mDataMatchResults.questions_results.size() > 0) {
                            mPercent.setText("" + mDataMatchResults.match + "%");
                            setData(mView);
                        }
                    }
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                }

            }

            @Override
            public void onFailure(ErrorResponse errorResponse) {
                getActivity().onBackPressed();
            }
        }, TAG + RequestEvent.class.getSimpleName());
   */
    }

    @Override
    protected void cancelPendingRequests(String... tags) {
        super.cancelPendingRequests(TAG + RequestEvent.class.getSimpleName());
    }

    public void eventToolbarAction(int position) {
        /* check if we need to call server for this action or just do some navigation*/

        String action = mEvent.getEvent_actions().get(position).action;
        if (action.equals("edit")) {
            transaction.addToBackStack("myFragment")
                    .replace(R.id.event_layout, EditEventFragment.getInstance(mEvent))
                    .commit();
            return;
        }
        if (action.equals("add_participant") || action.equals("invite")) {
            Fragment fragment = InviteParticipantsFragment.getInstance(mEvent, position, true, action);
            fragment.setTargetFragment(EventFragment.this, REQUEST_CODE_ADD_PARTICIPANTS);
            transaction.addToBackStack("myFragment")
                    .replace(R.id.event_layout, fragment)
                    .commit();
            return;
        }

        if (action.equals("message_host")) { // chat with host
            String id = mEvent.getEvent_actions().get(position).parameters.get("other_user_id");
            InboxActivity.startChat(getActivity(), null, id, ChatFragment.CHAT_TYPE_USER);
            return;
        }
        if (action.equals("chat")) {
            // event chat
            String id = mEvent.getEvent_actions().get(position).parameters.get("object_id");
            InboxActivity.startChat(getActivity(), null, id, ChatFragment.CHAT_TYPE_GROUP);
            return;
        }
        if (action.equals("share")) {
            return;
        }

        /* if we came here -> we must call the server */

        /*show loader  */
        rlActionLoader.setVisibility(View.VISIBLE);
        llActions.setClickable(false);

        // ServerConnector.getInstance().processRequest(new RequestEventActions(null, mEvent._id, action), new ServerConnector.OnResultListener() {
        ServerConnector.getInstance().processRequest(new RequestEventActions(mEvent.getEvent_actions().get(position)), new ServerConnector.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                rlActionLoader.setVisibility(View.GONE);
                llActions.setClickable(true);
                ResponseEvent responseEvent = (ResponseEvent) baseResponse;
                if (responseEvent != null) {
                    mEvent.setEvent(responseEvent);
                    setData(mView);
                }
            }

            @Override
            public void onFailure(ErrorResponse errorResponse) {
                rlActionLoader.setVisibility(View.GONE);
                llActions.setClickable(true);
            }
        });

    }

    private void showMoreMenu(List<PopupMenuItem> items) {
        if (mMoreActions == null) {
            mMoreActions = DataHelper.createEventMenuItems(items);
        }

        if (mMorePopup == null) {
            mMorePopup = new ListPopupWindow(getActivity());
        }

        mMorePopup.setAnchorView(mToolbar3Button);
        mMorePopup.setAdapter(new DiscoverMoreAdapter(mMoreActions));
        mMorePopup.setWidth(Utils.dpToPx(190));
        mMorePopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mMorePopup.dismiss();
                eventToolbarAction(2 + position);
            }
        });

        mMorePopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {

            }
        });

        mMorePopup.setModal(true);
        mMorePopup.show();
    }

    private void setPeopleCarousel() {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        PeopleCarouselFragment peopleCarouselFragment = (PeopleCarouselFragment) findFragment(PeopleCarouselFragment.TAG);

        DataPeopleHolder dataPeopleHolder = new DataPeopleHolder();
        dataPeopleHolder.setUsers(mEvent.dataPublicEvent.getAllUsers(Participation_Type.PARTICIPATING));

        if (peopleCarouselFragment != null) {
            peopleCarouselFragment.refreshData(dataPeopleHolder, dm.getResourceText(R.string.Coming), "");
        } else {
            peopleCarouselFragment = PeopleCarouselFragment.getInstance(dataPeopleHolder, dm.getResourceText(R.string.Coming), "");
            fragmentTransaction.add(mPeopleList.getId(), peopleCarouselFragment, PeopleCarouselFragment.TAG).commit();
        }
    }

    public void setEvent(DataEvent event) {
        mEvent = event;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_PARTICIPANTS) {
            mEvent = (DataEvent) data.getSerializableExtra(PeopleViewPagerFragment.EXTRA_EVENT);
            setData(mView);
        }
    }

    @Override
    public void onParticipantsChanged(DataEvent dataEvent) {
        if (dataEvent != null) {
            mEvent = dataEvent;
            setData(mView);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

}
