package com.everymatch.saas.ui.discover;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.everymatch.saas.R;
import com.everymatch.saas.server.Data.DataModelHolder;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.server.requests.RequestDataModel;
import com.everymatch.saas.server.requests.RequestDiscover;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.server.responses.ResponseDataModel;
import com.everymatch.saas.ui.base.BaseFragment;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.view.ViewDiscoverAd;

/**
 * Created by PopApp_laptop on 15/11/2015.
 */
public class FragmentDiscoverAd extends BaseFragment {
    public static final String TAG = "FragmentDiscoverAd";

    LinearLayout discoverAdHolder;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_discover_ad, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        discoverAdHolder = (LinearLayout) view.findViewById(R.id.discoverAdHolder);
        refreshData();
    }

    public void refreshData(){
        getData();
    }

    private void getData() {

        discoverAdHolder.removeAllViews();

        ServerConnector.getInstance().processRequest(new RequestDataModel(), new ServerConnector.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                EMLog.i(TAG, "onSuccess");
                ResponseDataModel responseDataModel = (ResponseDataModel) baseResponse;
                for (DataModelHolder.DataModel dataModel : responseDataModel.model) {
                    ViewDiscoverAd viewDiscoverAd = new ViewDiscoverAd(getActivity());
                    viewDiscoverAd.setModel(dataModel);

                    discoverAdHolder.addView(viewDiscoverAd);
                }

            }

            @Override
            public void onFailure(ErrorResponse errorResponse) {
                EMLog.i(TAG, "onFailure");
            }
        }, TAG + RequestDiscover.class.getSimpleName());
    }
}

