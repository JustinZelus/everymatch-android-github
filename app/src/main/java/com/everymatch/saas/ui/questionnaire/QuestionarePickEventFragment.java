package com.everymatch.saas.ui.questionnaire;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListPopupWindow;
import android.widget.ListView;
import android.widget.Toast;

import com.everymatch.saas.R;
import com.everymatch.saas.adapter.AdapterListSelect;
import com.everymatch.saas.adapter.DiscoverMoreAdapter;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.client.data.PopupMenuItem;
import com.everymatch.saas.server.Data.DataActivity;
import com.everymatch.saas.server.Data.DataEvent_Activity;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.ui.base.BaseFragment;
import com.everymatch.saas.util.IconManager;
import com.everymatch.saas.view.BaseIconTextView;
import com.everymatch.saas.view.BaseTextView;
import com.everymatch.saas.view.EventHeader;

import java.util.ArrayList;

/**
 * Created by PopApp_laptop on 12/10/2015.
 */
public class QuestionarePickEventFragment extends BaseFragment implements View.OnClickListener, AdapterListSelect.AdapterListSelectCallback, EventHeader.OnEventHeader {

    ArrayList<DataActivity> activities = ds.getUser().getUserActivities();
    int selectedPosition;
    DataActivity selectedActivity;
    private EventHeader mHeader;
    private ListPopupWindow mMorePopup;
    DiscoverMoreAdapter adapter;

    //Views
    BaseIconTextView tvActivityIcon;
    BaseTextView tvActivityName;
    ListView listView;
    AdapterListSelect adapterEvents;
    private int selectedEventPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (activities.size() == 0) {
            Toast.makeText(getActivity(), "No activities", Toast.LENGTH_SHORT).show();
            getActivity().onBackPressed();
        }

        ArrayList<PopupMenuItem> list = new ArrayList<>();
        for (DataActivity activity : activities)
            list.add(new PopupMenuItem(activity.text_title, activity.icon.getValue()));

        //TODO - set default selected activity
        selectedPosition = 0;

        adapter = new DiscoverMoreAdapter(list);
        adapterEvents = new AdapterListSelect(new ArrayList<String>(), getActivity(), this);
        adapterEvents.setIsMultiSelect(false);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_event_select_activity, container, false);

        //Reference
        tvActivityIcon = (BaseIconTextView) v.findViewById(R.id.createEventSelectActivityIconLeft);
        tvActivityName = (BaseTextView) v.findViewById(R.id.tvCreateEventSelectActivityName);
        listView = (ListView) v.findViewById(R.id.listView);
        listView.setAdapter(adapterEvents);

        //Clicks
        v.findViewById(R.id.createEventSelectActivitySubTitleHolder).setOnClickListener(this);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //mHeader = (EventHeader) getActivity().findViewById(R.id.eventHeader);
        mHeader = (EventHeader) view.findViewById(R.id.eventHeader);

        mHeader.setListener(this);
        mHeader.getBackButton().setText(Consts.Icons.icon_ArrowBack);
        mHeader.getIconOne().setVisibility(View.VISIBLE);
        mHeader.getIconTwo().setVisibility(View.GONE);
        mHeader.getIconThree().setVisibility(View.GONE);
        mHeader.getIconOne().setText(DataManager.getInstance().getResourceText(R.string.Start_Button));
        updateUi();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.createEventSelectActivitySubTitleHolder:
                if (mMorePopup != null && mMorePopup.isShowing()) {
                    mMorePopup.dismiss();
                    return;
                }
                showMoreMenu();
                break;
        }
    }

    private void showMoreMenu() {
        if (mMorePopup == null) {
            mMorePopup = new ListPopupWindow(getActivity());
        }

        mMorePopup.setAdapter(adapter);
        mMorePopup.setWidth(((LinearLayout) tvActivityName.getParent()).getWidth());
        mMorePopup.setAnchorView((LinearLayout) tvActivityName.getParent());

        mMorePopup.setAnimationStyle(R.anim.abc_fade_in);
        mMorePopup.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectedPosition != position) {
                    selectedPosition = position;
                    updateUi();
                }
                mMorePopup.dismiss();
            }
        });

        mMorePopup.setModal(true);
        mMorePopup.show();
    }

    private void updateUi() {
        selectedActivity = activities.get(selectedPosition);
        tvActivityName.setText(selectedActivity.text_title);
        tvActivityIcon.setText(IconManager.getInstance(getActivity()).getIconString(selectedActivity.icon.getValue()));

        //update event list
        ArrayList<String> eventsStrings = new ArrayList<>();
        for (DataEvent_Activity dataEvent : selectedActivity.getEvents())
            eventsStrings.add(dataEvent.text_title);

        adapterEvents.updateData(eventsStrings);
        mHeader.getIconOne().setText(adapterEvents.getSelectedCount() == 0 ? "" : "Start");
    }

    /**
     * called when an event is clicked
     *
     * @param item
     * @param position
     */
    @Override
    public void onSelect(Object item, int position, boolean enabled) {
        mHeader.getIconOne().setText(adapterEvents.getSelectedCount() == 0 ? "" : DataManager.getInstance().getResourceText(R.string.Start_Button));
        if (enabled)
            selectedEventPosition = position;
    }

    @Override
    public void onBackButtonClicked() {
        getActivity().onBackPressed();
    }

    @Override
    public void onOneIconClicked() {
        //set indexes to 0 because we have no welcome page
        //((QuestionnaireActivity) getActivity()).mQuestionIndex = 0;
        //((QuestionnaireActivity) getActivity()).mCurrentQuestionIndex = 0;

        ((QuestionnaireActivity) getActivity()).mDataActivity = selectedActivity;
        ((QuestionnaireActivity) getActivity()).prepareArrays(selectedActivity.getEvents()[selectedEventPosition].questions);
        ((QuestionnaireActivity) getActivity()).mDataEvent_activity = selectedActivity.getEvents()[selectedEventPosition];
        ((QuestionnaireActivity) getActivity()).goToNextQuestion(null);
    }

    @Override
    public void onTwoIconClicked() {

    }

    @Override
    public void onThreeIconClicked() {

    }
}
