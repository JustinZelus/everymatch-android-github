package com.everymatch.saas.ui.questionnaire;

import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.server.Data.DataAnswer;
import com.everymatch.saas.server.Data.DataLocation2;
import com.everymatch.saas.ui.questionnaire.base.QuestionnaireQuestionBaseFragment;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.Utils;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuestionnaireQuestionSelectionFragment extends QuestionnaireQuestionBaseFragment implements CompoundButton.OnCheckedChangeListener {
    public static final String TAG = "QuestionSelection";
    public static final String EXTRA_QUESTION_INDEX = "index";
    public static final String ANSWER_SEPARATOR = ", ";

    private CompoundButton mCheckBoxes[];
    private Drawable mCheckedDrawable;
    private JSONObject mAnswerJsonObject;
    boolean isMultiple;
    private int index;

    public static QuestionnaireQuestionSelectionFragment newInstance(int index) {
        QuestionnaireQuestionSelectionFragment fragment = new QuestionnaireQuestionSelectionFragment();
        Bundle args = new Bundle();
        args.putInt(EXTRA_QUESTION_INDEX, index);
        fragment.setArguments(args);
        return fragment;
    }

    public QuestionnaireQuestionSelectionFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCheckedDrawable = getResources().getDrawable(R.drawable.ic_action_yes);
        Bitmap bitmap = ((BitmapDrawable) mCheckedDrawable).getBitmap();
        mCheckedDrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, Utils.dpToPx(20), Utils.dpToPx(20), true));
        mCheckedDrawable.setColorFilter(new PorterDuffColorFilter(DataStore.getInstance().getIntColor(EMColor.PRIMARY), PorterDuff.Mode.MULTIPLY));
        index = getArguments().getInt(EXTRA_QUESTION_INDEX);
        isMultiple = mQuestionAndAnswer.question.multiple;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        inflater.inflate(R.layout.question_selection, (ViewGroup) view.findViewById(R.id.answers_container));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        LinearLayout container = (LinearLayout) view.findViewById(R.id.selections_options_container);

        DataAnswer answers[] = mQuestionAndAnswer.question.answers;

        String[] answersStr = new String[0];

        if (answers == null || answers.length == 0) {
            if (!TextUtils.isEmpty(mQuestionAndAnswer.question.range)) {
                String[] rangeStr = mQuestionAndAnswer.question.range.split(",");
                int min = Integer.parseInt(rangeStr[0]);
                int max = Integer.parseInt(rangeStr[1]);
                float step = mQuestionAndAnswer.question.step;
                if (step == 0)
                    step = 1;

                int size = (int) (((max - min) / step) + 1);
                answersStr = new String[size];
                int value = min;
                for (int i = 0; i < size; ++i) {
                    answersStr[i] = Integer.toString(value);
                    value += step;
                }
            } else {
                Log.e(TAG, "no answers!");
                setAnswer("");
            }
        } else {
            answersStr = new String[answers.length];
            for (int i = 0; i < answers.length; ++i) {
                answersStr[i] = answers[i].text_title;
            }
        }

