package com.everymatch.saas.ui.dialog.menus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.server.Data.DataEventHolder;
import com.everymatch.saas.view.EventDataRow;
import com.everymatch.saas.view.ViewSeperator;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PopApp_laptop on 06/03/2016.
 */
public class MenuMyEvents extends BaseMenuDialogFragment implements View.OnClickListener {
    public static final String ACTION_MENU_MY_EVENTS = "action.menu.my.events";
    public static final String EXTRA_EVENT_HOLDER = "extra.event.holder";
    public static final int REQUEST_CODE_MY_EVENTS = 109;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        menuContainer.removeAllViews();
        addRows();
    }

    private void addRows() {
        HashMap<String, DataEventHolder> map = ds.getUser().getAllEvents();

        for (Map.Entry<String, DataEventHolder> entry : map.entrySet()) {
            DataEventHolder eventHolder = entry.getValue();
            //String title = entry.getKey();
            EventDataRow edr = new EventDataRow(getActivity());
            edr.setTag(eventHolder);
            edr.setOnClickListener(this);
            edr.setTitle(eventHolder.text_title + " (" + eventHolder.getEvents().size() + ")");
            edr.getTitleView().setTextColor(ds.getIntColor(EMColor.WHITE));
            edr.getLeftIcon().setTextColor(ds.getIntColor(EMColor.WHITE));
            edr.setLeftIconOrImage(eventHolder.icon);
            edr.setDetails(null);
            edr.getWrapperLayout().setBackgroundColor(ds.getIntColor(EMColor.PRIMARY));
            menuContainer.addView(edr);
            menuContainer.addView(new ViewSeperator(getActivity(), null));
        }
    }

    @Override
    public void onClick(View v) {
        DataEventHolder eventHolder = (DataEventHolder) v.getTag();
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, new Intent(ACTION_MENU_MY_EVENTS).putExtra(EXTRA_EVENT_HOLDER, eventHolder));
        dismiss();
    }
}
