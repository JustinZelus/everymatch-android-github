package com.everymatch.saas.ui.questionnaire;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.server.Data.DataAnswer;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.singeltones.YesNoCallback;
import com.everymatch.saas.ui.dialog.DialogYesNo;
import com.everymatch.saas.ui.questionnaire.base.BaseIdsQuestion;
import com.everymatch.saas.view.EventDataRow;
import com.everymatch.saas.view.ViewSeperator;

import java.util.ArrayList;

/**
 * Created by PopApp_laptop on 20/12/2015.
 */
public class QuestionnaireQuestionList extends BaseIdsQuestion {

    public static final int REQUEST_CODE_GO_TO_ROLE = 203;
    /*Data*/

    /*Views*/
    private LinearLayout llAnswerHolder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        inflater.inflate(R.layout.question_list, (ViewGroup) view.findViewById(R.id.answers_container));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        llAnswerHolder = (LinearLayout) view.findViewById(R.id.answersHolder);
        recoverAnswerData();
        addAnswersRows();
    }

    public void addAnswersRows() {
        llAnswerHolder.removeAllViews();
        for (DataAnswer answer : mQuestion.answers) {
            if (answer.is_hide)
                continue;

            EventDataRow edr = new EventDataRow(getActivity());
            edr.setTag(answer);
            edr.getLeftMediaContainer().setVisibility(View.GONE);
            edr.getTitleView().setText(answer.text_title);
            /*set primary VI size to 30 sp*/
            edr.getRightIcon().setTextSize(TypedValue.COMPLEX_UNIT_SP, 35);

            edr.setDetails(answer.sub_title);

            /** if it's a role question*/
            if (mQuestionAndAnswer.question.role == true && answer.questions != null && answer.questions.length > 0) {
                setRoleRow(answer, edr);
            } else {
                setRegularRow(answer, edr);
            }

            llAnswerHolder.addView(edr);
            llAnswerHolder.addView(new ViewSeperator(getActivity(), null));
        }

        setTitleEnabled(selectedAnswers.size() > 0);

        setAnswer(null);
    }

    private void setRegularRow(DataAnswer answer, EventDataRow edr) {
        edr.setOnClickListener(onRegularClick);
        // decide if answer is selected by
        boolean isSelected = selectedAnswers.contains("" + mQuestionAndAnswer.getAnswerIdentifier(answer));

        if (isSelected) {
            edr.setRightIconText(Consts.Icons.icon_Done);
            edr.getRightIcon().setTextColor(ds.getIntColor(EMColor.PRIMARY));
        } else {
            edr.setRightIconText("");
        }
    }

    private void setRoleRow(DataAnswer answer, EventDataRow edr) {
        edr.getRightIcon().setText(Consts.Icons.icon_Arrowright);

        /** check if all mandatory question has been answered */
        boolean isAnsweredByClickingNext = false;
        try {
            QuestionAndAnswer qaa = mQuestionAndAnswer.subQuestionsMap.get(answer.answer_id).get(0);
            isAnsweredByClickingNext = qaa.isAnsweredConfirmedByClickingNext;
        } catch (Exception ex) {
        }
        //if (AnsweredAllMandatory(answer)) {
        if (isAnsweredByClickingNext) {
            edr.getRightIcon().setText(Consts.Icons.icon_Done);
            edr.getRightIcon().setTextColor(ds.getIntColor(EMColor.PRIMARY));
            edr.getRightText().setText(dm.getResourceText(R.string.remove));
            edr.getRightText().setTextColor(ds.getIntColor(EMColor.NEGATIVE));
            edr.getRightText().setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.text_size_normal));
            // edr.getRightText().setTextSize(Utils.dpToPx(7));
            edr.getRightText().setTag(answer);
            edr.getRightText().setOnClickListener(onRemoveClick);

            // add as answered question if not already exists
            if (!selectedAnswers.contains("" + answer.answer_id)) {
                selectedAnswers.add("" + answer.answer_id);
                setAnswer(getConcatedList());
            }
        } else {

        }
        edr.setOnClickListener(onRoleQuestionClick);
    }

    /*this method is not recursive*/
    private boolean AnsweredAllMandatory(DataAnswer answer) {
        /* get all sub question and answers for that answer */
        ArrayList<QuestionAndAnswer> subQuestions = mQuestionAndAnswer.subQuestionsMap.get(answer.answer_id);

        boolean hasMandatory = false;
        //check if this question has mandatory question at all
        for (QuestionAndAnswer qaa : subQuestions) {
            if (qaa.question.mandatory) {
                hasMandatory = true;
                break;
            }
        }
        //if we have at least one mandatory question - check if all of them has clicked next
        if (hasMandatory) {
            for (QuestionAndAnswer qaa : subQuestions) {
                if (qaa.question.mandatory && !qaa.isAnsweredConfirmedByClickingNext)
                    return false;
            }
            return true;
        } else {
            //no mandatory -> check if has at least one next click
            for (QuestionAndAnswer qaa : subQuestions) {
                if (qaa.isAnsweredConfirmedByClickingNext)
                    return true;
            }
        }

        return false;
    }

    private View.OnClickListener onRegularClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            QuestionnaireQuestionList.super.onClick(v);
        }
    };

    private View.OnClickListener onRoleQuestionClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DataAnswer ans = (DataAnswer) v.getTag();
            if (ans == null)
                return;

            //if (!mQuestion.multiple && selectedAnswers.size() > 0) return;
            QuestionnaireQuestionRole role = QuestionnaireQuestionRole.getInstance(ans);
            role.setTargetFragment(QuestionnaireQuestionList.this, REQUEST_CODE_GO_TO_ROLE);
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right)
                    .addToBackStack("question_role")
                    .add(R.id.fragment_container_full, role)
                    .commit();
        }
    };

    private View.OnClickListener onRemoveClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final DataAnswer answer = (DataAnswer) v.getTag();
            if (answer == null)
                return;

            new DialogYesNo(getActivity(), dm.getResourceText(R.string.Remove), dm.getResourceText(R.string.Confirm_Remove), new YesNoCallback() {
                @Override
                public void onYes() {
                    /** user clicked YES */
                    selectedAnswers.remove("" + answer.answer_id);
                    //delete all answered data
                    ArrayList<QuestionAndAnswer> subQuestions = mQuestionAndAnswer.subQuestionsMap.get(answer.answer_id);
                    for (QuestionAndAnswer qaa : subQuestions) {
                        qaa.restoreDefaultValues();
                    }

                    //unmark as answered by clicking next
                    try {
                        QuestionAndAnswer qaa = mQuestionAndAnswer.subQuestionsMap.get(answer.answer_id).get(0);
                        qaa.isAnsweredConfirmedByClickingNext = false;
                    } catch (Exception ex) {
                    }

                    addAnswersRows();
                    setAnswer(mQuestionAndAnswer.question.getAnswerValuesByAnswerIds(getConcatedList()));

                    //if we are editing...we would like to save
                    setTitleEnabled(mActivity.isInEditMode());
                }

                @Override
                public void onNo() {

                }
            }).show();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GO_TO_ROLE) {
            addAnswersRows();
        }
    }
}
