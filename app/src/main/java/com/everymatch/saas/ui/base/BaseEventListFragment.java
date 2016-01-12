package com.everymatch.saas.ui.base;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;

import com.everymatch.saas.R;
import com.everymatch.saas.adapter.EmBaseAdapter;
import com.everymatch.saas.adapter.EventsAdapter;
import com.everymatch.saas.server.Data.DataEvent;
import com.everymatch.saas.server.Data.DataEventHolder;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.singeltones.EventListener;
import com.everymatch.saas.util.EMLog;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Dor on 10/29/15.
 */
public abstract class BaseEventListFragment extends BaseListFragment implements AdapterView.OnItemClickListener {

    private static final String TAG = BaseEventListFragment.class.getSimpleName();

    protected HashMap<String, DataEventHolder> mEventMap = new HashMap<>();
    //protected ResponseGetUser.MyEventsHolder mEventMap;

    protected EventsAdapter mAdapter;

    protected EventListener mEventListener;

    protected String mCurrentEventKey;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof EventListener) {
            mEventListener = (EventListener) context;
        } else {
            throw new IllegalStateException(context + " must implement EventListener");
        }
    }

    /**
     * Set header for this page
     */
    protected void setHeader() {
        mEventHeader.setListener(this);
        mEventHeader.getBackButton().setText(Consts.Icons.icon_ArrowBack);
        mEventHeader.getIconOne().setText(Consts.Icons.icon_CreateEvent);
        mEventHeader.getIconTwo().setText(Consts.Icons.icon_Search);
        mEventHeader.getIconThree().setText(Consts.Icons.icon_MoreV);
        mEventHeader.setTitle(dm.getResourceText(R.string.events));
    }

    /**
     * Set the first data we already have in cache
     */
    @Override
    protected void initAdapter() {
        mAdapter = createAdapter();
        mAbsListView.setAdapter(mAdapter);
        mAbsListView.setOnItemClickListener(this);
    }

    public abstract EventsAdapter createAdapter();

    protected void addEventsToAdapter(String eventType, ArrayList<DataEvent> events) {

        int currentCount = mEventMap.get(eventType).getEvents().size();

        mEventMap.get(eventType).getEvents().addAll(events);

        EMLog.i(TAG, "addEventsToAdapter - adding " + events.size() + " events to " + eventType + " map");

        if (events.size() < PAGE_COUNT) {
            setNoMoreResults();
        }

        refreshAdapter();

        if (currentCount > 0) {
            mAdapter.setAnimatedPosition(currentCount);
        }
    }

    protected void refreshAdapter() {
        mAdapter.notifyDataSetChanged();
        mLoading = false;
    }

    @Override
    public void onBackButtonClicked() {
        getActivity().onBackPressed();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        DataEvent dataEvent = mEventMap.get(mCurrentEventKey).getEvents().get(position);
        mEventListener.onEventClick(dataEvent);
    }

    @Override
    public void performSearch(String s) {
        if (mAdapter != null){
            mAdapter.getFilter().filter(s);
        }
    }

    @Override
    public EmBaseAdapter getAdapter() {
        return mAdapter;
    }
}
