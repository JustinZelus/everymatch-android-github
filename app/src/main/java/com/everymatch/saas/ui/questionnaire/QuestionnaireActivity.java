package com.everymatch.saas.ui.questionnaire;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EventType;
import com.everymatch.saas.client.data.FormType;
import com.everymatch.saas.client.data.QuestionType;
import com.everymatch.saas.server.Data.DataActivity;
import com.everymatch.saas.server.Data.DataAnswer;
import com.everymatch.saas.server.Data.DataEvent;
import com.everymatch.saas.server.Data.DataEvent_Activity;
import com.everymatch.saas.server.Data.DataPeople;
import com.everymatch.saas.server.Data.DataPeopleHolder;
import com.everymatch.saas.server.Data.DataProfile;
import com.everymatch.saas.server.Data.DataQuestion;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.server.requests.RequestActivityProfile;
import com.everymatch.saas.server.requests.RequestCreateEvent;
import com.everymatch.saas.server.requests.RequestUpdateUserProfile;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.server.responses.ResponseApplication;
import com.everymatch.saas.server.responses.ResponseGetUser;
import com.everymatch.saas.server.responses.ResponseString;
import com.everymatch.saas.server.responses.ResponseUpdateProfile;
import com.everymatch.saas.singeltones.PeopleListener;
import com.everymatch.saas.singeltones.YesNoCallback;
import com.everymatch.saas.ui.BaseActivity;
import com.everymatch.saas.ui.base.BaseFragment;
import com.everymatch.saas.ui.dialog.DialogYesNo;
import com.everymatch.saas.ui.dialog.FragmentTimeZones;
import com.everymatch.saas.ui.discover.DiscoverActivity;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.QuestionUtils;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.EventHeader;
import com.everymatch.ucpa.SplashActivity;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class QuestionnaireActivity extends BaseActivity implements PeopleListener {
    private static final String TAG = QuestionnaireActivity.class.getSimpleName();

    public enum CREATE_MODE {CREATE_ACTIVITY, CREATE_EVENT, ANSWER_SINGLE_QUESTION, EDIT_ACTIVITY, EDIT_EVENT}

    public enum EDIT_EVENT_TYPE {SETTINGS, PROFILE, SIGNLE_QUESTION, PARTICIPANTS}

    public RequestCreateEvent.REQUEST_TYPE request_type = RequestCreateEvent.REQUEST_TYPE.POST_EVENT;
    public static CREATE_MODE create_mode = null;
    public static EDIT_EVENT_TYPE edit_event_type = null;

    public static final String EXTRA_ACTIVITY_ID = "activity_id";
    public static final String EXTRA_SELECTED_EVENT_ID = "extra.selected.event.id";
    public static final String EXTRA_QUESTIONS = "extra_questions";
    public static final String EXTRA_ANSWERS = "extra_answers";
    public static final String EXTRA_ANSWER_STR = "extra_answer_str";
    public static final String EXTRA_EVENT = "extra_event";
    public static final String EXTRA_EVENT_EDIT_TYPE = "extra_edit_type";

    public int mCurrentQuestionIndex = -1;
    public int mQuestionIndex = -1;

    DataActivity mDataActivity;
    DataEvent_Activity mDataEvent_activity;
    public DataEvent mGeneratedEvent;
    public boolean mHasBackPressed;

    public DataSetupQuestionsObject dataSetupQuestionsObject = new DataSetupQuestionsObject();

    /* question that must have answer +
     * not mandatory questions that been answered +
     * dependent questions followed previous question */
    public static ArrayList<QuestionAndAnswer> mQuestionsAndAnswers = new ArrayList<>();

    ArrayList<QuestionAndAnswer> mHiddenQuestionsAndAnswers = new ArrayList<>();

    /* unanswered not mandatory questions, once answered - moved to mQuestionsAndAnswers */
    public ArrayList<DataQuestion> mNotMandatoryUnansweredQuestions = new ArrayList<>();

    // A temporary array for storing question and answers that should not be included in summary adapter
    private ArrayList<QuestionAndAnswer> mTemporaryQuestionsAndAnswers = new ArrayList<>();

    //VIEWS
    public EventHeader mHeader;
    RelativeLayout mFragmentsContainer;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);
        getSupportActionBar().hide();
        mQuestionsAndAnswers = new ArrayList<>();
        request_type = RequestCreateEvent.REQUEST_TYPE.POST_EVENT;
        mGeneratedEvent = new DataEvent();
        mFragmentsContainer = (RelativeLayout) findViewById(R.id.fragment_container_full);
        progressDialog = new ProgressDialog(QuestionnaireActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Loading...");

        initHeader();

        switch (create_mode) {
            case CREATE_ACTIVITY:
                int activityId = getIntent().getIntExtra(EXTRA_ACTIVITY_ID, 0);
                if (activityId != 0) {
                    mDataActivity = ds.getApplicationData().getActivityById(activityId);
                    //prepare mQuestionsAndAnswers and mNotMandatoryUnansweredQuestions objects
                    prepareArrays(mDataActivity.questions);
                    goToWelcome();
                    return;
                }

                // we have no activity id so...
                goToPickActivity();

                break;
            case CREATE_EVENT:
                int activityIdd = getIntent().getIntExtra(EXTRA_ACTIVITY_ID, 0);
                mDataActivity = ds.getApplicationData().getActivityById(activityIdd);
                //TODO - load activity by event id
                String eventId = getIntent().getStringExtra(EXTRA_SELECTED_EVENT_ID);
                for (DataEvent_Activity dataEventActivity : mDataActivity.getEvents()) {
                    if (dataEventActivity.event_id.equals(eventId)) {
                        mDataEvent_activity = dataEventActivity;
                        break;
                    }
                }
                prepareArrays(mDataEvent_activity.questions);
                goToNextQuestion(null);
                break;

            case ANSWER_SINGLE_QUESTION:
                createSingleQuestionProcess();
                break;

            case EDIT_ACTIVITY:
                createEditActivityProcess();
                break;

            case EDIT_EVENT:
                createEditEventProcess();
                break;
        }

    }

    public static void answerSingleQuestion(Activity activity, DataQuestion dataQuestion, DataAnswer answer, String answerStr, int requestCode) {
        QuestionnaireActivity.create_mode = CREATE_MODE.ANSWER_SINGLE_QUESTION;
        Intent intent = new Intent(activity, QuestionnaireActivity.class);
        intent.putExtra(QuestionnaireActivity.EXTRA_QUESTIONS, dataQuestion);
        intent.putExtra(QuestionnaireActivity.EXTRA_ANSWERS, answer);
        intent.putExtra(QuestionnaireActivity.EXTRA_ANSWER_STR, answerStr);
        activity.startActivityForResult(intent, requestCode);
    }

    /*call this method to edit activity this one will return a results to the caller*/
    public static void editActivity(android.support.v4.app.Fragment fragment, int activityId, int requestCode) {
        QuestionnaireActivity.create_mode = CREATE_MODE.EDIT_ACTIVITY;
        Intent intent = new Intent(fragment.getActivity(), QuestionnaireActivity.class);
        intent.putExtra(QuestionnaireActivity.EXTRA_ACTIVITY_ID, activityId);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void editEvent(android.support.v4.app.Fragment fragment, DataEvent dataEvent, DataQuestion question, EDIT_EVENT_TYPE type, int requestCode) {
        QuestionnaireActivity.create_mode = CREATE_MODE.EDIT_EVENT;
        Intent intent = new Intent(fragment.getActivity(), QuestionnaireActivity.class);
        intent.putExtra(QuestionnaireActivity.EXTRA_EVENT, dataEvent);
        intent.putExtra(QuestionnaireActivity.EXTRA_QUESTIONS, question);
        intent.putExtra(QuestionnaireActivity.EXTRA_EVENT_EDIT_TYPE, type);
        fragment.startActivityForResult(intent, requestCode);
    }

    public static void createEvent(Activity activity, int activityId, String eventId) {
        QuestionnaireActivity.create_mode = CREATE_MODE.CREATE_EVENT;
        Intent intent = new Intent(activity, QuestionnaireActivity.class);
        intent.putExtra(QuestionnaireActivity.EXTRA_ACTIVITY_ID, activityId);
        intent.putExtra(QuestionnaireActivity.EXTRA_SELECTED_EVENT_ID, eventId);
        activity.startActivity(intent);
    }

    /**
     * Use this method in order to create a process for answering a single question
     */
    private void createSingleQuestionProcess() {
        try {
            // Get the data
            DataQuestion dataQuestion = (DataQuestion) getIntent().getSerializableExtra(EXTRA_QUESTIONS);
            DataAnswer answer = (DataAnswer) getIntent().getSerializableExtra(EXTRA_ANSWERS);
            String answerStr = getIntent().getStringExtra(EXTRA_ANSWER_STR);

            QuestionAndAnswer questionAndAnswer = new QuestionAndAnswer(dataQuestion);
            JSONObject jsonObject = new JSONObject();
            questionAndAnswer.userAnswerData = jsonObject;
            jsonObject.put("questions_id", dataQuestion.questions_id);
            jsonObject.put("status", "active");
            QuestionUtils.updateValueItem(dataQuestion.question_type, answer, questionAndAnswer.userAnswerData);
            //jsonObject.put("value", QuestionUtils.getAnswerValue(dataQuestion.question_type, answer));

            //questionAndAnswer.userAnswerData = QuestionUtils.getAnswerValue(dataQuestion.question_type, answer);
            questionAndAnswer.userAnswerStr = answerStr;

            mCurrentQuestionIndex = 0;
            mQuestionsAndAnswers.add(questionAndAnswer);

            // Create the appropriate fragment
            Fragment questionFragment = getNextQuestionFragment(dataQuestion.form_type, -1);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_full, questionFragment).commitAllowingStateLoss();


            // Hide summary in this case
            mHeader.getBackButton().setVisibility(View.GONE);
        } catch (Exception ex) {
            EMLog.e(TAG, ex.getMessage());
        }
    }

    /**
     * Use this method in order to create a process for editing an activity
     */
    private void createEditActivityProcess() {
        // Get the data
        int activityId = getIntent().getIntExtra(EXTRA_ACTIVITY_ID, -1);

        ResponseApplication responseApplication = ds.getApplicationData();
        ResponseGetUser responseGetUser = ds.getUser();

        DataQuestion[] activityQuestions = null;
        DataAnswer[] activityAnswers = null;

        // Find the questions and answers
        mDataActivity = ds.getApplicationData().getActivityClientIdById(activityId);

        activityQuestions = mDataActivity.questions;
        /*get the  answers for the activity questions (not necessarily answered))*/
        for (DataProfile profile : responseGetUser.profiles.getActivity_profiles()) {
            if (profile.client_id.equals(mDataActivity.client_id)) {
                activityAnswers = profile.answers;
                break;
            }
        }

        /* prepare arrays but now...with answers */
        for (DataQuestion question : activityQuestions) {
            try {
                QuestionAndAnswer questionAndAnswer = new QuestionAndAnswer(question);

                for (DataAnswer answer : activityAnswers) {
                    if (answer.questions_id == question.questions_id) {
                        questionAndAnswer.userAnswerStr = QuestionUtils.getAnsweredTitle(question, answer);
                        /* answer server form */
                        JSONObject jsonObject = new JSONObject();
                        questionAndAnswer.userAnswerData = jsonObject;
                        jsonObject.put("questions_id", question.questions_id);
                        jsonObject.put("status", "active");
                        QuestionUtils.updateValueItem(question.question_type, answer, questionAndAnswer.userAnswerData);
                        break;
                    }
                }

                questionAndAnswer.isAnsweredConfirmedByClickingNext = true;
                mQuestionsAndAnswers.add(questionAndAnswer);
            } catch (Exception ex) {
                EMLog.e(TAG, ex.getMessage());
                continue;
            }
        }

        mCurrentQuestionIndex = 0;

        // Create the appropriate fragment
        goToSummeryScreen(null);

        // Hide summary in this case
        mHeader.getBackButton().setVisibility(View.GONE);
    }

    /**
     * Use this method in order to create a process for editing an event
     */
    private void createEditEventProcess() {

        mCurrentQuestionIndex = 0;

        edit_event_type = (EDIT_EVENT_TYPE) getIntent().getSerializableExtra(EXTRA_EVENT_EDIT_TYPE);

        DataQuestion requiredSingleQuestion = (DataQuestion) getIntent().getSerializableExtra(EXTRA_QUESTIONS);

        mGeneratedEvent = (DataEvent) getIntent().getSerializableExtra(EXTRA_EVENT);

        ResponseApplication responseApplication = DataStore.getInstance().getApplicationData();

        for (DataActivity activity : responseApplication.getActivities()) {

            if (activity.client_id.equals(mGeneratedEvent.activity_client_id)) {

                for (DataEvent_Activity event : activity.getEvents()) {

                    if (event.event_id.equals(mGeneratedEvent.dataPublicEvent.event_id)) {

                        for (int i = 0; i < event.questions.length; i++) {

                            DataQuestion question = event.questions[i];

                            // In single question mode - we are only looking for a SINGLE question
                            if (requiredSingleQuestion != null && question.questions_id ==
                                    requiredSingleQuestion.questions_id) {
                                requiredSingleQuestion = question;
                                mCurrentQuestionIndex = i; // save position so the base questionare fragment will edit this answer position
                            }

                            QuestionAndAnswer questionAndAnswer = new QuestionAndAnswer(question);

                            for (DataAnswer answer : mGeneratedEvent.profile.answers) {
                                try {
                                    if (answer.questions_id == question.questions_id) {
                                        questionAndAnswer.userAnswerStr = QuestionUtils.getAnsweredTitle(question, answer);
                                        JSONObject jsonObject = new JSONObject();
                                        questionAndAnswer.userAnswerData = jsonObject;
                                         jsonObject.put("questions_id", question.questions_id);
                                        jsonObject.put("status", "active");
                                        // jsonObject.put("value", QuestionUtils.updateValueItem(question.question_type, answer););
                                        QuestionUtils.updateValueItem(question.question_type, answer, questionAndAnswer.userAnswerData);
                                        //questionAndAnswer.userAnswerData = QuestionUtils.getAnswerValue(question.question_type, answer);
                                        break;
                                    }
                                } catch (Exception ex) {
                                    EMLog.e(TAG, ex.getMessage());
                                }

                            }

                            questionAndAnswer.isAnsweredConfirmedByClickingNext = true;

                            /* Location and schedule should not be visible in the summary, hence save them for later*/
                            if (edit_event_type == EDIT_EVENT_TYPE.PROFILE && (FormType.SCHEDULE.equals(question.form_type) || FormType.LOCATION.equals(question.form_type))) {
                                mTemporaryQuestionsAndAnswers.add(questionAndAnswer);
                            } else {
                                mQuestionsAndAnswers.add(questionAndAnswer);
                            }
                        }

                        break;
                    }
                }
            }
        }

        // Recover setup question
        dataSetupQuestionsObject.isParticipating = mGeneratedEvent.dataPublicEvent.is_participating;
        dataSetupQuestionsObject.joinType = mGeneratedEvent.dataPublicEvent.join_type;
        dataSetupQuestionsObject.privacy = mGeneratedEvent.display_settings.type;

        if (mGeneratedEvent.dataPublicEvent.spots > 0) {
            dataSetupQuestionsObject.numberOfSpots = mGeneratedEvent.dataPublicEvent.spots;
        }

        request_type = RequestCreateEvent.REQUEST_TYPE.UPDATE_EVENT;
        mHeader.getBackButton().setVisibility(View.GONE);

        switch (edit_event_type) {
            case SETTINGS:
                goToPublishScreen(QuestionnarePublishFragment.MODE_EDIT_EVENT);
                break;

            case PROFILE:
                goToSummeryScreen(null);
                break;

            case SIGNLE_QUESTION:
                Fragment questionFragment = getNextQuestionFragment(requiredSingleQuestion.form_type, -1);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_full, questionFragment).commit();
                break;

            case PARTICIPANTS:
                QuestionAndAnswer setup = new QuestionAndAnswer(new DataQuestion());
                setup.question.form_type = "setup";
                setup.question.question_type = QuestionType.SETUP;
                setup.question.mandatory = true;
                setup.question.text_title = dm.getResourceText(R.string.Participants);
                mQuestionsAndAnswers.add(0, setup);

                BaseFragment setupFragment = getNextQuestionFragment(FormType.SETUP, -1);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container_full, setupFragment).commit();
                break;
        }

    }

    private void initHeader() {
        mHeader = (EventHeader) findViewById(R.id.eventHeader);
    }

    public void goToPickActivity() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_full, new QuestionnairePickActivityFragment(), QuestionnairePickActivityFragment.TAG)
                .commit();
    }

    /*
     *this method prepares our 2 arrays (mandatory and not mandatory)
     */
    public void prepareArrays(DataQuestion questions[]) {
        //clear arrays
        mQuestionsAndAnswers = new ArrayList<>();
        mNotMandatoryUnansweredQuestions = new ArrayList<>();

        /* we take the activity questions */
        for (DataQuestion dataQuestion : questions) {
            if (dataQuestion.mandatory)
                mQuestionsAndAnswers.add(new QuestionAndAnswer(dataQuestion));
            else mNotMandatoryUnansweredQuestions.add(dataQuestion);
        }
        if (create_mode == CREATE_MODE.CREATE_EVENT) {
            QuestionAndAnswer setup = new QuestionAndAnswer(new DataQuestion());
            setup.question.form_type = FormType.SETUP;
            setup.question.question_type = QuestionType.SETUP;
            setup.question.mandatory = true;
            setup.question.text_title = dm.getResourceText(R.string.Participants);
            mQuestionsAndAnswers.add(setup);
        }
    }

    public void goToDiscoverScreen(String activityId) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_ACTIVITY_ID, activityId);
        setResult(Activity.RESULT_OK, intent);
        finish();

        String currentActivities = ds.getUser().user_settings.user_activity_profile_id_list;

        if (TextUtils.isEmpty(currentActivities)) {
            ds.getUser().user_settings.user_activity_profile_id_list = activityId;
        } else {
            ds.getUser().user_settings.user_activity_profile_id_list += "," + activityId;
        }

        DiscoverActivity.start(this, DiscoverActivity.EXTRA_ACTIVITY_ID, activityId);
    }

    public void goToWelcome() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.slide_up_animation, R.anim.fade_out_delay, R.anim.exit_to_left, R.anim.exit_to_left)
                        //.replace(R.id.fragment_container_full, new QuestionnaireWelcomeFragment())
                .addToBackStack(null)
                .add(R.id.fragment_container_full, new QuestionnaireWelcomeFragment())
                .commit();
    }

    public void closeKeyboard() {
        try {
            // close keyboard
            View view = getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(
                        Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * here we are going to question that is not in QAA list
     * so we add it to qaa list only when user click's DONE in the question itself!
     */
    public void goToNotMandatoryQuestion(int index) {

        QuestionAndAnswer qaa = new QuestionAndAnswer(mNotMandatoryUnansweredQuestions.get(index));
        BaseFragment fragment = getNextQuestionFragment(qaa.question.form_type, -1);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                R.anim.enter_from_left, R.anim.exit_to_right)
                .addToBackStack(QuestionnaireNotMandatoryFragment.TAG);

        Bundle args = new Bundle();
        args.putBoolean(QuestionnaireQuestionBaseFragment.ARG_FROM_ANYTHING_ELSE, true);
        args.putSerializable(QuestionnaireQuestionBaseFragment.EXTRA_QUESTION_AND_ANSWER, qaa);
        fragment.setArguments(args);
        fragmentTransaction.add(R.id.fragment_container_full, fragment)
                .commitAllowingStateLoss();


        /*QuestionAndAnswer questionAndAnswer = new QuestionAndAnswer(mNotMandatoryUnansweredQuestions.remove(index));
        mQuestionsAndAnswers.add(questionAndAnswer);
        goToNextQuestion(null);*/
    }

    public void goToQuestion(int index) {
        goToQuestion(index, false);
    }

    public void goToQuestion(int index, boolean fromSummeryScreen) {
        closeKeyboard();
        /* no need to increment index if we came from summery screen */
        // if (!fromSummeryScreen)
        mCurrentQuestionIndex = index;
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (create_mode == CREATE_MODE.EDIT_ACTIVITY || create_mode == CREATE_MODE.EDIT_EVENT) {
            fragmentTransaction.remove(getSupportFragmentManager().findFragmentByTag(QuestionnaireSummeryFragment.TAG));
        }

        if (fromSummeryScreen) {
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left,
                    R.anim.enter_from_left, R.anim.exit_to_right)
                    .addToBackStack("question" + index);

        } else {
            getSupportFragmentManager().popBackStackImmediate();
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        }

        Bundle args = new Bundle();
        args.putBoolean(QuestionnaireQuestionBaseFragment.ARG_FROM_SUMMERY, fromSummeryScreen);
        Fragment fragment = getNextQuestionFragment(mQuestionsAndAnswers.get(index).question.form_type, index);
        fragment.setArguments(args);
        if (fromSummeryScreen)
            fragmentTransaction.add(R.id.fragment_container_full, fragment).commitAllowingStateLoss();
        else
            fragmentTransaction.replace(R.id.fragment_container_full, fragment).commitAllowingStateLoss();
    }

    public void goToNextQuestion(View v) {
        // in case we answer question that been answered before, need to get back to it
        if (mCurrentQuestionIndex == mQuestionIndex)
            ++mQuestionIndex;

        if (isAnsweredAll(false, false)) {
            if (!mNotMandatoryUnansweredQuestions.isEmpty()) {
                goToNotMandatoryQuestionScreen();
            } else {
                Log.d(TAG, "goToNextQuestion got to the end");
                sendAnswersToServer();
            }
        } else {
            Log.d(TAG, "goToNextQuestion : " + mQuestionIndex);
            goToQuestion(mQuestionIndex);
        }
    }

    public BaseFragment getNextQuestionFragment(String questionForm, int questionIndex) {
        Log.i(TAG, "getNextQuestionFragment type " + questionForm);

        switch (questionForm) {
            case FormType.LOCATION:
                return new QuestionnaireQuestionLocationFragment();
            case FormType.BUTTON_SELECTOR:
                return new QuestionnaireQuestionButtonSelectorFragment();
            case FormType.DATE:
                return new QuestionnaireQuestionDateFragment();
            case FormType.NUMBER_SLIDER:
                return new QuestionnaireQuestionNumberSilderFragment();
            case FormType.NUMBER_PICKER:
            case FormType.NUMBER_INPUT:
                return new QuestionnaireQuestionNumberFragment();
            case FormType.NUMBER_RANGE:
                return new QuestionnaireQuestionRangeFragment();
            case FormType.DATE_TIME:
                return new QuestionnaireQuestionDateTimeFragment();
            case FormType.LIST:
                return new QuestionnaireQuestionList();
            case FormType.FROM_TO:
                return new QuestionnaireQuestionRangeFragment();
            //return new QuestionFromTo();
            case FormType.IMAGE_UPLOAD:
                return new QuestionnaireQuestionPickImageFragment();
            case FormType.TEXT_AREA:
            case FormType.TEXT_INPUT:
                return new QuestionnaireQuestionTextArea();
            case FormType.SCHEDULE:
                return new QuestionnareQuestionScheduleFragment();
            case FormType.SETUP:
                return new QuestionnareSetup();
            case FormType.TIME:
                return new QuestionTime();
            default:
                EMLog.e(TAG, "no fragment found that matches this form type! " + questionForm);
                return new QuestionnaireQuestionButtonSelectorFragment();
        }
    }

    public void goToSummeryScreen(View v) {
        /*if(QuestionnaireSummeryFragment.isShown)
            return;*/

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        if (create_mode == CREATE_MODE.EDIT_ACTIVITY || create_mode == CREATE_MODE.EDIT_EVENT) {
            fragmentTransaction.replace(R.id.fragment_container_full, new QuestionnaireSummeryFragment(), QuestionnaireSummeryFragment.TAG);
        } else {
            fragmentTransaction.setCustomAnimations(R.anim.slide_up_animation,
                    R.anim.sign_no_animation,
                    R.anim.sign_no_animation,
                    R.anim.slide_down_animation);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.add(R.id.fragment_container_full, new QuestionnaireSummeryFragment(), QuestionnaireSummeryFragment.TAG);
        }

        fragmentTransaction.commit();
    }

    public void goToNotMandatoryQuestionScreen() {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .replace(R.id.fragment_container_full, new QuestionnaireNotMandatoryFragment())
                .commit();
    }

    public boolean isAnsweredAll(boolean includeNotMandatory, boolean excludeLastQuetion) {

        if (create_mode == CREATE_MODE.EDIT_EVENT) {
            return true;
        }

        // check if there's not mandatory question that the user didn't answer yet
        if (includeNotMandatory && mNotMandatoryUnansweredQuestions.size() > 0)
            return false;

        // check unanswered questions
        int size = mQuestionsAndAnswers.size();
        if (excludeLastQuetion)
            --size;

        // we check if all answers confirmed by clicking next
        for (int i = 0; i < size; ++i)
            if (mQuestionsAndAnswers.get(i).isAnsweredConfirmedByClickingNext == false /*&& !mQuestionsAndAnswers.get(i).question.role*/)
                return false;

        return true;
    }

    public void sendAnswersToServer() {
        Log.d(TAG, "sendAnswersToServer");

        try {
            // create answers array
            JSONArray answers = new JSONArray();
            /*add all answer except setup question*/
            for (QuestionAndAnswer questionAndAnswer : mQuestionsAndAnswers) {
                if (questionAndAnswer.userAnswerData != null && questionAndAnswer.question.question_type != QuestionType.SETUP) {
                    if (questionAndAnswer.question.form_type.equals(FormType.FROM_TO) ||
                            questionAndAnswer.question.form_type.equals(FormType.NUMBER_RANGE)) {
                        String value = questionAndAnswer.userAnswerData.getString("value").toString();
                        value = value.replace(" - ", ",");
                        questionAndAnswer.userAnswerData.put("value", value);
                        answers.put(questionAndAnswer.userAnswerData);
                    } else
                        answers.put(questionAndAnswer.userAnswerData);
                }
            }

            for (QuestionAndAnswer questionAndAnswer : mTemporaryQuestionsAndAnswers) {
                if (questionAndAnswer.userAnswerData != null)
                    answers.put(questionAndAnswer.userAnswerData);
            }

            // add answers and activities id
            JSONObject profile = new JSONObject();

            if (mDataActivity != null) {
                profile.put("activities_id", mDataActivity.client_id);
            }
            if (create_mode == CREATE_MODE.CREATE_ACTIVITY)
                profile.put("status", "active");
            else if (create_mode == CREATE_MODE.EDIT_ACTIVITY)
                profile.put("status", mDataActivity.status);

            // profile.put("status", "draft");

            profile.put("answers", answers);

            JSONObject output = new JSONObject();
            output.put("profile", profile);

            JSONObject entity = null;
            if (create_mode == CREATE_MODE.CREATE_ACTIVITY || create_mode == CREATE_MODE.EDIT_ACTIVITY) {
                entity = new JSONObject();
            } else if (create_mode == CREATE_MODE.CREATE_EVENT || create_mode == CREATE_MODE.EDIT_EVENT) {

                if (create_mode == CREATE_MODE.EDIT_EVENT) {
                    mDataEvent_activity = new DataEvent_Activity();
                    mDataEvent_activity.client_id = mGeneratedEvent.client_id;
                    mDataActivity = new DataActivity();
                    mDataActivity.client_id = mGeneratedEvent.activity_client_id;
                }

                profile.put("events_id", mDataEvent_activity.event_id);

                entity = dataSetupQuestionsObject.getEntityObject();
                if (request_type == RequestCreateEvent.REQUEST_TYPE.UPDATE_EVENT) {
                    entity.put("event_description", mGeneratedEvent.dataPublicEvent.event_description);
                    entity.put("event_title", mGeneratedEvent.dataPublicEvent.event_title);
                    entity.put("_id", mGeneratedEvent._id);
                    //entity.put("image_url",mGeneratedEvent.dataPublicEvent.event_title);
                }
            }

            output.put("entity", entity);

            sendRequest(output);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendRequest(JSONObject output) {

        progressDialog.setTitle(null);
        progressDialog.show();

        switch (create_mode) {

            case CREATE_ACTIVITY:
            case EDIT_ACTIVITY:
                ServerConnector.getInstance().processRequest(new RequestActivityProfile(mDataActivity.client_id, output.toString()), new ServerConnector.OnResultListener() {
                    @Override
                    public void onSuccess(BaseResponse baseResponse) {
                        Log.d(TAG, "RequestActivityProfile onSuccess");
                        progressDialog.dismiss();

                        if (create_mode == CREATE_MODE.CREATE_ACTIVITY) {
                            String responseStr = ((ResponseString) baseResponse).responseStr;
                            try {
                                DataProfile dataProfile = new Gson().fromJson(responseStr, DataProfile.class);
                                if (dataProfile != null)
                                    ds.getUser().profiles.addOrUpdateProfile(dataProfile);
                            } catch (Exception ex) {
                                EMLog.e(TAG, "could not parse created profile: " + ex.getMessage());
                            }
                            goToDiscoverScreen(mDataActivity.client_id);
                        } else {

                            // Update our global user profile
                            ResponseString response = (ResponseString) baseResponse;
                            DataProfile profile = new Gson().fromJson(response.responseStr, DataProfile.class);

                            for (int i = 0; i < DataStore.getInstance().getUser().profiles.getActivity_profiles().size(); i++) {
                                if (DataStore.getInstance().getUser().profiles.getActivity_profiles().get(i).client_id.equals(mDataActivity.client_id)) {
                                    DataStore.getInstance().getUser().profiles.getActivity_profiles().set(i, profile);
                                    break;
                                }
                            }

                            setResult(Activity.RESULT_OK);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(ErrorResponse errorResponse) {
                        Log.w(TAG, "RequestActivityProfile onFailure: " + errorResponse.getStatusCode() +
                                " " + errorResponse.getServerRawResponse());
                        progressDialog.dismiss();

                        //Toast.makeText(QuestionnaireActivity.this, "equestActivityProfile onFailure: " + errorResponse.getStatusCode() + " " + errorResponse.getServerRawResponse(), Toast.LENGTH_LONG).show();
                    }
                });
                break;

            case EDIT_EVENT:
            case CREATE_EVENT:
                ServerConnector.getInstance().processRequest(new RequestCreateEvent(mDataActivity.client_id, mDataEvent_activity.client_id, output.toString(), request_type, mGeneratedEvent._id), new ServerConnector.OnResultListener() {
                    @Override
                    public void onSuccess(BaseResponse baseResponse) {
                        Log.d(TAG, "RequestActivityProfile onSuccess");
                        String response = ((ResponseString) baseResponse).responseStr;
                        switch (request_type) {
                            case POST_EVENT:
                                progressDialog.dismiss();
                                mGeneratedEvent = new Gson().fromJson(response, DataEvent.class);
                                goToPublishScreen(-1);
                                request_type = RequestCreateEvent.REQUEST_TYPE.UPDATE_EVENT;
                                break;
                            case UPDATE_EVENT:
                                mGeneratedEvent = new Gson().fromJson(response, DataEvent.class);

                                if (create_mode == CREATE_MODE.EDIT_EVENT) {
                                    progressDialog.dismiss();
                                    Intent intent = new Intent();
                                    intent.putExtra(EXTRA_EVENT, mGeneratedEvent);
                                    setResult(Activity.RESULT_OK, intent);
                                    finish();
                                } else {
                                    request_type = RequestCreateEvent.REQUEST_TYPE.PUBLISH_EVENT;
                                    publishEvent();
                                }

                                break;
                            case PUBLISH_EVENT:
                                mGeneratedEvent = new Gson().fromJson(response, DataEvent.class);
                                //Toast.makeText(QuestionnaireActivity.this, "Event was successfully uploaded!", Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }

                    @Override
                    public void onFailure(ErrorResponse errorResponse) {
                        Log.w(TAG, "RequestCreateEvent onFailure: " + errorResponse.getStatusCode() + " " + errorResponse.getServerRawResponse());
                        progressDialog.dismiss();
                        //Toast.makeText(QuestionnaireActivity.this, "RequestCreateEvent onFailure: " + errorResponse.getStatusCode() + " " + errorResponse.getServerRawResponse(), Toast.LENGTH_LONG).show();
                    }
                });
                break;

            case ANSWER_SINGLE_QUESTION:

                ServerConnector.getInstance().processRequest(new RequestUpdateUserProfile(output.toString()), new ServerConnector.OnResultListener() {
                    @Override
                    public void onSuccess(BaseResponse baseResponse) {
                        Log.d(TAG, "RequestUpdateUserProfile onSuccess");

                        if (!isFinishing()) {

                            ResponseUpdateProfile responseUpdateProfile = (ResponseUpdateProfile) baseResponse;

                            // Update our user
                            if (responseUpdateProfile != null && !Utils.isArrayEmpty(responseUpdateProfile.answers)) {
                                ResponseGetUser user = DataStore.getInstance().getUser();
                                user.profiles.user_profile.answers = responseUpdateProfile.answers;
                            }

                            progressDialog.dismiss();
                            setResult(Activity.RESULT_OK);
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(ErrorResponse errorResponse) {
                        EMLog.w(TAG, "RequestUpdateUserProfile onFailure: " + errorResponse.getStatusCode() + " " + errorResponse.getServerRawResponse());
                        if (!isFinishing()) {
                            progressDialog.dismiss();
                            //Toast.makeText(QuestionnaireActivity.this, "RequestCreateEvent onFailure: " + errorResponse.getStatusCode() + " " + errorResponse.getServerRawResponse(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
                break;
        }
    }

    private void publishEvent() {
        //progressDialog.dismiss();
        /*now we listen to the push we got after the publish and
        * only then we go to next screen */

        /*LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                progressDialog.dismiss();

                *//* now we'v got the published event *//*
                String eventName = intent.getStringExtra(PusherManager.EXTRA_PUSHER_EVENT_NAME);
                Serializable data = intent.getSerializableExtra(PusherManager.EXTRA_PUSHER_EVENT_DATA);

                mGeneratedEvent = (DataEvent) data;
                *//*add out new Event to upcoming list and hosting list *//*
                DataStore.getInstance().getUser().addOrUpdateEventIntoMap(mGeneratedEvent, EventType.UPCOMING);
                DataStore.getInstance().getUser().addOrUpdateEventIntoMap(mGeneratedEvent, EventType.HOSTING);
                *//*now we going to the event page*//*

                DiscoverActivity.start(QuestionnaireActivity.this, DiscoverActivity.EXTRA_EVENT_ID, mGeneratedEvent._id);

                Toast.makeText(QuestionnaireActivity.this, "Event was successfully created!", Toast.LENGTH_SHORT).show();
            }
        }, new IntentFilter(PusherManager.ACTION_PUSHER_EVENT));*/


        ServerConnector.getInstance().processRequest(new RequestCreateEvent(mDataActivity.client_id, mDataEvent_activity.client_id, "", RequestCreateEvent.REQUEST_TYPE.PUBLISH_EVENT, mGeneratedEvent._id), new ServerConnector.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                progressDialog.dismiss();
                EMLog.d(TAG, "RequestCreateEvent onSuccess");
                String response = ((ResponseString) baseResponse).responseStr;
                mGeneratedEvent = new Gson().fromJson(response, DataEvent.class);
                DataStore.getInstance().getUser().addOrUpdateEventIntoMap(mGeneratedEvent, EventType.UPCOMING);
                DataStore.getInstance().getUser().addOrUpdateEventIntoMap(mGeneratedEvent, EventType.HOSTING);
                DiscoverActivity.start(QuestionnaireActivity.this, DiscoverActivity.EXTRA_EVENT_ID, mGeneratedEvent._id);
            }

            @Override
            public void onFailure(ErrorResponse errorResponse) {
                EMLog.w(TAG, "RequestCreateEvent onFailure: " + errorResponse.getStatusCode() + " " + errorResponse.getServerRawResponse());
                progressDialog.dismiss();
                //Toast.makeText(QuestionnaireActivity.this, "RequestCreateEvent onFailure: " + errorResponse.getStatusCode() + " " + errorResponse.getServerRawResponse(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void goToPublishScreen(int mode) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        if (mode == -1) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_up_animation, R.anim.sign_no_animation,
                    R.anim.sign_no_animation,
                    R.anim.slide_down_animation);
        }
        //.addToBackStack(QuestionnarePublishFragment.TAG)
        fragmentTransaction.replace(R.id.fragment_container_full, QuestionnarePublishFragment.getInstance(mode)).commit();
    }

    public void resetCurrentQuestion() {
        mHasBackPressed = false;

        try {
            ResponseGetUser responseGetUser = DataStore.getInstance().getUser();

            DataAnswer[] activityAnswers = null;

            for (DataProfile profile : responseGetUser.profiles.getActivity_profiles()) {
                if (profile.client_id.equals(mDataActivity.client_id)) {
                    activityAnswers = profile.answers;
                    break;
                }
            }


            QuestionAndAnswer questionAndAnswer = new QuestionAndAnswer(mDataActivity.questions[mCurrentQuestionIndex]);

            for (int j = 0; j < activityAnswers.length; j++) {
                if (activityAnswers[j].questions_id == mDataActivity.questions[mCurrentQuestionIndex].questions_id) {
                    questionAndAnswer.userAnswerStr = QuestionUtils.getAnsweredTitle(mDataActivity.questions[mCurrentQuestionIndex],
                            activityAnswers[j]);
                    questionAndAnswer.userAnswerData = new JSONObject(new Gson().toJson(activityAnswers[j]));
                    questionAndAnswer.isAnsweredConfirmedByClickingNext = true;
                    mQuestionsAndAnswers.set(mCurrentQuestionIndex, questionAndAnswer);
                    break;
                }
            }
        } catch (Exception e) {
        }
    }

    public void performBackOperations(boolean fromSummery) {
        mHasBackPressed = true;
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(QuestionnaireSummeryFragment.TAG);

        Fragment timeZone = getSupportFragmentManager().findFragmentByTag(FragmentTimeZones.TAG);
         /* check if timeZone screen is up */
        if (timeZone != null && timeZone.isVisible()) {
            getSupportFragmentManager().popBackStackImmediate();
            return;
        }

        /* check if summery screen is up */
        if (fragment != null && fragment.isVisible() && !fromSummery && create_mode != CREATE_MODE.EDIT_ACTIVITY) {
            mCurrentQuestionIndex = mQuestionIndex;
            getSupportFragmentManager().popBackStackImmediate();
            return;
        }

        if (create_mode == CREATE_MODE.CREATE_ACTIVITY) {
            fragment = getSupportFragmentManager().findFragmentByTag(QuestionnairePickActivityFragment.TAG);
            if (fragment != null && fragment.isVisible()) {
                if ((ds.getUser().profiles.getActivity_profiles().size() == 0)) {
                    /*user has no profiles*/
                    showExitDialog();
                } else {
                    /*user already has a profile*/
                    QuestionnaireActivity.this.finish();
                }

            } else {/*user is in question*/
                showConfirmExitDialog(dm.getResourceText(R.string.CancelCreateWarningTitle), dm.getResourceText(R.string.CancelCreateActivitySubtitle));
            }
            return;
        }

        if (create_mode == CREATE_MODE.CREATE_EVENT) {
            /*show confirm message
            * if yes is clicked -> go back to caller*/
            showConfirmExitDialog(dm.getResourceText(R.string.CancelCreateWarningTitle), dm.getResourceText(R.string.CancelCreateEventSubtitle));
            return;
        }

        if (create_mode == CREATE_MODE.EDIT_ACTIVITY) {
            // check if we are in question
            List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
            fragment = (fragmentList != null && fragmentList.size() >= 0) ? fragmentList.get(fragmentList.size() - 1) : null;
            if (fragment != null && fragment instanceof QuestionnaireQuestionBaseFragment) {
                // maybe need to restore default value here...
                getSupportFragmentManager().popBackStackImmediate();
                return;
            }
            showConfirmExitDialog(dm.getResourceText(R.string.CancelCreateWarningTitle), dm.getResourceText(R.string.CancelCreateEventSubtitle));
            return;
        }


        super.onBackPressed();
    }

    @Override
    public void onBackPressed() {
        performBackOperations(false);
    }

    public void forceBack() {
        super.onBackPressed();
    }

    public void showConfirmExitDialog(String title, String message) {
        new DialogYesNo(QuestionnaireActivity.this, title, message, dm.getResourceText(R.string.Cancel), dm.getResourceText(R.string.Yes), new YesNoCallback() {
            @Override
            public void onYes() {
                if (create_mode == CREATE_MODE.CREATE_ACTIVITY) {
                    /* go to pick activity */
                    mCurrentQuestionIndex = -1;
                    mQuestionIndex = -1;
                    goToPickActivity();
                } else
                    QuestionnaireActivity.this.finish();
            }

            @Override
            public void onNo() {
               /*nothing here*/
            }
        }).show();
    }

    private void showExitDialog() {
        new DialogYesNo(QuestionnaireActivity.this, dm.getResourceText(R.string.Notice), dm.getResourceText(R.string.AddProfileLogout), dm.getResourceText(R.string.Logout), dm.getResourceText(R.string.Ok), new YesNoCallback() {
            @Override
            public void onYes() {
                /*nothing here*/
            }

            @Override
            public void onNo() {
                Utils.doLogoutOperation();
                Intent intent = new Intent(QuestionnaireActivity.this, SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                QuestionnaireActivity.this.finish();
            }
        }).show();
    }

    @Override
    public void onUserClick(DataPeople user) {
    }

    public boolean isInEditMode() {
        if (create_mode == CREATE_MODE.EDIT_ACTIVITY || create_mode == CREATE_MODE.EDIT_EVENT)
            return true;
        return false;
    }

    @Override
    public void onViewAllUsersClick(DataPeopleHolder holder) {
    }

}
