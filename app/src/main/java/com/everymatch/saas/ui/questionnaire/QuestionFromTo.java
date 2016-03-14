package com.everymatch.saas.ui.questionnaire;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.everymatch.saas.R;
import com.everymatch.saas.ui.questionnaire.base.QuestionnaireQuestionBaseFragment;
import com.everymatch.saas.util.EMLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PopApp_laptop on 04/01/2016.
 */
public class QuestionFromTo extends QuestionnaireQuestionBaseFragment implements AdapterView.OnItemSelectedListener {
    public final String TAG = getClass().getName();

    private Spinner spFrom, spTo;
    float length;


    private float from, to;
    ArrayAdapter<String> adapterFrom, adapterTo;
    List<String> fromArr = new ArrayList<>();
    List<String> toArr = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = super.onCreateView(inflater, container, savedInstanceState);
        inflater.inflate(R.layout.question_from_to, (ViewGroup) view.findViewById(R.id.answers_container));
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String[] rangeStr = mQuestionAndAnswer.question.range.split(",");
        float step = mQuestionAndAnswer.question.step;
        try {
            from = Integer.parseInt(rangeStr[0]);
            to = Integer.parseInt(rangeStr[1]);
        } catch (Exception ex) {
            from = 1;
            to = 10;
        }
        // TODO - FIX
        length = to - from + 1;

        for (float i = from; i <= to; i += step) {
            fromArr.add("" + i);
            toArr.add("" + i);
        }

        adapterFrom = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, fromArr);
        adapterFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adapterTo = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, toArr);
        adapterTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spFrom = (Spinner) view.findViewById(R.id.spFrom);
        spTo = (Spinner) view.findViewById(R.id.spTo);

        spFrom.setAdapter(adapterFrom);
        spTo.setAdapter(adapterTo);

        spFrom.setOnItemSelectedListener(this);
        spTo.setOnItemSelectedListener(this);

        recoverAnswerData();
    }

    @Override
    public void recoverDefaultAnswer() {
        recoverAnswerData();
    }

    private void recoverAnswerData() {
        if (mQuestionAndAnswer.userAnswerData != null && mQuestionAndAnswer.userAnswerData.has("value")) {
            try {
                //get the textual values
                from = Float.parseFloat(mQuestionAndAnswer.userAnswerData.getString("value").split(",")[0]);
                to = Float.parseFloat(mQuestionAndAnswer.userAnswerData.getString("value").split(",")[1]);


                int f = adapterFrom.getPosition("" + from);
                int t = adapterFrom.getPosition("" + to);
                spFrom.setSelection(f);
                spTo.setSelection(t);

            } catch (Exception ex) {
                EMLog.e(TAG, ex.getMessage());
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        //make sure from is not bigger then to
        if (spFrom.getSelectedItemPosition() >= spTo.getSelectedItemPosition()) {
            if (spFrom.getSelectedItemPosition() + 2 < adapterTo.getCount()) {
                spTo.setSelection(spFrom.getSelectedItemPosition() + 1);
            } else {
                spFrom.setSelection(adapterFrom.getCount() - 2);
                spTo.setSelection(adapterTo.getCount() - 1);
            }
        }

        String ans = spFrom.getSelectedItem() + " - " + spTo.getSelectedItem();
        setAnswer(ans);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
