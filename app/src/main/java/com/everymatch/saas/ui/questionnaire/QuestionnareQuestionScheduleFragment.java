package com.everymatch.saas.ui.questionnaire;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.everymatch.saas.ui.questionnaire.base.QuestionnaireQuestionBaseFragment;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.NotifierPopup;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.BaseTextView;
import com.everymatch.saas.view.RangeTimePickerDialog;

import org.json.JSONObject;

import java.util.Calendar;

/**
 * Created by PopApp_laptop on 18/10/2015.
 */
public class QuestionnareQuestionScheduleFragment extends QuestionnaireQuestionBaseFragment implements View.OnClickListener {
    public final String TAG = getClass().getName();

    public static final String EXTRA_TIME_ZONE = "extra.time.zone";
    private static final int REQUEST_CODE_DIALOG_FRAGMENT = 2;
    //public static final String TIME_FORMAT = "dd.MM.yyyy";
    public static final String TIME_FORMAT = "EEE, MMM d, yyyy";

    //Data
    DataDate dataDateFrom = new DataDate();
    DataDate dataDateTo = new DataDate();
    DataTimeZone mDataTimeZone;
    private int timeZoneIndex;


    //Views
    TextView edrTimeZone;
    BaseTextView tvFromDate, tvFromTime, tvToDate, tvToTime;
    BaseTextView tvTimeZoneValue;
    BaseTextView tvOptional;
    private View mToContainer;
    private View mQuestionToLine;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Calendar cFrom = Calendar.getInstance();
        cFrom.set(Calendar.MINUTE, 0);
        cFrom.add(Calendar.HOUR, 1);

        dataDateFrom.year = cFrom.get(Calendar.YEAR);
        dataDateFrom.month = cFrom.get(Calendar.MONTH) + 1;
        dataDateFrom.day = cFrom.get(Calendar.DAY_OF_MONTH);
        dataDateFrom.hour = cFrom.get(Calendar.HOUR_OF_DAY);
        dataDateFrom.minute = cFrom.get(Calendar.MINUTE);
        dataDateFrom.second = cFrom.get(Calendar.SECOND);

        // Set one rounded hour from now
        Calendar cTo = Calendar.getInstance();
        cTo.add(Calendar.HOUR_OF_DAY, 1);
        cTo.set(Calendar.MINUTE, 0);

        dataDateTo.year = cTo.get(Calendar.YEAR);
        dataDateTo.month = cTo.get(Calendar.MONTH) + 1;
        dataDateTo.day = cTo.get(Calendar.DAY_OF_MONTH);
        dataDateTo.hour = 23;
        dataDateTo.minute = 59;
        dataDateTo.second = 59;

        mDataTimeZone = ds.getUser().user_settings.getTime_zone();
        if (mDataTimeZone != null)
            timeZoneIndex = ds.getApplicationData().getTimeZoneIndex(mDataTimeZone);
        //mDataTimeZone = ds.getApplicationData().getTime_zone().get(timeZoneIndex);
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
        tvOptional = (BaseTextView) view.findViewById(R.id.tvScheduleOptional);
        tvOptional.setText(dm.getResourceText(R.string.Set_End_Time));
        tvOptional.setOnClickListener(this);

        mQuestionToLine = view.findViewById(R.id.question_schedule_to_line);
        mQuestionToLine.setOnClickListener(this);
        mToContainer = view.findViewById(R.id.question_schedule_to_container);

        tvTimeZoneValue = (BaseTextView) view.findViewById(R.id.tvSettingsTimeZoneValue);
        tvTimeZoneValue.setOnClickListener(this);

