package com.everymatch.saas.ui.me;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.everymatch.saas.server.request_manager.ProfileManager;
import com.everymatch.saas.server.responses.ResponseApplication;
import com.everymatch.saas.server.responses.ResponseGetUser;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.singeltones.GenericCallback;
import com.everymatch.saas.singeltones.PusherManager;
import com.everymatch.saas.ui.BaseActivity;
import com.everymatch.saas.ui.base.BaseFragment;
import com.everymatch.saas.ui.me.settings.SettingsFragment;
import com.everymatch.saas.util.QuestionUtils;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.EventDataRow;
import com.everymatch.saas.view.EventHeader;
import com.everymatch.saas.view.ViewSeperator;
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

    public static final int REQUEST_CODE_FIRST_NAME = 103;
    public static final int REQUEST_CODE_LAST_NAME = 104;

    // Views
    private EventHeader mHeader;
    private LinearLayout mQuestionContainer, mStaticQuestionsHolder;
    private ImageView mUserImage;
    private ScrollView mScrollView;
    private View mImageContainer;
    private View mTextChangeImage;
    // private FloatingEditTextLayout fetFirstName, fetLastName;
    //private EditText etFirstName, etLastName;

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
        mStaticQuestionsHolder = (LinearLayout) view.findViewById(R.id.staticQuestionsHolder);
        mHeader = (EventHeader) view.findViewById(R.id.fragment_my_profile_event_header);
        mTextChangeImage = view.findViewById(R.id.fragment_my_profile_change_image_text);

        //etFirstName = (EditText) view.findViewById(R.id.etFirstName);
        //etLastName = (EditText) view.findViewById(R.id.etLastName);

        mTextChangeImage.setOnClickListener(this);


        mScrollView.post(new Runnable() {
            @Override
            public void run() {
                //mImageContainer.getLayoutParams().height = mScrollView.getMeasuredHeight() / 2;
                //mImageContainer.getLayoutParams().height = mImageContainer.getLayoutParams().width;
                // mImageContainer.requestLayout();

                mUserImage.getLayoutParams().height = Utils.getScreenWidth(getActivity());
                mUserImage.requestLayout();

                refreshData();
                setHeader();
            }
        });

        //fetFirstName = (FloatingEditTextLayout) view.findViewById(R.id.fetFirstName);
        //fetLastName = (FloatingEditTextLayout) view.findViewById(R.id.fetLastName);
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
        //fetFirstName.getEtValue().setText(mMyUser.first_name);
        //fetLastName.getEtValue().setText(mMyUser.last_name);

        //etFirstName.setText(ds.getUser().first_name);
        //etLastName.setText(ds.getUser().last_name);
        //***************************************

        //add static questions
        addStaticQuestion();

        //add dynamic questions
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

    private void addStaticQuestion() {
        mStaticQuestionsHolder.removeAllViews();

        //first name
        EventDataRow edrFirstName = new EventDataRow(getActivity());
        edrFirstName.setTitle(dm.getResourceText(R.string.FirstName));
        edrFirstName.setDetails(ds.getUser().first_name);
        edrFirstName.getLeftMediaContainer().setVisibility(View.GONE);
        edrFirstName.setRightIconText(Consts.Icons.icon_Next);
        edrFirstName.setTag("first");
        edrFirstName.setLargePaddingTopBottom();
        edrFirstName.setOnClickListener(this);

        mStaticQuestionsHolder.addView(edrFirstName);
        mStaticQuestionsHolder.addView(new ViewSeperator(getActivity(), null));

        //last name
        EventDataRow edrLastName = new EventDataRow(getActivity());
        edrLastName.setTag("last");
        edrLastName.setTitle(dm.getResourceText(R.string.LastName));
        edrLastName.setDetails(ds.getUser().last_name);
        edrLastName.getLeftMediaContainer().setVisibility(View.GONE);
        edrLastName.setRightIconText(Consts.Icons.icon_Next);
        edrLastName.setLargePaddingTopBottom();
        edrLastName.setOnClickListener(this);

        mStaticQuestionsHolder.addView(edrLastName);
        mStaticQuestionsHolder.addView(new ViewSeperator(getActivity(), null));


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
                    imageSize = mUserImage.getMeasuredWidth();
                    imageSize = Utils.getScreenWidth(getActivity());
                    /*Picasso.with(getActivity())
                            .load(Utils.getImageUrl(imageUrl, imageSize, imageSize))
                            .into(mUserImage);*/

                    Picasso.with(getActivity())
                            .load(Utils.getImageUrl(imageUrl, mImageContainer.getLayoutParams().height / 2,
                                    0)).into(mUserImage);

                    //Picasso.with(getActivity()).load(Utils.getImageUrl(imageUrl, imageSize, 0)).into(mUserImage);
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
        eventDataRow.setLargePaddingTopBottom();
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
        view.setBackgroundColor(ds.getIntColor(EMColor.FOG));
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
        if (v.getTag() != null) {
            //need to set first or last name

            String type = (String) v.getTag();
            Fragment fragment = null;
            if (!Utils.isEmpty(type) && type.equals("first")) {
                fragment = FirstLastNameFragment.getInstance(dm.getResourceText(R.string.FirstName), ds.getUser().first_name);
                fragment.setTargetFragment(MyProfileFragment.this, REQUEST_CODE_FIRST_NAME);
            } else if (!Utils.isEmpty(type) && type.equals("last")) {
                fragment = FirstLastNameFragment.getInstance(dm.getResourceText(R.string.LastName), ds.getUser().last_name);
                fragment.setTargetFragment(MyProfileFragment.this, REQUEST_CODE_LAST_NAME);
            }

            ((BaseActivity) getActivity()).replaceFragment(R.id.fragment_container, fragment,
                    SettingsFragment.TAG, true, null, R.anim.enter_from_right, R.anim.exit_to_left,
                    R.anim.enter_from_left, R.anim.exit_to_right);

            return;
        }
        mCallback.onQuestionClick((DataQuestion) v.getTag(R.id.TAG_1), (DataAnswer) v.getTag(R.id.TAG_2), (String) v.getTag(R.id.TAG_3));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_FIRST_NAME || requestCode == REQUEST_CODE_LAST_NAME) {
            if (requestCode == REQUEST_CODE_FIRST_NAME) {
                String value = data.getStringExtra(FirstLastNameFragment.EXTRA_VALUE);
                ds.getUser().first_name = value;
            }
            if (requestCode == REQUEST_CODE_LAST_NAME) {
                String value = data.getStringExtra(FirstLastNameFragment.EXTRA_VALUE);
                ds.getUser().last_name = value;
            }

            showDialog(dm.getResourceText(R.string.Loading));
            ProfileManager.UpdateProfile(new GenericCallback() {
                @Override
                public void onDone(boolean success, Object data) {
                    stopDialog();
                }
            });
        }
    }

    public interface MyProfileCallbacks {
        void onQuestionClick(DataQuestion dataQuestion, DataAnswer answer, String answerStr);
    }

    @Override
    protected void handleBroadcast(Serializable eventData, String eventName) {
        if (PusherManager.PUSHER_EVENT_PROFILES.equals(eventName)) {
            mMyUser = ds.getUser();
            refreshData();
        }
    }
}
