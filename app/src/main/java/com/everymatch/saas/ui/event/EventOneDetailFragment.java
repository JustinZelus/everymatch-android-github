package com.everymatch.saas.ui.event;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.everymatch.saas.R;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.view.EventDataRow;
import com.everymatch.saas.view.EventHeader;

/**
 * A simple {@link Fragment} subclass.
 */
public class EventOneDetailFragment extends Fragment implements EventHeader.OnEventHeader {

    public static final String TAG = EventOneDetailFragment.class.getSimpleName();
    public static final String EVENT_TITLE = "eventTitle";
    public static final String EVENT_DETAIL = "eventDetail";

    private EventHeader mHeader;
    private EventDataRow mDetailsRow;
    private String mTitle;
    private String mDetail;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_one_detail, container, false);

        Bundle bundle = getArguments();
        if (bundle != null) {

            mTitle = bundle.getString(EVENT_TITLE);
            mDetail = bundle.getString(EVENT_DETAIL);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mHeader = (EventHeader) view.findViewById(R.id.event_one_detail_header);
        mHeader.setListener(this);
        mHeader.setTitle(mTitle);
        mHeader.getBackButton().setText(Consts.Icons.icon_ArrowBack);
        mHeader.getIconOne().setVisibility(View.GONE);
        mHeader.getIconTwo().setVisibility(View.GONE);
        mHeader.getIconThree().setVisibility(View.GONE);

        mDetailsRow = (EventDataRow) view.findViewById(R.id.event_one_detail_row);
        mDetailsRow.getLeftIcon().setVisibility(View.GONE);
        mDetailsRow.getRightIcon().setVisibility(View.GONE);
        mDetailsRow.getRightIcon().setTextSize(20);
        mDetailsRow.getTitleView().setVisibility(View.GONE);
        mDetailsRow.setDetails(mDetail);

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
