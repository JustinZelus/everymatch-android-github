package com.everymatch.saas.ui.sign;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Patterns;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.everymatch.saas.Constants;
import com.everymatch.saas.R;
import com.everymatch.saas.adapter.AdapterCountryPhone;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.server.request_manager.ProfileManager;
import com.everymatch.saas.server.requests.RequestTNC;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.server.responses.ResponseApplication;
import com.everymatch.saas.server.responses.ResponsePhoneNumberCheck;
import com.everymatch.saas.server.responses.ResponseString;
import com.everymatch.saas.singeltones.GenericCallback;
import com.everymatch.saas.singeltones.Preferences;
import com.everymatch.saas.singeltones.YesNoCallback;
import com.everymatch.saas.ui.BaseActivity;
import com.everymatch.saas.ui.WebViewActivity;
import com.everymatch.saas.ui.base.BaseFragment;
import com.everymatch.saas.ui.dialog.DialogYesNo;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.NotifierPopup;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.BaseTextView;
import com.everymatch.saas.view.EventHeader;

import org.json.JSONObject;

import java.util.Locale;

import pl.tajchert.nammu.Nammu;
import pl.tajchert.nammu.PermissionCallback;

/**
 * Created by PopApp_laptop on 18/02/2016.
 */
public class SMSFragment extends BaseFragment implements View.OnClickListener {
    public static final String TAG = "SMSFragment";

    //Data
    String phone;
    boolean isUpdate;
    boolean smsSendSuccess;

    ResponseApplication.DataCountryPhoneCode countryPhoneCode = null;
    //Views
    EditText etPhone;
    AutoCompleteTextView etCountryPhoneCode;
    TextView tvTerms;
    EventHeader mHeader;
    Button btnContinue;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isUpdate = !(ds.getUser() == null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sms, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mHeader = (EventHeader) view.findViewById(R.id.eventHeader);
        setHeader();
        etPhone = (EditText) view.findViewById(R.id.etPhone);
        etCountryPhoneCode = (AutoCompleteTextView) view.findViewById(R.id.etCountryPhoneCode);
        etCountryPhoneCode.setHint(dm.getResourceText(R.string.Select_Country_Code));
        tvTerms = (BaseTextView) view.findViewById(R.id.fragment_login_terms_of_use);
        btnContinue = (Button) view.findViewById(R.id.btnContinue);
        setCountryPhoneAdapter();

        //terms
        setTextViewHTML(tvTerms, dm.getResourceText(R.string.RegisterTnc));
        tvTerms.setMovementMethod(LinkMovementMethod.getInstance());

        loadUserPhoneNumber();

        btnContinue.setOnClickListener(this);
    }