//        addLineView(container);

        // create the views
        mCheckBoxes = new CompoundButton[answersStr.length];
        for (int i = 0; i < answersStr.length; ++i) {
            CompoundButton checkBox = isMultiple ?
                    (CheckBox) mActivity.getLayoutInflater().inflate(R.layout.checkbox, null) :
                    (RadioButton) mActivity.getLayoutInflater().inflate(R.layout.radiobutton, null);
            checkBox.setButtonDrawable(null);

            mCheckBoxes[i] = checkBox;
//            checkBox.getBackgroundTintList(new ColorStateList());
            checkBox.setText(answersStr[i]);
            checkBox.setOnCheckedChangeListener(this);
            container.addView(checkBox);

            int padding = getResources().getDimensionPixelOffset(R.dimen.margin_xs);
            int rightPadding = getResources().getDimensionPixelOffset(R.dimen.margin_s);

            checkBox.setPadding(padding, padding, rightPadding, padding);

            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) checkBox.getLayoutParams();
            layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;

            //LinearLayout.LayoutParams.MATCH_PARENT;
            checkBox.setLayoutParams(layoutParams);

            addLineView(container);
        }

        if (!TextUtils.isEmpty(mQuestionAndAnswer.userAnswerStr))
            recoverAnswer();
        else {
            if (mQuestionAndAnswer.question.default_value != null)
                recoverDefaultAnswer();
        }
    }


    private void recoverAnswer() {
        int markAnswerIndex = 0;
        final String markedAnswers[] = mQuestionAndAnswer.userAnswerStr.split(ANSWER_SEPARATOR);

        for (int i = 0; i < mCheckBoxes.length; i++) {
            if (markedAnswers.length == markAnswerIndex)
                return;

            if (mCheckBoxes[i].getText().equals(markedAnswers[markAnswerIndex])) {
                mCheckBoxes[i].setChecked(true);
                ++markAnswerIndex;
            }
        }
    }

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

    private void addLineView(LinearLayout container) {
        // add line separator
        View lineView = mActivity.getLayoutInflater().inflate(R.layout.line_view, null);
        container.addView(lineView);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) lineView.getLayoutParams();
        layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT;
        layoutParams.height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                1, getResources().getDisplayMetrics());
    }

    @Override
    public void setAnswer(String answer) {
        super.setAnswer(answer);
        //TODO
    }

    public String createIdsList() {
        String result = "";
        for (int i = 0; i < mCheckBoxes.length; i++) {
            if (mCheckBoxes[i].isChecked()) {
                result += mQuestionAndAnswer.question.answers[i].answer_id + ",";
            }
        }

        if (!TextUtils.isEmpty(result))
            result = result.substring(0, result.length() - 1);

        return result;
    }

    @Override
    public String createLocationJsonObject(String locationStr) {
//        return super.createLocationJsonObject(locationStr);
        return mAnswerJsonObject.toString();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String answer = "";
        boolean isAnyChecked = false;
        CompoundButton checkBox;
        for (int i = 0; i < mCheckBoxes.length; ++i) {
            checkBox = mCheckBoxes[i];
            if (checkBox.isChecked()) {
                isAnyChecked = true;
                answer += checkBox.getText() + ANSWER_SEPARATOR;

                // prepare json object
                if (("location".equals(mQuestionAndAnswer.question.question_type) || "event_list".equals(mQuestionAndAnswer.question.question_type)) &&
                        mQuestionAndAnswer.question.answers != null && mQuestionAndAnswer.question.answers.length > 0) {

                    try {
                        JSONObject locationJson = Utils.convertKeyValueToJSON((LinkedTreeMap)
                                mQuestionAndAnswer.question.answers[i].value);

                        DataLocation2 dataLocation = new DataLocation2();
                        dataLocation.coordinates = new DataLocation2.DataCoordinate();

                        JSONObject coordinateJson = new JSONObject(locationJson.get("coordinate").toString());
                        dataLocation.coordinates.type = coordinateJson.get("type").toString();
                        dataLocation.coordinates.value = new float[1][2];
                        String coordinatesStr = coordinateJson.get("value").toString();
                        int coordinatesSeperatorIndex = coordinatesStr.indexOf(',');
                        dataLocation.coordinates.value[0][0] = Float.parseFloat(coordinatesStr.substring(2, coordinatesSeperatorIndex - 1));
                        dataLocation.coordinates.value[0][1] = Float.parseFloat(coordinatesStr.substring(coordinatesSeperatorIndex + 1, coordinatesStr.length() - 2));

                        dataLocation.distance_value = Integer.parseInt(locationJson.get("distance_value").toString());
                        dataLocation.distance_units = locationJson.get("distance_units").toString();

                        dataLocation.country_code = locationJson.get("country_code").toString();

                        //dataLocation.country_name = locationJson.get("country_name").toString();;
                        dataLocation.city_code = locationJson.get("city_code").toString();

                        dataLocation.text_address = new HashMap<>(); //JSONObject();
                        dataLocation.text_address.put(ds.getCulture(), locationJson.get("text_address").toString());

                        mAnswerJsonObject = new JSONObject(new Gson().toJson(dataLocation));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        if (isAnyChecked)
            setAnswer(answer.substring(0, answer.length() - ANSWER_SEPARATOR.length()));
        else clearAnswer();

        buttonView.setCompoundDrawablesWithIntrinsicBounds(null, null,
                isChecked ? mCheckedDrawable : null, null);

        buttonView.setTextColor(
                isChecked ? DataStore.getInstance().getIntColor(EMColor.PRIMARY) :
                        DataStore.getInstance().getIntColor(EMColor.NIGHT));
    }


    @Override
    public JSONObject createDateJsonObject(String dateStr) {
        JSONObject jsonObject = super.createDateJsonObject(dateStr);

        int selectedIndex = 0;
        for (; selectedIndex < mCheckBoxes.length; ++selectedIndex) {
            if (mCheckBoxes[selectedIndex].isChecked()) {
                break;
            }
        }

        if (selectedIndex >= mCheckBoxes.length) {
            return jsonObject;
        }

        LinkedTreeMap linkedTreeMap = (LinkedTreeMap) ((LinkedTreeMap) mQuestionAndAnswer.question.answers[0].value).get("schedule");
        LinkedTreeMap timeZoneLinkedTreeMap = (LinkedTreeMap) linkedTreeMap.get("time_zone");

        //TODO: fix the format!
        try {
            JSONObject timeZoneJson = new JSONObject();
            timeZoneJson.put("country_code", timeZoneLinkedTreeMap.get("country_code"));
            timeZoneJson.put("gmt", timeZoneLinkedTreeMap.get("gmt"));
//            timeZoneJson.put("country_code", mActivity.getResources().getConfiguration().locale.getCountry());
//            timeZoneJson.put("gmt", new GregorianCalendar().getTimeZone().getRawOffset()/60/60/1000);

            String year = linkedTreeMap.get("year").toString();
            String month = linkedTreeMap.get("month").toString();
            String day = linkedTreeMap.get("day").toString();
            String hour = linkedTreeMap.get("hour").toString();
            String minute = linkedTreeMap.get("minute").toString();
            String second = linkedTreeMap.get("second").toString();

            jsonObject.put("year", TextUtils.isEmpty(year) ? 0 : Integer.parseInt(year));
            jsonObject.put("month", TextUtils.isEmpty(month) ? 0 : Integer.parseInt(month));
            jsonObject.put("day", TextUtils.isEmpty(day) ? 0 : Integer.parseInt(day));
            jsonObject.put("hour", TextUtils.isEmpty(hour) ? 0 : Float.parseFloat(hour));
            jsonObject.put("minute", TextUtils.isEmpty(minute) ? 0 : Float.parseFloat(minute));
            jsonObject.put("second", TextUtils.isEmpty(second) ? 0 : Float.parseFloat(second));
            jsonObject.put("time_zone", timeZoneJson);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObject;
    }
}
