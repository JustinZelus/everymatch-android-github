package com.everymatch.saas.ui.dialog.menus;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ViewAnimator;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.server.Data.DataActivity;
import com.everymatch.saas.server.Data.DataEvent_Activity;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.ui.questionnaire.QuestionnaireActivity;
import com.everymatch.saas.util.IconManager;
import com.everymatch.saas.view.EventDataRow;
import com.everymatch.saas.view.ViewSeperator;

import java.util.ArrayList;

/**
 * Created by PopApp_laptop on 24/01/2016.
 */
public class MenuCreateEvent extends BaseMenuDialogFragment {

    public static final String ARG_START_HEIGHT = "arg.start.height";
    public static final int REQUEST_CODE_CREATE_EVENT = 101;
    //Data
    ArrayList<DataActivity> activities = ds.getUser().getUserActivities();
    private int startHeight;
    private int selectedActivityId;
    private String selectedEventId;


    //Views
    LinearLayout EventsListHolder, ActivitiesListHolder;
    private ViewAnimator viewAnimator;

    public static MenuCreateEvent getInstance(int startHeight) {
        MenuCreateEvent answer = new MenuCreateEvent();
        Bundle bundle = new Bundle(1);
        bundle.putInt(ARG_START_HEIGHT, startHeight);
        answer.setArguments(bundle);
        return answer;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        startHeight = getArguments().getInt(ARG_START_HEIGHT);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        inflater.inflate(R.layout.menu_create_event, (ViewGroup) view.findViewById(R.id.menuDialogContainer));
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //View holder = LayoutInflater.from(getActivity()).inflate(R.layout.menu_create_event, menuContainer);
        EventsListHolder = (LinearLayout) view.findViewById(R.id.rlCreateEventEventsListHolder);
        ActivitiesListHolder = (LinearLayout) view.findViewById(R.id.rlCreateEventActivitiesListHolder);
        viewAnimator = (ViewAnimator) view.findViewById(R.id.viewAnimator);

        viewAnimator.setInAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_in_left));
        viewAnimator.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), android.R.anim.slide_out_right));

        addActivities();
    }

    private void addActivities() {
        ActivitiesListHolder.removeAllViews();

        int i = 0;
        for (final DataActivity dataActivity : activities) {
            EventDataRow edr = new EventDataRow(getActivity());
            edr.setTag(dataActivity);
            edr.setOnClickListener(onActivityClick);
            edr.setTitle(dataActivity.text_title);
            edr.getRightIcon().setVisibility(View.GONE);
            edr.getLeftIcon().setText(IconManager.getInstance(getActivity()).getIconString(dataActivity.icon.getValue()));
            edr.getLeftIcon().setTextColor(ds.getIntColor(EMColor.WHITE));
            edr.setDetails(null);
            edr.getWrapperLayout().setBackgroundColor(ds.getIntColor(EMColor.PRIMARY));
            edr.getTitleView().setTextColor(ds.getIntColor(EMColor.WHITE));
            ActivitiesListHolder.addView(edr);

            if (activities.size() - 1 != i)
                ActivitiesListHolder.addView(new ViewSeperator(getActivity(), null));
            i++;
        }
    }

    private void addEvents(DataActivity dataActivity) {
        EventsListHolder.removeAllViews();

        //Add Back Arrow
        EventDataRow edrBack = new EventDataRow(getActivity());
        edrBack.setTitle(DataManager.getInstance().getResourceText(R.string.Back));
        edrBack.getRightIcon().setVisibility(View.GONE);
        edrBack.getLeftIcon().setText(Consts.Icons.icon_ArrowBack);
        edrBack.getLeftIcon().setTextColor(ds.getIntColor(EMColor.PRIMARY));
        edrBack.setDetails(null);
        edrBack.getWrapperLayout().setBackgroundColor(ds.getIntColor(EMColor.WHITE));
        edrBack.getTitleView().setTextColor(ds.getIntColor(EMColor.PRIMARY));

        edrBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewAnimator.showNext();
            }
        });
        EventsListHolder.addView(edrBack);
        EventsListHolder.addView(new ViewSeperator(getActivity(), null));

        //Add Events Rows
        int i = 0;
        for (DataEvent_Activity event : dataActivity.events) {
            EventDataRow edr = new EventDataRow(getActivity());
            edr.setTag(event);
            edr.setOnClickListener(onEventClick);
            edr.setTitle(event.text_title);
            edr.getRightIcon().setVisibility(View.GONE);
            edr.getLeftIcon().setText(IconManager.getInstance(getActivity()).getIconString(event.icon.getValue()));
            edr.getLeftIcon().setTextColor(ds.getIntColor(EMColor.PRIMARY));
            edr.setDetails(null);
            edr.getWrapperLayout().setBackgroundColor(ds.getIntColor(EMColor.WHITE));
            edr.getTitleView().setTextColor(ds.getIntColor(EMColor.PRIMARY));
            EventsListHolder.addView(edr);

            if (dataActivity.events.length - 1 != i)
                EventsListHolder.addView(new ViewSeperator(getActivity(), null));
            i++;
        }
    }

    View.OnClickListener onActivityClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DataActivity dataActivity = (DataActivity) v.getTag();
            selectedActivityId = dataActivity._id;
            viewAnimator.showNext();
            addEvents(dataActivity);
        }
    };

    View.OnClickListener onEventClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DataEvent_Activity dataEventActivity = (DataEvent_Activity) v.getTag();
            selectedEventId = dataEventActivity.event_id;
            Intent i = new Intent();
            i.putExtra(QuestionnaireActivity.EXTRA_ACTIVITY_ID, selectedActivityId);
            i.putExtra(QuestionnaireActivity.EXTRA_SELECTED_EVENT_ID, selectedEventId);
            getTargetFragment().onActivityResult(REQUEST_CODE_CREATE_EVENT, Activity.RESULT_OK, i);
            dismiss();
        }
    };

    @Override
    protected void startDialogAnimation() {
        ObjectAnimator translateAnimator;

        translateAnimator = ObjectAnimator.ofFloat(menuContainer, View.TRANSLATION_Y.getName(), -menuContainer.getMeasuredHeight(), 0 /*startHeight*/);
        translateAnimator.setDuration(350);
        translateAnimator.setInterpolator(new AccelerateInterpolator());
        translateAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mIsAnimating = false;
            }
        });

        translateAnimator.start();
    }
}
