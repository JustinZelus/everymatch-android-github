package com.everymatch.saas.ui.discover;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.client.data.IconType;
import com.everymatch.saas.server.Data.DataActivity;
import com.everymatch.saas.server.Data.DataIcon;
import com.everymatch.saas.server.Data.DataMatchResults;
import com.everymatch.saas.server.Data.DataPeople;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.server.request_manager.MatchManager;
import com.everymatch.saas.server.requests.RequestAddFriend;
import com.everymatch.saas.server.requests.RequestDeleteFriend;
import com.everymatch.saas.server.requests.RequestMatch;
import com.everymatch.saas.server.requests.RequestOtherUser;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.server.responses.ResponseOtherUser;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.singeltones.GenericCallback;
import com.everymatch.saas.ui.base.BaseFragment;
import com.everymatch.saas.ui.common.EventCarouselFragment;
import com.everymatch.saas.ui.match.MatchActivity;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.IconManager;
import com.everymatch.saas.util.TypeFaceProvider;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.EventDataRow;
import com.everymatch.saas.view.EventHeader;
import com.squareup.picasso.Picasso;

/**
 * Created by PopApp_laptop on 31/08/2015.
 */
public class PeopleFragment extends BaseFragment implements EventHeader.OnEventHeader {

    public static final String TAG = PeopleFragment.class.getSimpleName();

    private static final String EXTRA_PEOPLE = "extra.people";


    // Views
    private EventHeader mHeader;
    private LinearLayout mDetailsContainer;
    private ImageView mUserImage;
    private ScrollView mScrollView;
    private View mImageContainer;
    private TextView mTextHowYouMatch;
    private TextView mTextAbout;
    private TextView mTextAboutTitle;
    private EventDataRow mTextAge;
    private EventDataRow mTextLocation;
    private EventDataRow mTextPersonality;
    private FrameLayout mEventsContainer;
    private LinearLayout mActivitiesContainer;
    private TextView mTextActivityProfileTitle;

    // Data
    private DataPeople mCacheUser; // The object the comes in args to this fragment
    private ResponseOtherUser mUserFullObject; // The object the is being fetched from the server
    private Callbacks mCallbacks;

    public static PeopleFragment getInstance(DataPeople user) {
        PeopleFragment fragment = new PeopleFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_PEOPLE, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Callbacks) {
            mCallbacks = (Callbacks) context;
        } else {
            throw new IllegalStateException("Context must implements " + Callbacks.class.getName());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCacheUser = (DataPeople) getArguments().getSerializable(EXTRA_PEOPLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_people, container, false);
        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mUserImage = (ImageView) view.findViewById(R.id.fragment_people_image);
        mScrollView = (ScrollView) view.findViewById(R.id.fragment_people_scroll_view);
        mImageContainer = view.findViewById(R.id.fragment_people_image_container);
        mDetailsContainer = (LinearLayout) view.findViewById(R.id.fragment_people_details_container);
        mHeader = (EventHeader) view.findViewById(R.id.fragment_people_event_header);
        mTextAbout = (TextView) view.findViewById(R.id.fragment_people_text_about);
        mTextAboutTitle = (TextView) view.findViewById(R.id.fragment_people_text_about_title);
        mTextHowYouMatch = (TextView) view.findViewById(R.id.fragment_people_text_how_your_match);
        mTextAge = (EventDataRow) view.findViewById(R.id.fragment_people_data_row_age);
        mTextLocation = (EventDataRow) view.findViewById(R.id.fragment_people_data_row_location);
        mTextPersonality = (EventDataRow) view.findViewById(R.id.fragment_people_data_row_personality);
        mEventsContainer = (FrameLayout) view.findViewById(R.id.fragment_people_events_container);
        mActivitiesContainer = (LinearLayout) view.findViewById(R.id.fragment_people_activities_container);
        mTextActivityProfileTitle = (TextView) view.findViewById(R.id.fragment_people_activity_profile_title);

        mScrollView.post(new Runnable() {
            @Override
            public void run() {
                mImageContainer.getLayoutParams().height = mScrollView.getMeasuredHeight() / 2;
                mImageContainer.requestLayout();
            }
        });

        setHeader();
        fetchUserData();
    }

    /**
     * Set header for this page
     */
    private void setHeader() {
        mHeader.setListener(this);
        mHeader.getBackButton().setText(Consts.Icons.icon_ArrowBack);
        mHeader.getIconOne().setVisibility(View.GONE);
        mHeader.getIconTwo().setText(Consts.Icons.icon_Chat);
        mHeader.getIconThree().setText(Consts.Icons.icon_Favorite); // Place holder for space
        mHeader.getIconThree().setVisibility(View.INVISIBLE);
        mHeader.setTitle(mCacheUser.first_name + " " + mCacheUser.last_name);
    }

