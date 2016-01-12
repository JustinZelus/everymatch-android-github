package com.everymatch.saas.ui.questionnaire;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.util.EMLog;

import org.json.JSONObject;

import java.util.GregorianCalendar;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuestionnaireQuestionDateTimeFragment extends QuestionnaireQuestionBaseFragment implements View.OnClickListener{

    private static final String TAG = QuestionnaireQuestionDateTimeFragment.class.getSimpleName();
    Button mDateButton, mTimeButton;
    boolean mIsDateSet, mIsTimeSet;

    int year, monthOfYear, dayOfMonth, hourOfDay, minute;

    TimePickerDialog.OnTimeSetListener mOnTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mIsTimeSet = true;
            String timeStr = ""+hourOfDay+":"+ (minute<10?"0":"") +minute;
            mTimeButton.setText(timeStr);

            QuestionnaireQuestionDateTimeFragment.this.hourOfDay = hourOfDay;
            QuestionnaireQuestionDateTimeFragment.this.minute = minute;

            checkIfCanSetAnswer();
        }
    };

    DatePickerDialog.OnDateSetListener mOnDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            mIsDateSet = true;
            String dateStr = ""+dayOfMonth+"/"+monthOfYear+"/"+year;
            mDateButton.setText(dateStr);

            QuestionnaireQuestionDateTimeFragment.this.year = year;
            QuestionnaireQuestionDateTimeFragment.this.monthOfYear = monthOfYear;
            QuestionnaireQuestionDateTimeFragment.this.dayOfMonth = dayOfMonth;

            checkIfCanSetAnswer();
        }
    };

    private void checkIfCanSetAnswer() {
        if (mIsDateSet && mIsTimeSet)
            setAnswer(mDateButton.getText().toString() + " " + mTimeButton.getText().toString());

    }

    public QuestionnaireQuestionDateTimeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        inflater.inflate(R.layout.question_date_time, (ViewGroup) view.findViewById(R.id.answers_container));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDateButton = (Button) view.findViewById(R.id.date_button);
        mDateButton.setOnClickListener(this);

        mTimeButton = (Button) view.findViewById(R.id.time_button);
        mTimeButton.setOnClickListener(this);

        mDateButton.getBackground().setColorFilter(new PorterDuffColorFilter(DataStore.getInstance().getIntColor(EMColor.FOG), PorterDuff.Mode.MULTIPLY));
        mTimeButton.getBackground().setColorFilter(new PorterDuffColorFilter(DataStore.getInstance().getIntColor(EMColor.FOG), PorterDuff.Mode.MULTIPLY));

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
        if (TextUtils.isEmpty(mQuestionAndAnswer.userAnswerStr))
            return;

        String dateAndTime[] = mQuestionAndAnswer.userAnswerStr.split(" ");

        mTimeButton.setText(dateAndTime[1]);
        String timeStr[] = dateAndTime[1].split(":");
        hourOfDay = Integer.parseInt(timeStr[0]);
        minute = Integer.parseInt(timeStr[1]);

        mDateButton.setText(dateAndTime[0]);
        String dateStr[] = dateAndTime[0].split("/");
        year = Integer.parseInt(dateStr[2]);
        monthOfYear = Integer.parseInt(dateStr[1]);
        dayOfMonth = Integer.parseInt(dateStr[0]);

        setAnswer(mQuestionAndAnswer.userAnswerStr);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.date_button:
                new DatePickerDialog(mActivity, mOnDateSetListener , 2015, 1, 1).show();
                break;

            case R.id.time_button:
                new TimePickerDialog(mActivity, mOnTimeSetListener, 12, 0, false).show();
                break;
        }
    }

    @Override
    public void setAnswer(String answer) {
        super.setAnswer(answer);
        //TODO
    }

    @Override
    public JSONObject createDateJsonObject(String dateStr) {
        JSONObject jsonObject = super.createDateJsonObject(dateStr);
        try {
            JSONObject timeZoneJson = new JSONObject();
            timeZoneJson.put("country_code", mActivity.getResources().getConfiguration().locale.getCountry());
            timeZoneJson.put("gmt", new GregorianCalendar().getTimeZone().getRawOffset()/60/60/1000);

            jsonObject.put("year", year);
            jsonObject.put("month", monthOfYear);
            jsonObject.put("day", dayOfMonth);
            jsonObject.put("hour", hourOfDay);
            jsonObject.put("minute", minute);
            jsonObject.put("second", 0);
            jsonObject.put("time_zone", timeZoneJson);

        } catch (Exception e){
            e.printStackTrace();
        }
/*
        "year": 1970,
        "month": 10,
        "day": 31,
        "hour": 10,
        "minute": 47,
        "second": 5,
        "time_zone": {
            "country_code": "FR",
            "gmt": 1
        }
 */

        return jsonObject;
    }
}
