package com.everymatch.saas.ui.questionnaire;

import com.everymatch.saas.client.data.QuestionType;
import com.everymatch.saas.server.Data.DataAnswer;
import com.everymatch.saas.server.Data.DataQuestion;
import com.everymatch.saas.util.EMLog;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class QuestionAndAnswer implements Serializable {
    public final String TAG = getClass().getName();

    public DataQuestion question;
    public String userAnswerStr;
    public JSONObject userAnswerData;
    boolean isDependentsQuestion;
    boolean isAnsweredConfirmedByClickingNext = false;
    public HashMap<Integer, ArrayList<QuestionAndAnswer>> subQuestionsMap;

    //original values
    private HashMap<Integer, ArrayList<QuestionAndAnswer>> originalSubQuestionsMap;
    private String originalUserAnswerStr;
    private JSONObject originalUserAnswerData;


    public QuestionAndAnswer(DataQuestion dataQuestion) {
        this.question = dataQuestion;

        // check if we have a default value
        if (question.default_value != null) {
            try {
                this.userAnswerData = new JSONObject(question.default_value);
            } catch (Exception ex) {
                EMLog.e(TAG, ex.getMessage());
            }
        }
        // check if we have a isDefault answers
        generateIsDefaultAnswers();

        if (this.question.default_value != null)
            setUserAnswerStrByDefaultValue();

        subQuestionsMap = new HashMap<>();
        //originalSubQuestionsMap = new HashMap<>();

        /* drill down and add sub questionAndAnswers in subQuestionsMap if needed*/
        if (dataQuestion.role) {
            for (DataAnswer answer : dataQuestion.answers) {
                if (answer.questions != null && answer.questions.length > 0) {
                    ArrayList<QuestionAndAnswer> arr = new ArrayList();
                    ArrayList<QuestionAndAnswer> arrCopy = new ArrayList();
                    for (DataQuestion subQuestion : answer.questions) {
                        arr.add(new QuestionAndAnswer(subQuestion));
                        arrCopy.add(new QuestionAndAnswer(new DataQuestion(subQuestion)));
                    }
                    subQuestionsMap.put(answer.answer_id, arr);
                    //originalSubQuestionsMap.put(answer.answer_id, arrCopy);
                }
            }
        }

        // save generated default values in case user click "remove" on role question
        setOriginalData();
    }

    private void setOriginalData() {
        originalUserAnswerData = userAnswerData;
        originalUserAnswerStr = userAnswerStr;
    }

    /**
     * this method generates user answer str (user friendly form)) by default value from server
     */
    private void setUserAnswerStrByDefaultValue() {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("questions_id", question.questions_id);
            jsonObject.put("status", "active");
            switch (question.question_type) {
                case QuestionType.NUMBER:
                case QuestionType.GENDER:
                case QuestionType.GENDER_RANGE:
                case QuestionType.NUMBER_RANGE:
                case QuestionType.SCALE:
                case QuestionType.ABOUT_ME:
                case QuestionType.IMAGE_UPLOAD:
                    jsonObject.put("value", question.default_value);
                    break;
                case QuestionType.AGE:
                case QuestionType.DATE:
                case QuestionType.EVENT_DATE:
                    jsonObject.put("value", question.default_value);
                    break;
                case QuestionType.LOCATION:
                case QuestionType.MY_LOCATION:
                case QuestionType.EVENT_LOCATION:
                case QuestionType.EVENT_LIST:
                    jsonObject.put("value", question.default_value);
                    break;
                case QuestionType.IDS:
                    userAnswerStr = question.getAnswerValuesByAnswerIds(question.default_value);
                    jsonObject.put("value", question.default_value);
                    break;
                case QuestionType.EVENT_SCHEDULE:
                    jsonObject.put("value", question.default_value);
                    break;
                case QuestionType.SETUP:
                    jsonObject.put("value", question.default_value);
                    break;
            }

            userAnswerData = jsonObject;
        } catch (Exception ex) {
            EMLog.e(TAG, ex.getMessage());
        }
    }

    public QuestionAndAnswer getQaaByQuestionId(int answerId, int questionId) {
        ArrayList<QuestionAndAnswer> qaaList = subQuestionsMap.get(answerId);
        if (qaaList != null) {
            for (QuestionAndAnswer qaa : qaaList) {
                if (qaa.question.questions_id == questionId)
                    return qaa;
            }
        }
        return null;
    }

    /**
     * this method run over the answers and generates a default answer from all the "is_default" answers
     */
    private void generateIsDefaultAnswers() {
        if (question.answers != null) {
            if (question.question_type.equals(QuestionType.IDS)) {
                String defaultList = "";
                for (DataAnswer a : question.answers) {
                    if (a.is_default)
                        defaultList += a.answer_id + ",";
                }
                if (defaultList.endsWith(","))
                    defaultList = defaultList.substring(0, defaultList.length() - 1).trim();
                if (defaultList.length() > 0)
                    this.question.default_value = defaultList;
            }
        }
    }

    public void restoreDefaultValues() {
        userAnswerData = originalUserAnswerData;
        userAnswerStr = originalUserAnswerStr;
        //isAnsweredConfirmedByClickingNext = originalIsAnsweredConfirmedByClickingNext;
        //isDependentsQuestion = originalIsDependentsQuestion;
        //question = originalQuestion;
        subQuestionsMap.clear();
        //subQuestionsMap = new HashMap<>(originalSubQuestionsMap);
    }

    // this method tell's if user selected all available answers he could
    public boolean isAllAnswersSelected() {
        if (question.question_type.equals(QuestionType.IDS)) {
            int ansCount = userAnswerStr.split(",").length;
            if (ansCount == question.answers.length)
                return true;
        }
        return false;
    }
}