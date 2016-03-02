package com.everymatch.saas.ui.questionnaire;

import android.animation.ObjectAnimator;
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
import com.everymatch.saas.util.Utils;
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
    public DataManager dm = DataManager.getInstance();
    public boolean wasChanges = false;

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

        updateSaveButton();
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
            mHeader.getIconOne().setAlpha(wasChanges ? 1.0f : 0.5f);


        } else if (mActivity.create_mode == QuestionnaireActivity.CREATE_MODE.EDIT_EVENT) {
            mHeader.setSaveCancelMode(dm.getResourceText(R.string.Edit_Event_Profile));
            // mHeader.setTitle(dm.getResourceText(R.string.Edit_Event_Profile));
            // mHeader.getIconOne().setText(dm.getResourceText(R.string.Save));
        }

        if (mActivity.IS_VIEW_MODE) {
            mHeader.getIconOne().setVisibility(View.GONE);
            mHeader.getBackButton().setText(Consts.Icons.icon_New_Close);
            mHeader.getCenterText().setText("");
            mHeader.setTitle(mActivity.mGeneratedEvent.dataPublicEvent.event_title);
        }

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mActivity.goToQuestion(position, true);
    }

    @Override
    public void onBackButtonClicked() {
        mActivity.mCurrentQuestionIndex = mActivity.mQuestionIndex;
        if (mActivity.isInEditMode()) {
            // if (wasChanges)
            //    mActivity.showConfirmExitDialog(dm.getResourceText(R.string.CancelCreateWarningTitle), dm.getResourceText(R.string.CancelCreateActivitySubtitle));
            //else
            ((QuestionnaireActivity) getActivity()).forceBack();
            return;
        }

        getActivity().getSupportFragmentManager().popBackStackImmediate();
    }

    public void updateSaveButton() {
        if ((mActivity.create_mode == QuestionnaireActivity.CREATE_MODE.EDIT_ACTIVITY || mActivity.create_mode == QuestionnaireActivity.CREATE_MODE.EDIT_EVENT)) {
            mHeader.getIconOne().setClickable(wasChanges);
            ObjectAnimator.ofFloat(mHeader.getIconOne(), View.ALPHA.getName(), wasChanges ? 1.0f : 0.5f).start();
        }
    }

    @Override
    public void onOneIconClicked() {
        /* Save click */
        if (mActivity.isInEditMode()) {
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
        wasChanges = true;
        setListAdapter(new SummeryAdapter());
        updateSaveButton();
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

            ((TextView) convertView.findViewById(R.id.arrow_image)).setText(Consts.Icons.icon_Arrowright);

            TextView tvTitle = (TextView) convertView.findViewById(R.id.question_textview);
            tvTitle.setText(question.text_title);

            TextView tvValue = (TextView) convertView.findViewById(R.id.answer_textview);

            //check if userAnswerStr contains units and add it if not
            String answerStr = mQuestionsAndAnswers.get(position).userAnswerStr;
            if (!Utils.isEmpty(answerStr) && !answerStr.endsWith(question.getUnits())) {
                answerStr += " " + question.getUnits();
            }
            setSummary(tvValue, answerStr, mQuestionsAndAnswers.get(position));

            return convertView;
        }
    }

    private void setSummary(TextView tvValue, String text, QuestionAndAnswer qaa) {
        if ((QuestionnaireActivity.CREATE_MODE.EDIT_ACTIVITY == mActivity.create_mode || QuestionnaireActivity.CREATE_MODE.EDIT_EVENT == mActivity.create_mode) && TextUtils.isEmpty(text)) {
            tvValue.setText(dm.getResourceText(R.string.Unanswered));
            tvValue.setTextColor(DataStore.getInstance().getIntColor(EMColor.MOON));
        } else {
            tvValue.setText(text);
            tvValue.setTextColor(DataStore.getInstance().getIntColor(EMColor.PRIMARY));
            if (qaa.isAllAnswersSelected())
                tvValue.setText(dm.getResourceText(R.string.All));
        }
    }
}
