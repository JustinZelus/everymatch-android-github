package com.everymatch.saas.ui.me.settings;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ToggleButton;

import com.everymatch.saas.BuildConfig;
import com.everymatch.saas.Constants;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.server.Data.DataTimeZone;
import com.everymatch.saas.server.Data.UserSettings;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.server.requests.RequestLoadProviders;
import com.everymatch.saas.server.requests.RequestUpdateSettings;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.server.responses.ResponseApplication;
import com.everymatch.saas.server.responses.ResponseLoadProviders;
import com.everymatch.saas.server.responses.ResponseString;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.singeltones.Preferences;
import com.everymatch.saas.singeltones.YesNoCallback;
import com.everymatch.saas.ui.BaseActivity;
import com.everymatch.saas.ui.WebViewActivity;
import com.everymatch.saas.ui.base.BaseFragment;
import com.everymatch.saas.ui.dialog.DialogYesNo;
import com.everymatch.saas.ui.dialog.FragmentCurrencies;
import com.everymatch.saas.ui.dialog.FragmentTimeZones;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.IconManager;
import com.everymatch.saas.util.ShapeDrawableUtils;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.EventDataRow;
import com.everymatch.saas.view.EventHeader;
import com.everymatch.ucpa.SplashActivity;
import com.google.gson.Gson;

/**
 * Created by PopApp_laptop on 06/10/2015.
 */
public class SettingsFragment extends BaseFragment implements EventHeader.OnEventHeader, View.OnClickListener {
    public static java.lang.String TAG = "SettingsFragment";

    private static final String EXTRA_ACCESS_TOKEN = "key";
    public static final String EXTRA_TIME_ZONE = "extra.time.zone";
    public static final String EXTRA_CURRENCY = "extra.currency";

    private static final int REQUEST_CODE_GET_TOKEN = 1;
    private static final int REQUEST_CODE_DIALOG_TIME_ZONE = 2;
    private static final int REQUEST_CODE_DIALOG_CURRENCY = 3;
    private static final int REQUEST_CODE_DIALOG_UNITS = 4;
    private static final int REQUEST_CODE_DIALOG_LANGUAGE = 5;

    //Views
    private EventHeader mHeader;
    EventDataRow edrTimeZone, edrCurrencies, edrUnits, edrLanguage;
    LinearLayout llProviders;
    ToggleButton toggleButton;

    //Data
    public static ResponseApplication.DataCulture userSelectedCulture;
    private boolean languageWasChanged = false;
    private boolean canSave = false;
    private int providerClickedPosition = -1;
    private boolean isClicked = false;
    private ResponseLoadProviders.Provider[] mProviders;

    /* first user data - for cancel click */
    String userSettingsOriginalJson;
    //*************************************

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        for (ResponseApplication.DataCulture culture : ds.getApplicationData().getCultures()) {
            if (culture.culture_name.equals(ds.getCulture())) {
                userSelectedCulture = culture;
                break;
            }
        }
        getProviders();
        //set original values
        userSettingsOriginalJson = new Gson().toJson(ds.getUser().user_settings);

        //distance = user.user_settings.getDistance();
        //weight = user.user_settings.weight;
        //default_culture = user.user_settings.default_culture;

        //***************************************
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        llProviders = (LinearLayout) v.findViewById(R.id.providers);

        /** Time Zone *********************************************/
        edrTimeZone = (EventDataRow) v.findViewById(R.id.edrTimeZone);
        edrTimeZone.getRightIcon().setVisibility(View.VISIBLE);
        edrTimeZone.setRightIconText(Consts.Icons.icon_Arrowright);
        edrTimeZone.setTitle(dm.getResourceText(R.string.Time_Zone));
        edrTimeZone.setLargePaddingTopBottom();
        edrTimeZone.setOnClickListener(this);
        edrTimeZone.setDetails(ds.getUser().user_settings.getTime_zone().title);

        /** Units *********************************************/
        edrUnits = (EventDataRow) v.findViewById(R.id.edrUnits);
        edrUnits.setRightIconText(Consts.Icons.icon_Arrowright);
        edrUnits.setDetails(ds.getUser().user_settings.getUnitsPromo());
        edrUnits.setTitle(dm.getResourceText(R.string.Units));
        edrUnits.setLargePaddingTopBottom();
        edrUnits.setOnClickListener(this);

