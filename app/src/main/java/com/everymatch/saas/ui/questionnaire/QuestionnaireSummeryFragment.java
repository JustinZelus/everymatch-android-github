package com.everymatch.saas.ui.questionnaire;

import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.server.Data.DataQuestion;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.view.EventHeader;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuestionnaireSummeryFragment extends ListFragment implements AdapterView.OnItemClickListener, EventHeader.OnEventHeader {

    public static final String TAG = "QuestionnaireSummery";
    public static final int REQUEST_CODE_SUMMERY = 121;
    //Data
    QuestionnaireActivity mActivity;
    public static boolean isShown = false;
    public DataManager dm = DataManager.getInstance();

    //Views
    private EventHeader mHeader;

    ArrayList<QuestionAndAnswer> mQuestionsAndAnswers = new ArrayList<QuestionAndAnswer>();

    public QuestionnaireSummeryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (QuestionnaireActivity) getActivity();

        mActivity.getSupportActionBar().hide();

        for (int i = 0; i < mActivity.mQuestionsAndAnswers.size(); ++i)
            if (mActivity.mQuestionsAndAnswers.get(i).isAnsweredConfirmedByClickingNext) //mActivity.mQuestionsAndAnswers.get(i).userAnswerStr != null)
                mQuestionsAndAnswers.add(mActivity.mQuestionsAndAnswers.get(i));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_questionaire_summery,
                container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setListAdapter(new SummeryAdapter());
        getListView().setOnItemClickListener(this);

        setHeader(view);

        if (mActivity.create_mode == QuestionnaireActivity.CREATE_MODE.EDIT_ACTIVITY) {

            // If back press - cancel the answered question
            if (mActivity.mHasBackPressed) {
                mActivity.resetCurrentQuestion();
                mQuestionsAndAnswers.set(mActivity.mCurrentQuestionIndex, mActivity.mQuestionsAndAnswers.get(mActivity.mCurrentQuestionIndex));
            }
        } else if (mActivity.create_mode == QuestionnaireActivity.CREATE_MODE.EDIT_EVENT) {
        }
    }

    private void setHeader(View view) {
        mHeader = (EventHeader) view.findViewById(R.id.eventHeader);
        mHeader.setListener(this);
        mHeader.getBackButton().setText(Consts.Icons.icon_New_Close);
        mHeader.getIconOne().setText(dm.getResourceText(R.string.Cancel_Exit));
        mHeader.getIconTwo().setVisibility(View.GONE);
        mHeader.getIconThree().setVisibility(View.GONE);
        mHeader.setTitle(dm.getResourceText(R.string.Summary));

        if (mActivity.create_mode == QuestionnaireActivity.CREATE_MODE.EDIT_ACTIVITY) {
            mHeader.setTitle(dm.getResourceText(R.string.Edit_Activity));
            mHeader.getIconOne().setText(dm.getResourceText(R.string.Save));

        } else if (mActivity.create_mode == QuestionnaireActivity.CREATE_MODE.EDIT_EVENT) {
            mHeader.setTitle(dm.getResourceText(R.string.Edit_Event_settings));
            mHeader.getIconOne().setText(dm.getResourceText(R.string.Save));
        }

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //getActivity().getSupportFragmentManager().popBackStackImmediate();
        mActivity.goToQuestion(position, true);
    }

    public void close() {

    }

    @Override
    public void onBackButtonClicked() {
        mActivity.mCurrentQuestionIndex = mActivity.mQuestionIndex;

        if (mActivity.create_mode == QuestionnaireActivity.CREATE_MODE.EDIT_ACTIVITY ||
                mActivity.create_mode == QuestionnaireActivity.CREATE_MODE.EDIT_EVENT) {
            mActivity.showConfirmExitDialog(dm.getResourceText(R.string.CancelCreateWarningTitle), dm.getResourceText(R.string.CancelCreateActivitySubtitle));
            return;
        }

        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    public void onOneIconClicked() {
        /* cancel click */
        if (mActivity.create_mode == QuestionnaireActivity.CREATE_MODE.EDIT_ACTIVITY ||
                mActivity.create_mode == QuestionnaireActivity.CREATE_MODE.EDIT_EVENT) {
            mActivity.sendAnswersToServer();
            return;
        }

        mActivity.performBackOperations(true);
    }

    @Override
    public void onTwoIconClicked() {

    }

    @Override
    public void onThreeIconClicked() {

    }

    public void onUpdate() {
        setListAdapter(new SummeryAdapter());
    }

    private class SummeryAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mQuestionsAndAnswers.size();
        }

        @Override
        public Object getItem(int position) {
            return mQuestionsAndAnswers.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                convertView = inflater.inflate(R.layout.question_list_item, null);
            }

            DataQuestion question = mQuestionsAndAnswers.get(position).question;
            ((TextView) convertView.findViewById(R.id.question_textview)).setText(question.text_title);
            setSummary((TextView) convertView.findViewById(R.id.answer_textview), mQuestionsAndAnswers.get(position).userAnswerStr, mQuestionsAndAnswers.get(position));
            ((TextView) convertView.findViewById(R.id.arrow_image)).setText(Consts.Icons.icon_Arrowright);

            return convertView;
        }
    }

    private void setSummary(TextView itemTextView, String text, QuestionAndAnswer qaa) {
        if ((QuestionnaireActivity.CREATE_MODE.EDIT_ACTIVITY == mActivity.create_mode ||
                QuestionnaireActivity.CREATE_MODE.EDIT_EVENT == mActivity.create_mode) && TextUtils.isEmpty(text)) {
            itemTextView.setText(dm.getResourceText(R.string.Unanswered));
            itemTextView.setTextColor(DataStore.getInstance().getIntColor(EMColor.MOON));
        } else {
            itemTextView.setText(text);
            itemTextView.setTextColor(DataStore.getInstance().getIntColor(EMColor.PRIMARY));
            if (qaa.isAllAnswersSelected())
                itemTextView.setText(dm.getResourceText(R.string.All));

        }
    }
}
