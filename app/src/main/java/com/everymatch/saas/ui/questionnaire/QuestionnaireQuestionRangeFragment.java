package com.everymatch.saas.ui.questionnaire;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.RangeSeekBar;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuestionnaireQuestionRangeFragment extends QuestionnaireQuestionBaseFragment {
    private static final String TAG = "QuestionRange";

    //Views
    TextView mValueTextView;
    RangeSeekBar<Integer> mRangeBar;
    boolean isTouched = false;


    //Data
    int mMin, mMax;
    float mStep;
    boolean sendAnswerOnChange;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        inflater.inflate(R.layout.question_range, (ViewGroup) view.findViewById(R.id.answers_container));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mValueTextView = (TextView) view.findViewById(R.id.value_textview);
        mRangeBar = (RangeSeekBar<Integer>) view.findViewById(R.id.seekbar);

        mRangeBar.setOnRangeSeekBarChangeListener(listener);
        mRangeBar.setNotifyWhileDragging(true);

        mStep = mQuestionAndAnswer.question.step;
        if (mStep == 0)
            mStep = 1;

        String[] rangeStr = mQuestionAndAnswer.question.range.split(",");
        mMin = Integer.parseInt(rangeStr[0]);
        mMax = Integer.parseInt(rangeStr[1]);
        mRangeBar.setRangeValues(mMin, mMax);

        listener.onRangeSeekBarValuesChanged(mRangeBar, mMin, mMax);

        //add units
        if (mQuestion.units != null && mQuestion.units.containsKey("value") && !Utils.isEmpty(mQuestion.units.get("value").toString())) {
            units = "(" + mQuestion.units.get("value").toString() + ")";
            units = mQuestion.getUnits();
            tvTitle.setText(mQuestion.text_title + "\n" + units);
        }

        recoverAnswer();
    }

    RangeSeekBar.OnRangeSeekBarChangeListener<Integer> listener = new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
        @Override
        public void onRangeSeekBarValuesChanged(RangeSeekBar rangeSeekBar, Integer low, Integer high) {

            //input check
            if (low == high) {
                if (high < mMax)
                    high++;
                else {
                    low--;
                }
            }
            mValueTextView.setText(isTouched ? low.toString() + " - " + high : "");
            isTouched = true;
            if (sendAnswerOnChange)
                setAnswer(mValueTextView.getText().toString());
            sendAnswerOnChange = true;

        }

    };

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
        try {
            if (mQuestionAndAnswer.userAnswerData != null && mQuestionAndAnswer.userAnswerData.has("value")) {
                String valuesStr[] = mQuestionAndAnswer.userAnswerData.getString("value").split(",");
                int from = Integer.parseInt(valuesStr[0].trim());
                int to = Integer.parseInt(valuesStr[1].trim());
                mRangeBar.setSelectedMinValue(from);
                mRangeBar.setSelectedMaxValue(to);

                mValueTextView.setText("" + from + " - " + to);
                setAnswer(mValueTextView.getText().toString());
            }
        } catch (Exception ex) {
            EMLog.e(TAG, ex.getMessage());
        }

    }

    @Override
    public void setAnswer(String answer) {
        super.setAnswer(answer);
    }
}
