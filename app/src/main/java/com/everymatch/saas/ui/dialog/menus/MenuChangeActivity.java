package com.everymatch.saas.ui.dialog.menus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.server.Data.DataActivity;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.ui.discover.DiscoverFragment;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.EventDataRow;
import com.everymatch.saas.view.ViewSeperator;

import java.util.ArrayList;

/**
 * Created by PopApp_laptop on 27/01/2016.
 */
public class MenuChangeActivity extends BaseMenuDialogFragment implements View.OnClickListener {

    public static final int REQUEST_CODE_CHANGE_ACTIVITY = 103;
    private ArrayList<DataActivity> activities = ds.getUser().getUserActivities();

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        menuContainer.removeAllViews();

        int white = ds.getIntColor(EMColor.WHITE);

        //Add title
        EventDataRow edrTitle = new EventDataRow(getActivity());
        edrTitle.setTitle(DataManager.getInstance().getResourceText(R.string.Select_Profile));
        edrTitle.getRightIcon().setVisibility(View.GONE);
        edrTitle.getLeftIcon().setText("");
        edrTitle.setRightText(DataManager.getInstance().getResourceText(R.string.Add_activity_profile));
        edrTitle.getRightText().setTextColor(white);
        edrTitle.getLeftMediaContainer().setVisibility(View.GONE);
        edrTitle.setDetails(null);
        edrTitle.getWrapperLayout().setBackgroundColor(ds.getIntColor(EMColor.PRIMARY));
        edrTitle.getTitleView().setTextColor(white);

        ((LinearLayout) (edrTitle.getRightText().getParent())).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DiscoverFragment) getTargetFragment()).onAddActivityClick();
                dismiss();
            }
        });
        menuContainer.addView(edrTitle);
        menuContainer.addView(new ViewSeperator(getActivity(), null));

        int i = 0;
        for (DataActivity activity : activities) {
            EventDataRow edr = new EventDataRow(getActivity());
            edr.setTag(activity);
            edr.setTitle(activity.text_title);
            edr.setLeftIconOrImage(activity.icon);
            edr.getLeftIcon().setTextColor(white);
            edr.setRightIconText(Consts.Icons.icon_Details);
            edr.getRightIcon().setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
            int padding = Utils.dpToPx(7);
            edr.getRightIcon().setPadding(padding, padding, padding, padding);
            edr.getRightIcon().setTextColor(white);
            edr.getRightIcon().setTag(activity);
            edr.getRightIcon().setOnClickListener(onEditClick);
            edr.setDetails(null);
            edr.getWrapperLayout().setBackgroundColor(ds.getIntColor(EMColor.PRIMARY));
            edr.getTitleView().setTextColor(white);
            edr.setOnClickListener(MenuChangeActivity.this);
            menuContainer.addView(edr);
            if (activities.size() - 1 != i)
                menuContainer.addView(new ViewSeperator(getActivity(), null));
            i++;
        }
    }

    View.OnClickListener onEditClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DataActivity activity = (DataActivity) v.getTag();
            ((DiscoverFragment) getTargetFragment()).onEditActivityClick(activity);
            dismiss();
        }
    };


    @Override
    public void onClick(View v) {
        DataActivity activity = (DataActivity) v.getTag();
        ((DiscoverFragment) getTargetFragment()).onActivitySelected(activity);
        dismiss();
    }
}
