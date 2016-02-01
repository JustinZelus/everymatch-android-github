package com.everymatch.saas.ui.questionnaire;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.view.RangeSeekBar;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuestionnaireQuestionRangeFragment extends QuestionnaireQuestionBaseFragment {
    private static final String TAG = "QuestionRange";

    TextView mValueTextView;
    RangeSeekBar<Integer> mRangeBar;

    int mMin, mMax;
    float mStep;

    public QuestionnaireQuestionRangeFragment() {
    }

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
        RangeSeekBar.OnRangeSeekBarChangeListener<Integer> listener = new RangeSeekBar.OnRangeSeekBarChangeListener<Integer>() {
            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar rangeSeekBar, Integer low, Integer high) {
                Log.d(TAG, "low = " + low + " high = " + high);
                mValueTextView.setText(low.toString() + " - " + high);
                setAnswer(low.toString() + " - " + high);
            }
        };

        mRangeBar.setOnRangeSeekBarChangeListener(listener);
        mRangeBar.setNotifyWhileDragging(true);

        mStep = mQuestionAndAnswer.question.step;
        if (mStep == 0)
            mStep = 1;

        String[] rangeStr = mQuestionAndAnswer.question.range.split(",");
        mMin = Integer.parseInt(rangeStr[0]);
        mMax = Integer.parseInt(rangeStr[1]);
        mRangeBar.setRangeValues(mMin, mMax);

//        StateListDrawable ld = (StateListDrawable) mRangeBar.getProgressDrawable();
//        ClipDrawable d1 = (ClipDrawable) ld.findDrawableByLayerId(R.id.progress_circular);
////        ClipDrawable d1 = (ClipDrawable) ld.findDrawableByLayerId(R.id.progress_horizontal_circular);
//        d1.setColorFilter(getResources().getColor(R.color.primary), PorterDuff.Mode.SRC_IN);

        listener.onRangeSeekBarValuesChanged(mRangeBar, mMin, mMax);

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
        if (TextUtils.isEmpty(mQuestionAndAnswer.userAnswerStr)) {
            mRangeBar.setSelectedMinValue(mMin);
            mRangeBar.setSelectedMaxValue(mMax);
            return;
        }

        String valuesStr[] = mQuestionAndAnswer.userAnswerStr.split(",");
        mRangeBar.setSelectedMinValue(Integer.parseInt(valuesStr[0]));
        mRangeBar.setSelectedMaxValue(Integer.parseInt(valuesStr[1]));
        setAnswer(mQuestionAndAnswer.userAnswerStr);
    }

    @Override
    public void setAnswer(String answer) {
        super.setAnswer(answer);
    }
}
