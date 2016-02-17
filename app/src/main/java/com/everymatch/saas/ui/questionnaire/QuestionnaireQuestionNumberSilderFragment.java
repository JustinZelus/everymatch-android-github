package com.everymatch.saas.ui.questionnaire;

import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.view.BaseSeekBar;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuestionnaireQuestionNumberSilderFragment extends QuestionnaireQuestionBaseFragment {
    TextView mValueTextView;
    BaseSeekBar mSeekBar;
    boolean isTouched = false;

    int mMin, mMax;
    float mStep;
    int currentValue;

    private String TAG = this.getClass().getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        inflater.inflate(R.layout.question_number_slider, (ViewGroup) view.findViewById(R.id.answers_container));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mValueTextView = (TextView) view.findViewById(R.id.value_textview);
        mSeekBar = (BaseSeekBar) view.findViewById(R.id.seekbar);

        mSeekBar.getProgressDrawable().setColorFilter(new PorterDuffColorFilter(DataStore.getInstance().getIntColor(EMColor.PRIMARY), PorterDuff.Mode.MULTIPLY));

        mStep = mQuestionAndAnswer.question.step == 0 ? 1 : mQuestionAndAnswer.question.step;
        String[] rangeStr = mQuestionAndAnswer.question.range.split(",");
        mMin = Integer.parseInt(rangeStr[0]);
        mMax = Integer.parseInt(rangeStr[1]);
        mSeekBar.setKeyProgressIncrement((int) Math.max(mStep, 1f));
        mSeekBar.setMax(mMax - mMin);

        recoverAnswerData();
        mSeekBar.setProgress(currentValue);
        mValueTextView.setText(isTouched ? "" + currentValue : "");

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                String progressStr = String.valueOf((progress) + (mMin));
                mValueTextView.setText(progressStr);
                setAnswer(progressStr);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (!isTouched) {
                    isTouched = true;
                    mSeekBar.setThumbEnabled(true);
                }
            }
        });

        //add units
        if (mQuestion.units != null && mQuestion.units.containsKey("value") && mQuestion.units.get("value") != null) {
            units = "(" + mQuestion.units.get("value").toString() + ")";
            units = mQuestion.getUnits();
            tvTitle.setText(mQuestion.text_title + "\n" + units);
        }
    }

    @Override
    public void recoverDefaultAnswer() {
        recoverAnswerData();
    }

    private void recoverAnswerData() {
        if (mQuestionAndAnswer.userAnswerData != null && mQuestionAndAnswer.userAnswerData.has("value")) {
            try {
                isTouched = true;
                final String markedAnswer = mQuestionAndAnswer.userAnswerData.getString("value").trim();
                currentValue = Integer.parseInt(markedAnswer);
                mSeekBar.setThumbEnabled(true);
            } catch (Exception ex) {
                currentValue = (mMax - mMin) / 2;
                EMLog.e(TAG, ex.getMessage());
            }
        } else {
            currentValue = (mMax - mMin) / 2;
            mSeekBar.setThumbEnabled(false);
            mValueTextView.setText("");
        }
    }
}
