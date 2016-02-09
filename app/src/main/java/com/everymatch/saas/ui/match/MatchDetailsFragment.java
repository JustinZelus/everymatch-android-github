package com.everymatch.saas.ui.match;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.server.Data.DataMatchResults;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.ui.base.BaseFragment;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.QuestionUtils;
import com.everymatch.saas.view.EventHeader;
import com.squareup.picasso.Picasso;

/**
 * Created by PopApp_laptop on 19/01/2016.
 */
public class MatchDetailsFragment extends BaseFragment implements EventHeader.OnEventHeader {
    public final String TAG = getClass().getName();

    public static final String ARG_DATA_QUESTION_RESULT = "arg.data.question.result";
    //Data
    private DataMatchResults.DataQuestionResult dataQuestionResult;

    //Views
    private EventHeader mHeader;
    LinearLayout llRowsHolder;
    private MatchActivity matchActivity;


    public static MatchDetailsFragment getInstance(DataMatchResults.DataQuestionResult dataQuestionResult) {
        MatchDetailsFragment answer = new MatchDetailsFragment();
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(ARG_DATA_QUESTION_RESULT, dataQuestionResult);
        answer.setArguments(bundle);
        return answer;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        matchActivity = (MatchActivity) activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.dataQuestionResult = (DataMatchResults.DataQuestionResult) getArguments().getSerializable(ARG_DATA_QUESTION_RESULT);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_match_details, container, false);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHeader = (EventHeader) view.findViewById(R.id.eventHeader);
        setHeader();
        llRowsHolder = (LinearLayout) view.findViewById(R.id.rowsContainer);
        addRows();
    }


    private void setHeader() {
        mHeader.setListener(this);
        mHeader.getBackButton().setText(Consts.Icons.icon_ArrowBack);
        mHeader.getIconOne().setVisibility(View.GONE);
        mHeader.getIconTwo().setVisibility(View.GONE);
        mHeader.getIconThree().setVisibility(View.GONE);
        mHeader.setTitle("Match Details");
    }


    private void addRows() {
        llRowsHolder.removeAllViews();
        View myQuestion = LayoutInflater.from(getActivity()).inflate(R.layout.view_conversation_item, llRowsHolder, false);
        View friendQuestion = LayoutInflater.from(getActivity()).inflate(R.layout.view_conversation_item, llRowsHolder, false);
        friendQuestion.findViewById(R.id.tv_view_conversation_iconMore).setVisibility(View.GONE);
        myQuestion.findViewById(R.id.tv_view_conversation_iconMore).setVisibility(View.GONE);

        try {

            //set other user data
            friendQuestion.findViewById(R.id.rlAgoHolder).setVisibility(View.GONE);
            ((TextView) friendQuestion.findViewById(R.id.tv_view_conversation_username)).setText(matchActivity.mDataPeople.first_name + " answered");

            boolean shouldAdd = true;
            //set answer value
            if (dataQuestionResult.other_match_question_answer.containsKey("value")) {
                String value = dataQuestionResult.other_match_question_answer.get("value").toString();
                String friendly = QuestionUtils.getAnsweredTitleFromUserAnswerData(dataQuestionResult.question, value);
                ((TextView) friendQuestion.findViewById(R.id.tv_view_conversation_content)).setText(friendly);
            } else {
                shouldAdd = false;
            }

            //set other user image
            ImageView otherUserImage = ((ImageView) friendQuestion.findViewById(R.id.img_view_conversation_image));
            Picasso.with(getActivity())
                    .load(matchActivity.mDataPeople.image_url)
                    .into(otherUserImage);


            //set my data
            myQuestion.findViewById(R.id.rlAgoHolder).setVisibility(View.GONE);
            ((TextView) myQuestion.findViewById(R.id.tv_view_conversation_username)).setText(ds.getUser().first_name + " answered");
            //set answer value
            if (dataQuestionResult.my_answer.containsKey("value")) {
                String value = dataQuestionResult.my_answer.get("value").toString();
                String friendly = QuestionUtils.getAnsweredTitleFromUserAnswerData(dataQuestionResult.question, value);
                ((TextView) myQuestion.findViewById(R.id.tv_view_conversation_content)).setText(friendly);
            } else {
                shouldAdd = false;
            }

            //set my image
            ImageView myImage = ((ImageView) myQuestion.findViewById(R.id.img_view_conversation_image));
            Picasso.with(getActivity())
                    .load(ds.getUser().image_url)
                    .into(myImage);


            //add the rows
            if (shouldAdd) {
                llRowsHolder.addView(myQuestion);
                llRowsHolder.addView(friendQuestion);
            }
        } catch (Exception ex) {
            EMLog.e(TAG, ex.getMessage());
        }
    }

    @Override
    public void onBackButtonClicked() {
        getActivity().onBackPressed();
    }

    @Override
    public void onOneIconClicked() {

    }

    @Override
    public void onTwoIconClicked() {

    }

    @Override
    public void onThreeIconClicked() {

    }
}
