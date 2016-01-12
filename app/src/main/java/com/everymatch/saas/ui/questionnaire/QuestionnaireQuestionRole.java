package com.everymatch.saas.ui.questionnaire;

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
import com.everymatch.saas.singeltones.YesNoCallback;
import com.everymatch.saas.ui.base.BaseFragment;
import com.everymatch.saas.ui.dialog.DialogYesNo;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.EventDataRow;
import com.everymatch.saas.view.ViewSeperator;

import java.util.ArrayList;

/**
 * Created by PopApp_laptop on 20/12/2015.
 */
public class QuestionnaireQuestionRole extends QuestionnaireQuestionBaseFragment {
    public static final String ARG_DATA_ANSWER = "arg.answers";
    public static final int ACTION_ROLE_QUESTION = 200;

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
    public void recoverDefaultAnswer() {

    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvTitle.setText(mAnswer.text_title);
        setHeader();
        llQuestionsHolder = (LinearLayout) view.findViewById(R.id.questionsHolder);
        addQuestionsRows();
    }

    private void setHeader() {
        mHeader.getBackButton().setVisibility(View.VISIBLE);
        mHeader.getBackButton().setText(dm.getResourceText(R.string.Cancel));
        mHeader.getIconOne().setText(dm.getResourceText(R.string.Done));
    }

    private void addQuestionsRows() {
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
                edr.setDetails(qaa.question.mandatory ? dm.getResourceText(R.string.Unanswered) : qaa.question.irrelevant_default_state);
                edr.getDetailsView().setTextColor(ds.getIntColor(EMColor.MOON));
            }
        }
    }

    @Override
    public void onBackButtonClicked() {
        new DialogYesNo(getActivity(), "Are you sure?", "all your answers will be deleted", new YesNoCallback() {

            @Override
            public void onYes() {
                /** user clicked YES */
                //mQuestionAndAnswer.restoreDefaultValues();
                restorePreviewsData();
                getFragmentManager().popBackStackImmediate();
            }

            @Override
            public void onNo() {

            }
        }).show();
    }

    @Override
    public void onClick(View v) {
        //super.onClick(v);
        DataQuestion question = (DataQuestion) v.getTag();
        BaseFragment questionFragment = mActivity.getNextQuestionFragment(question.form_type, -1);
        questionFragment.setTargetFragment(this, ACTION_ROLE_QUESTION);

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
                .replace(R.id.fragment_container_full, questionFragment)
                .commit();
    }

    @Override
    public void onOneIconClicked() {
        mActivity.getSupportFragmentManager().popBackStackImmediate();
    }
}