    /**
     * Fetch current user data
     */
    private void fetchUserData() {

        ServerConnector.getInstance().processRequest(new RequestOtherUser(mCacheUser.users_id),
                new ServerConnector.OnResultListener() {
                    @Override
                    public void onSuccess(BaseResponse baseResponse) {
                        mProgressBar.setVisibility(View.GONE);
                        mUserFullObject = (ResponseOtherUser) baseResponse;

                        if (mUserFullObject == null) {
                            return;
                        }

                        mDetailsContainer.setVisibility(View.VISIBLE);
                        setUserData();
                    }

                    @Override
                    public void onFailure(ErrorResponse errorResponse) {
                        mProgressBar.setVisibility(View.GONE);
                        //mNoConnectionView.setVisibility(View.VISIBLE);
                    }
                }, TAG + RequestOtherUser.class.getName());
    }

    /**
     * Set user data once object fetched from the server
     */
    private void setUserData() {

        // User image
        if (!TextUtils.isEmpty(mUserFullObject.image_url)) {
            Picasso.with(getActivity()).load(Utils.getImageUrl(mUserFullObject.image_url, mImageContainer.getLayoutParams().height / 2,
                    0)).into(mUserImage);
        }

        // How you match text
        mTextHowYouMatch.setText(mTextHowYouMatch.getText() + " " + mUserFullObject.first_name);

        // Age
        mTextAge.setTitle(dm.getResourceText("Age"));
        mTextAge.setRightText(mUserFullObject.age);

        // Location
        mTextLocation.setTitle(dm.getResourceText("Location"));
        if (mUserFullObject.location != null) {
            String location = "";

            if (!TextUtils.isEmpty(mUserFullObject.location.city)) {
                location += mUserFullObject.location.city;

                if (!TextUtils.isEmpty(mUserFullObject.location.country_code)) {
                    location += ", " + mUserFullObject.location.country_code;
                }

            } else if (!TextUtils.isEmpty(mUserFullObject.location.country_code)) {
                location += mUserFullObject.location.country_code;
            }

            mTextLocation.setRightText(location);
        }

        mTextPersonality.setTitle(dm.getResourceText("Personality"));

        // About
        if (TextUtils.isEmpty(mUserFullObject.about)) {
            mTextAbout.setVisibility(View.GONE);
            mTextAboutTitle.setVisibility(View.GONE);
        } else {
            mTextAbout.setText(mUserFullObject.about);
        }

        // Events
        if (!Utils.isArrayListEmpty(mUserFullObject.events)) {
            mEventsContainer.setVisibility(View.VISIBLE);
            String eventsText = String.format(dm.getResourceText(R.string.Events) + " (%d)", mUserFullObject.events.size());
            EventCarouselFragment eventCarouselFragment = EventCarouselFragment.getInstance(mUserFullObject.events, eventsText, "");
            getActivity().getSupportFragmentManager().beginTransaction().add(mEventsContainer.getId(),
                    eventCarouselFragment, EventCarouselFragment.TAG).commit();
        }

        // Activities
        if (!Utils.isArrayEmpty(mUserFullObject.activities)) {
            mActivitiesContainer.setVisibility(View.VISIBLE);

            String activitiesText = String.format(mTextActivityProfileTitle.getText() + " (%d)", mUserFullObject.activities.length);
            mTextActivityProfileTitle.setText(activitiesText);

            for (DataActivity dataActivity : mUserFullObject.activities) {

                EventDataRow eventDataRow = new EventDataRow(getContext());
                //the EDR itself will hold the
                eventDataRow.setTag(dataActivity);
                //eventDataRow.getRightText().setTag(dataActivity);
                eventDataRow.setOnClickListener(onClickEdrMatch);

                eventDataRow.setBackgroundColor(ds.getIntColor(EMColor.WHITE));
                eventDataRow.getDetailsView().setVisibility(View.GONE);
                eventDataRow.getTitleView().setText(dataActivity.text_title);
                eventDataRow.getRightIcon().setVisibility(View.GONE);
                eventDataRow.getRightText().setVisibility(View.VISIBLE);
                eventDataRow.getRightText().setText(dm.getResourceText(R.string.See_Your_Match));

                DataIcon dataIcon = dataActivity.icon;

                if (IconType.FONT.equals(dataIcon.getType())) {
                    eventDataRow.getLeftIcon().setText(IconManager.getInstance(getContext()).getIconString(dataIcon.getValue()));
                    eventDataRow.getLeftIcon().setTextColor(ds.getIntColor(EMColor.NIGHT));
                } else {
                    int picturesSize = eventDataRow.getLeftImage().getLayoutParams().width;
                    Picasso.with(getContext()).load(Utils.getImageUrl(dataIcon.getValue(), picturesSize, 0)).into(eventDataRow.getLeftImage());
                }

                eventDataRow.getLeftImage().setVisibility(View.VISIBLE);
                eventDataRow.getRightText().setText(dm.getResourceText(R.string.See_Your_Match));
                eventDataRow.getRightText().setTextColor(ds.getIntColor(EMColor.PRIMARY));
                eventDataRow.getRightText().setTypeface(TypeFaceProvider.getTypeFace(TypeFaceProvider.FONT_LATO));


                mActivitiesContainer.addView(eventDataRow);

                // Add separator
                View view = new View(getContext());
                mActivitiesContainer.addView(view);
                view.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
                view.getLayoutParams().height = Utils.dpToPx(1);
                view.setBackgroundColor(DataStore.getInstance().getIntColor(EMColor.FOG));
                view.requestLayout();
            }

        }

        // Friendship
        mHeader.getIconThree().setVisibility(View.VISIBLE);
        mHeader.getIconThree().setText(mUserFullObject.is_friend ? Consts.Icons.icon_Heart : Consts.Icons.icon_Favorite);
    }

