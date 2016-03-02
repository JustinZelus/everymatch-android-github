package com.everymatch.saas.ui.event;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.client.data.FormType;
import com.everymatch.saas.server.Data.DataActivity;
import com.everymatch.saas.server.Data.DataEvent;
import com.everymatch.saas.server.Data.DataEvent_Activity;
import com.everymatch.saas.server.Data.DataQuestion;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.ui.base.BaseFragment;
import com.everymatch.saas.ui.questionnaire.QuestionnaireActivity;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.EventDataRow;
import com.everymatch.saas.view.EventHeader;

import java.text.MessageFormat;

/**
 * Created by dors on 11/15/15.
 */
public class EditEventFragment extends BaseFragment implements EventHeader.OnEventHeader, View.OnClickListener {

    private static final String KEY_EVENT = "key.event";

    private static final String TAG = EditEventFragment.class.getSimpleName();
    public  static final int CODE_EDIT_EVENT = 19;

    // Data
    private DataEvent mEvent;

    // Views
    EventDataRow mRowSettings;
    EventDataRow mRowProfile;
    EventDataRow mRowLocation;
    EventDataRow mRowSchedule;
    EventDataRow mRowParticipants;

    public static BaseFragment getInstance(DataEvent event) {
        EditEventFragment editEventFragment = new EditEventFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_EVENT, event);
        editEventFragment.setArguments(args);
        return editEventFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEvent = (DataEvent) getArguments().getSerializable(KEY_EVENT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_event, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHeader((EventHeader) view.findViewById(R.id.fragment_edit_event_header));
        mRowSettings = (EventDataRow) view.findViewById(R.id.fragment_edit_event_settings);
        mRowProfile = (EventDataRow) view.findViewById(R.id.fragment_edit_event_profile);
        mRowLocation = (EventDataRow) view.findViewById(R.id.fragment_edit_event_location);
        mRowSchedule = (EventDataRow) view.findViewById(R.id.fragment_edit_event_schedule);
        mRowParticipants = (EventDataRow) view.findViewById(R.id.fragment_edit_event_participants);
        mRowSettings.setOnClickListener(this);
        mRowProfile.setOnClickListener(this);
        mRowLocation.setOnClickListener(this);
        mRowSchedule.setOnClickListener(this);
        mRowParticipants.setOnClickListener(this);
        setDetails();
    }

    public String numberFormat(int n) {
        String answer = "" + n;
        try {
            answer = (String.format("%02d", n));
        } catch (Exception ex) {
        }
        return answer;
    }

    /***
     * Set event details
     */
    private void setDetails() {

        // Profile - here we get the labels of the important questions

        String profileSummary = "";

        DataActivity activity = ds.getApplicationData().getActivityClientIdById(Integer.parseInt(mEvent.activity_client_id));
        DataEvent_Activity event = activity.getEvent_activityById(mEvent.dataPublicEvent.event_id);

        for (DataQuestion question : event.questions) {

            if (question.is_important) {
                if (TextUtils.isEmpty(profileSummary)) {
                    profileSummary += question.text_title;
                } else {
                    profileSummary += ", " + question.text_title;
                }
            }

            if (question.form_type.equals(FormType.SCHEDULE)) {
                mRowSchedule.setTag(R.id.TAG_1, question);
            } else if (question.form_type.equals(FormType.LOCATION)) {
                mRowLocation.setTag(R.id.TAG_1, question);
            }

            //break;
        }


        mRowProfile.setDetails(profileSummary);


        // Location
        mRowLocation.setDetails(mEvent.dataPublicEvent.getLocation().text_address);

        // Schedule
        String strDate = Utils.getDateStringFromDataDate(mEvent.dataPublicEvent.schedule.from, "EEE, MMM d, yyyy");
        if (mEvent.dataPublicEvent.schedule.from != null)
            strDate += "    " + numberFormat(mEvent.dataPublicEvent.schedule.from.hour) + ":"
                    + numberFormat(mEvent.dataPublicEvent.schedule.from.minute) + " - " +
                    numberFormat(mEvent.dataPublicEvent.schedule.to.hour) +
                    ":" + numberFormat(mEvent.dataPublicEvent.schedule.to.minute);
        mRowSchedule.setDetails(strDate);


        // Participants
        int size = mEvent.dataPublicEvent.getAllUsers().size();
        mRowParticipants.setTitle(dm.getResourceText(R.string.Participants));

        if (mEvent.dataPublicEvent.spots == -1) {
            mRowParticipants.setDetails(DataManager.getInstance().getResourceText(R.string.Unlimited));
        } else {
            try {
                mRowParticipants.setDetails("" + size + " " + new MessageFormat(DataManager.getInstance().getResourceText
                        (R.string.Event_open_spots)).format(new Object[]{mEvent.dataPublicEvent.spots}));
            } catch (Exception e) {
                mRowParticipants.setDetails(String.valueOf(mEvent.dataPublicEvent.spots));
            }
        }
    }

    /**
     * Set header for this page
     */
    private void setHeader(EventHeader eventHeader) {
        eventHeader.setListener(this);
        eventHeader.getBackButton().setText(Consts.Icons.icon_ArrowBack);
        eventHeader.getIconOne().setVisibility(View.GONE);
        eventHeader.getIconTwo().setVisibility(View.GONE);
        eventHeader.getIconThree().setVisibility(View.GONE);
        eventHeader.setTitle(DataManager.getInstance().getResourceText(R.string.EventEdit));
    }

    @Override
    public void onBackButtonClicked() {
        getActivity().onBackPressed();
    }

    @Override
    public void onOneIconClicked() {
        getActivity().onBackPressed();
    }

    @Override
    public void onTwoIconClicked() {
    }

    @Override
    public void onThreeIconClicked() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_edit_event_settings:
                QuestionnaireActivity.editEvent(this, mEvent, null, QuestionnaireActivity.EDIT_EVENT_TYPE.SETTINGS, CODE_EDIT_EVENT);
                break;

            case R.id.fragment_edit_event_profile:
                QuestionnaireActivity.editEvent(this, mEvent, null, QuestionnaireActivity.EDIT_EVENT_TYPE.PROFILE, CODE_EDIT_EVENT);
                break;

            case R.id.fragment_edit_event_location:
            case R.id.fragment_edit_event_schedule:
                DataQuestion question = (DataQuestion) v.getTag(R.id.TAG_1);
                QuestionnaireActivity.editEvent(this, mEvent, question, QuestionnaireActivity.EDIT_EVENT_TYPE.SIGNLE_QUESTION, CODE_EDIT_EVENT);
                break;

            case R.id.fragment_edit_event_participants:
                QuestionnaireActivity.editEvent(this, mEvent, null, QuestionnaireActivity.EDIT_EVENT_TYPE.PARTICIPANTS, CODE_EDIT_EVENT);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == CODE_EDIT_EVENT) {
            if (data != null && data.hasExtra(QuestionnaireActivity.EXTRA_EVENT)) {
                mEvent = (DataEvent) data.getSerializableExtra(QuestionnaireActivity.EXTRA_EVENT);
                setDetails();
            }
        }
    }
}