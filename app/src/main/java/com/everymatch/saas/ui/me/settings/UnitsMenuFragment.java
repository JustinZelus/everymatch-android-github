package com.everymatch.saas.ui.me.settings;

import android.view.View;

import com.everymatch.saas.R;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.ui.BaseActivity;
import com.everymatch.saas.ui.base.BaseAbstractFragment;
import com.everymatch.saas.view.EventDataRow;
import com.everymatch.saas.view.ViewSeperator;

/**
 * Created by PopApp_laptop on 15/12/2015.
 */
public class UnitsMenuFragment extends BaseAbstractFragment {
    //Data

    //Views
    EventDataRow edrDistance, edrWeight;

    @Override
    protected void setHeader() {
        super.setHeader();
        mEventHeader.setTitle(dm.getResourceText(getString(R.string.Units)));
    }

    @Override
    protected void addRows() {
        //add distance
        edrDistance = new EventDataRow(getActivity());
        edrDistance.getLeftMediaContainer().setVisibility(View.GONE);
        edrDistance.setTitle(dm.getResourceText(R.string.Distance));
        edrDistance.setRightIconText(Consts.Icons.icon_Arrowright);
        edrDistance.setRightText(ds.getUser().user_settings.getDistance());
        edrDistance.setDetails(null);
        edrDistance.setTag(dm.getResourceText(R.string.Distance));
        edrDistance.setOnClickListener(clickListener);
        rowsContainer.addView(edrDistance);
        rowsContainer.addView(new ViewSeperator(getActivity(), null));

        //add weight
        edrWeight = new EventDataRow(getActivity());
        edrWeight.getLeftMediaContainer().setVisibility(View.GONE);
        edrWeight.setTitle(dm.getResourceText(R.string.Weight));
        edrWeight.setRightIconText(Consts.Icons.icon_Arrowright);
        edrWeight.setOnClickListener(clickListener);
        edrWeight.setRightText(ds.getUser().user_settings.weight);
        edrWeight.setDetails(null);
        edrWeight.setTag(dm.getResourceText(R.string.Weight));
        rowsContainer.addView(edrWeight);
        rowsContainer.addView(new ViewSeperator(getActivity(), null));
    }


    View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String type = (String) v.getTag();
            UnitFragment fragment = UnitFragment.getInstance(type);
            ((BaseActivity) getActivity()).replaceFragment(R.id.fragment_container, fragment,
                    SettingsFragment.TAG, true, null, R.anim.enter_from_right, R.anim.exit_to_left,
                    R.anim.enter_from_left, R.anim.exit_to_right);
        }
    };

}
