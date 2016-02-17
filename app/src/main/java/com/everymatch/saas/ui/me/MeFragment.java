package com.everymatch.saas.ui.me;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.server.Data.DataActivity;
import com.everymatch.saas.server.responses.ResponseGetUser;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.ui.base.BaseFragment;
import com.everymatch.saas.ui.discover.DiscoverActivity;
import com.everymatch.saas.ui.questionnaire.QuestionnaireActivity;
import com.everymatch.saas.util.IconManager;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.EventDataRow;
import com.everymatch.saas.view.EventHeader;
import com.everymatch.saas.view.ViewSeperator;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends BaseFragment implements EventHeader.OnEventHeader, AdapterView.OnItemClickListener, View.OnClickListener {
    public static final String TAG = MeFragment.class.getSimpleName();
    private static final int REQUEST_CODE_EDIT_ACTIVITY = 123;
    private EventHeader mHeader;
    private boolean isClicked = false;
    ResponseGetUser user;

    //Views
    CircularImageView imgUser;
    Button btnAddProfile;
    private MeCallback meCallback;
    TextView tvAddProfile;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MeCallback) {
            meCallback = (MeCallback) context;
        } else {
            throw new IllegalArgumentException(context + " must implements MeCallback");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        user = ds.getUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_me, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);
        //mHeader = (EventHeader) v.findViewById(R.id.fragment_me_header);
        mHeader = ((DiscoverActivity) getActivity()).getmHeader();
        ((DiscoverActivity) getActivity()).setSelectedMenuItem(DiscoverActivity.DISCOVER_MENU_ITEMS.MORE);

        imgUser = (CircularImageView) v.findViewById(R.id.imgMeImage);
        tvAddProfile = (TextView) v.findViewById(R.id.tvAddProfile);
        updateUI(v);

        btnAddProfile.setOnClickListener(this);
        tvAddProfile.setOnClickListener(this);
        setHeader();
        addActivities(v);
    }

    private void updateUI(View v) {
        imgUser.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams params = imgUser.getLayoutParams();
                params.height = params.width;
                imgUser.setLayoutParams(params);
                imgUser.requestLayout();

                String imageUrl = Utils.getImageUrl(ds.getUser().image_url, imgUser.getLayoutParams().width, imgUser.getLayoutParams().width);
                Picasso.with(getContext()).load(imageUrl).into(imgUser);
            }
        });
        ((TextView) v.findViewById(R.id.tvMeUserName)).setText(ds.getUser().getName());
        ((TextView) v.findViewById(R.id.tvMeMemberSince)).setText(Utils.getDateStringFromDataDate(ds.getUser().created_date, "MMM,yyyy"));

        EventDataRow eventDataRowSettings = (EventDataRow) v.findViewById(R.id.event_row_me_settings);
        eventDataRowSettings.setOnClickListener(this);
        eventDataRowSettings.setTitle(dm.getResourceText(R.string.Settings));

        v.findViewById(R.id.event_row_me_profile).setOnClickListener(this);
        ((EventDataRow) v.findViewById(R.id.event_row_me_profile)).setTitle(dm.getResourceText(R.string.Profile_Label));

        btnAddProfile = (Button) v.findViewById(R.id.btnMeAddProfile);
    }

    private void setHeader() {
        mHeader.setListener(this);
        mHeader.getBackButton().setText(Consts.Icons.icon_ArrowBack);
        mHeader.getIconOne().setVisibility(View.GONE);
        mHeader.getIconTwo().setVisibility(View.GONE);
        mHeader.getIconThree().setVisibility(View.GONE);
        mHeader.setTitle(dm.getResourceText(R.string.More));
        mHeader.getTitle().setOnClickListener(null);
        mHeader.setArrowDownVisibility(false);
    }

    private void addActivities(View v) {
        LinearLayout linearLayout = (LinearLayout) v.findViewById(R.id.llMeActivities);
        linearLayout.removeAllViews();

        String str[] = user.user_settings.user_activity_profile_id_list.split(",");
        ArrayList<DataActivity> tmp = new ArrayList<DataActivity>();
        for (String id : str) {
            final DataActivity activity = ds.getApplicationData().getActivityById(id);
            if (activity != null) {
                tmp.add(activity);

                EventDataRow row = new EventDataRow(getActivity());
                row.setTitle((activity.text_title));
                row.getRightIcon().setVisibility(View.GONE);
                row.getDetailsView().setVisibility(View.GONE);
                row.getLeftIcon().setText(IconManager.getInstance(getActivity()).getIconString(activity.icon.getValue()));
                row.getLeftIcon().setTextColor(ds.getIntColor(EMColor.NIGHT));
                row.getRightText().setTextColor(ds.getIntColor(EMColor.NIGHT));
                row.setBackgroundColor(DataStore.getInstance().getIntColor(EMColor.WHITE));
                linearLayout.addView(row);
                linearLayout.addView(new ViewSeperator(getActivity(), null));
                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        QuestionnaireActivity.editActivity(MeFragment.this, Integer.valueOf(activity.client_id), REQUEST_CODE_EDIT_ACTIVITY);
                    }
                });
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
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

    }

    @Override
    public void onThreeIconClicked() {
        if (!isClicked) {
            mHeader.getTitle().setVisibility(View.GONE);
            mHeader.getEditTitle().setVisibility(View.VISIBLE);
            mHeader.getEditTitle().setFocusable(true);

            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputMethodManager.toggleSoftInputFromWindow(mHeader.getEditTitle().getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);

            isClicked = true;
        } else {
            mHeader.getTitle().setVisibility(View.VISIBLE);
            mHeader.setTitle(dm.getResourceText(R.string.Me));
            mHeader.getEditTitle().setVisibility(View.GONE);

            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);

            isClicked = false;
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.event_row_me_settings:
                meCallback.onSettingsClick();
                break;
            case R.id.btnMeAddProfile:
                if (ds.getUser().profiles.getActivity_profiles().size() == ds.getApplicationData().getActivities().length) {
                    Toast.makeText(getActivity(), "You already filled all profiles!", Toast.LENGTH_SHORT).show();
                    return;
                }
                meCallback.onAddProfileClick();
                break;
            case R.id.tvAddProfile:
                if (ds.getUser().profiles.getActivity_profiles().size() == ds.getApplicationData().getActivities().length) {
                    Toast.makeText(getActivity(), "You already filled all profiles!", Toast.LENGTH_SHORT).show();
                    return;
                }
                meCallback.onAddProfileClick();
                break;
            case R.id.event_row_me_profile:
                meCallback.onProfileClick();
                break;
        }
    }

    public interface MeCallback {
        void onProfileClick();

        void onSettingsClick();

        void onAddProfileClick();
    }

    @Override
    protected void handleBroadcast(Serializable eventData, String eventName) {
        //  EMLog.i(TAG, "eventName " + eventName + "\neventData = " + eventData);
        updateUI(getView());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Tell caller activity that a profile activity has been edited
        if (requestCode == REQUEST_CODE_EDIT_ACTIVITY && resultCode == Activity.RESULT_OK) {
            getActivity().setResult(Activity.RESULT_OK);
            //getActivity().finish();
        }
    }
}
