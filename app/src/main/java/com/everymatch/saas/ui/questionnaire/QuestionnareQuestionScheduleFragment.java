package com.everymatch.saas.ui.questionnaire;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.everymatch.saas.R;
import com.everymatch.saas.server.Data.DataDate;
import com.everymatch.saas.server.Data.DataTimeZone;
import com.everymatch.saas.ui.dialog.FragmentTimeZones;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.NotifierPopup;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.BaseTextView;
import com.everymatch.saas.view.RangeTimePickerDialog;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by PopApp_laptop on 18/10/2015.
 */
public class QuestionnareQuestionScheduleFragment extends QuestionnaireQuestionBaseFragment implements View.OnClickListener {
    public final String TAG = getClass().getName();

    public static final String EXTRA_TIME_ZONE = "extra.time.zone";
    private static final int REQUEST_CODE_DIALOG_FRAGMENT = 2;
    public static final String TIME_FORMAT = "dd,MM,yyyy";


    TextView edrTimeZone;
    BaseTextView tvFromDate, tvFromTime, tvToDate, tvToTime;
    DataDate dataDateFrom = new DataDate();
    DataDate dataDateTo = new DataDate();
    BaseTextView tvTimeZoneValue;
    DataTimeZone mDataTimeZone;

    private View mToContainer;
    private View mQuestionToLine;
    private boolean wasToSet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Calendar cFrom = Calendar.getInstance();
        cFrom.set(Calendar.MINUTE, 0);

        dataDateFrom.year = cFrom.get(Calendar.YEAR);
        dataDateFrom.month = cFrom.get(Calendar.MONTH)+1;
        dataDateFrom.day = cFrom.get(Calendar.DAY_OF_MONTH);
        dataDateFrom.hour = cFrom.get(Calendar.HOUR_OF_DAY);
        dataDateFrom.minute = cFrom.get(Calendar.MINUTE);
        dataDateFrom.second = cFrom.get(Calendar.SECOND);

        // Set one rounded hour from now
        Calendar cTo = Calendar.getInstance();
        cTo.add(Calendar.HOUR_OF_DAY, 1);
        cTo.set(Calendar.MINUTE, 0);

        dataDateTo.year = cTo.get(Calendar.YEAR);
        dataDateTo.month = cTo.get(Calendar.MONTH)+1;
        dataDateTo.day = cTo.get(Calendar.DAY_OF_MONTH);
        dataDateTo.hour = cTo.get(Calendar.HOUR_OF_DAY);
        dataDateTo.minute = cTo.get(Calendar.MINUTE);
        dataDateTo.second = cTo.get(Calendar.SECOND);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        inflater.inflate(R.layout.question_schedule, (ViewGroup) view.findViewById(R.id.answers_container));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        edrTimeZone = (TextView) view.findViewById(R.id.EdrTimeZone);
        edrTimeZone.setOnClickListener(this);
        tvFromDate = (BaseTextView) view.findViewById(R.id.tvScheduleFromDate);
        tvFromDate.setOnClickListener(this);
        tvFromTime = (BaseTextView) view.findViewById(R.id.tvScheduleFromTime);
        tvFromTime.setOnClickListener(this);
        tvToDate = (BaseTextView) view.findViewById(R.id.tvScheduleToDate);
        tvToDate.setOnClickListener(this);
        tvToTime = (BaseTextView) view.findViewById(R.id.tvScheduleToTime);
        tvToTime.setOnClickListener(this);

        mQuestionToLine = view.findViewById(R.id.question_schedule_to_line);
        mQuestionToLine.setOnClickListener(this);
        mToContainer = view.findViewById(R.id.question_schedule_to_container);

        tvFromDate.setText(Utils.getDateStringFromDataDate(dataDateFrom, TIME_FORMAT));
        tvFromTime.setText(dataDateFrom.getHourString());

        tvToDate.setText(Utils.getDateStringFromDataDate(dataDateTo, TIME_FORMAT));
        tvToTime.setText(dataDateTo.getHourString());

        tvTimeZoneValue = (BaseTextView) view.findViewById(R.id.tvSettingsTimeZoneValue);
        if (mDataTimeZone != null) {
            setTimeZoneText();
        } else {
            mDataTimeZone = ds.getApplicationData().getTime_zone().get(0);
            setTimeZoneText();
        }

        if (!TextUtils.isEmpty(mQuestionAndAnswer.userAnswerStr))
            recoverAnswer();
        else if (mQuestionAndAnswer.question.default_value != null) {
            recoverDefaultAnswer();
        } else {
            setAnswer();
        }