        updateUi();
        recoverAnswer();
        setAnswer();
    }

    @Override
    protected void setHeader() {
        super.setHeader();

        if (mActivity.isInEditMode())
            mHeader.setSaveCancelMode(dm.getResourceText(R.string.Edit_Event_Schedule));
    }

    @Override
    public void onStart() {
        super.onStart();
        //allow next always
        setHeader();
        setTitleEnabled(true);
    }

    private void updateUi() {
        tvFromDate.setText(Utils.getDateStringFromDataDate(dataDateFrom, TIME_FORMAT));
        tvFromTime.setText(dataDateFrom.getHourString());

        tvToDate.setText(Utils.getDateStringFromDataDate(dataDateTo, TIME_FORMAT));
        tvToTime.setText(dataDateTo.getHourString());

        setTimeZoneText();
    }

    private void setTimeZoneText() {
        if (mDataTimeZone != null) {
            tvTimeZoneValue.setText(mDataTimeZone.utc + " " + mDataTimeZone.title);
        }
    }

    @Override
    public void recoverDefaultAnswer() {
        recoverAnswer();
    }

    private void recoverAnswer() {
        if (mQuestionAndAnswer.userAnswerData == null || !mQuestionAndAnswer.userAnswerData.has("value"))
            return;
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

            //hide optional marl if user already clicked it
            if (!(dataDateTo.hour == 23 && dataDateTo.minute == 59))
                showOptionalFiled(false);

            /* get time zone */
            /* there is a problem here : i can't get the right timezone by the given country_code and gmt */
            JSONObject time_zone = value.getJSONObject("from").getJSONObject("time_zone");
            //String countryCode = time_zone.getString("country_code");
            //String gmt = time_zone.getString("gmt");
            timeZoneIndex = time_zone.getInt("index");
            if (mDataTimeZone == null)
                mDataTimeZone = ds.getApplicationData().getTime_zone().get(timeZoneIndex);

            updateUi();

        } catch (Exception ex) {
            EMLog.e(TAG, ex.getMessage());
        }

        setAnswer();
    }

    private void showOptionalFiled(boolean addHour) {
        mToContainer.setVisibility(View.VISIBLE);
        tvOptional.setVisibility(View.GONE);

        if (!addHour)
            return;

        // update to to be one hour after from
        Calendar calendar = Utils.getCalendarFromDataDate(dataDateFrom);
        calendar.add(Calendar.HOUR_OF_DAY, 1);

        dataDateTo.year = calendar.get(Calendar.YEAR);
        dataDateTo.month = calendar.get(Calendar.MONTH);
        dataDateTo.day = calendar.get(Calendar.DAY_OF_MONTH);
        dataDateTo.hour = calendar.get(Calendar.HOUR_OF_DAY);
        dataDateTo.minute = calendar.get(Calendar.MINUTE);
        dataDateTo.second = calendar.get(Calendar.SECOND);

        updateUi();
        setAnswer();
    }

    @Override
    public void onClick(View v) {
        Calendar calendar;
        Calendar calMin;
        switch (v.getId()) {
            case R.id.tvScheduleOptional:
                showOptionalFiled(true);

                break;
            case R.id.tvSettingsTimeZoneValue:
                FragmentTimeZones dialogTimeZones = FragmentTimeZones.getInstance(mDataTimeZone, timeZoneIndex);
                dialogTimeZones.setTargetFragment(this, REQUEST_CODE_DIALOG_FRAGMENT);
                ((QuestionnaireActivity) getActivity()).replaceFragment(R.id.fragment_container_full, dialogTimeZones,
                        FragmentTimeZones.TAG, true, null, R.anim.enter_from_right, R.anim.exit_to_left,
                        R.anim.enter_from_left, R.anim.exit_to_right);
                break;
            case R.id.tvScheduleFromDate:
                DatePickerDialog fromDialog = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        /*if (!dateInputCheck(true, year, monthOfYear, dayOfMonth)) {
                            showInvalidTimePopup();
                            return;
                        }*/
                        //date is fine, check hour
                        dataDateFrom.year = year;
                        dataDateFrom.month = monthOfYear + 1;
                        dataDateFrom.day = dayOfMonth;

                        //if from later than to
                        Utils.getDateDromDataDate(dataDateFrom).after(Utils.getDateDromDataDate(dataDateTo));
                        {
                            dataDateTo.year = dataDateFrom.year;
                            dataDateTo.month = dataDateFrom.month;
                            dataDateTo.day = dataDateFrom.day;
                            if (tvOptional.getVisibility() != View.VISIBLE) {
                                fixHoursIfNeeded();
                            }
                        }

                        updateUi();
                        setAnswer();
                    }
                }, dataDateFrom.year, dataDateFrom.month - 1, dataDateFrom.day);
                //set min date to now
                //fromDialog.getDatePicker().setMinDate(new Date().getTime() - 1000);
                calMin = Calendar.getInstance();
                calMin.set(Calendar.SECOND, calMin.getMinimum(Calendar.SECOND));
                calMin.set(Calendar.HOUR, calMin.getMinimum(Calendar.HOUR_OF_DAY));
                fromDialog.getDatePicker().setMinDate(calMin.getTimeInMillis() + 1000);
                //set max date to dateTo
                // Calendar calMax = Utils.getCalendarFromDataDate(dataDateTo);
                //calMax.add(Calendar.MONTH, -1);
                Calendar m = Calendar.getInstance();
                m.add(Calendar.YEAR, 5);
                fromDialog.getDatePicker().setMaxDate(m.getTime().getTime() - 10000);
                fromDialog.show();
                break;
            case R.id.tvScheduleFromTime:
                RangeTimePickerDialog fromHourDialog = new RangeTimePickerDialog(mActivity, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        dataDateFrom.hour = hourOfDay;
                        dataDateFrom.minute = minute;

                        if (dataDateFrom.isSameDay(dataDateTo)) {
                            if (tvOptional.getVisibility() != View.VISIBLE) {
                                fixHoursIfNeeded();
                            }
                        }
                        updateUi();
                        setAnswer();
                    }
                }, dataDateFrom.hour, dataDateFrom.minute, true);
                fromHourDialog.show();

                break;
            case R.id.question_schedule_to_line:
            case R.id.tvScheduleToDate:

                DatePickerDialog toDialog = new DatePickerDialog(mActivity, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        dataDateTo.year = year;
                        dataDateTo.month = monthOfYear + 1;
                        dataDateTo.day = dayOfMonth;

                        if (dataDateFrom.isSameDay(dataDateTo)) {
                            Utils.getDateDromDataDate(dataDateFrom).after(Utils.getDateDromDataDate(dataDateTo));
                            fixHoursIfNeeded();
                        }

                        updateUi();
                        setAnswer();

                    }
                }, dataDateTo.year, dataDateTo.month - 1, dataDateTo.day);

                // Set minimum date to "from" date
                try {
                    calMin = Utils.getCalendarFromDataDate(dataDateFrom);
                    calMin.set(Calendar.SECOND, calMin.getMinimum(Calendar.SECOND));
                    calMin.set(Calendar.HOUR, calMin.getMinimum(Calendar.HOUR_OF_DAY));
                    calMin.add(Calendar.MONTH, -1);
                    toDialog.getDatePicker().setMinDate(calMin.getTimeInMillis() + 1000);
                } catch (Exception ex) {
                    EMLog.e(TAG, ex.getMessage());
                }

                //set max to 5 year
                Calendar max = Calendar.getInstance();
                max.add(Calendar.YEAR, 5);
                toDialog.getDatePicker().setMaxDate(max.getTimeInMillis() - 1000);

                toDialog.show();

                break;
            case R.id.tvScheduleToTime:
                new TimePickerDialog(mActivity, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        dataDateTo.hour = hourOfDay;
                        dataDateTo.minute = minute;
                        if (dataDateFrom.isSameDay(dataDateTo)) {
                            if (!Utils.getDateDromDataDate(dataDateFrom).before(Utils.getDateDromDataDate(dataDateTo)))
                                fixHoursIfNeeded();
                        }

                        updateUi();
                        setAnswer();
                    }
                }, dataDateTo.hour, dataDateTo.minute, true).show();
                break;
        }
    }

    private void fixHoursIfNeeded() {
        Calendar to = Utils.getCalendarFromDataDate(dataDateFrom);

        to.add(Calendar.HOUR_OF_DAY, 1);

        dataDateTo.year = to.get(Calendar.YEAR);
        dataDateTo.month = to.get(Calendar.MONTH);
        dataDateTo.day = to.get(Calendar.DAY_OF_MONTH);
        dataDateTo.hour = to.get(Calendar.HOUR_OF_DAY);
        dataDateTo.minute = to.get(Calendar.MINUTE);
        dataDateTo.second = to.get(Calendar.SECOND);


/*
        if (dataDateFrom.hour >= dataDateTo.hour) {
            if (dataDateTo.hour > 1) {
                dataDateFrom.hour = dataDateTo.hour - 1;
            } else {
                //set hours to now
                Calendar cFrom = Calendar.getInstance();
                dataDateFrom.hour = cFrom.get(Calendar.HOUR_OF_DAY);
                dataDateFrom.minute = 0;
                dataDateFrom.second = 0;

                // Set one rounded hour from now
                cFrom.add(Calendar.HOUR_OF_DAY, 1);
                dataDateTo.hour = cFrom.get(Calendar.HOUR_OF_DAY);
                dataDateTo.minute = 0;
            }
        }*/

    }

    /**
     * return's true if to is after from
     */
    private boolean dateInputCheck(boolean dataIsFrom, int year, int monthOfYear, int dayOfMonth) {
        if (dataIsFrom) {
            // dataDataFrom values
            if (year > dataDateTo.year)
                return false;
            if (year < dataDateTo.year)
                return true;

            // this is the same year
            if (monthOfYear > dataDateTo.month)
                return false;
            if (monthOfYear < dataDateTo.month)
                return true;

            //this is also the same month
            if (dayOfMonth > dataDateTo.day)
                return false;

        } else {
            // dataDataTo values
            if (dataDateFrom.year > year)
                return false;
            if (dataDateFrom.year < year)
                return true;

            // this is the same year
            if (dataDateFrom.month > monthOfYear)
                return false;
            if (dataDateFrom.month < monthOfYear)
                return true;

            //this is also the same month
            if (dataDateTo.day > dayOfMonth)
                return false;

        }
        return true;
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
        if (tvOptional.getVisibility() == View.VISIBLE) {
            //user has not set to date
            dataDateTo.year = dataDateFrom.year;
            dataDateTo.month = dataDateFrom.month;
            dataDateTo.day = dataDateFrom.day;
            dataDateTo.hour = 23;
            dataDateTo.minute = 59;
            dataDateTo.second = 59;
        }
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
                    timeZoneIndex = ds.getApplicationData().getTimeZoneIndex(mDataTimeZone);
                    setTimeZoneText();
                }
                break;
        }
    }

    public JSONObject createDateJsonObject(DataDate dataDate) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject timeZoneJson = new JSONObject();

            timeZoneJson.put("country_code", mDataTimeZone.country_code);
            timeZoneJson.put("gmt", "" + mDataTimeZone.getGmt());
            timeZoneJson.put("index", "" + timeZoneIndex);

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
