package com.everymatch.saas.ui.questionnaire;

import android.app.DatePickerDialog;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.ui.questionnaire.base.QuestionnaireQuestionBaseFragment;
import com.everymatch.saas.util.EMLog;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuestionnaireQuestionDateFragment extends QuestionnaireQuestionBaseFragment implements View.OnClickListener {

    private static final String TAG = QuestionnaireQuestionDateFragment.class.getSimpleName();

    Button mDateButton;
    int year = 1980, monthOfYear = 0, dayOfMonth = 1;

    DatePickerDialog.OnDateSetListener mOnDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            String dateStr = "" + dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
            mDateButton.setText(dateStr);
            mDateButton.setTextColor(ds.getIntColor(EMColor.NIGHT));
            QuestionnaireQuestionDateFragment.this.year = year;
            QuestionnaireQuestionDateFragment.this.monthOfYear = monthOfYear;
            QuestionnaireQuestionDateFragment.this.dayOfMonth = dayOfMonth;

            setAnswer(dateStr);
        }
    };

    public QuestionnaireQuestionDateFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        inflater.inflate(R.layout.question_date, (ViewGroup) view.findViewById(R.id.answers_container));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mDateButton = (Button) view.findViewById(R.id.date_button);
        mDateButton.getBackground().setColorFilter(new PorterDuffColorFilter(DataStore.getInstance().getIntColor(EMColor.FOG), PorterDuff.Mode.MULTIPLY));
        mDateButton.setOnClickListener(this);

        try {
            if (!TextUtils.isEmpty(mQuestionAndAnswer.userAnswerStr))
                recoverAnswer();
            else {
                if (mQuestionAndAnswer.question.default_value != null)
                    recoverDefaultAnswer();
            }
        } catch (Exception e) {
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

        mDateButton.setText(mQuestionAndAnswer.userAnswerStr);
        String dateStr[] = mQuestionAndAnswer.userAnswerStr.split("/");

        year = Integer.parseInt(dateStr[2]);
        dayOfMonth = Integer.parseInt(dateStr[1]);
        monthOfYear = Integer.parseInt(dateStr[0]);

        setAnswer(mQuestionAndAnswer.userAnswerStr);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.date_button:

                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.YEAR, -16);
                DatePickerDialog dialog = new DatePickerDialog(mActivity, mOnDateSetListener, year, monthOfYear, dayOfMonth);
                dialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
                dialog.show();

                //new DatePickerDialog(mActivity, mOnDateSetListener, year, monthOfYear, dayOfMonth).show();

            {
                //dialog.getDatePicker().setMaxDate(new Date().getTime());
                //int year = "age".equals(mQuestionAndAnswer.question.question_type) ? 2015 - 16 : 2015;
                //new DatePickerDialog(mActivity, mOnDateSetListener, year, 1, 1).show();
            }
            break;
        }
    }

    @Override
    public JSONObject createDateJsonObject(String dateStr) {
        JSONObject jsonObject = super.createDateJsonObject(dateStr);
        try {
            JSONObject timeZoneJson = new JSONObject();
            String countyCode = mActivity.getResources().getConfiguration().locale.getCountry();
            if (countyCode.trim().equals(""))
                countyCode = "IL";
            timeZoneJson.put("country_code", countyCode);
            timeZoneJson.put("gmt", new GregorianCalendar().getTimeZone().getRawOffset() / 60 / 60 / 1000);

            jsonObject.put("year", year);
            jsonObject.put("month", monthOfYear + 1);
            jsonObject.put("day", dayOfMonth);
            jsonObject.put("hour", 0);
            jsonObject.put("minute", 0);
            jsonObject.put("second", 0);
            jsonObject.put("time_zone", timeZoneJson);

        } catch (Exception e) {
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
