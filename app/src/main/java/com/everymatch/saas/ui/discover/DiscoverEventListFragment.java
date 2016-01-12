package com.everymatch.saas.ui.discover;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.adapter.EventsAdapter;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.server.Data.DataEventHolder;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.server.requests.RequestDiscover;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.server.responses.ResponseDiscover;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.ui.base.BaseEventListFragment;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.Utils;

import java.text.MessageFormat;

/**
 * Created by Dor on 10/29/15.
 */
public class DiscoverEventListFragment extends BaseEventListFragment {

    public static final String TAG = DiscoverEventListFragment.class.getSimpleName();

    private static final String EXTRA_ACTIVITY_ID = "extra.activity.id";
    private static final String EXTRA_EVENT_HOLDER = "extra.event.holder";
    private static final String TYPE_EVENTS = "events";
    private static final String DISCOVER_EVENTS = "discover_events"; // A dummy string for the event map

    private String mActivityId;

    public static BaseEventListFragment getInstance(DataEventHolder eventHolder, String activityId) {
        DiscoverEventListFragment fragment = new DiscoverEventListFragment();
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_EVENT_HOLDER, eventHolder);
        args.putString(EXTRA_ACTIVITY_ID, activityId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataEventHolder dataEventHolder = (DataEventHolder) getArguments().getSerializable(EXTRA_EVENT_HOLDER);
        mEventMap.put(DISCOVER_EVENTS, dataEventHolder);
        mActivityId = getArguments().getString(EXTRA_ACTIVITY_ID);
        mCurrentEventKey = DISCOVER_EVENTS;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListHeader();
    }

    private void setListHeader(){
        mAbsListView.setPadding(mAbsListView.getPaddingLeft(), 0, mAbsListView.getPaddingRight(), mAbsListView.getPaddingBottom());
        LayoutInflater.from(getActivity()).inflate(R.layout.view_list_header_text, mTopContainer, true);
        TextView topText = (TextView) mTopContainer.findViewById(R.id.view_list_header_text);

        try {
            topText.setText(dm.getResourceText(new MessageFormat(DataManager.getInstance().getResourceText
                    (R.string._0_Events_Found)).format(new Object[]{mEventMap.get(DISCOVER_EVENTS).count})));
        } catch (Exception e) {
        }
    }

    @Override
    public EventsAdapter createAdapter() {
        return mAdapter = new EventsAdapter(mEventMap.get(DISCOVER_EVENTS).getEvents(), getActivity(), EventsAdapter.TYPE_MATCH);
    }

    @Override
    protected void setHeader() {
        mEventHeader.setListener(this);
        mEventHeader.getBackButton().setText(Consts.Icons.icon_ArrowBack);
        mEventHeader.getIconOne().setVisibility(View.GONE);
        mEventHeader.getIconTwo().setVisibility(View.GONE);
        mEventHeader.getIconThree().setVisibility(View.GONE);
        mEventHeader.setTitle(Utils.makeTextCamelCase(dm.getResourceText(R.string.Suggested_Events)));
    }

    @Override
    protected void fetchNextPage() {
        RequestDiscover requestDiscover = new RequestDiscover(mActivityId, TYPE_EVENTS, mAdapter.getCount(), PAGE_COUNT);
        ServerConnector.getInstance().processRequest(requestDiscover, new ServerConnector.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                EMLog.i(TAG, "onSuccess");

                ResponseDiscover responseDiscover = (ResponseDiscover) baseResponse;

                if (responseDiscover == null){
                    setNoMoreResults();
                } else{
                    DiscoverEventListFragment.super.addEventsToAdapter(DISCOVER_EVENTS, responseDiscover.getEventHolder().getEvents());
                }

                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(ErrorResponse errorResponse) {
                EMLog.i(TAG, "onFailure");
            }
        }, TAG + RequestDiscover.class.getSimpleName());
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
