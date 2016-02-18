package com.everymatch.saas.ui.questionnaire;

import android.os.Bundle;
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
import com.everymatch.saas.ui.base.BaseFragment;
import com.everymatch.saas.view.EventHeader;

import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuestionnaireNotMandatoryFragment extends BaseFragment
        implements AdapterView.OnItemClickListener, EventHeader.OnEventHeader {

    public static final String TAG = "QuestionnaireNotMandatory";
    public static final int REQUEST_CODE_NOT_MANDATORY = 121;

    //Data
    public static final String QUESTION_NUMBER_FORMAT = "%s/%s";
    QuestionnaireActivity mActivity;
    ArrayList<DataQuestion> mNotMandatoryQuestions = new ArrayList<>();

    //Views
    private EventHeader mHeader;
    private ListView mListView;

    public QuestionnaireNotMandatoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (QuestionnaireActivity) getActivity();
        mNotMandatoryQuestions = mActivity.mNotMandatoryUnansweredQuestions;

        mActivity.getSupportActionBar().hide();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_not_mandatory_questions_list,
                container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(new NotMandatoryAdapter());
        mListView.setOnItemClickListener(this);

        mHeader = (EventHeader) view.findViewById(R.id.eventHeader);
        setHeader();
        try {
            showQuestionNumber();
        } catch (Exception e) {
        }
    }

    private void setHeader() {
        mHeader.setListener(this);
        mHeader.getBackButton().setText(Consts.Icons.icon_Details);
        mHeader.getIconOne().setText(DataManager.getInstance().getResourceText(R.string.Done).toUpperCase());
        mHeader.getIconOne().setTextColor(DataStore.getInstance().getIntColor(EMColor.WHITE));
        mHeader.getIconOne().setClickable(true);
        mHeader.getIconTwo().setVisibility(View.GONE);
        mHeader.getIconThree().setVisibility(View.GONE);
        mHeader.setTitle("");
    }

    private void showQuestionNumber() {
        if (mActivity.create_mode == QuestionnaireActivity.CREATE_MODE.CREATE_ACTIVITY || mActivity.create_mode == QuestionnaireActivity.CREATE_MODE.CREATE_EVENT) {
            int mandatoryCount = 0;
            for (QuestionAndAnswer qaa : mActivity.mQuestionsAndAnswers) {
                if (qaa.question.mandatory)
                    mandatoryCount++;
            }
            mHeader.getCenterText().setText(String.format(QUESTION_NUMBER_FORMAT, mandatoryCount + 1, mandatoryCount + 1));
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //getFragmentManager().popBackStackImmediate();
        mActivity.goToNotMandatoryQuestion(position);
    }

    @Override
    public void onBackButtonClicked() {
        mActivity.goToSummeryScreen(null);
    }

    @Override
    public void onOneIconClicked() {
        mActivity.sendAnswersToServer();
    }

    @Override
    public void onTwoIconClicked() {
    }

    @Override
    public void onThreeIconClicked() {
    }

    public void onUpdate() {
        mListView.setAdapter(new NotMandatoryAdapter());
    }

    private class NotMandatoryAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mNotMandatoryQuestions.size();
        }

        @Override
        public Object getItem(int position) {
            return mNotMandatoryQuestions.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                LayoutInflater inflater = getActivity().getLayoutInflater();
                convertView = inflater.inflate(R.layout.question_list_item, parent, false);
            }

            DataQuestion item = mNotMandatoryQuestions.get(position);
            ((TextView) convertView.findViewById(R.id.arrow_image)).setText(Consts.Icons.icon_Arrowright);

            TextView tvTitle = (TextView) convertView.findViewById(R.id.question_textview);
            tvTitle.setText(item.text_title);

            TextView tvValue = (TextView) convertView.findViewById(R.id.answer_textview);
            QuestionAndAnswer qaa = new QuestionAndAnswer(item);
            String value = qaa.getSummeryValue();
            tvValue.setText(value);
            if (!value.equals(dm.getResourceText(R.string.Unanswered)))
                tvValue.setTextColor(ds.getIntColor(EMColor.PRIMARY));

            return convertView;

            /*
            String answerStr = "";
            if (item.answers != null && item.answers.length > 0) {
                for (DataAnswer answer : item.answers) {
                    if (answer.is_default) {
                        answerStr += answer.text_title + ", ";
                    }
                }
            }

            if (TextUtils.isEmpty(answerStr)) {
                answerStr = item.irrelevant_default_state;
            } else {
                answerStr = answerStr.substring(0, answerStr.length() - 2); // Remove the ", "
                ((TextView) convertView.findViewById(R.id.answer_textview)).setTextColor(ds.getIntColor(EMColor.PRIMARY));
            }

            if (answerStr != null) {
                answerStr = Utils.setFirstLetterUpperCase(answerStr);
            }

            ((TextView) convertView.findViewById(R.id.answer_textview)).setText(answerStr);
*/

        }
    }
}