        /** Currency *********************************************/
        edrCurrencies = (EventDataRow) v.findViewById(R.id.edrCurrency);
        edrCurrencies.setRightIconText(Consts.Icons.icon_Arrowright);
        edrCurrencies.setDetails(ds.getUser().user_settings.currency);
        edrCurrencies.setTitle(dm.getResourceText(R.string.Currency));
        edrCurrencies.setLargePaddingTopBottom();
        edrCurrencies.setOnClickListener(this);

        /** Language *********************************************/
        edrLanguage = (EventDataRow) v.findViewById(R.id.edrLanguage);
        edrLanguage.setRightIconText(Consts.Icons.icon_Arrowright);
        edrLanguage.setDetails(ds.getCulture());
        edrLanguage.setTitle(dm.getResourceText(R.string.Culture_text));
        edrLanguage.setLargePaddingTopBottom();
        edrLanguage.setOnClickListener(this);

        (v.findViewById(R.id.btnSettingsLogout)).setOnClickListener(this);
        (v.findViewById(R.id.btnSettingsLogout)).setBackgroundDrawable(ShapeDrawableUtils.getButtonStroked(ds.getIntColor(EMColor.NEGATIVE)));
        (v.findViewById(R.id.btnSettingsChangePassword)).setOnClickListener(this);
        (v.findViewById(R.id.btnSettingsChangePassword)).setBackgroundDrawable(ShapeDrawableUtils.getButtonStroked());


