package com.everymatch.saas.ui.event;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
public class EventDetailsFragment extends Fragment implements EventHeader.OnEventHeader {

    private EventHeader mHeader;
    private EventDataRow mDetailsRow;
    private EventDataRow mLocationRow;
    private EventDataRow mDateRow;
    private EventDataRow mPeople;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_event_details, container, false);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mHeader = (EventHeader) view.findViewById(R.id.event_details_eventHeader);
        mHeader.setListener(this);
        mHeader.setTitle("Wigan vs. Bury");
        mHeader.getBackButton().setText(Consts.Icons.icon_ArrowBack);
        mHeader.getIconOne().setVisibility(View.GONE);
        mHeader.getIconTwo().setVisibility(View.GONE);
        mHeader.getIconThree().setVisibility(View.GONE);

        mDetailsRow = (EventDataRow) view.findViewById(R.id.row_1);
        mDetailsRow.getLeftIcon().setVisibility(View.GONE);
        mDetailsRow.getRightIcon().setText(Consts.Icons.icon_Arrowright);
        mDetailsRow.getRightIcon().setTextSize(20);
        mDetailsRow.setTitle("Interests");
        mDetailsRow.setDetails("Training sessions, Match day bar");
        mDetailsRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToOneDetailFragment(mDetailsRow.getTitleView().getText().toString(), mDetailsRow.getDetailsView().getText().toString());
            }
        });

        mLocationRow = (EventDataRow) view.findViewById(R.id.row_2);
        mLocationRow.getLeftIcon().setVisibility(View.GONE);
        mLocationRow.getRightIcon().setText(Consts.Icons.icon_Arrowright);
        mLocationRow.getRightIcon().setTextSize(20);
        mLocationRow.setTitle("Have tickets");
        mLocationRow.setDetails("Yes");
        mLocationRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToOneDetailFragment(mLocationRow.getTitleView().getText().toString(), mLocationRow.getDetailsView().getText().toString());
            }
        });

        mDateRow = (EventDataRow) view.findViewById(R.id.row_3);
        mDateRow.getLeftIcon().setVisibility(View.GONE);
        mDateRow.getRightIcon().setText(Consts.Icons.icon_Arrowright);
        mDateRow.getRightIcon().setTextSize(20);
        mDateRow.setTitle("Numeric");
        mDateRow.setDetails("5-10");
        mDateRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToOneDetailFragment(mDateRow.getTitleView().getText().toString(), mDateRow.getDetailsView().getText().toString());
            }
        });

        mPeople = (EventDataRow) view.findViewById(R.id.row_4);
        mPeople.getLeftIcon().setVisibility(View.GONE);
        mPeople.getRightIcon().setText(Consts.Icons.icon_Arrowright);
        mPeople.getRightIcon().setTextSize(20);
        mPeople.setTitle("Date of birth");
        mPeople.setDetails("jan 30,2015");
        mPeople.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToOneDetailFragment(mPeople.getTitleView().getText().toString(), mPeople.getDetailsView().getText().toString());
            }
        });

    }

    private void goToOneDetailFragment(String title, String detail) {
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);

        EventOneDetailFragment eventOneDetailFragment = new EventOneDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EventOneDetailFragment.EVENT_TITLE, title);
        bundle.putString(EventOneDetailFragment.EVENT_DETAIL, detail);
        eventOneDetailFragment.setArguments(bundle);

        transaction
                .addToBackStack("myFragment")
                .replace(R.id.event_layout, eventOneDetailFragment)
                .commit();
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
