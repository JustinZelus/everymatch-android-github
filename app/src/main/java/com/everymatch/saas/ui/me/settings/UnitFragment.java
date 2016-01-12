package com.everymatch.saas.ui.me.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.AdapterView;

import com.everymatch.saas.adapter.AdapterDataRow;
import com.everymatch.saas.adapter.EmBaseAdapter;
import com.everymatch.saas.server.Data.ApplicationSettings;
import com.everymatch.saas.server.Data.units.DataWeight;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.ui.base.BaseListFragment;

/**
 * Created by PopApp_laptop on 15/12/2015.
 */
public class UnitFragment extends BaseListFragment implements AdapterView.OnItemClickListener {

    public static final String EXTRA_POSITION = "extra.position";
    AdapterDataRow<DataWeight> mAdapter;
    ApplicationSettings.UnitsHolder units;
    /*this is the unit position the user pressed on before*/
    private int unitPosition;

    public static UnitFragment getInstance(int position) {
        UnitFragment answer = new UnitFragment();
        Bundle bundle = new Bundle(1);
        bundle.putInt(EXTRA_POSITION, position);
        answer.setArguments(bundle);
        return answer;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        unitPosition = getArguments().getInt(EXTRA_POSITION);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        units = ds.getApplicationData().getSettings().getUnits();
    }

    @Override
    protected void initAdapter() {
        super.initAdapter();
        //mAbsListView.setDividerHeight(Utils.dpToPx(1));
        //mAbsListView.setPadding(0, 0, 0, 0);
        mAdapter = new AdapterDataRow<DataWeight>(units.getWeight(), getActivity(), AdapterDataRow.ADAPTER_MODE.MODE_SELECT_ONE) {
            @Override
            protected void handleViewCreation(int position, DataWeight item, ViewHolder holder) {
                holder.tvDetails.setVisibility(View.GONE);
                holder.tvTitle.setText((units.getUnitValueByPosition(unitPosition, position)));
            }
        };
        mAdapter.setSelectedPosition(units.getCurrentSelectedPositionByUserSettings(unitPosition));
        mAbsListView.setAdapter(mAdapter);
        mAbsListView.setOnItemClickListener(this);
    }

    @Override
    protected void setHeader() {
        super.setHeader();
        mEventHeader.setListener(this);
        mEventHeader.setTitle(dm.getResourceText(units.getUnitListNameByPosition(unitPosition)));
        mEventHeader.getBackButton().setText(Consts.Icons.icon_ArrowBack);
        mEventHeader.getIconOne().setVisibility(View.GONE);
        mEventHeader.getIconTwo().setVisibility(View.GONE);
        mEventHeader.getIconThree().setVisibility(View.GONE);
    }

    @Override
    protected void fetchNextPage() {

    }

    @Override
    public EmBaseAdapter getAdapter() {
        return null;
    }

    @Override
    public void onBackButtonClicked() {
        getActivity().onBackPressed();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mAdapter.setSelectedPosition(position);
        String value = units.getUnitValueByPosition(unitPosition, position);
        /*let's update user settings*/
        units.updateUserUnitByPositionAndValue(unitPosition, value);
    }
}
