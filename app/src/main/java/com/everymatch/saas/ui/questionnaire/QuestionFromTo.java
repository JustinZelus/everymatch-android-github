package com.everymatch.saas.ui.questionnaire;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.everymatch.saas.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PopApp_laptop on 04/01/2016.
 */
public class QuestionFromTo extends QuestionnaireQuestionBaseFragment implements AdapterView.OnItemSelectedListener {
    private Spinner spFrom, spTo;


    private int from, to;
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
        int step = mQuestionAndAnswer.question.step;
        try {
            from = Integer.parseInt(rangeStr[0]);
            to = Integer.parseInt(rangeStr[1]);
        } catch (Exception ex) {
            from = 1;
            to = 10;
        }
        // TODO - FIX


        int length = to - from + 1;

        for (int i = from; i < to; i += step) {
            fromArr.add("" + i);
            toArr.add("" + i);
        }

        //adapterFrom = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, fromArr);
        //adapterFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> adapterFrom = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, fromArr);
        adapterFrom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> adapterTo = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, toArr);
        adapterTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //adapterTo = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, toArr);
        //adapterTo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        spFrom = (Spinner) view.findViewById(R.id.spFrom);
        spTo = (Spinner) view.findViewById(R.id.spTo);

        spFrom.setAdapter(adapterFrom);
        spTo.setAdapter(adapterTo);


        spFrom.setOnItemSelectedListener(this);
        spTo.setOnItemSelectedListener(this);

    }

    @Override
    public void recoverDefaultAnswer() {
        recoverAnswerData();
    }

    private void recoverAnswerData() {

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String ans = "";
        if (view == spFrom) {
            ans = fromArr.get(position) + " - " + toArr.get(spTo.getSelectedItemPosition());
        } else {
            ans = fromArr.get(spFrom.getSelectedItemPosition()) + " - " + toArr.get(position);
        }

        setAnswer(ans);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
