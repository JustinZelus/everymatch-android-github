package com.everymatch.saas.ui.questionnaire;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.ui.questionnaire.base.QuestionnaireQuestionBaseFragment;
import com.everymatch.saas.util.EMLog;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuestionnaireQuestionNumberFragment extends QuestionnaireQuestionBaseFragment implements TextWatcher {
    private static final String TAG = QuestionnaireQuestionNumberFragment.class.getSimpleName();
    EditText etValue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        inflater.inflate(R.layout.question_number, (ViewGroup) view.findViewById(R.id.answers_container));
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etValue = (EditText) view.findViewById(R.id.etNumber);

        etValue.getBackground().setColorFilter(new PorterDuffColorFilter(DataStore.getInstance().getIntColor(EMColor.FOG), PorterDuff.Mode.MULTIPLY));
        etValue.addTextChangedListener(this);

        recoverAnswerData();

    }

    private void recoverAnswerData() {
        if (mQuestionAndAnswer.userAnswerData != null && mQuestionAndAnswer.userAnswerData.has("value")) {
            try {
                final String markedAnswer = mQuestionAndAnswer.userAnswerData.getString("value");
                etValue.setText(markedAnswer);
            } catch (Exception ex) {
                EMLog.e(TAG, ex.getMessage());
            }
        }
    }

    @Override
    public void recoverDefaultAnswer() {
        recoverAnswerData();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (s == null || s.length() == 0)
            clearAnswer();
        else setAnswer(s.toString());
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
