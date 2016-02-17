package com.everymatch.saas.ui.questionnaire;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.server.Data.DataAnswer;
import com.everymatch.saas.server.Data.DataQuestion;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.ui.base.BaseFragment;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.EventDataRow;
import com.everymatch.saas.view.ViewSeperator;

import java.util.ArrayList;

/**
 * Created by PopApp_laptop on 20/12/2015.
 */
public class QuestionnaireQuestionRole extends QuestionnaireQuestionBaseFragment {
    public final String TAG = getClass().getName();
    public static final String ARG_DATA_ANSWER = "arg.answers";
    public static final int REQUEST_CODE_ROLE_SUB_QUESTION = 200;

    /*Data*/
    private DataAnswer mAnswer;

    /*Views*/
    LinearLayout llQuestionsHolder;

    public static QuestionnaireQuestionRole getInstance(DataAnswer dataAnswer) {
        QuestionnaireQuestionRole answer = new QuestionnaireQuestionRole();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARG_DATA_ANSWER, dataAnswer);
        answer.setArguments(bundle);
        return answer;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAnswer = (DataAnswer) getArguments().getSerializable(ARG_DATA_ANSWER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        inflater.inflate(R.layout.question_role, (ViewGroup) view.findViewById(R.id.answers_container));
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        //here we need to set title enabled not only if we have userAnswerData (see on base)
        try {
            for (DataQuestion question : mAnswer.questions) {
                if (question.mandatory) {
                    QuestionAndAnswer qaa = mQuestionAndAnswer.getQaaByQuestionId(mAnswer.answer_id, question.questions_id);
                    if (qaa.userAnswerData == null ||
                            !qaa.userAnswerData.has("value") ||
                            Utils.isEmpty(qaa.userAnswerData.get("value").toString())) {
                        setTitleEnabled(false);
                        return;
                    }
                }
            }
            setTitleEnabled(true);
        } catch (Exception ex) {
            EMLog.e(TAG, ex.getMessage());
        }
    }

    @Override
    public void recoverDefaultAnswer() {

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTitle.setText(mAnswer.text_title);
        llQuestionsHolder = (LinearLayout) view.findViewById(R.id.questionsHolder);
        addQuestionsRows();
    }

    private void addQuestionsRows() {
        llQuestionsHolder.removeAllViews();
        for (DataQuestion question : mAnswer.questions) {
            EventDataRow edr = new EventDataRow(getActivity());
            edr.setTag(question);
            edr.getLeftMediaContainer().setVisibility(View.GONE);
            edr.setRightIconText(Consts.Icons.icon_Arrowright);
            edr.getTitleView().setText(question.text_title);
            setDetailsValue(edr);
            edr.setOnClickListener(this);

            llQuestionsHolder.addView(edr);
            llQuestionsHolder.addView(new ViewSeperator(getActivity(), null));
        }

        setTitleEnabled(isAnsweredAllMandatory());
    }

    private boolean isAnsweredAllMandatory() {
        ArrayList<QuestionAndAnswer> subQuestions = mQuestionAndAnswer.subQuestionsMap.get(mAnswer.answer_id);
        for (QuestionAndAnswer qaa : subQuestions) {
            if (qaa.question.mandatory && Utils.isEmpty(qaa.userAnswerStr))
                return false;
        }
        return true;
    }

    /**
     * this method set the subTitle
     */
    private void setDetailsValue(EventDataRow edr) {
        DataQuestion question = (DataQuestion) edr.getTag();
        QuestionAndAnswer qaa = mQuestionAndAnswer.getQaaByQuestionId(mAnswer.answer_id, question.questions_id);
        if (qaa != null) {
            if (!Utils.isEmpty(qaa.userAnswerStr)) {
                edr.setDetails(qaa.userAnswerStr);
                edr.getDetailsView().setTextColor(ds.getIntColor(EMColor.PRIMARY));
            } else {
                if (qaa.question.mandatory) {
                    edr.setDetails(dm.getResourceText(R.string.Unanswered));
                } else {
                    if (qaa.question.irrelevant_default_state.equals("all"))
                        edr.setDetails(dm.getResourceText(R.string.All));
                    else
                        edr.setDetails(dm.getResourceText(R.string.None));
                }
                edr.getDetailsView().setTextColor(ds.getIntColor(EMColor.MOON));
            }
        }
    }

    @Override
    protected void showQuestionNumber() {
        //on need to show question number
        return;
    }

    @Override
    public void onClick(View v) {
        //super.onClick(v);
        DataQuestion question = (DataQuestion) v.getTag();
        BaseFragment questionFragment = mActivity.getNextQuestionFragment(question.form_type, -1);
        questionFragment.setTargetFragment(this, REQUEST_CODE_ROLE_SUB_QUESTION);

        /* fragment arguments */
        Bundle args = new Bundle();
        args.putBoolean(QuestionnaireQuestionBaseFragment.ARG_FROM_SUMMERY, true);
        args.putBoolean(QuestionnaireQuestionBaseFragment.ARG_IS_SUB_QUESTION, true);
        args.putInt(QuestionnaireQuestionBaseFragment.ARG_ROLE_ANSWER_ID, mAnswer.answer_id);
        args.putSerializable(QuestionnaireQuestionBaseFragment.ARG_SUB_QUESTION_OBJECT, question);
        questionFragment.setArguments(args);

        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                .addToBackStack("questionRole" + question.questions_id)
                .add(R.id.fragment_container_full, questionFragment)
                .commit();
    }

    @Override
    public void onOneIconClicked() {
        try {
            //mark the first qaa as answered -> on list, we check if the first qaa is clicked to mark remove
            QuestionAndAnswer qaa = mQuestionAndAnswer.subQuestionsMap.get(mAnswer.answer_id).get(0);
            if (qaa != null) {
                qaa.isAnsweredConfirmedByClickingNext = true;
            }
        } catch (Exception ex) {
        }

        getTargetFragment().onActivityResult(QuestionnaireQuestionList.REQUEST_CODE_GO_TO_ROLE, Activity.RESULT_OK, new Intent());
        mActivity.getSupportFragmentManager().popBackStackImmediate();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ROLE_SUB_QUESTION) {
            addQuestionsRows();
        }
    }
}

