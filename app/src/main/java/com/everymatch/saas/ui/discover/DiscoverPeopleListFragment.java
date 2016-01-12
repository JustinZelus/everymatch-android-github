package com.everymatch.saas.ui.discover;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.adapter.PeopleUsersAdapter;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.Data.DataPeopleHolder;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.server.requests.RequestDiscover;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.server.responses.ResponseDiscover;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.ui.base.BasePeopleListFragment;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.Utils;

import java.text.MessageFormat;

/**
 * Created by dors on 10/28/15.
 */
public class DiscoverPeopleListFragment extends BasePeopleListFragment {

    public static final String TAG = DiscoverPeopleListFragment.class.getSimpleName();

    private static final String TYPE_USERS = "users";
    /*for load more data*/
    protected static final String EXTRA_ACTIVITY_ID = "extra.id";


    private String mActivityId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMode = DataStore.ADAPTER_MODE_MATCH;
        mActivityId = getArguments().getString(EXTRA_ACTIVITY_ID);
        mPeopleHolder = (DataPeopleHolder) getArguments().getSerializable(EXTRA_PEOPLE_HOLDER);
    }

    /**
     * Discover instance
     */
    public static BasePeopleListFragment getInstance(DataPeopleHolder holder, String activityId) {
        DiscoverPeopleListFragment peopleListFragment = new DiscoverPeopleListFragment();
        Bundle args = new Bundle(2);
        args.putSerializable(EXTRA_PEOPLE_HOLDER, holder);
        args.putString(EXTRA_ACTIVITY_ID, activityId);
        peopleListFragment.setArguments(args);
        return peopleListFragment;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHeader();
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
                    (R.string._0_Users_Found)).format(new Object[]{mPeopleHolder.count})));
        } catch (Exception e) {
        }
    }

    @Override
    protected void setHeader() {
        mEventHeader.setListener(this);
        mEventHeader.getBackButton().setText(Consts.Icons.icon_ArrowBack);
        mEventHeader.getIconOne().setVisibility(View.GONE);
        mEventHeader.getIconTwo().setVisibility(View.GONE);
        mEventHeader.getIconThree().setVisibility(View.GONE);
        mEventHeader.setTitle(Utils.makeTextCamelCase(dm.getResourceText(R.string.People_Matches)));
    }

    @Override
    protected void setActionButtons() {
        // Nothing in here
    }

    @Override
    public PeopleUsersAdapter createAdapter() {
        return  new PeopleUsersAdapter(getActivity(), mPeopleHolder.getUsers(), mMode);
    }

    @Override
    protected void fetchNextPage() {
        final RequestDiscover requestDiscover = new RequestDiscover(mActivityId, TYPE_USERS, mAdapter.getCount(), PAGE_COUNT);
        ServerConnector.getInstance().processRequest(requestDiscover, new ServerConnector.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                EMLog.i(TAG, "onSuccess");
                ResponseDiscover responseDiscover = (ResponseDiscover) baseResponse;

                if (responseDiscover == null){
                    setNoMoreResults();
                } else{
                    DiscoverPeopleListFragment.super.addUsersToAdapter(responseDiscover.getPeopleHolder().getUsers());
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
    public void onDetach() {
        ServerConnector.getInstance().cancelPendingRequests(TAG + RequestDiscover.class.getSimpleName());
        super.onDetach();
    }
}
