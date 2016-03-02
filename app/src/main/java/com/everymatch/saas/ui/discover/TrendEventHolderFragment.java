package com.everymatch.saas.ui.discover;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.everymatch.saas.R;
import com.everymatch.saas.server.Data.DataEvent;
import com.everymatch.saas.server.Data.ResponseTrendEvent;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.server.requests.RequestDiscover;
import com.everymatch.saas.server.requests.RequestTrends;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.ui.base.BaseFragment;
import com.everymatch.saas.util.EMLog;

import java.util.ArrayList;

import cn.trinea.android.view.autoscrollviewpager.AutoScrollViewPager;

/**
 * Created by PopApp_laptop on 01/03/2016.
 */
public class TrendEventHolderFragment extends BaseFragment {
    public static final String TAG = "TrendEventHolderFragment";

    public static final String ARG_ACTIVITY_CLIENT_ID = "arg.activity.client.id";
    public static final String ARG_COLLECTION_NAME = "arg.collection.name";

    //DATA
    public ArrayList<TrendEventFragment> fragments;
    private TrendAdapter adapter;
    private String activityClientId;
    private String collectionName;

    //VIEWS
    AutoScrollViewPager viewPager;
    LinearLayout holder;

    public static TrendEventHolderFragment getInstance(String activityClientId, String collectionName) {
        TrendEventHolderFragment answer = new TrendEventHolderFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ARG_ACTIVITY_CLIENT_ID, activityClientId);
        bundle.putString(ARG_COLLECTION_NAME, collectionName);

        answer.setArguments(bundle);
        return answer;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        collectionName = getArguments().getString(ARG_COLLECTION_NAME);
        activityClientId = getArguments().getString(ARG_ACTIVITY_CLIENT_ID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_trend_event_holder, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewPager = (AutoScrollViewPager) view.findViewById(R.id.trendingEventViewPager);
        viewPager.setCycle(true);
        viewPager.setInterval(8000);

        holder = (LinearLayout) view.findViewById(R.id.holder);
        if (fragments == null) {
            fetchEvents();
            return;
        }

        setAdapter();
    }

    public void fetchEvents() {
        fragments = new ArrayList<>();
       if(adapter!=null) adapter.notifyDataSetChanged();

        ServerConnector.getInstance().processRequest(new RequestTrends(fragments.size(), 10, collectionName, activityClientId), new ServerConnector.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                EMLog.i(TAG, "onSuccess");
                ResponseTrendEvent responseTrendEvent = (ResponseTrendEvent) baseResponse;
                if (responseTrendEvent == null)
                    return;

                for (DataEvent dataEvent : responseTrendEvent.getEvents()) {
                    TrendEventFragment fragment = TrendEventFragment.getInstance(dataEvent);
                    fragments.add(fragment);
                }

                setAdapter();
            }

            @Override
            public void onFailure(ErrorResponse errorResponse) {
                EMLog.i(TAG, "onFailure");
            }
        }, TAG + RequestDiscover.class.getSimpleName());
    }

    private void setAdapter() {
        if (fragments.size() == 0) {
            holder.setVisibility(View.GONE);
            return;
        }

        holder.setVisibility(View.VISIBLE);
        if (adapter == null)
            adapter = new TrendAdapter(getChildFragmentManager());
        viewPager.setOffscreenPageLimit(5);
        viewPager.setAdapter(adapter);
        viewPager.startAutoScroll();
    }

    public class TrendAdapter extends FragmentPagerAdapter {
        public TrendAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
