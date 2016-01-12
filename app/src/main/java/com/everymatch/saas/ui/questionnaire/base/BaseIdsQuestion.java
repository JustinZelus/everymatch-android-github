package com.everymatch.saas.ui.questionnaire.base;

import android.view.View;

import com.everymatch.saas.server.Data.DataAnswer;
import com.everymatch.saas.ui.questionnaire.QuestionnaireQuestionBaseFragment;
import com.everymatch.saas.util.EMLog;

import java.util.HashSet;

/**
 * Created by PopApp_laptop on 22/12/2015.
 */
public class BaseIdsQuestion extends QuestionnaireQuestionBaseFragment {
    public final String TAG = getClass().getName();

    /*Data*/
    protected HashSet<String> selectedAnswers = new HashSet();

    /**
     * this method actually draw the elements on the screen
     */
    protected void addAnswersRows() {
    }

    protected String getSelectedIds() {
        String ans = "";
        for (String s : selectedAnswers) {
            ans += (ans.trim().equals("") ? "" : ",") + s.trim();
        }
        if (ans.endsWith(","))
            ans = ans.substring(0, ans.length() - 1);
        return ans;
    }

    @Override
    protected String createIdsList() {
        return getSelectedIds();
    }

    @Override
    public void recoverDefaultAnswer() {
        recoverAnswerData();
        addAnswersRows();
    }

    /**
     * check if we have previews data and load it
     */
    protected void recoverAnswerData() {
        selectedAnswers.clear();
        if (mQuestionAndAnswer.userAnswerData != null && mQuestionAndAnswer.userAnswerData.has("value")) {
            try {
                final String markedAnswers[] = mQuestionAndAnswer.userAnswerData.getString("value").split(",");
                for (String s : markedAnswers)
                    selectedAnswers.add(s);
            } catch (Exception ex) {
                EMLog.e(TAG, ex.getMessage());
            }
        }
    }

    @Override
    public void onClick(View v) {
        DataAnswer ans = (DataAnswer) v.getTag();
        if (ans == null)
            return;

        if (selectedAnswers.contains("" + ans.answer_id)) {
            if (selectedAnswers.size() == 1) {
                /* don't delete the last mark */
                return;
            }
            selectedAnswers.remove("" + ans.answer_id);
        } else {
            if (mQuestion.multiple) {
                selectedAnswers.add("" + ans.answer_id);
            } else {
                if (selectedAnswers.size() == 0)
                    selectedAnswers.add("" + ans.answer_id);
                else {
                    selectedAnswers.clear();
                    selectedAnswers.add("" + ans.answer_id);
                }
            }
        }

        /** in id's question type we set user answer str to the values itself and
         * overriding  createIdsList will bring the user answer data value*/
        String answers = mQuestion.getAnswerValuesByAnswerIds(getSelectedIds());
        setAnswer(answers);
        addAnswersRows();
        setTitleEnabled(selectedAnswers.size() > 0);
    }

}
