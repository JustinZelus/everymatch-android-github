package com.everymatch.saas.util;

import android.text.TextUtils;
import android.util.Log;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.FormType;
import com.everymatch.saas.client.data.QuestionType;
import com.everymatch.saas.server.Data.DataAnswer;
import com.everymatch.saas.server.Data.DataDate;
import com.everymatch.saas.server.Data.DataLocation;
import com.everymatch.saas.server.Data.DataQuestion;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by dors on 11/3/15.
 */
public class QuestionUtils {

    private static final String TAG = QuestionUtils.class.getSimpleName();

    public static void createJsonAnswerByQuestionType(String question_type, JSONObject
            jsonObject, String answer, DataAnswer[] answers) throws JSONException {
        switch (question_type) {
            case QuestionType.NUMBER:
            case QuestionType.GENDER:
            case QuestionType.GENDER_RANGE:
            case QuestionType.NUMBER_RANGE:
            case QuestionType.SCALE:
            case QuestionType.ABOUT_ME:
            case QuestionType.IMAGE_UPLOAD:
                /*case "event_schedule":*/
                jsonObject.put("value", answer);
                break;
            case QuestionType.AGE:
            case QuestionType.DATE:
            case QuestionType.EVENT_DATE:
                jsonObject.put("value", createDateJsonObject(answer));
                break;
            case QuestionType.LOCATION:
            case QuestionType.MY_LOCATION:
            case QuestionType.EVENT_LOCATION:
            case QuestionType.EVENT_LIST:
                jsonObject.put("value", createLocationJsonObject(answer));
                break;
            case QuestionType.IDS:
                jsonObject.put("value", createIdsList(answer, answers));
                break;
            case QuestionType.EVENT_SCHEDULE:
                jsonObject.put("value", createJsonObject());
                break;

            default:
                Log.e(TAG, "Question Type: " + question_type + " was not found");
                break;
        }
    }

    public static JSONObject createDateJsonObject(String dateStr) {
        JSONObject jsonObject = new JSONObject();
        return jsonObject;
    }

    public static JSONObject createJsonObject() {
        JSONObject jsonObject = new JSONObject();
        return jsonObject;
    }

    public static JSONObject createLocationJsonObject(String locationStr) {
        JSONObject jsonObject = new JSONObject();
        return jsonObject;
    }

    /**
     * this function creates id list according to the user answers
     */
    public static String createIdsList(String answer, DataAnswer[] answers) {
        ArrayList a = new ArrayList<>(Arrays.asList(answer.trim().split("[\\s]*,[\\s]*")));

        ArrayList output = new ArrayList<>();
        String out = "";
        for (DataAnswer ans : answers) {
            if (a.contains(ans.text_title.trim())) {
                output.add(ans.answer_id);
                out += ans.answer_id + ",";
            }
        }
        if (out.endsWith(","))
            out = out.substring(0, out.length() - 1);

        return out;
    }

    /**
     * @param question
     * @param answer
     * @return the text to show on
     */
    public static String getAnsweredTextByAnswer(DataQuestion question, DataAnswer answer) {

        String value = "";

        if (answer == null || answer.value == null) {
            value = DataManager.getInstance().getResourceText(R.string.Unanswered);
        } else {

            try {
                Gson gson;

                switch (question.form_type) {

                    case FormType.LOCATION:
                        gson = new Gson();
                        DataLocation location = gson.fromJson(gson.toJson(answer.value), DataLocation.class);

                        if (!TextUtils.isEmpty(location.text_address)) {
                            value = location.text_address;
                        } else if (!TextUtils.isEmpty(location.city)) {
                            value += location.city;

                            if (!TextUtils.isEmpty(location.country_code)) {
                                value += ", " + location.country_code;
                            }

                        } else if (!TextUtils.isEmpty(location.country_code)) {
                            value += location.country_code;
                        }
                        break;

                    case FormType.DATE_TIME:
                        gson = new Gson();
                        DataDate dateTime = gson.fromJson(gson.toJson(answer.value), DataDate.class);
                        value = dateTime.month + "/" + dateTime.day + "/" + dateTime.year + " " + dateTime.hour + ":" + dateTime.minute;
                        break;

                    case FormType.DATE:
                        gson = new Gson();
                        DataDate date = gson.fromJson(gson.toJson(answer.value), DataDate.class);
                        value = date.month + "/" + date.day + "/" + date.year;
                        break;

                    case FormType.LIST:
                    case FormType.BUTTON_SELECTOR:

                        if (question.question_type.equals(QuestionType.GENDER)) {
                            value = (String) answer.value;
                        } else {
                            ArrayList<String> answered = new ArrayList<>();

                            for (DataAnswer answers : question.answers) {
                                if (((String) answer.value).contains(answers.answer_id + "")) {
                                    answered.add(answers.text_title);
                                }
                            }

                            if (!Utils.isArrayListEmpty(answered)) {
                                value = TextUtils.join(", ", answered);
                            }
                        }

                        break;

                    default:
                        value = (String) answer.value;
                        break;
                }
            } catch (Exception e) {
                EMLog.e("Parse error", e.getMessage());
            }
        }

        return TextUtils.isEmpty(value) ? DataManager.getInstance().getResourceText(R.string.Unanswered) : value;
    }

    public static JSONObject setAnswerData(String questionType, DataAnswer answer) {
        JSONObject object = null;
        try {
            Gson gson = new Gson();

            object = new JSONObject(gson.toJson(answer));

            switch (questionType) {
                case QuestionType.LOCATION:
                case QuestionType.MY_LOCATION:
                case QuestionType.EVENT_LOCATION:
                case QuestionType.EVENT_LIST:
                    JSONObject valueObject = object.getJSONObject("value");
                    DataLocation dataLocation = gson.fromJson(valueObject.toString(), DataLocation.class);
                    JSONObject addressJson = new JSONObject();
                    addressJson.put(DataStore.getInstance().getCulture(), dataLocation.text_address);
                    //addressJson.put(EverymatchApplication.getContext().getString(R.string.host_language), dataLocation.text_address);
                    valueObject.put("text_address", addressJson);
                    break;
            }
        } catch (Exception e) {
            EMLog.e(TAG, "error in QuestionUtils.setAnswerData: " + e.getMessage());
        }

        return object;
    }
}
