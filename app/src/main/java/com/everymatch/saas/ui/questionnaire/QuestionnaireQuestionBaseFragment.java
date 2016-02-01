package com.everymatch.saas.ui.questionnaire;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.everymatch.saas.Constants;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.client.data.QuestionType;
import com.everymatch.saas.server.Data.DataAnswer;
import com.everymatch.saas.server.Data.DataQuestion;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.ui.base.BaseFragment;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.view.EventHeader;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public abstract class QuestionnaireQuestionBaseFragment extends BaseFragment implements EventHeader.OnEventHeader, View.OnClickListener {

    public static final String ARG_FROM_SUMMERY = "arg.back_on_next";
    public static final String ARG_FROM_ANYTHING_ELSE = "arg.from.anything.else";
    public static final String EXTRA_QUESTION_AND_ANSWER = "extra.question.and.answer";
    public static final String ARG_IS_SUB_QUESTION = "arg.is.sub.question";
    public static final String ARG_SUB_QUESTION_OBJECT = "arg.sub.question.object";
    public static final String ARG_ROLE_ANSWER_ID = "arg.role.answer.id";
    private static final String QUESTION_NUMBER_FORMAT = "%s/%s";


    /*Data*/
    QuestionnaireActivity mActivity;
    protected QuestionAndAnswer mQuestionAndAnswer;
    protected String originalDataJson;

    protected boolean mHaveAnswer = false;
    boolean isSubQuestion = false;
    int roleAnswerId;
    protected DataQuestion mQuestion;
    boolean fromSummery;
    boolean fromAnythingElse;

    /*Views*/
    protected EventHeader mHeader;
    protected TextView tvTitle;

    public QuestionnaireQuestionBaseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (QuestionnaireActivity) getActivity();
        mActivity.getSupportActionBar().hide();
        if (getArguments() != null && getArguments().containsKey(ARG_FROM_SUMMERY))
            fromSummery = getArguments().getBoolean(ARG_FROM_SUMMERY);
        if (getArguments() != null && getArguments().containsKey(ARG_FROM_ANYTHING_ELSE)) {
            fromAnythingElse = getArguments().getBoolean(ARG_FROM_ANYTHING_ELSE);
            mQuestionAndAnswer = (QuestionAndAnswer) getArguments().getSerializable(EXTRA_QUESTION_AND_ANSWER);
            mQuestion = mQuestionAndAnswer.question;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        /* init QuestionAndAnswer object it can be sub-QuestionAndAnswer;*/
        getSubQuestionAndAnswerIfNeeded();

        mHeader = (EventHeader) view.findViewById(R.id.eventHeader);
        setHeader();

        tvTitle = (TextView) view.findViewById(R.id.title_textview);
        tvTitle.setText(mQuestionAndAnswer.question.text_title);

        if (mActivity.isAnsweredAll(true, true)) {

            if (mActivity.create_mode != QuestionnaireActivity.CREATE_MODE.EDIT_EVENT) {
                mHeader.getIconOne().setText(dm.getResourceText(R.string.Done));
            } else {
                mHeader.getIconOne().setText(dm.getResourceText(R.string.Save));
            }
        } else {
            setTitleEnabled(false);
        }

        // first question (not sub question) - nothing in summery
        if (mActivity.mQuestionIndex <= 0 && !isSubQuestion) {
            mHeader.getBackButton().setVisibility(View.INVISIBLE);
        }

        try {
            if (!isSubQuestion)
                showQuestionNumber();
        } catch (Exception e) {
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        /** if user not yet answered and there is a default value -> recover that answer*/
        if (TextUtils.isEmpty(mQuestionAndAnswer.userAnswerStr) && mQuestionAndAnswer.question.default_value != null) {
            setAnswer(mQuestionAndAnswer.question.default_value);
            recoverDefaultAnswer();
        }
    }

    private void setHeader() {
        mHeader.setListener(this);
        mHeader.getBackButton().setText(Consts.Icons.icon_Details);
        mHeader.getIconOne().setText(dm.getResourceText(R.string.Next));
        mHeader.getIconOne().setTextSize(TypedValue.COMPLEX_UNIT_SP, Constants.ACTION_TEXT_SIZE_SP);
        mHeader.getIconOne().setTextColor(ds.getIntColor(EMColor.FOG));
        mHeader.getIconOne().setClickable(false);
        mHeader.getIconTwo().setVisibility(View.GONE);
        mHeader.getIconThree().setVisibility(View.GONE);
        mHeader.setTitle("");

        if (isSubQuestion) {
            mHeader.getBackButton().setVisibility(View.VISIBLE);
            mHeader.getBackButton().setText(Consts.Icons.icon_New_Close);
            mHeader.getIconOne().setText(dm.getResourceText(R.string.Done));
        }

        if (fromSummery || fromAnythingElse) {
            mHeader.getIconOne().setText(dm.getResourceText(R.string.Save));
            mHeader.getBackButton().setText(Consts.Icons.icon_New_Close);
            mHeader.getBackButton().setVisibility(View.VISIBLE);
            setTitleEnabled(false);
        }
    }

    protected void showQuestionNumber() {
        if (isSubQuestion || fromSummery || fromAnythingElse)
            return;

        int mandatoryCount = 0;
        for (QuestionAndAnswer qaa : mActivity.mQuestionsAndAnswers) {
            if (qaa.question.mandatory)
                mandatoryCount++;
        }

        if (mActivity.create_mode == QuestionnaireActivity.CREATE_MODE.CREATE_ACTIVITY
                || mActivity.create_mode == QuestionnaireActivity.CREATE_MODE.CREATE_EVENT) {
            mHeader.getCenterText().setText(String.format(QUESTION_NUMBER_FORMAT, mActivity.mCurrentQuestionIndex + 1,
                    mandatoryCount + 1));
        }
    }

    /**
     * this method called when there's a default answer-> the answer format is just like we send it to the server
     * notice! sometimes Json and sometimes not!
     * so...just take the data in mQuestionAndAnswer.userAnswerData
     */
    public abstract void recoverDefaultAnswer();

    public void setAnswer(String answer) {
        // in case answer is empty stop here
        if (TextUtils.isEmpty(answer)) {
            mQuestionAndAnswer.userAnswerData = null;
            return;
        }

        mHaveAnswer = true;
        setTitleEnabled(true);

        // the way the user see the answer
        mQuestionAndAnswer.userAnswerStr = answer;

        /* answer server form */
        JSONObject jsonObject = new JSONObject();
        mQuestionAndAnswer.userAnswerData = jsonObject;
        try {
            jsonObject.put("questions_id", mQuestion.questions_id);
            jsonObject.put("status", "active");

            switch (mQuestion.question_type) {
                case QuestionType.NUMBER_RANGE:
                    jsonObject.put("value", answer.replace("-", ","));
                    break;
                case QuestionType.NUMBER:
                case QuestionType.GENDER:
                case QuestionType.GENDER_RANGE:
                case QuestionType.SCALE:
                case QuestionType.ABOUT_ME:
                case QuestionType.IMAGE_UPLOAD:
                    jsonObject.put("value", answer);
                    break;
                case QuestionType.AGE:
                case QuestionType.DATE:
                case QuestionType.EVENT_DATE:
                    jsonObject.put("value", createDateJsonObject(answer));
                    break;
                case QuestionType.LOCATION:
                case QuestionType.MY_LOCATION:
                case QuestionType.EVENT_LOCATION:
                case QuestionType.EVENT_LIST:
                    jsonObject.put("value", createLocationJsonObject(answer));
                    break;
                case QuestionType.IDS:
                    jsonObject.put("value", createIdsList());
                    break;
                case QuestionType.EVENT_SCHEDULE:
                    jsonObject.put("value", createJsonObject());
                    break;
                case QuestionType.SETUP:
                    jsonObject.put("value", createJsonObject());
                    break;

                case QuestionType.PACE:
                case QuestionType.TIME:
                    jsonObject.put("value", getAnswerValue()/*ust number in this case*/);
                    break;


                default:
                    EMLog.e(getClass().getName(), "Question Type: " + mQuestion.questions_id + " was not found");
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * this method returns the value in case it's not a json object
     */
    protected String getAnswerValue() {
        return "";
    }

    /**
     * this function creates id list according to the user answers
     */
    protected String createIdsList() {
        return "";
    }

    protected JSONObject createDateJsonObject(String dateStr) {
        JSONObject jsonObject = new JSONObject();
        return jsonObject;
    }

    protected JSONObject createJsonObject() {
        JSONObject jsonObject = new JSONObject();
        return jsonObject;
    }

    protected JSONObject createLocationJsonObject(String locationStr) {
        JSONObject jsonObject = new JSONObject();
        return jsonObject;
    }

    public void clearAnswer() {
        mHaveAnswer = false;
        setTitleEnabled(false);
        mQuestionAndAnswer.userAnswerStr = null;
    }

    @Override
    public void onBackButtonClicked() {
        if (fromSummery) {
            restorePreviewsData();
            getFragmentManager().popBackStack();
            return;
        }

        if (fromAnythingElse) {
            getFragmentManager().popBackStack();
            return;
        }

        // check if already came from summery screen
        if (getArguments() != null && getArguments().getBoolean(ARG_FROM_SUMMERY, false)) {
            getFragmentManager().popBackStack();
        } else {
            mActivity.goToSummeryScreen(null);
        }
    }

    @Override
    public void onOneIconClicked() { // onNext clicked
        //if (!mHaveAnswer) return;
        mQuestionAndAnswer.isAnsweredConfirmedByClickingNext = true;
        if (isSubQuestion) {
            mActivity.getSupportFragmentManager().popBackStackImmediate();
            return;
        }

        // check if answer have dependent question
        if (mQuestionAndAnswer.question.have_dependent_questions && mQuestionAndAnswer.question.answers != null && mQuestionAndAnswer.question.answers.length > 0) {
            // find the answer in answers array
            DataAnswer pickedAnswer = null;
            for (DataAnswer dataAnswer : mQuestionAndAnswer.question.answers) {
                if (dataAnswer.text_title != null && dataAnswer.text_title.equalsIgnoreCase(mQuestionAndAnswer.userAnswerStr)) {
                    pickedAnswer = dataAnswer;
                    break;
                }
            }

            // add dependent question to mandatory questions array
            if (pickedAnswer != null && pickedAnswer.questions != null) {
                for (int i = 0; i < pickedAnswer.questions.length; i++) {
                    QuestionAndAnswer dependentQuestion = new QuestionAndAnswer(pickedAnswer.questions[i]);
                    dependentQuestion.isDependentsQuestion = true;

                    // add the question to position mQuestionIndex to be the next to be displayed
                    mActivity.mQuestionsAndAnswers.add(mActivity.mQuestionIndex + 1, dependentQuestion);
                }
            }
        }

        if (fromSummery) {
            updateSummeryData();
            getFragmentManager().popBackStack();
        } else if (fromAnythingElse) {
            //get the not mandatory question position
            mActivity.mNotMandatoryUnansweredQuestions.remove(mQuestionAndAnswer.question);
            mActivity.mQuestionsAndAnswers.add(mQuestionAndAnswer);

            //update the not mandatory fragment
            //Fragment fragment = .get(mActivity.getSupportFragmentManager().getFragments().size() - 1);
            for (Fragment fragment : mActivity.getSupportFragmentManager().getFragments()) {
                if (fragment != null && fragment instanceof QuestionnaireNotMandatoryFragment) {
                    setTargetFragment(fragment, QuestionnaireNotMandatoryFragment.REQUEST_CODE_NOT_MANDATORY);
                    ((QuestionnaireNotMandatoryFragment) getTargetFragment()).onUpdate();
                    break;
                }
            }

            getFragmentManager().popBackStackImmediate();
        } else {
            // go to next
            mActivity.goToNextQuestion(null);
        }
    }

    private void updateSummeryData() {
        Fragment fragment = mActivity.getSupportFragmentManager().findFragmentByTag(QuestionnaireSummeryFragment.TAG);
        setTargetFragment(fragment, QuestionnaireSummeryFragment.REQUEST_CODE_SUMMERY);
        ((QuestionnaireSummeryFragment) getTargetFragment()).onUpdate();
    }

    @Override
    public void onTwoIconClicked() {
    }

    @Override
    public void onThreeIconClicked() {
    }

    @Override
    public void onClick(View v) {

    }

    protected void restorePreviewsData() {
        QuestionAndAnswer qaa = new Gson().fromJson(originalDataJson, QuestionAndAnswer.class);
        if (!isSubQuestion) {
            mActivity.mQuestionsAndAnswers.set(mActivity.mCurrentQuestionIndex, qaa);
        }
        // mQuestionAndAnswer = qaa;
        // mQuestionAndAnswer.subQuestionsMap = qaa.subQuestionsMap;
    }

    protected void setTitleEnabled(boolean enabled) {
        //mHeader.getIconOne().setAlpha(enabled ? 1f : 0.5f);
        mHeader.getIconOne().setClickable(enabled);
        mHeader.getIconOne().setTextColor(ds.getIntColor(EMColor.WHITE));
        ObjectAnimator.ofFloat(mHeader.getIconOne(), View.ALPHA.getName(), enabled ? 1.0f : 0.5f).start();
    }

    public void getSubQuestionAndAnswerIfNeeded() {

        // because if it's from anything else we already has the mQuestionAndAnswer
        if (!fromAnythingElse)
            mQuestionAndAnswer = mActivity.mCurrentQuestionIndex < 0 ? mActivity.mQuestionsAndAnswers.get(0) : mActivity.mQuestionsAndAnswers.get(mActivity.mCurrentQuestionIndex);

        /* arguments */
        if (getArguments() != null && getArguments().containsKey(ARG_IS_SUB_QUESTION))
            isSubQuestion = getArguments().getBoolean(ARG_IS_SUB_QUESTION, false);

        if (isSubQuestion) {
            roleAnswerId = getArguments().getInt(ARG_ROLE_ANSWER_ID);
            /* our  mQuestionAndAnswer is dipper one*/
            mQuestion = (DataQuestion) getArguments().getSerializable(ARG_SUB_QUESTION_OBJECT);
            ArrayList<QuestionAndAnswer> subQuestions = mQuestionAndAnswer.subQuestionsMap.get(roleAnswerId);
            for (QuestionAndAnswer QAA : subQuestions) {
                if (QAA.question.questions_id == mQuestion.questions_id) {
                    mQuestionAndAnswer = QAA;
                    break;
                }
            }
        } else {
            mQuestion = mQuestionAndAnswer.question;
        }


        if (originalDataJson == null)
            originalDataJson = new Gson().toJson(mQuestionAndAnswer);
    }
}