        toggleButton = (ToggleButton) v.findViewById(R.id.btnNetworkToggle);
        toggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Constants.API_SERVICE_URL = "https://api.everymatch.me/";
                    Constants.AUTH2_SERVICE_URL = "https://oauth2.everymatch.me/";
                } else {
                    Constants.API_SERVICE_URL = "http://192.168.1.101:4434/";
                    Constants.AUTH2_SERVICE_URL = "http://192.168.1.101:4432/";
                }
            }
        });
        if (Constants.API_SERVICE_URL.equals("https://api.everymatch.me/")) {
            toggleButton.setChecked(true);
        }

        toggleButton.setVisibility(BuildConfig.DEBUG ? View.VISIBLE : View.GONE);
        toggleButton.setVisibility(View.GONE);

        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHeader(view);
        if (mProviders != null) addProviders();
        updateUi();

        toggleButton.setVisibility(BuildConfig.DEBUG ? View.VISIBLE : View.GONE);
    }

    private void updateUi() {
        edrTimeZone.setDetails(ds.getUser().user_settings.getTime_zone().title);
        edrUnits.setDetails(ds.getUser().user_settings.getUnitsPromo());
        edrCurrencies.setDetails(ds.getUser().user_settings.currency);

        edrLanguage.setDetails(userSelectedCulture.text_title);
    }

    private void setHeader(View view) {
        mHeader = (EventHeader) view.findViewById(R.id.fragment_settings_header);
        mHeader.setListener(this);
        mHeader.getIconTwo().setVisibility(View.GONE);
        mHeader.getIconThree().setVisibility(View.GONE);
        mHeader.setSaveCancelMode(dm.getResourceText(R.string.Settings));
        setSaveEnable();
    }

    @Override
    public void onBackButtonClicked() {
        //restore values
        UserSettings userSettings = new Gson().fromJson(userSettingsOriginalJson, UserSettings.class);
        ds.getUser().user_settings.setTime_zone(userSettings.getTime_zone());
        ds.getUser().user_settings.setDistance(userSettings.getDistance());
        ds.getUser().user_settings.weight = userSettings.weight;

        //no need to set language because it never changed until save click
        getActivity().onBackPressed();
    }

    @Override
    public void onOneIconClicked() {
        if (!userSelectedCulture.culture_name.equals(ds.getCulture())) {
            languageWasChanged = true;
            //user has change language-> show reload dialog
            new DialogYesNo(getActivity(), dm.getResourceText(R.string.Notice), dm.getResourceText(R.string.Change_Language_Alert_Subtitle), dm.getResourceText(R.string.No), dm.getResourceText(R.string.Yes), new YesNoCallback() {
                @Override
                public void onYes() {
                    updateSettings();
                }

                @Override
                public void onNo() {
                }
            }).show();
        } else {
            languageWasChanged = false;
            updateSettings();
        }

    }

    @Override
    public void onTwoIconClicked() {

    }

    @Override
    public void onThreeIconClicked() {
    }

    /**
     * this method load provaides and show them on UI
     */
    public void getProviders() {
        ServerConnector.getInstance().processRequest(new RequestLoadProviders(true), new ServerConnector.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {

                if (!isAdded()) {
                    return;
                }

                Log.i(TAG, "RequestLoadProviders onSuccess");
                ResponseLoadProviders responseLoadProviders = (ResponseLoadProviders) baseResponse;
                mProviders = responseLoadProviders.getProviders();
                Log.d(TAG, "responseLoadProviders number " + mProviders.length);
                addProviders();
            }

            @Override
            public void onFailure(ErrorResponse errorResponse) {
                Log.i(TAG, "RequestLoadProviders onFailure");

            }
        }, TAG + RequestLoadProviders.class.getSimpleName());
    }

    private void addProviders() {
        llProviders.removeAllViews();
        // show providers on UI
        for (int i = 0; i < mProviders.length; ++i) {
            final ResponseLoadProviders.Provider provider = mProviders[i];
            EventDataRow row = new EventDataRow(getActivity());
            row.setTitle(provider.text_title);
            row.getRightIcon().setVisibility(View.GONE);
            row.getDetailsView().setVisibility(View.GONE);
            if (provider.Icon != null) {
                try {
                    row.getLeftIcon().setText(IconManager.getInstance(getActivity()).getIconString(provider.Icon.getValue()));
                    row.getLeftIcon().setTextColor(Color.parseColor(provider.background_color));
                } catch (Exception ex) {
                }
            }
            row.setBackgroundColor(DataStore.getInstance().getIntColor(EMColor.WHITE));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 1);
            row.setLayoutParams(params);
            llProviders.addView(row);
            final int finalI = i;
            if (provider.user_login.LoginProvider != null) {
                row.getRightIcon().setVisibility(View.VISIBLE);
                row.getRightIcon().setText(Consts.Icons.icon_StatusPositive);
                row.getRightIcon().setTextColor(Color.parseColor("#AADE0B"));
            } else {
                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), WebViewActivity.class);
                        intent.putExtra(WebViewActivity.EXTRA_VIEW_URL, provider.client_url.
                                replace("[culture_name]", ds.getCulture()).
                                replace("[app_host]", "/").
                                replace("[app_id]", getString(R.string.app_id)));
                        intent.putExtra(WebViewActivity.EXTRA_RETURN_URL, Constants.AUTH2_SERVICE_URL + "?");
                        intent.putExtra(WebViewActivity.EXTRA_RETURN_DATA_NAME, EXTRA_ACCESS_TOKEN);
                        providerClickedPosition = finalI;
                        startActivityForResult(intent, REQUEST_CODE_GET_TOKEN);
                    }
                });
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_GET_TOKEN:
                if (resultCode == Activity.RESULT_OK) {
                    String token = data.getStringExtra(EXTRA_ACCESS_TOKEN);
                    Log.d(TAG, "result token is: " + token);
                    if (token != null && token.length() > 0 && providerClickedPosition >= 0) {
                        EventDataRow row = (EventDataRow) llProviders.getChildAt(providerClickedPosition);
                        if (row != null) {
                            row.getRightIcon().setVisibility(View.VISIBLE);
                            row.getRightIcon().setText(Consts.Icons.icon_StatusPositive);
                            row.getRightIcon().setTextColor(Color.parseColor("#AADE0B"));
                        }
                    }
                }
                break;
            case REQUEST_CODE_DIALOG_TIME_ZONE:
                DataTimeZone dataTimeZone = (DataTimeZone) data.getSerializableExtra(EXTRA_TIME_ZONE);
                if (dataTimeZone != null) {
                    ds.getUser().user_settings.setTime_zone(dataTimeZone);
                    updateUi();
                }
                break;
            case REQUEST_CODE_DIALOG_CURRENCY:
                ResponseApplication.DataCurrency dataCurrency = (ResponseApplication.DataCurrency) data.getSerializableExtra(EXTRA_CURRENCY);
                if (dataCurrency != null) {
                    ds.getUser().user_settings.currency = dataCurrency.code;
                    updateUi();
                }
                break;
        }
    }

    @Override
    public void onClick(View v) {
        canSave = true;
        setSaveEnable();

        switch (v.getId()) {
            case R.id.btnSettingsSaveSettings:
                updateSettings();
                break;
            case R.id.btnSettingsLogout:
                ServerConnector.getInstance().cancelPendingRequests(RequestLoadProviders.class.getName());
                Utils.doLogoutOperation();
                Intent intent = new Intent(getActivity(), SplashActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
                break;
            case R.id.btnSettingsChangePassword:
                ((BaseActivity) getActivity()).replaceFragment(R.id.fragment_container, new ChangePasswordFragment(),
                        SettingsFragment.TAG, true, null, R.anim.enter_from_right, R.anim.exit_to_left,
                        R.anim.enter_from_left, R.anim.exit_to_right);
                break;
            case R.id.edrTimeZone:
                FragmentTimeZones dialogTimeZones = new FragmentTimeZones();
                dialogTimeZones.setTargetFragment(this, REQUEST_CODE_DIALOG_TIME_ZONE);
                ((BaseActivity) getActivity()).replaceFragment(R.id.fragment_container, dialogTimeZones,
                        SettingsFragment.TAG, true, null, R.anim.enter_from_right, R.anim.exit_to_left,
                        R.anim.enter_from_left, R.anim.exit_to_right);
                break;
            case R.id.edrCurrency:
                FragmentCurrencies fragmentCurrencies = new FragmentCurrencies();
                fragmentCurrencies.setTargetFragment(this, REQUEST_CODE_DIALOG_CURRENCY);
                ((BaseActivity) getActivity()).replaceFragment(R.id.fragment_container, fragmentCurrencies,
                        SettingsFragment.TAG, true, null, R.anim.enter_from_right, R.anim.exit_to_left,
                        R.anim.enter_from_left, R.anim.exit_to_right);
                break;
            case R.id.edrUnits:
                UnitsMenuFragment unitsFragment = new UnitsMenuFragment();
                unitsFragment.setTargetFragment(this, REQUEST_CODE_DIALOG_UNITS);
                ((BaseActivity) getActivity()).replaceFragment(R.id.fragment_container, unitsFragment,
                        SettingsFragment.TAG, true, null, R.anim.enter_from_right, R.anim.exit_to_left,
                        R.anim.enter_from_left, R.anim.exit_to_right);
                break;

            case R.id.edrLanguage:
                LanguageFragment languageFragment = new LanguageFragment();
                languageFragment.setTargetFragment(this, REQUEST_CODE_DIALOG_LANGUAGE);
                ((BaseActivity) getActivity()).replaceFragment(R.id.fragment_container, languageFragment,
                        LanguageFragment.TAG, true, null, R.anim.enter_from_right, R.anim.exit_to_left,
                        R.anim.enter_from_left, R.anim.exit_to_right);
                break;
        }
    }

    private void updateSettings() {
        Preferences.getInstance().setLanguage(userSelectedCulture.culture_name);

        ServerConnector.getInstance().processRequest(new RequestUpdateSettings(ds.getUser()), new ServerConnector.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                try {
                    Log.i(TAG, "RequestUpdateSettings onSuccess");
                    String responseStr = ((ResponseString) baseResponse).responseStr;
                    //JSONObject settings = new JSONObject(responseStr);
                    //settings.getJSONObject("units").getString("distance");
                    UserSettings userSettings = new Gson().fromJson(responseStr, UserSettings.class);
                    if (userSettings != null) {
                        EMLog.d(TAG, "Update Setting Success!");
                        canSave = false;
                        setSaveEnable();
                        //we are not updating user settings because we already changed them
                        //ds.getUser().user_settings = userSettings;

                        //update user language preference
                        Preferences.getInstance().setLanguage(ds.getCulture());

                        // make restart if needed (on language changed)
                        if (languageWasChanged) {
                            Intent intent = new Intent(getActivity(), SplashActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            getActivity().finish();
                        } else {
                            getActivity().onBackPressed();
                        }
                    }
                } catch (Exception ex) {
                    EMLog.e(TAG, ex.getMessage());
                }
            }

            @Override
            public void onFailure(ErrorResponse errorResponse) {
                Log.i(TAG, "RequestLoadProviders onFailure");
                //minor change plus
            }
        });
    }

    @Override
    protected void cancelRequests() {
        super.cancelPendingRequests(TAG + RequestLoadProviders.class.getSimpleName());
    }

    public void setSaveEnable() {
        mHeader.getIconOne().setAlpha(canSave ? 1f : 0.5f);
        mHeader.getIconOne().setEnabled(canSave);
    }
}
