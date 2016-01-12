package com.everymatch.saas.ui.questionnaire;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.client.data.FormType;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.ShapeDrawableUtils;
import com.everymatch.saas.view.BaseEditText;

/**
 * Created by PopApp_laptop on 14/10/2015.
 */
public class QuestionnaireQuestionTextArea extends QuestionnaireQuestionBaseFragment implements TextWatcher {
    private static final String TAG = QuestionnaireQuestionTextArea.class.getSimpleName();
    public static final String ARG_IS_MULTI_LINE = "arg.is.multi.line";

    /*Views*/
    BaseEditText etAnswer;

    /*Data*/


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        inflater.inflate(R.layout.question_text_area, (ViewGroup) view.findViewById(R.id.answers_container));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etAnswer = (BaseEditText) view.findViewById(R.id.value_editText);
        etAnswer.setBackgroundDrawable(ShapeDrawableUtils.getButtonStroked(ds.getIntColor(EMColor.MOON)));
        etAnswer.addTextChangedListener(this);
        if (mQuestion.form_type.equals(FormType.TEXT_AREA)) {
            etAnswer.setSingleLine(false);
            etAnswer.setMinLines(5);
        }

        if (!TextUtils.isEmpty(mQuestionAndAnswer.userAnswerStr))
            recoverAnswer();
        else {
            if (mQuestionAndAnswer.question.default_value != null)
                recoverDefaultAnswer();
        }
    }

    @Override
    public void recoverDefaultAnswer() {
        try {
            String str = mQuestionAndAnswer.question.default_value;
            setAnswer(str);
            recoverAnswer();
        } catch (Exception e) {
            e.printStackTrace();
            EMLog.e(TAG, "could not parse default value on questionId: " + mQuestionAndAnswer.question.questions_id);
        }
    }

    private void recoverAnswer() {
        if (!TextUtils.isEmpty(mQuestionAndAnswer.userAnswerStr)) {
            etAnswer.setText(mQuestionAndAnswer.userAnswerStr);
            etAnswer.setSelection(etAnswer.getText().length());
            return;
        }

        setAnswer(mQuestionAndAnswer.userAnswerStr);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        super.setAnswer(s.toString());
    }
}