    /**
     * Make a request for cancel or add friendship with this user
     */
    private void addOrCancelFriendship() {

        if (mUserFullObject.is_friend) {

            mHeader.getIconThree().setText(Consts.Icons.icon_Favorite);
            mUserFullObject.is_friend = false;

            ServerConnector.getInstance().processRequest(new RequestDeleteFriend(mUserFullObject.users_id), new ServerConnector.OnResultListener() {
                @Override
                public void onSuccess(BaseResponse baseResponse) {
                    EMLog.i(TAG, "addOrCancelFriendship::RequestDeleteFriend - onSuccess");
                }

                @Override
                public void onFailure(ErrorResponse errorResponse) {
                    EMLog.i(TAG, "addOrCancelFriendship::RequestDeleteFriend - onFailure");
                    mHeader.getIconThree().setText(Consts.Icons.icon_Heart);
                    mUserFullObject.is_friend = true;
                }
            }, TAG + RequestDeleteFriend.class.getName());
        } else {

            mHeader.getIconThree().setText(Consts.Icons.icon_Heart);
            mUserFullObject.is_friend = true;

            ServerConnector.getInstance().processRequest(new RequestAddFriend(mUserFullObject.users_id), new ServerConnector.OnResultListener() {

                @Override
                public void onSuccess(BaseResponse baseResponse) {
                    EMLog.i(TAG, "addOrCancelFriendship::RequestAddFriend - onSuccess");
                }

                @Override
                public void onFailure(ErrorResponse errorResponse) {
                    EMLog.i(TAG, "addOrCancelFriendship::RequestAddFriend - onFailure");
                    mHeader.getIconThree().setText(Consts.Icons.icon_Favorite);
                    mUserFullObject.is_friend = false;
                }
            }, TAG + RequestAddFriend.class.getName());
        }
    }

    private View.OnClickListener onClickEdrMatch = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                final EventDataRow edr = (EventDataRow) v;

                if (edr.getRightText().getTag() != null) {
                    DataMatchResults dataMatchResults = (DataMatchResults) edr.getRightText().getTag();
                    if ((int) dataMatchResults.match == 0)
                        return;
                    //this is the second time user press the text -> should go to match page
                    MatchActivity.start(getActivity(), dataMatchResults, RequestMatch.MATCH_TYPE_USER_TO_USER, mUserFullObject, null);
                    return;
                }
                // get match data and set tag to right text
                DataActivity dataActivity = (DataActivity) v.getTag();
                if (dataActivity == null) return;
                MatchManager.getMatch(RequestMatch.MATCH_TYPE_USER_TO_USER, mUserFullObject.users_id, dataActivity.client_id, new GenericCallback() {
                    @Override
                    public void onDone(boolean success, Object data) {
                        if (success) {
                            DataMatchResults matchResults = (DataMatchResults) data;
                            edr.getRightText().setTag(matchResults);
                            edr.setRightText("" + matchResults.match + "%");
                        }
                    }
                });
            } catch (Exception ex) {
                EMLog.e(TAG, ex.getMessage());
            }
        }
    };

    @Override
    public void onDetach() {
        ServerConnector.getInstance().cancelPendingRequests(TAG + RequestOtherUser.class.getName());
        ServerConnector.getInstance().cancelPendingRequests(TAG + RequestDeleteFriend.class.getName());
        ServerConnector.getInstance().cancelPendingRequests(TAG + RequestAddFriend.class.getName());
        super.onDetach();
    }

    @Override
    public void onTwoIconClicked() {
        mCallbacks.onChatButtonClick(mCacheUser.users_id);
    }

    @Override
    public void onThreeIconClicked() {
        addOrCancelFriendship();
    }

    @Override
    public void onBackButtonClicked() {
        getActivity().onBackPressed();
    }

    public interface Callbacks {
        void onChatButtonClick(String userId);
    }

    @Override
    public void onOneIconClicked() {
    }
}
