package com.everymatch.saas.ui.questionnaire;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.server.Data.DataActivity;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.ui.base.BaseFragment;
import com.everymatch.saas.util.ShapeDrawableUtils;
import com.everymatch.saas.view.EventHeader;
import com.everymatch.saas.view.IconImageView;

/**
 * A placeholder fragment containing a simple view.
 */
public class QuestionnaireWelcomeFragment extends BaseFragment {

    DataActivity mDataActivity;
    EventHeader mHeader;

    public QuestionnaireWelcomeFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataActivity = ((QuestionnaireActivity) getActivity()).mDataActivity;
        ((QuestionnaireActivity) getActivity()).getSupportActionBar().hide();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_question_welcome, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ((TextView) view.findViewById(R.id.title_textview)).setText(mDataActivity.text_welcome);
        ((TextView) view.findViewById(R.id.subtitle_textview)).setText(mDataActivity.text_description);

        view.findViewById(R.id.start_button).setBackgroundDrawable(ShapeDrawableUtils.getRoundendButton());
        IconImageView imageView = (IconImageView) view.findViewById(R.id.icon_image);
        imageView.setIconImage(mDataActivity.icon);
        setHeader(view);
    }

    private void setHeader(View view) {
        mHeader = (EventHeader) view.findViewById(R.id.eventHeader);

        mHeader.setListener(new EventHeader.OnEventHeader() {
            @Override
            public void onBackButtonClicked() {
                //getActivity().onBackPressed();
                getActivity().getSupportFragmentManager().popBackStackImmediate();
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
        });
        mHeader.getBackButton().setText(Consts.Icons.icon_New_Close);
        mHeader.getIconOne().setVisibility(View.GONE);
        mHeader.getIconTwo().setVisibility(View.GONE);
        mHeader.getIconThree().setVisibility(View.GONE);
        mHeader.setTitle(mDataActivity.text_title);
    }

}
