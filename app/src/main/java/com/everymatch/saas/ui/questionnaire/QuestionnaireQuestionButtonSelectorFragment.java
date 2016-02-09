package com.everymatch.saas.ui.questionnaire;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.everymatch.saas.R;
import com.everymatch.saas.server.Data.DataAnswer;
import com.everymatch.saas.ui.questionnaire.base.BaseIdsQuestion;
import com.everymatch.saas.util.IconManager;
import com.everymatch.saas.view.questions.ViewButtonQuestion;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuestionnaireQuestionButtonSelectorFragment extends BaseIdsQuestion implements View.OnClickListener {
    public final String TAG = getClass().getName();

    private LinearLayout btnHolder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        inflater.inflate(R.layout.question_yes_no, (ViewGroup) view.findViewById(R.id.answers_container));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnHolder = (LinearLayout) view.findViewById(R.id.btnHolder);

        recoverAnswerData();
        this.addAnswersRows();
    }

    public void addAnswersRows() {
        btnHolder.removeAllViews();
        for (DataAnswer answer : mQuestion.answers) {
            if (answer.is_hide)
                continue;

            ViewButtonQuestion v = new ViewButtonQuestion(getActivity());
            v.setTag(answer);
            v.getTvText().setText(answer.text_title);
            v.getTvIcon().setText(IconManager.getInstance(getActivity()).getIconString(answer.icon.getValue()));

            v.setSelected(selectedAnswers.contains("" + mQuestionAndAnswer.getAnswerIdentifier(answer)));

           /* if (mQuestion.question_type.equals(QuestionType.IDS))
                v.setSelected(selectedAnswers.contains("" + answer.answer_id));
            else
                v.setSelected(selectedAnswers.contains("" + answer.text_title));*/

            v.setOnClickListener(this);
            btnHolder.addView(v);
        }
    }

}
