package com.everymatch.saas.server.Data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Dacid on 29/06/2015.
 */
public class DataQuestion implements Serializable {
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
    public int step;
    public String range;
    public boolean mandatory;
    public boolean user_profile;
    public String form_type;
    public int score;
    public boolean is_important;


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
}