package com.everymatch.saas.server.Data;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.FormType;
import com.everymatch.saas.client.data.QuestionType;
import com.everymatch.saas.client.data.Types;
import com.everymatch.saas.ui.questionnaire.QuestionTime;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.Utils;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Dacid on 29/06/2015.
 */
public class DataQuestion implements Serializable {
    public final String TAG = getClass().getName();

    public int questions_id;
    public boolean have_dependent_questions;
    public DataAnswer answers[];
    public int version;
    public String status;
    public String tags;
    public boolean role;

    @SerializedName("default_value")
    //public HashMap<String, String> default_value;
    public String default_value;

    public boolean is_hide;
    public String text_title;
    public String text_subtitle;
    public String question_type;
    public String match_type;
    public int match_question_id;
    public int display_order;
    public boolean multiple;
    public String irrelevant_default_state;
    public boolean deal_breaker;
    public DataIcon icon;
    public float step;
    public String range;
    public boolean mandatory;
    public boolean user_profile;
    public String form_type;
    public int score;
    public boolean is_important;
    public HashMap<String, Object> units;


    public DataQuestion() {
    }

    public DataQuestion(DataQuestion other) {
        if (other.answers != null) {
            this.answers = new DataAnswer[other.answers.length];
            for (int i = 0; i < other.answers.length; ++i) {
                this.answers[i] = new DataAnswer(other.answers[i]);
            }
        } else
            this.answers = other.answers;

        this.questions_id = other.questions_id;
        this.have_dependent_questions = other.have_dependent_questions;
        this.version = other.version;
        this.status = other.status;
        this.tags = other.tags;
        this.role = other.role;
        this.default_value = other.default_value;
        this.is_hide = other.is_hide;
        this.text_title = other.text_title;
        this.text_subtitle = other.text_subtitle;
        this.question_type = other.question_type;
        this.match_type = other.match_type;
        this.match_question_id = other.match_question_id;
        this.display_order = other.display_order;
        this.multiple = other.multiple;
        this.irrelevant_default_state = other.irrelevant_default_state;
        this.deal_breaker = other.deal_breaker;
        this.icon = other.icon;
        this.step = other.step;
        this.range = other.range;
        this.mandatory = other.mandatory;
        this.user_profile = other.user_profile;
        this.form_type = other.form_type;
        this.score = other.score;
        this.is_important = other.is_important;

    }

    /* this method returns concated answer values by given answer id's list */
    public String getAnswerValuesByAnswerIds(String answerIds) {
        String ans = "";
        if (answers == null) {
            return "";
        }
        ArrayList ids = new ArrayList<>(Arrays.asList(answerIds.trim().split("[\\s]*,[\\s]*")));
        for (DataAnswer answer : answers) {
            if (ids.contains(("" + answer.answer_id).trim()))
                ans += (answer.text_title) + ",";
        }
        if (ans.endsWith(","))
            ans = ans.substring(0, ans.length() - 1);
        return ans;
    }

    public String getAnswerValuesByAnswerValues(String answerValues) {
        String ans = "";
        if (answers == null) {
            return "";
        }
        ArrayList ids = new ArrayList<>(Arrays.asList(answerValues.trim().split("[\\s]*,[\\s]*")));
        for (DataAnswer answer : answers) {
            if (ids.contains(("" + answer.value).trim()))
                ans += (answer.text_title) + ",";
        }
        if (ans.endsWith(","))
            ans = ans.substring(0, ans.length() - 1);
        return ans;
    }

    private String getTimeUnits() {
        String answer = "";
        try {

            float mStep = step == 0 ? 1 : step;
            String[] rangeStr = range.split(",");
            int mMin = Integer.parseInt(rangeStr[0]);
            int mMax = Integer.parseInt(rangeStr[1]);

            //need to change the unit according to user unit
            if (question_type.equals(QuestionType.PACE)) {
                if (DataStore.getInstance().getUser().user_settings.getDistance().equals(Types.UNIT_MILE)) {
                    EMLog.d(TAG, "user in mile...");
                    mMin = (int) ((double) mMin * 1.61);
                    mMax = (int) ((double) mMax * 1.61);
                }
            }

            QuestionTime.TIME_MODE time_mode = null;
            if (mMax < 60) {
                time_mode = QuestionTime.TIME_MODE.TIME_MODE_SEC;
            } else if (mMax >= 60 && mStep < 60) {
                time_mode = QuestionTime.TIME_MODE.TIME_MODE_MIN;
            } else if ((mMax >= 60 && mStep >= 60) || mMax > 3600) {
                time_mode = QuestionTime.TIME_MODE.TIME_MODE_HOUR;
            }

            DataManager dm = DataManager.getInstance();
            String time = dm.getResourceText(R.string.Seconds_Short);
            if (time_mode == QuestionTime.TIME_MODE.TIME_MODE_MIN)
                time = dm.getResourceText(R.string.Minutes_Short);
            if (time_mode == QuestionTime.TIME_MODE.TIME_MODE_HOUR)
                time = dm.getResourceText(R.string.Hours_Short);

            if (question_type.equals(QuestionType.TIME)) {
                //add units
                //if (units != null && units.containsKey("value") && units.get("value") != null) {
                answer = "(" + time + ")";
                // }
            }

            if (question_type.equals(QuestionType.PACE)) {
                //add units
                if (units != null && units.containsKey("value") && units.get("value") != null) {
                    answer = "(" + time + "/" + dm.getResourceText(units.get("value").toString()) + ")";
                }
            }

            if (question_type.equals(QuestionType.DISTANCE)) {
                //add units
                if (units != null && units.containsKey("value") && units.get("value") != null) {
                    answer = "(" + dm.getResourceText(units.get("value").toString() + ")");
                }
            }

        } catch (Exception ex) {
            EMLog.e(TAG, ex.getMessage());
        }
        return answer;
    }

    public String getUnits() {
        String answer = "";
        try {
            if (form_type.equals(FormType.TIME)) {
                answer = getTimeUnits();
                return answer;
            }

            if (units != null && units.containsKey("value") && units.get("value") != null && !Utils.isEmpty(units.get("value").toString())) {
                answer = "(" + units.get("value").toString() + ")";
            }

        } catch (Exception ex) {
            EMLog.e(TAG, ex.getMessage());
        }

        return answer;
    }
}