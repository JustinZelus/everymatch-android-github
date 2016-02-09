package com.everymatch.saas.ui.me.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.server.Data.units.DataUnit;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.ui.base.BaseAbstractFragment;
import com.everymatch.saas.view.EventDataRow;
import com.everymatch.saas.view.ViewSeperator;

import java.util.ArrayList;

/**
 * Created by PopApp_laptop on 15/12/2015.
 */
public class UnitFragment extends BaseAbstractFragment {
    public static final String ARG_UNIT_TYPE = "arg.unit.type";

    //Data
    String unitType;

    public static UnitFragment getInstance(String unitType) {
        UnitFragment answer = new UnitFragment();
        Bundle bundle = new Bundle(1);
        bundle.putString(ARG_UNIT_TYPE, unitType);
        answer.setArguments(bundle);
        return answer;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        unitType = getArguments().getString(ARG_UNIT_TYPE);
    }

    @Override
    protected void setHeader() {
        super.setHeader();
        mEventHeader.setTitle(unitType);
    }

    @Override
    protected void addRows() {
        ArrayList<DataUnit> units = new ArrayList<>();
        String userUnit = "";
        if (unitType.equals(dm.getResourceText(R.string.Distance))) {
            units = ds.getApplicationData().getSettings().getUnits().getDistance();
            userUnit = ds.getUser().user_settings.getDistance();
        } else if (unitType.equals(dm.getResourceText(R.string.Weight))) {
            units = ds.getApplicationData().getSettings().getUnits().getWeight();
            userUnit = ds.getUser().user_settings.weight;
        }

        //add Units
        for (DataUnit dataUnit : units) {
            EventDataRow edrDistance = new EventDataRow(getActivity());
            edrDistance.getLeftMediaContainer().setVisibility(View.GONE);
            edrDistance.setTitle(dm.getResourceText(dataUnit.name));
            edrDistance.setRightIconText(userUnit.equals(dataUnit.name) ? Consts.Icons.icon_StatusPositive : "");
            edrDistance.getRightIcon().setTextColor(ds.getIntColor(EMColor.PRIMARY));
            edrDistance.setRightText(null);
            edrDistance.setDetails(null);
            edrDistance.setTag(dataUnit);
            edrDistance.setOnClickListener(clickListener);
            rowsContainer.addView(edrDistance);
            rowsContainer.addView(new ViewSeperator(getActivity(), null));
        }
    }

    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DataUnit unit = (DataUnit) v.getTag();
            if (unitType.equals(dm.getResourceText(R.string.Distance))) {
                ds.getUser().user_settings.setDistance(unit.name);
            } else if (unitType.equals(dm.getResourceText(R.string.Weight))) {
                ds.getUser().user_settings.weight = unit.name;
            }
            getActivity().onBackPressed();
        }
    };
}
