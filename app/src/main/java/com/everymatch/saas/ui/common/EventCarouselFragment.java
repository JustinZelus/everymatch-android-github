package com.everymatch.saas.ui.common;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.adapter.BaseRecyclerViewAdapter;
import com.everymatch.saas.adapter.EventCarouselAdapter;
import com.everymatch.saas.server.Data.DataEvent;
import com.everymatch.saas.server.Data.DataEventHolder;
import com.everymatch.saas.singeltones.EventListener;
import com.everymatch.saas.util.Utils;

import java.util.ArrayList;

/**
 * Created by dors on 7/20/15.
 */
public class EventCarouselFragment extends Fragment {

    public static final String TAG = EventCarouselFragment.class.getSimpleName();

    private static final String EXTRA_EVENT_HOLDER = "extra.event.holder";
    private static final String EXTRA_LEFT_TEXT = "extra.left.text";
    private static final String EXTRA_RIGHT_TEXT = "extra.right.text";

    private EventListener mListener;
    private DataEventHolder mEventHolder;
    private RecyclerView mEventRecycler;
    private View mRootView;
    private String mLeftText;
    private String mRightText;
    private TextView mLeftTextView;
    private TextView mRightTextView;

    public EventCarouselFragment() {
    }

    @NonNull
    public static EventCarouselFragment getInstance(DataEventHolder dataEventHolder, String leftText, String rightText) {
        EventCarouselFragment eventCarouselFragment = new EventCarouselFragment();
        Bundle bundle = new Bundle(3);
        bundle.putSerializable(EXTRA_EVENT_HOLDER, dataEventHolder);
        bundle.putString(EXTRA_LEFT_TEXT, leftText);
        bundle.putString(EXTRA_RIGHT_TEXT, rightText);
        eventCarouselFragment.setArguments(bundle);
        return eventCarouselFragment;
    }

    @NonNull
    public static EventCarouselFragment getInstance(ArrayList<DataEvent> events, String leftText, String rightText) {
        EventCarouselFragment eventCarouselFragment = new EventCarouselFragment();
        Bundle bundle = new Bundle(3);
        bundle.putSerializable(EXTRA_EVENT_HOLDER, new DataEventHolder(events));
        bundle.putString(EXTRA_LEFT_TEXT, leftText);
        bundle.putString(EXTRA_RIGHT_TEXT, rightText);
        eventCarouselFragment.setArguments(bundle);
        return eventCarouselFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof EventListener) {
            mListener = (EventListener) context;
        } else {
            throw new IllegalStateException(context + " must implements EventListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_event_carousel, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRootView = view;
        mEventRecycler = (RecyclerView) view.findViewById(R.id.fragment_event_carousel_recycler);
        mEventRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mEventRecycler.setHasFixedSize(true);
        mEventRecycler.addItemDecoration(new SpacesItemDecoration(getResources().getDimensionPixelSize(R.dimen.margin_xs)));
        mLeftTextView = (TextView) view.findViewById(R.id.view_component_header_text_left);
        mRightTextView = (TextView) view.findViewById(R.id.view_component_header_text_right);

        bindData();

        View viewAll = view.findViewById(R.id.view_component_header_text_right);

        // View all
        viewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onViewAllEventsClick(mEventHolder);
            }
        });

        // Left text

    }

    public void refreshData(DataEventHolder dataEventHolder, String leftText, String rightText) {
        getArguments().putSerializable(EXTRA_EVENT_HOLDER, dataEventHolder);
        getArguments().putString(EXTRA_LEFT_TEXT, leftText);
        getArguments().putString(EXTRA_RIGHT_TEXT, rightText);
        bindData();
    }

    private void bindData() {

        mLeftText = getArguments().getString(EXTRA_LEFT_TEXT);
        mRightText = getArguments().getString(EXTRA_RIGHT_TEXT);

        mLeftTextView.setText(mLeftText);
        mRightTextView.setText(mRightText);

        mEventHolder = (DataEventHolder) getArguments().getSerializable(EXTRA_EVENT_HOLDER);

        if (Utils.isArrayListEmpty(mEventHolder.getEvents())) {
            mRootView.setVisibility(View.GONE);
            return;
        }

        mRootView.setVisibility(View.VISIBLE);

        EventCarouselAdapter eventsAdapter = new EventCarouselAdapter(mEventHolder.getEvents());
        mEventRecycler.setAdapter(eventsAdapter);
        eventsAdapter.setOnItemClickListener(new BaseRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                mListener.onEventClick(mEventHolder.getEvents().get(position));
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
