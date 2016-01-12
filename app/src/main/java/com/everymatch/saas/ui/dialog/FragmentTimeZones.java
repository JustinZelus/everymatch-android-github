package com.everymatch.saas.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ListView;

import com.everymatch.saas.R;
import com.everymatch.saas.adapter.AdapterTimeZone;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.ui.base.BaseFragment;
import com.everymatch.saas.ui.me.settings.SettingsFragment;
import com.everymatch.saas.util.ShapeDrawableUtils;
import com.everymatch.saas.view.EventHeader;

/**
 * Created by PopApp_laptop on 22/11/2015.
 */
public class FragmentTimeZones extends BaseFragment implements View.OnClickListener, EventHeader.OnEventHeader {
    ListView mListView;
    AdapterTimeZone mAdapter;
    Button btnSelect;
    private EventHeader mHeader;
    private boolean isClicked = false;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new AdapterTimeZone(DataStore.getInstance().getApplicationData().getTime_zone(), getActivity());

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_time_zones, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        //getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        //setStyle(DialogFragment.STYLE_NO_FRAME, android.R.style.Theme);
        mHeader = (EventHeader) view.findViewById(R.id.eventHeader);
        setHeader();

        mListView = (ListView) view.findViewById(R.id.listViewTimeZone);
        btnSelect = (Button) view.findViewById(R.id.btnSelect);

        btnSelect.setBackgroundDrawable(ShapeDrawableUtils.getRoundendButton());
        btnSelect.setOnClickListener(this);
        mListView.setAdapter(mAdapter);
    }

    private void setHeader() {
        mHeader.getBackButton().setText(Consts.Icons.icon_New_Close);
        mHeader.getIconOne().setText("Time Zone");

        mHeader.setListener(this);
        mHeader.getBackButton().setText(Consts.Icons.icon_ArrowBack);
        mHeader.getIconOne().setVisibility(View.GONE);
        mHeader.getIconTwo().setText(Consts.Icons.icon_Search);
        mHeader.getIconThree().setVisibility(View.GONE);
        mHeader.setTitle("Time Zone");

        mHeader.getEditTitle().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                mAdapter.getFilter().filter(s);
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent result = new Intent();
        result.putExtra(SettingsFragment.EXTRA_TIME_ZONE, mAdapter.getSelectedTimeZone());
        getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, result);
        getActivity().onBackPressed();
    }

    @Override
    public void onBackButtonClicked() {
        getActivity().onBackPressed();
    }

    @Override
    public void onOneIconClicked() {

    }

    @Override
    public void onTwoIconClicked() {
        if (!isClicked) {
            mHeader.getTitle().setVisibility(View.GONE);
            mHeader.getEditTitle().setVisibility(View.VISIBLE);
            mHeader.getEditTitle().setFocusable(true);

            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInputFromWindow(mHeader.getEditTitle().getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

            isClicked = true;
        } else {
            mHeader.getTitle().setVisibility(View.VISIBLE);
            mHeader.setTitle("Events");
            mHeader.getEditTitle().setVisibility(View.GONE);

            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);

            isClicked = false;

            mAdapter.cancelSearch();
            mHeader.getEditTitle().setText("");
        }
    }

    @Override
    public void onThreeIconClicked() {

    }
}
