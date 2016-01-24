package com.everymatch.saas.server.Data;

import com.everymatch.saas.server.responses.BaseResponse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by PopApp_laptop on 20/12/2015.
 */
public class DataMatchResults extends BaseResponse implements Serializable {
    public float match;
    private ArrayList<DataQuestionResult> questions_results;

    public ArrayList<DataQuestionResult> getQuestions_results() {
        if (questions_results == null)
            questions_results = new ArrayList<>();
        return questions_results;
    }

    public class DataQuestionResult implements Serializable {
        public float match;
        public float my_distance;
        public float other_distance;
        public int other_default_radius;
        public DataQuestion question;
        public DataQuestion match_question;
        public HashMap<String, Object> my_answer;
        public HashMap<String, Object> other_match_question_answer;
    }

}
