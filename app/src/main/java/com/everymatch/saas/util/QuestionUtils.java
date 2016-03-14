package com.everymatch.saas.util;

import android.text.TextUtils;
import android.util.Log;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataManager;
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
    private static DataManager dm = DataManager.getInstance();

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
     * this method get's user answer data format and returns the title
     */
    public static String getAnsweredTitleFromUserAnswerData(DataQuestion question, String userAnswerData) {
        DataAnswer dataAnswer = new DataAnswer();
        dataAnswer.value = userAnswerData;
        String ans = getAnsweredTitle(question, dataAnswer);
        return ans;
    }

    /**
     * @param question
     * @param answer
     * @return the text to show on
     */
    public static String getAnsweredTitle(DataQuestion question, DataAnswer answer) {
        String value = "";

        if (answer == null || answer.value == null) {
            value = DataManager.getInstance().getResourceText(R.string.Unanswered);
        } else {
            Gson gson = new Gson();
            String json = gson.toJson(answer.value);
            try {
                switch (question.form_type) {
                    case FormType.LOCATION:
                        // DataLocation location = gson.fromJson(gson.toJson(answer.value), DataLocation.class);
                        JSONObject jsonObject = new JSONObject(json);
                        DataLocation location = DataLocation.fromJsonObject(jsonObject);

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
                        if (question.question_type.equals(QuestionType.GENDER) || question.question_type.equals(QuestionType.GENDER_RANGE)) {
                            //value = (String) answer.value;
                            ArrayList<String> answered = new ArrayList<>();
                            for (DataAnswer answers : question.answers) {
                                if (((String) answers.value).trim().equals(answers.value.toString().trim() + "")) {
                                    answered.add(answers.text_title);
                                }
                            }

                            if (!Utils.isArrayListEmpty(answered)) {
                                value = TextUtils.join(", ", answered);
                            }
                        } else {
                            ArrayList<String> answered = new ArrayList<>();
                            for (DataAnswer answers : question.answers) {
                                if ((answer.value.toString()).contains(answers.answer_id + "")) {
                                    answered.add(answers.text_title);
                                }
                            }

                            if (!Utils.isArrayListEmpty(answered)) {
                                value = TextUtils.join(", ", answered);
                            }
                        }

                        break;
                    case FormType.TIME:
                        int sec = (int) (Double.parseDouble(answer.value.toString()));
                        value = Utils.getHourMinSecFromSeconds(sec);
                        break;

                    case FormType.FROM_TO:
                        value = answer.value.toString().replace(",", "-");
                        break;

                    case FormType.SCHEDULE:
                        DataDate from = new Gson().fromJson(gson.toJson(json), DataDate.class);

                        break;

                    default:
                        value = (String) answer.value;
                        break;
                }
            } catch (Exception e) {
                EMLog.e("Parse error", e.getMessage());
                return TextUtils.isEmpty(value) ? dm.getResourceText(R.string.Unanswered) : value;
            }
        }

        //add units
        value += " " + question.getUnits();

        return TextUtils.isEmpty(value) ? dm.getResourceText(R.string.Unanswered) : value;
    }

    public static void updateValueItem(String questionType, DataAnswer answer, JSONObject userAnswerData) {
        try {
            switch (questionType) {
                case QuestionType.LOCATION:
                case QuestionType.MY_LOCATION:
                case QuestionType.EVENT_LOCATION:
                case QuestionType.EVENT_LIST:
                case QuestionType.EVENT_SCHEDULE:
                    //here we need to converts answer.value to json object
                    String str = new Gson().toJson(answer.value);
                    JSONObject jsonObject = new JSONObject(str);
                    userAnswerData.put("value", jsonObject);
                    return;
                default:
                    userAnswerData.put("value", answer.value);
                    break;
            }
        } catch (Exception e) {
            EMLog.e(TAG, "error in QuestionUtils.getAnswerValue: " + e.getMessage());
        }
    }

    /**
     * return the value answer in server format - not must to be a jsonObject
     */
    public static Object getAnswerValue(String questionType, DataAnswer answer) {
        JSONObject value = new JSONObject();
        try {
            Gson gson = new Gson();
            value = new JSONObject(gson.toJson(answer));

            switch (questionType) {
                case QuestionType.NUMBER:
                case QuestionType.GENDER:
                case QuestionType.GENDER_RANGE:
                case QuestionType.NUMBER_RANGE:
                case QuestionType.SCALE:
                case QuestionType.ABOUT_ME:
                case QuestionType.IMAGE_UPLOAD:
                    return answer;
                case QuestionType.AGE:
                case QuestionType.DATE:
                case QuestionType.EVENT_DATE:
                    //TODO implement this
                    return "";
                //jsonObject.put("value", createDateJsonObject(answer));

                case QuestionType.LOCATION:
                case QuestionType.MY_LOCATION:
                case QuestionType.EVENT_LOCATION:
                case QuestionType.EVENT_LIST:
                    JSONObject valueObject = value.getJSONObject("value");
                    DataLocation dataLocation = DataLocation.fromJsonObject(valueObject);
                    value.put("city", dataLocation.city);
                    value.put("country_code", dataLocation.country_code);
                    value.put("country_name ", dataLocation.country_name);
                    value.put("country_code", dataLocation.country_code);
                    value.put("text_address", dataLocation.text_address);
                    value.put("distance_units", dataLocation.distance_units);
                    value.put("distance_value", dataLocation.distance_value);
                    value.put("distance_units", dataLocation.distance_units);
                    value.put("distance_units", dataLocation.distance_units);
                    value.put("place_name", dataLocation.place_name);
                    value.put("place_id", dataLocation.place_id);

                    return value;
                case QuestionType.IDS:
                    //jsonObject.put("value", createIdsList());
                    //TODO implement this
                    return "";
                case QuestionType.EVENT_SCHEDULE:
                    //TODO implement this
                    //jsonObject.put("value", createJsonObject());
                    return "";
                case QuestionType.SETUP:
                    //TODO implement this
                    //jsonObject.put("value", createJsonObject());
                    return "";
                case QuestionType.PACE:
                case QuestionType.TIME:
                    //TODO implement this
                    //jsonObject.put("value", getAnswerValue()/*ust number in this case*/);
                    return "";

                default:
                    break;
            }
        } catch (Exception e) {
            EMLog.e(TAG, "error in QuestionUtils.getAnswerValue: " + e.getMessage());
        }

        return value;
    }
}
