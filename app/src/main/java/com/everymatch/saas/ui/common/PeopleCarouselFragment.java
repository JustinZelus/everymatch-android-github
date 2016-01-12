package com.everymatch.saas.ui.common;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.everymatch.saas.R;
import com.everymatch.saas.adapter.BaseRecyclerViewAdapter;
import com.everymatch.saas.adapter.PeopleCarouselAdapter;
import com.everymatch.saas.server.Data.DataPeopleHolder;
import com.everymatch.saas.singeltones.PeopleListener;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.ComponentHeader;

/**
 * Created by dors on 7/20/15.
 */
public class PeopleCarouselFragment extends Fragment {

    public static final String TAG = PeopleCarouselFragment.class.getSimpleName();

    private static final String EXTRA_DATA_PEOPLE_HOLDER = "extra.data.people.holder";
    private static final String EXTRA_LEFT_TEXT = "extra.left.text";
    private static final String EXTRA_RIGHT_TEXT = "extra.right.text";

    private PeopleListener mListener;
    private DataPeopleHolder mHolder;
    private ComponentHeader mComponentHeader;
    private RecyclerView mPeopleRecycler;
    private String mLeftText;
    private String mRightText;
    private View mRootView;

    public PeopleCarouselFragment() {
    }

    @NonNull
    public static PeopleCarouselFragment getInstance(DataPeopleHolder holder) {
        PeopleCarouselFragment peopleCarouselFragment = new PeopleCarouselFragment();
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(EXTRA_DATA_PEOPLE_HOLDER, holder);
        peopleCarouselFragment.setArguments(bundle);
        return peopleCarouselFragment;
    }

    @NonNull
    public static PeopleCarouselFragment getInstance(DataPeopleHolder holder , String leftText, String rightText) {
        PeopleCarouselFragment peopleCarouselFragment = new PeopleCarouselFragment();
        Bundle bundle = new Bundle(1);
        bundle.putSerializable(EXTRA_DATA_PEOPLE_HOLDER, holder);
        bundle.putString(EXTRA_LEFT_TEXT, leftText);
        bundle.putString(EXTRA_RIGHT_TEXT, rightText);
        peopleCarouselFragment.setArguments(bundle);
        return peopleCarouselFragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof PeopleListener) {
            mListener = (PeopleListener) activity;
        } else {
            throw new IllegalStateException(activity + " must implements PeopleListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_people_carousel, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRootView = view;
        mComponentHeader = (ComponentHeader) view.findViewById(R.id.fragment_people_carousel_header);
        mPeopleRecycler = (RecyclerView) view.findViewById(R.id.fragment_people_carousel_recycler);
        mPeopleRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mPeopleRecycler.setHasFixedSize(true);
        mPeopleRecycler.addItemDecoration(new SpacesItemDecoration(getResources().getDimensionPixelSize(R.dimen.margin_xs)));
        bindData();
        view.findViewById(R.id.view_component_header_text_right).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onViewAllUsersClick(mHolder);
            }
        });
    }

    public void refreshData(DataPeopleHolder holder, String leftText, String rightText) {
        getArguments().putSerializable(EXTRA_DATA_PEOPLE_HOLDER, holder);
        getArguments().putString(EXTRA_LEFT_TEXT, leftText);
        getArguments().putString(EXTRA_RIGHT_TEXT, rightText);
        bindData();
    }

    private void bindData() {
        mHolder = (DataPeopleHolder) getArguments().getSerializable(EXTRA_DATA_PEOPLE_HOLDER);
        mLeftText = getArguments().getString(EXTRA_LEFT_TEXT);
        mRightText = getArguments().getString(EXTRA_RIGHT_TEXT);

        if (mLeftText != null && mRightText != null) {
            mComponentHeader.setTexts(mLeftText, mRightText);
        }

        if (Utils.isArrayListEmpty(mHolder.getUsers())){
            mRootView.setVisibility(View.GONE);
            return;
        }

        mRootView.setVisibility(View.VISIBLE);

        PeopleCarouselAdapter peopleAdapter = new PeopleCarouselAdapter(mHolder.getUsers());
         mPeopleRecycler.setAdapter(peopleAdapter);
        peopleAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mListener.onUserClick(mHolder.getUsers().get(position));
            }
        });

    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        final private int space;

        public SpacesItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.left = space;
            }

            outRect.right = space;
        }
    }
}
