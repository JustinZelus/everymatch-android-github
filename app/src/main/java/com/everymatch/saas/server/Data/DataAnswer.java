package com.everymatch.saas.server.Data;

import java.io.Serializable;

/**
 * Created by Dacid on 29/06/2015.
 */
public class DataAnswer implements Serializable {
    public DataQuestion questions[];
    public int answer_id;
    public String text_title;
    public String sub_title;
    public String status;
    public String answer_type;
    public boolean is_default;
    public boolean is_hide;
    public Object value;
    //public DataLocation value; //TODO: uncomment!
    public int display_order;
    public DataIcon icon;
    public String tags;
    public int questions_id;
    public String text_label;

    public DataAnswer(DataAnswer other) {
        if (other.questions != null) {
            this.questions = new DataQuestion[other.questions.length];
            for (int i = 0; i < other.questions.length; i++) {
                this.questions[i] = new DataQuestion(other.questions[i]);
            }
        } else
            this.questions = other.questions;


        this.answer_id = other.answer_id;
        this.text_title = other.text_title;
        this.sub_title = other.sub_title;
        this.status = other.status;
        this.answer_type = other.answer_type;
        this.is_default = other.is_default;
        this.is_hide = other.is_hide;
        this.value = other.value;
        this.display_order = other.display_order;
        this.icon = other.icon;
        this.tags = other.tags;
        this.questions_id = other.questions_id;
        this.text_label = other.text_label;
    }
}