    private void loadUserPhoneNumber() {
        Nammu.askForPermission(getActivity(), Manifest.permission.READ_PHONE_STATE, new PermissionCallback() {
            @Override
            public void permissionGranted() {
                Locale locale = Locale.getDefault();
                String myCountry = locale.getCountry();

                if (Utils.isEmpty(myCountry)) {
                    countryPhoneCode = Preferences.getInstance().getDataphoneCountryCode();
                    if (countryPhoneCode != null)
                        etCountryPhoneCode.setText(countryPhoneCode.country);
                    return;
                }

                for (ResponseApplication.DataCountryPhoneCode phoneCode : ds.getApplicationData().getCountry_phone_codes()) {
                    if (!Utils.isEmpty(phoneCode.country_code) && phoneCode.country_code.toLowerCase().equals(myCountry.toLowerCase())) {
                        countryPhoneCode = phoneCode;
                        etCountryPhoneCode.setText(countryPhoneCode.country);
                        return;
                    }
                }

                EMLog.d(TAG, "got READ_PHONE_STATE permission");
                TelephonyManager tMgr = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
                String mPhoneNumber = tMgr.getLine1Number();
            }

            @Override
            public void permissionRefused() {
                EMLog.d(TAG, "not got permission");
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void setCountryPhoneAdapter() {
        AdapterCountryPhone customerAdapter = new AdapterCountryPhone(getActivity(), android.R.layout.simple_list_item_1, ds.getApplicationData().getCountry_phone_codes());
        etCountryPhoneCode.setThreshold(1);
        etCountryPhoneCode.setTextColor(ds.getIntColor(EMColor.MOON));
        etCountryPhoneCode.setAdapter(customerAdapter);
        etCountryPhoneCode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                countryPhoneCode = ds.getApplicationData().getCountry_phone_codes().get(position);
            }
        });
    }

    private void setHeader() {
        mHeader.getCenterText().setText(dm.getResourceText(R.string.Account_setup));
    }

    protected void setTextViewHTML(TextView text, String html) {
        CharSequence sequence = Html.fromHtml(html);
        SpannableStringBuilder strBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = strBuilder.getSpans(0, sequence.length(), URLSpan.class);
        for (URLSpan span : urls) {
            makeLinkClickable(strBuilder, span);
        }
        text.setText(strBuilder);
    }

    protected void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span) {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                String url = span.getURL();

                //startProgressDialog();

                final Intent intent = new Intent(getActivity(), WebViewActivity.class);

                if (url.contains("tnc")) {
                    url = Constants.TERMS_AND_CONDITIONS_URL;
                    intent.putExtra(WebViewActivity.EXTRA_SCREEN_TITLE, dm.getResourceText(getString(R.string.Terms_and_conditions)));
                } else {
                    url = Constants.PRIVACY_SETTINGS_URL;
                    intent.putExtra(WebViewActivity.EXTRA_SCREEN_TITLE, dm.getResourceText(getString(R.string.Company_PrivacyPolicy)));
                }

                RequestTNC requestTNC = new RequestTNC(url);
                ServerConnector.getInstance().processRequest(requestTNC, new ServerConnector.OnResultListener() {
                    @Override
                    public void onSuccess(BaseResponse baseResponse) {
                        try {
                            //stopProgressDialog();
                            ResponseString responseString = (ResponseString) baseResponse;
                            JSONObject jsonObject = new JSONObject(responseString.responseStr);
                            String content = jsonObject.getString("text");
                            intent.putExtra(WebViewActivity.EXTRA_VIEW_CONTENT, content);
                            startActivity(intent);
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onFailure(ErrorResponse errorResponse) {
                        //stopProgressDialog();
                    }
                }, TAG + RequestTNC.class.getSimpleName());

            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnContinue:
                phone = etPhone.getText().toString().trim();
                if (phone.length() == 0) {
                    showErrorPopup("Phone number cannot be empty");
                    return;
                }
                if (!Patterns.PHONE.matcher(phone).matches()) {
                    showErrorPopup("Please enter a valid number");
                    return;
                }


                if (phone.startsWith("0"))
                    phone = phone.substring(1, phone.length());
                if (countryPhoneCode == null) {
                    showErrorPopup("Please select a valid country phone code");
                    return;
                }

                if (isUpdate && phone.equals(ds.getUser().phone)) {
                    showErrorPopup("your phone is already registered");
                    return;
                }

                Preferences.getInstance().setDataphoneCountryCode(countryPhoneCode);
                String title = dm.getResourceText(R.string.Sms_number_alert_title);
                String subTitle = dm.getResourceText(R.string.Sms_number_alert_subtitle) + "\n" + countryPhoneCode.code + " " + phone;
                new DialogYesNo(getActivity(), title, subTitle, dm.getResourceText(R.string.Edit_btn), dm.getResourceText(R.string.Ok), new YesNoCallback() {
                    @Override
                    public void onYes() {
                        sendSms();
                    }

                    @Override
                    public void onNo() {
                    }
                }).show();
                break;
        }
    }

    private void sendSms() {
        showDialog(dm.getResourceText(R.string.Loading));
        ProfileManager.PhoneNumberCheck(countryPhoneCode.code, phone, new GenericCallback() {
            @Override
            public void onDone(boolean success, Object data) {
                stopDialog();
                try {
                    if (!success) {
                        //call failed
                    }

                    smsSendSuccess = success;
                    ResponsePhoneNumberCheck response = (ResponsePhoneNumberCheck) data;
                    if (!response.success) {
                        ((BaseActivity) getActivity()).showErrorDialog(response.getErrorMessage());
                        return;
                    }
                    int layoutRes = isUpdate ? R.id.fragment_container : R.id.activity_sign_main_layout;
                    FragmentEnterCode fragmentEnterCode = FragmentEnterCode.getInstance(countryPhoneCode, phone, response);
                    //if update number -> pass target fragment
                    if (isUpdate)
                        fragmentEnterCode.setTargetFragment(getTargetFragment(), getTargetRequestCode());

                    getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_up_animation, 0)
                            .replace(layoutRes, fragmentEnterCode, RegistrationFragment.TAG).commit();

                } catch (Exception ex) {
                    EMLog.e(TAG, ex.getMessage());
                }
            }
        });
    }

    private void showErrorPopup(String message) {
        NotifierPopup.Builder builder = new NotifierPopup.Builder(getActivity());
        builder.setDuration(3000);
        builder.setMessage(message);
        builder.setGravity(Gravity.TOP);
        builder.setType(NotifierPopup.TYPE_ERROR);
        builder.setView(getView());
        builder.show();
    }
}
