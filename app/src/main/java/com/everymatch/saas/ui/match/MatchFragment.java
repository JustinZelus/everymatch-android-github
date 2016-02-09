package com.everymatch.saas.ui.match;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.server.Data.DataMatchResults;
import com.everymatch.saas.server.requests.RequestMatch;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.ui.BaseActivity;
import com.everymatch.saas.ui.base.BaseFragment;
import com.everymatch.saas.util.IconManager;
import com.everymatch.saas.view.EventDataRow;
import com.everymatch.saas.view.EventHeader;
import com.everymatch.saas.view.ViewSeperator;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by PopApp_laptop on 18/01/2016.
 */
public class MatchFragment extends BaseFragment implements EventHeader.OnEventHeader {
    //Data
    MatchActivity matchActivity;
    DataMatchResults dataMatchResults;
    boolean isAnimationLoaded;

    //Views
    private EventHeader mHeader;
    CircularImageView imgLeft, imgRight;
    TextView tvMatchValue;
    LinearLayout llResultContainer;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        matchActivity = (MatchActivity) activity;
        dataMatchResults = matchActivity.mMatchResults;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_match, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHeader = (EventHeader) view.findViewById(R.id.eventHeader);
        imgLeft = (CircularImageView) view.findViewById(R.id.imgLeftImage);
        imgRight = (CircularImageView) view.findViewById(R.id.imgRightImage);
        tvMatchValue = (TextView) view.findViewById(R.id.tvMatchValue);
        llResultContainer = (LinearLayout) view.findViewById(R.id.llResultContainer);

        updateUI();
        setHeader();
        startImagesAnimation();
    }

    private void setResultsRows() {
        llResultContainer.removeAllViews();
        for (DataMatchResults.DataQuestionResult dataQuestionResult : dataMatchResults.getQuestions_results()) {
            EventDataRow edr = new EventDataRow(getActivity());
            edr.setTag(dataQuestionResult);
            edr.getLeftMediaContainer().setVisibility(View.GONE);
            edr.setRightIconText(IconManager.getInstance(getActivity()).getIconString("icon-Match"));
            edr.getRightIcon().setTextColor(ds.getIntColor(EMColor.PRIMARY));
            edr.setRightText(null);
            edr.setDetails(null);
            edr.setTitle(dataQuestionResult.match_question.text_title);

            edr.setOnClickListener(onRowClick);

            if (dataQuestionResult.my_answer != null && dataQuestionResult.other_match_question_answer != null) {
                llResultContainer.addView(edr);
                llResultContainer.addView(new ViewSeperator(getActivity(), null));
            }
        }
    }

    private void updateUI() {
        tvMatchValue.setText("" + (int) dataMatchResults.match + "%");

        // load other user/event image
        switch (matchActivity.matchType) {
            case RequestMatch.MATCH_TYPE_USER_TO_USER:
                Picasso.with(getActivity())
                        .load(matchActivity.mDataPeople.image_url)
                        .into(imgRight);
                break;

            case RequestMatch.MATCH_TYPE_USER_TO_EVENT:
                Picasso.with(getActivity())
                        .load(matchActivity.mDataEvent.dataPublicEvent.image_url)
                        .into(imgRight);
                break;
        }

        // load my image
        Picasso.with(getActivity())
                .load(ds.getUser().image_url)
                .into(imgLeft);

        //add results rows
        setResultsRows();

    }

    private void startImagesAnimation() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int duration = isAnimationLoaded ? 100 : 1500;
                int alphaDuration = isAnimationLoaded ? 100 : 1300;
                int halfWidth = getView().getMeasuredWidth() / 2;
                int imgWidth = imgRight.getMeasuredWidth();

                int startR = halfWidth + (imgWidth);
                int endR = halfWidth - (imgWidth / 4);

                //animate right image
                ObjectAnimator.ofFloat(imgRight, "X", startR, endR)
                        .setDuration(duration)
                        .start();

                ObjectAnimator.ofFloat(imgRight, View.ALPHA.getName(), 0, 1).setDuration(alphaDuration).start();

                //animate left image
                ObjectAnimator.ofFloat(imgLeft, "X", halfWidth - (imgWidth * 2), halfWidth - imgWidth + imgWidth / 4)
                        .setDuration(duration)
                        .start();

                ObjectAnimator.ofFloat(imgLeft, View.ALPHA.getName(), 0, 1).setDuration(alphaDuration).start();

                isAnimationLoaded = true;
            }
        }, 300);
    }

    View.OnClickListener onRowClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DataMatchResults.DataQuestionResult dataQuestionResult = (DataMatchResults.DataQuestionResult) v.getTag();
            ((BaseActivity) getActivity()).addFragment(R.id.fragment_container, MatchDetailsFragment.getInstance(dataQuestionResult), "", true, "", R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        }
    };

    private void setHeader() {
        mHeader.setListener(this);
        mHeader.getBackButton().setText(Consts.Icons.icon_ArrowBack);
        mHeader.getIconOne().setVisibility(View.GONE);
        mHeader.getIconTwo().setVisibility(View.GONE);
        mHeader.getIconThree().setVisibility(View.GONE);
        mHeader.setTitle("Match");
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
