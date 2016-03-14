package com.everymatch.saas.ui.questionnaire;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.ui.questionnaire.base.QuestionnaireQuestionBaseFragment;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.view.RangeSeekBar;

import java.util.ArrayList;

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
    ArrayList<String> values = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        inflater.inflate(R.layout.question_range, (ViewGroup) view.findViewById(R.id.answers_container));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //load question values
        mStep = mQuestionAndAnswer.question.step;
        //if (mStep == 0) mStep = 1;

        String[] rangeStr = mQuestionAndAnswer.question.range.split(",");
        mMin = Integer.parseInt(rangeStr[0]);
        mMax = Integer.parseInt(rangeStr[1]);

        //create values array
        for (float i = mMin; i <= mMax; i += mStep) {
            values.add("" + String.format("%.1f", i));
        }

        mValueTextView = (TextView) view.findViewById(R.id.value_textview);
        mRangeBar = (RangeSeekBar<Integer>) view.findViewById(R.id.seekbar);

        mRangeBar.setOnRangeSeekBarChangeListener(listener);
        mRangeBar.setNotifyWhileDragging(true);


        //mRangeBar.setRangeValues(mMin, mMax);
        mRangeBar.setRangeValues(0, values.size());

        listener.onRangeSeekBarValuesChanged(mRangeBar, 0, values.size());

        //add units
        tvTitle.setText(mQuestion.text_title + "\n" + mQuestion.getUnits());

        recoverAnswer();
    }

    RangeSeekBar.OnRangeSeekBarChangeListener<Integer> listener = new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
        @Override
        public void onRangeSeekBarValuesChanged(RangeSeekBar rangeSeekBar, Integer low, Integer high) {

            //input check
            if (low == high) {
                if (low > mMin) low--;
                else high++;
            }

            String fromValue = values.get(low);
            String toValue = values.get(Math.min(values.size() - 1, high));
            mValueTextView.setText(isTouched ? getValue("" + fromValue) + " - " + getValue("" + toValue) : "");
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
                float from = Float.parseFloat(valuesStr[0].trim());
                float to = Float.parseFloat(valuesStr[1].trim());

                //get real positions in values array
                int fromPos = values.indexOf("" + from);
                int toPos = values.indexOf("" + to);
                mRangeBar.setSelectedMinValue(fromPos);
                mRangeBar.setSelectedMaxValue(toPos);

                mValueTextView.setText("" + getValue("" + from) + " - " + getValue("" + to));
                setAnswer(mValueTextView.getText().toString());
            }
        } catch (Exception ex) {
            EMLog.e(TAG, ex.getMessage());
        }

    }

    public String getValue(String val) {
        val = val.replaceAll("\\n", "");
        boolean isInt;
        try {
            isInt = (mStep == Math.round(mStep));
            if (!isInt) return val;
            //need to show int value;
            return "" + (int) Float.parseFloat(val);
        } catch (Exception ex) {
            return val;
        }
    }

    @Override
    public void setAnswer(String answer) {
        super.setAnswer(answer);
    }
}
