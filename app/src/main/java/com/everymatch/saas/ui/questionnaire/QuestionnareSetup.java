package com.everymatch.saas.ui.questionnaire;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.client.data.JoinType;
import com.everymatch.saas.ui.questionnaire.base.QuestionnaireQuestionBaseFragment;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.BaseTextView;
import com.rey.material.widget.Switch;

import org.json.JSONObject;

/**
 * Created by PopApp_laptop on 18/10/2015.
 */
public class QuestionnareSetup extends QuestionnaireQuestionBaseFragment implements Switch.OnCheckedChangeListener, View.OnClickListener {

    //Data
    String[] spots, privacy;

    //Views
    BaseTextView tvEventSetupSpotsNumber, tvEventSetupPrivacy;
    com.rey.material.widget.Switch switchSetupEventJoinType, switchSetupIsParticipating;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spots = new String[100];
        spots[0] = dm.getResourceText(R.string.Unlimited);
        for (int i = 1; i < 100; ++i)
            spots[i] = ("" + i);
        privacy = new String[2];
        privacy[0] = dm.getResourceText(R.string.Public);
        privacy[1] = dm.getResourceText(R.string.Private);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        inflater.inflate(R.layout.question_setup, (ViewGroup) view.findViewById(R.id.answers_container));

        tvEventSetupSpotsNumber = (BaseTextView) view.findViewById(R.id.tvEventSetupSpotsNumber);
        ((TextView) view.findViewById(R.id.tvSpotsTitle)).setText(ds.getApplicationData().event.setup_questions.get(0).text_title);
        tvEventSetupPrivacy = (BaseTextView) view.findViewById(R.id.tvEventSetupPrivacy);

        switchSetupIsParticipating = (Switch) view.findViewById(R.id.SwitchSetupIsParticipating);
        switchSetupEventJoinType = (Switch) view.findViewById(R.id.SwitchSetupEventJoinType);

        view.findViewById(R.id.edrJoinType).setOnClickListener(this);
        ((TextView) view.findViewById(R.id.edrJoinType)).setText(ds.getApplicationData().event.setup_questions.get(1).text_title);
        view.findViewById(R.id.edrIsParticipating).setOnClickListener(this);
        ((TextView) view.findViewById(R.id.tvPrivacy)).setText(ds.getApplicationData().event.setup_questions.get(2).text_title);

        switchSetupEventJoinType.setOnCheckedChangeListener(this);
        switchSetupIsParticipating.setOnCheckedChangeListener(this);
        switchSetupEventJoinType.setChecked(false);

        view.findViewById(R.id.edrSetupNumberOfSpots).setOnClickListener(this);
        view.findViewById(R.id.edrSetupPrivacy).setOnClickListener(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mActivity.create_mode == QuestionnaireActivity.CREATE_MODE.EDIT_EVENT) {
            view.findViewById(R.id.llAreYouParticipatingHolder).setVisibility(View.GONE);
        } else {
            // Default setup values
            switchSetupEventJoinType.setChecked(false);
        }
        recoverAnswer();
    }

    @Override
    protected void setHeader() {
        super.setHeader();

        if (mActivity.isInEditMode())
            mHeader.setSaveCancelMode(dm.getResourceText(R.string.Edit_Event_Participants));
    }

    @Override
    public void onStart() {
        super.onStart();
        setHeader();
    }


    @Override
    public void recoverDefaultAnswer() {
        //can't have default answer here
    }


    private void recoverAnswer() {
        try {
            //spots
            if (mActivity.dataSetupQuestionsObject.numberOfSpots == -2) {
                tvEventSetupSpotsNumber.setText(dm.getResourceText(R.string.click_to_set));
                tvEventSetupSpotsNumber.setTextColor(ds.getIntColor(EMColor.MOON));
                setTitleEnabled(false);
            } else {
                int numberOfSpots = mActivity.dataSetupQuestionsObject.numberOfSpots;
                tvEventSetupSpotsNumber.setText(numberOfSpots == -1 ? spots[0] : numberOfSpots + "");
                tvEventSetupSpotsNumber.setTextColor(ds.getIntColor(EMColor.PRIMARY));
                setTitleEnabled(true);
            }


            // privacy
            tvEventSetupPrivacy.setText(Utils.setFirstLetterUpperCase(mActivity.dataSetupQuestionsObject.privacy));

            // participating
            switchSetupIsParticipating.setChecked(mActivity.dataSetupQuestionsObject.isParticipating);

            // Join Type
            switchSetupEventJoinType.setChecked(mActivity.dataSetupQuestionsObject.joinType.equalsIgnoreCase(JoinType.FREE) ? false : true);

        } catch (Exception ex) {
        }
        if (mActivity.isInEditMode()) {

            //Recover Spots
            //tvEventSetupSpotsNumber.setText("" + mActivity.dataSetupQuestionsObject.numberOfSpots);

            //int numberOfSpots = mActivity.dataSetupQuestionsObject.numberOfSpots;
            //tvEventSetupSpotsNumber.setText(numberOfSpots == -1 ? spots[0] : numberOfSpots + "");
            //tvEventSetupSpotsNumber.setTextColor(ds.getIntColor(EMColor.PRIMARY));

            // Recover Privacy
            //tvEventSetupPrivacy.setText(mActivity.dataSetupQuestionsObject.privacy);

            //no are you participating in edit mode

            // Join Type
            //switchSetupEventJoinType.setChecked(mActivity.dataSetupQuestionsObject.joinType.equalsIgnoreCase(JoinType.FREE) ? false : true);
        }

        setAnswer();
    }

    @Override
    public void onCheckedChanged(Switch view, boolean checked) {
        if (view == switchSetupEventJoinType)
            mActivity.dataSetupQuestionsObject.joinType = checked ? JoinType.APPROVAL : JoinType.FREE;
        else if (view == switchSetupIsParticipating)
            mActivity.dataSetupQuestionsObject.isParticipating = checked;

        setAnswer();
    }

    DialogInterface.OnClickListener spotsClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            tvEventSetupSpotsNumber.setText(spots[which]);
            mActivity.dataSetupQuestionsObject.numberOfSpots = which == 0 ? -1 : Integer.parseInt(spots[which]);
            tvEventSetupSpotsNumber.setTextColor(ds.getIntColor(EMColor.PRIMARY));
            setAnswer();
        }
    };
    DialogInterface.OnClickListener privacyClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            tvEventSetupPrivacy.setText(privacy[which]);
            mActivity.dataSetupQuestionsObject.privacy = privacy[which];
            setAnswer();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.edrSetupNumberOfSpots:
                showPicker(spots, spotsClickListener);
                break;
            case R.id.edrSetupPrivacy:
                showPicker(privacy, privacyClickListener);
                break;
            case R.id.edrJoinType:
                switchSetupEventJoinType.toggle();
                break;
            case R.id.edrIsParticipating:
                switchSetupIsParticipating.toggle();
                break;
        }
    }

    void showPicker(String[] items, DialogInterface.OnClickListener clickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Select");
        builder.setItems(items, clickListener);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void setAnswer() {
        if (mActivity.dataSetupQuestionsObject.numberOfSpots != -2)
            setAnswer("setup");
    }

    @Override
    protected JSONObject createJsonObject() {
        return super.createJsonObject();
    }

}