        if (wasToSet) {
            mToContainer.setVisibility(View.VISIBLE);
        }
    }

    private void setTimeZoneText() {
        if (mDataTimeZone != null) {
            tvTimeZoneValue.setText(mDataTimeZone.utc + " " + mDataTimeZone.title);
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
            setAnswer();
            mDataTimeZone = ds.getApplicationData().getTimeZoneBySystem();
            return;
        }
        //update UI according to previews answer
        try {
            JSONObject value = mQuestionAndAnswer.userAnswerData.getJSONObject("value");
            dataDateFrom.year = value.getJSONObject("from").getInt("year");
            dataDateFrom.month = value.getJSONObject("from").getInt("month");
            dataDateFrom.day = value.getJSONObject("from").getInt("day");
            dataDateFrom.hour = value.getJSONObject("from").getInt("hour");
            dataDateFrom.minute = value.getJSONObject("from").getInt("minute");
            dataDateFrom.second = value.getJSONObject("from").getInt("second");

            dataDateTo.year = value.getJSONObject("to").getInt("year");
            dataDateTo.month = value.getJSONObject("to").getInt("month");
            dataDateTo.day = value.getJSONObject("to").getInt("day");
            dataDateTo.hour = value.getJSONObject("to").getInt("hour");
            dataDateTo.minute = value.getJSONObject("to").getInt("minute");
            dataDateTo.second = value.getJSONObject("to").getInt("second");

            if (mDataTimeZone != null) {
                setTimeZoneText();
            } else {
            /* get time zone */
            /* there is a problem here : i can't get the right timezone by the given country_code and gmt */
                JSONObject time_zone = value.getJSONObject("from").getJSONObject("time_zone");
                String countryCode = time_zone.getString("country_code");
                String gmt = time_zone.getString("gmt");

                if (!TextUtils.isEmpty(countryCode) && !TextUtils.isEmpty(gmt)) {
                    this.mDataTimeZone = ds.getApplicationData().getTimeZoneByCountryCodeAndGmt(countryCode, gmt);
                    if (mDataTimeZone != null) {
                        setTimeZoneText();
                    } else {
                        this.mDataTimeZone = ds.getApplicationData().getTimeZoneBySystem();
                        setTimeZoneText();
                    }
                }
            }
            tvFromDate.setText(dataDateFrom.getYearString());
            tvFromTime.setText(dataDateFrom.getHourString());

            tvToDate.setText(dataDateTo.getYearString());
            tvToTime.setText(dataDateTo.getHourString());

        } catch (Exception ex) {
            EMLog.e(TAG, ex.getMessage());
        }

        setAnswer(mQuestionAndAnswer.userAnswerStr);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.EdrTimeZone:
                FragmentTimeZones dialogTimeZones = new FragmentTimeZones();
                dialogTimeZones.setTargetFragment(this, REQUEST_CODE_DIALOG_FRAGMENT);
                ((QuestionnaireActivity) getActivity()).replaceFragment(R.id.fragment_container_full, dialogTimeZones,
                        TAG, true, null, R.anim.enter_from_right, R.anim.exit_to_left,
                        R.anim.enter_from_left, R.anim.exit_to_right);
                break;
            case R.id.tvScheduleFromDate:
                DatePickerDialog fromDialog = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dataDateFrom.year = year;
                        dataDateFrom.month = monthOfYear;
                        dataDateFrom.day = dayOfMonth;
                        tvFromDate.setText(Utils.getDateStringFromDataDate(dataDateFrom, TIME_FORMAT));

                        if (wasToSet) {

                            // Move the "to" date to one hour late in the same day
                            if (Utils.isAfterDate(dataDateFrom, dataDateTo)) {
                                dataDateTo.day = dataDateFrom.day;
                                dataDateTo.month = dataDateFrom.month+1;
                                dataDateTo.year = dataDateFrom.year;
                                dataDateTo.hour = dataDateFrom.hour + 1;
                                dataDateTo.minute = dataDateFrom.minute;
                                dataDateTo.second = dataDateFrom.second;

                                tvToDate.setText(Utils.getDateStringFromDataDate(dataDateTo, TIME_FORMAT));
                                tvToTime.setText(dataDateTo.getHourString());
                            }
                        }

                        setAnswer();
                    }
                }, dataDateFrom.year, dataDateFrom.month, dataDateFrom.day);
                fromDialog.getDatePicker().setMinDate(new Date().getTime() - 1000);
                fromDialog.show();
                break;
            case R.id.tvScheduleFromTime:

                RangeTimePickerDialog fromHourDialog = new RangeTimePickerDialog(mActivity, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        dataDateFrom.hour = hourOfDay;
                        dataDateFrom.minute = minute;
                        tvFromTime.setText(dataDateFrom.getHourString());

                        if (wasToSet) {

                            // Move the "to" date to one hour late
                            if (Utils.isAfterOrSameHour(dataDateFrom, dataDateTo)) {
                                dataDateTo.hour = dataDateFrom.hour + 1;
                                dataDateTo.minute = dataDateFrom.minute;
                                dataDateTo.second = dataDateFrom.second;
                                tvToTime.setText(dataDateTo.getHourString());
                            }
                        }

                        setAnswer();

                    }
                }, dataDateFrom.hour, dataDateFrom.minute, true);

                fromHourDialog.show();

                break;
            case R.id.question_schedule_to_line:
            case R.id.tvScheduleToDate:

                int year = dataDateTo.year;
                int month = dataDateTo.month;
                int day = dataDateTo.day;

                if (!wasToSet) {
                    year = dataDateFrom.year;
                    month = dataDateFrom.month;
                    day = dataDateFrom.day;
                }

                DatePickerDialog toDialog = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

                        if (!wasToSet) { // First "to" value will be to one hour from "from" value
                            dataDateTo.hour = dataDateFrom.hour + 1;
                            dataDateTo.minute = dataDateFrom.minute;
                            tvToTime.setText(dataDateTo.getHourString());
                        }

                        wasToSet = true;

                        mToContainer.setVisibility(View.VISIBLE);
                        dataDateTo.year = year;
                        dataDateTo.month = monthOfYear+1;
                        dataDateTo.day = dayOfMonth;
                        tvToDate.setText(Utils.getDateStringFromDataDate(dataDateTo, TIME_FORMAT));
                        setAnswer();
                    }
                }, year, month, day);

                // Set minimum date to "from" date
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, dataDateFrom.year);
                calendar.set(Calendar.MONTH, dataDateFrom.month);
                calendar.set(Calendar.DAY_OF_MONTH, dataDateFrom.day);
                toDialog.getDatePicker().setMinDate(calendar.getTimeInMillis() - 1000);
                toDialog.show();

                break;
            case R.id.tvScheduleToTime:
                new TimePickerDialog(mActivity, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        // To date can't be after From date
                        if (Utils.isDataDateToday(dataDateFrom) && (hourOfDay < dataDateFrom.hour ||
                                hourOfDay == dataDateFrom.hour && minute <= dataDateFrom.minute)) {
                            showInvalidTimePopup();
                            return;
                        }

                        dataDateTo.hour = hourOfDay;
                        dataDateTo.minute = minute;
                        tvToTime.setText(dataDateTo.getHourString());
                        setAnswer();
                    }
                }, dataDateTo.hour, dataDateTo.minute, true).show();
                break;
        }
    }

    private void showInvalidTimePopup() {
        NotifierPopup.Builder builder = new NotifierPopup.Builder(getActivity());
        builder.setMessage(R.string.From_Field_Value_Must_Be_Before_To_Field_Value);
        builder.setType(NotifierPopup.TYPE_ERROR);
        builder.setView(mToContainer);
        builder.setTopOffset(Utils.dpToPx(25)); // Status bar height
        builder.show();
    }

    @Override
    public JSONObject createJsonObject() {
        JSONObject from = createDateJsonObject(dataDateFrom);
        JSONObject to = createDateJsonObject(dataDateTo);
        try {
            JSONObject out = new JSONObject();
            /*if (mDataTimeZone != null) {
                JSONObject time_zone = new JSONObject();
                time_zone.put("country_code", mDataTimeZone.country_code);
                time_zone.put("gmt", mDataTimeZone.utc);

                from.put("time_zone", time_zone);
                to.put("time_zone", time_zone);
            }*/
            out.put("from", from);
            out.put("to", to);
            return out;

        } catch (Exception e) {
            Log.e(getClass().getName(), "Error: can't create JsonObject in setAnswer(): " + e.getMessage());
            return new JSONObject();
        }
    }

    void setAnswer() {
        String s = dataDateFrom.getYearString() + " - " + dataDateFrom.getHourString();
        super.setAnswer(s);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_DIALOG_FRAGMENT:
                DataTimeZone dataTimeZone = (DataTimeZone) data.getSerializableExtra(EXTRA_TIME_ZONE);
                if (dataTimeZone != null) {
                    mDataTimeZone = dataTimeZone;
                    setTimeZoneText();
                }
                break;
        }
    }

    public JSONObject createDateJsonObject(DataDate dataDate) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject timeZoneJson = new JSONObject();
            if (mDataTimeZone == null) {
                timeZoneJson.put("country_code", mActivity.getResources().getConfiguration().locale.getCountry());
                timeZoneJson.put("gmt", new GregorianCalendar().getTimeZone().getRawOffset() / 60 / 60 / 1000);
            } else {
                timeZoneJson.put("country_code", mDataTimeZone.country_code);
                timeZoneJson.put("gmt", "" + mDataTimeZone.getGmt());
            }

            jsonObject.put("year", dataDate.year);
            jsonObject.put("month", dataDate.month);
            jsonObject.put("day", dataDate.day);
            jsonObject.put("hour", dataDate.hour);
            jsonObject.put("minute", dataDate.minute);
            jsonObject.put("second", dataDate.second);
            jsonObject.put("time_zone", timeZoneJson);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
