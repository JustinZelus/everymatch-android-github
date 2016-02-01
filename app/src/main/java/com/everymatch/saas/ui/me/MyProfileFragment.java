package com.everymatch.saas.ui.me;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.client.data.QuestionType;
import com.everymatch.saas.server.Data.DataAnswer;
import com.everymatch.saas.server.Data.DataQuestion;
import com.everymatch.saas.server.responses.ResponseApplication;
import com.everymatch.saas.server.responses.ResponseGetUser;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.singeltones.PusherManager;
import com.everymatch.saas.ui.base.BaseFragment;
import com.everymatch.saas.util.QuestionUtils;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.EventDataRow;
import com.everymatch.saas.view.EventHeader;
import com.everymatch.saas.view.FloatingEditTextLayout;
import com.squareup.picasso.Picasso;

import java.io.Serializable;

/**
 * Created by dors on 11/1/15.
 */
public class MyProfileFragment extends BaseFragment implements EventHeader.OnEventHeader, View.OnClickListener {

    public static final String TAG = MyProfileFragment.class.getSimpleName();
    private static final int TAG_QUESTION = 100;
    private static final int TAG_ANSWER = 101;
    private static final int MAX_IMAGE_SIZE = 800;

    // Views
    private EventHeader mHeader;
    private LinearLayout mQuestionContainer;
    private ImageView mUserImage;
    private ScrollView mScrollView;
    private View mImageContainer;
    private View mTextChangeImage;
    private FloatingEditTextLayout fetFirstName, fetLastName;

    // Data
    private ResponseGetUser mMyUser;
    private ResponseApplication mApplication;
    private MyProfileCallbacks mCallback;

    public static MyProfileFragment getInstance() {
        MyProfileFragment myProfileFragment = new MyProfileFragment();
        return myProfileFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MyProfileCallbacks) {
            mCallback = (MyProfileCallbacks) context;
        } else {
            throw new IllegalArgumentException(context + " must implements MyProfileCallbacks");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHeader = (EventHeader) view.findViewById(R.id.fragment_my_profile_event_header);
        mUserImage = (ImageView) view.findViewById(R.id.fragment_my_profile_image);
        mScrollView = (ScrollView) view.findViewById(R.id.fragment_my_profile_scroll_view);
        mImageContainer = view.findViewById(R.id.fragment_my_profile_image_container);
        mQuestionContainer = (LinearLayout) view.findViewById(R.id.fragment_my_profile_question_container);
        mHeader = (EventHeader) view.findViewById(R.id.fragment_my_profile_event_header);
        mTextChangeImage = view.findViewById(R.id.fragment_my_profile_change_image_text);
        mTextChangeImage.setOnClickListener(this);

        mScrollView.post(new Runnable() {
            @Override
            public void run() {
                mImageContainer.getLayoutParams().height = mScrollView.getMeasuredHeight() / 2;
                mImageContainer.requestLayout();
                refreshData();
                setHeader();
            }
        });

        fetFirstName = (FloatingEditTextLayout) view.findViewById(R.id.fetFirstName);
        fetLastName = (FloatingEditTextLayout) view.findViewById(R.id.fetLastName);
    }

    /**
     * Set header of this page
     */
    private void setHeader() {
        mHeader.setListener(this);
        mHeader.getBackButton().setText(Consts.Icons.icon_ArrowBack);
        mHeader.getIconOne().setVisibility(View.GONE);
        mHeader.getIconTwo().setVisibility(View.GONE);
        mHeader.getIconThree().setVisibility(View.GONE);
        mHeader.setTitle(dm.getResourceText(R.string.My_Profile));
    }

    /**
     * Set my user data
     */
    public void refreshData() {
        mMyUser = DataStore.getInstance().getUser();

        /* update static questions */
        fetFirstName.getEtValue().setText(mMyUser.first_name);
        fetLastName.getEtValue().setText(mMyUser.last_name);

        mQuestionContainer.removeAllViews();

        mApplication = DataStore.getInstance().getApplicationData();

        for (DataQuestion dataQuestion : mApplication.getUser_profile_questions()) {
            if (QuestionType.IMAGE_UPLOAD.equals(dataQuestion.form_type)) {
                setImageData(dataQuestion);
            } else {
                setQuestionData(dataQuestion);
            }
        }
    }

    private void setImageData(DataQuestion dataQuestion) {

        if (mMyUser.profiles.user_profile == null) {
            return;
        }

        for (DataAnswer answer : mMyUser.profiles.user_profile.answers) {
            if (answer.questions_id == dataQuestion.questions_id) {
                String imageUrl = (String) answer.value;
                mTextChangeImage.setTag(R.id.TAG_2, answer);

                if (!TextUtils.isEmpty(imageUrl)) {
                    int imageSize = Math.min(mImageContainer.getLayoutParams().height / 2, MAX_IMAGE_SIZE);
                    Picasso.with(getActivity()).load(Utils.getImageUrl(imageUrl, imageSize, 0)).into(mUserImage);
                }

                break;
            }
        }

        mTextChangeImage.setTag(R.id.TAG_1, dataQuestion);
        mTextChangeImage.setTag(R.id.TAG_3, "");
    }

    private void setQuestionData(DataQuestion dataQuestion) {

        DataAnswer requiredAnswer = null;

        if (mMyUser.profiles.user_profile == null) {
            return;
        }

        // Find the related answer
        for (DataAnswer answer : mMyUser.profiles.user_profile.answers) {
            if (answer.questions_id == dataQuestion.questions_id) {
                requiredAnswer = answer;
                break;
            }
        }

        EventDataRow eventDataRow = new EventDataRow(getActivity());
        eventDataRow.setTitle(dataQuestion.text_title);
        eventDataRow.getDetailsView().setVisibility(View.VISIBLE);
        eventDataRow.getLeftIcon().setVisibility(View.GONE);
        eventDataRow.getRightIcon().setVisibility(View.VISIBLE);
        eventDataRow.getRightIcon().setText(Consts.Icons.icon_Next);
        eventDataRow.getLeftMediaContainer().setVisibility(View.GONE);
        eventDataRow.setOnClickListener(this);
        eventDataRow.getDetailsView().setText(QuestionUtils.getAnsweredTitle(dataQuestion, requiredAnswer));

        eventDataRow.setTag(R.id.TAG_1, dataQuestion);
        eventDataRow.setTag(R.id.TAG_2, requiredAnswer);
        eventDataRow.setTag(R.id.TAG_3, eventDataRow.getDetailsView().getText().toString());
        mQuestionContainer.addView(eventDataRow);

        // Add separator
        View view = new View(getContext());
        mQuestionContainer.addView(view);
        view.getLayoutParams().width = LinearLayout.LayoutParams.MATCH_PARENT;
        view.getLayoutParams().height = Utils.dpToPx(1);
        view.setBackgroundColor(DataStore.getInstance().getIntColor(EMColor.FOG));
        view.requestLayout();
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
    public void onClick(View v) {
        mCallback.onQuestionClick((DataQuestion) v.getTag(R.id.TAG_1), (DataAnswer) v.getTag(R.id.TAG_2), (String) v.getTag(R.id.TAG_3));
    }

    public interface MyProfileCallbacks {
        void onQuestionClick(DataQuestion dataQuestion, DataAnswer answer, String answerStr);
    }

    @Override
    protected void handleBroadcast(Serializable eventData, String eventName) {
        if (PusherManager.PUSHER_EVENT_PROFILES.equals(eventName))
            refreshData();
    }
}
