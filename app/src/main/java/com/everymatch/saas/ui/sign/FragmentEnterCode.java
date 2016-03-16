package com.everymatch.saas.ui.sign;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.telephony.SmsMessage;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.server.request_manager.ProfileManager;
import com.everymatch.saas.server.responses.ResponseApplication;
import com.everymatch.saas.server.responses.ResponseCodeCheck;
import com.everymatch.saas.server.responses.ResponsePhoneNumberCheck;
import com.everymatch.saas.singeltones.GenericCallback;
import com.everymatch.saas.singeltones.YesNoCallback;
import com.everymatch.saas.ui.BaseActivity;
import com.everymatch.saas.ui.dialog.DialogYesNo;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.ShapeDrawableUtils;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.EventHeader;

/**
 * Created by PopApp_laptop on 18/02/2016.
 */
public class FragmentEnterCode extends BaseSignFragment implements View.OnClickListener {
    public final String TAG = getClass().getName();

    public static final String ARG_COUNTRY_PHONE_CODE = "arg.country.phone.code";
    public static final String ARG_PHONE = "arg.phone";
    public static final String ARG_RESPONSE_PHONE_NUMBER_CHECK = "arg.response.phone.phone.check";
    public static final String ACTION_SMS = "android.provider.Telephony.SMS_RECEIVED";
    public static final int MAX_PROGRESS = 240;

    //public static final String SENDER_NUMBER = "+447481345723";

    //DATA
    private ResponsePhoneNumberCheck responsePhoneNumberCheck;
    private String phone;
    private int progress = 0;
    private Handler handler = new Handler();

    //VIEWS
    private EventHeader mHeader;
    private EditText etCode;
    private TextView tvNumberHelp, tvResend, tvWrongNumber;
    private ProgressBar pbCallMe;
    private TextView tvCallMe;
    RelativeLayout rlCallMeHolder;

    public static FragmentEnterCode getInstance(ResponseApplication.DataCountryPhoneCode countryPhoneCode, String phone, ResponsePhoneNumberCheck responsePhoneNumberCheck) {
        FragmentEnterCode answer = new FragmentEnterCode();
        Bundle bundle = new Bundle(3);
        bundle.putSerializable(ARG_COUNTRY_PHONE_CODE, countryPhoneCode);
        bundle.putString(ARG_PHONE, phone);
        bundle.putSerializable(ARG_RESPONSE_PHONE_NUMBER_CHECK, responsePhoneNumberCheck);
        answer.setArguments(bundle);
        return answer;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phone = getArguments().getString(ARG_PHONE);
        responsePhoneNumberCheck = (ResponsePhoneNumberCheck) getArguments().getSerializable(ARG_RESPONSE_PHONE_NUMBER_CHECK);
        countryPhoneCode = (ResponseApplication.DataCountryPhoneCode) getArguments().getSerializable(ARG_COUNTRY_PHONE_CODE);
        mHideProviders = true;
        handler.postDelayed(incrementProgress, 1000);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_enter_code, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mHeader = (EventHeader) view.findViewById(R.id.eventHeader);
        tvNumberHelp = (TextView) view.findViewById(R.id.tvNumberHelp);
        tvResend = (TextView) view.findViewById(R.id.tvResendCode);
        tvWrongNumber = (TextView) view.findViewById(R.id.tvWrongNumber);
        etCode = (EditText) view.findViewById(R.id.etCode);
        rlCallMeHolder = (RelativeLayout) view.findViewById(R.id.rlCallMeHolder);
        view.findViewById(R.id.rlWrongNumberHolder).setBackgroundDrawable(ShapeDrawableUtils.getButtonStroked(ds.getIntColor(EMColor.PRIMARY)));
        rlCallMeHolder.setBackgroundDrawable(ShapeDrawableUtils.getButtonStroked(ds.getIntColor(EMColor.PRIMARY)));
        tvCallMe = (TextView) view.findViewById(R.id.tvCallMe);
        pbCallMe = (ProgressBar) view.findViewById(R.id.pbCallMe);
        pbCallMe.setMax(MAX_PROGRESS);
        pbCallMe.setScaleY(Utils.dpToPx(40));
        pbCallMe.getProgressDrawable().setColorFilter(Utils.getAlphaPrimaryColor(), android.graphics.PorterDuff.Mode.SRC_IN);
        tvNumberHelp.setText(countryPhoneCode.code + " " + phone);
        setHeader();

        tvResend.setOnClickListener(this);
        tvWrongNumber.setOnClickListener(this);
        etCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() == 6) {
                    //we have full code -> send it to server!
                    sendCode();
                }
            }
        });

    }

    @Override
    public void setHeader(EventHeader header) {
        //super.setHeader(header);
    }

    @Override
    public void onStart() {
        super.onStart();
        IntentFilter filter = new IntentFilter(ACTION_SMS);
        filter.setPriority(5822);
        getActivity().registerReceiver(smsReceiver, filter);
    }

    @Override
    public void onStop() {
        super.onStop();
        getActivity().unregisterReceiver(smsReceiver);
    }

    @Override
    public void onDestroyView() {
        handler.removeCallbacks(incrementProgress);
        super.onDestroyView();
    }

    private void setHeader() {
        mHeader.getCenterText().setText(dm.getResourceText(R.string.Account_setup));
    }

    private void sendSms() {
        showDialog(dm.getResourceText(R.string.Loading));
        ProfileManager.PhoneNumberCheck(countryPhoneCode.code, phone, false, new GenericCallback() {
            @Override
            public void onDone(boolean success, Object data) {
                stopDialog();
                try {
                    if (!success) {
                        if (data != null) {
                            ResponsePhoneNumberCheck response = (ResponsePhoneNumberCheck) data;
                            ((BaseActivity) getActivity()).showErrorDialog(response.getErrorMessage());
                            return;
                        }
                    }

                    ResponsePhoneNumberCheck response = (ResponsePhoneNumberCheck) data;
                    if (!response.success) {
                        ((BaseActivity) getActivity()).showErrorDialog(response.getErrorMessage());
                        return;
                    }

                    //call was successfull
                    FragmentEnterCode.this.responsePhoneNumberCheck = response;

                } catch (Exception ex) {
                    EMLog.e(TAG, ex.getMessage());
                }
            }
        });
    }

    private void sendCode() {
        showDialog(dm.getResourceText(R.string.Loading));
        final String code = etCode.getText().toString().trim();
        ProfileManager.CodeCheck(countryPhoneCode.code, phone, code, new GenericCallback() {
            @Override
            public void onDone(boolean success, Object data) {
                stopDialog();
                if (!success) {
                    return;
                }
                ResponseCodeCheck response = (ResponseCodeCheck) data;

                //if user enter the same phone server will return new_user = false and we dont want that
                if (isUpdate) {
                    showDialog(dm.getResourceText(R.string.Loading));
                    ds.getUser().phone = phone;
                    ds.getUser().country_code = countryPhoneCode.code;
                    ProfileManager.UpdateNumber(countryPhoneCode.code + phone, code, new GenericCallback() {
                        @Override
                        public void onDone(boolean success, Object data) {
                            stopDialog();
                            if (!success)
                                return;
                            try {
                                FragmentEnterCode.this.getActivity().getSupportFragmentManager().popBackStackImmediate();
                                FragmentEnterCode.this.getTargetFragment().onActivityResult(UpdateMobileNumberFragment.REQUEST_CODE_UPDATE_NUMBER, Activity.RESULT_OK, null);
                            } catch (Exception ex) {
                                EMLog.e(TAG, ex.getMessage());
                            }

                        }
                    });
                    return;
                }

                if (response.success == true && response.is_new_user == false) {
                    String userName = countryPhoneCode.code + phone;
                    String code = etCode.getText().toString().trim();
                    getToken(userName, code);
                    return;
                }

                if (response.success == true && response.is_new_user == true) {
                    int layoutRes = isUpdate ? R.id.fragment_container : R.id.activity_sign_main_layout;
                    android.support.v4.app.Fragment loginFragment = LoginFragment.newInstance(countryPhoneCode, phone, code);
                    getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_up_animation, 0)
                            .replace(layoutRes, loginFragment, LoginFragment.TAG).commit();
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tvResendCode:
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
            case R.id.tvWrongNumber:
                getActivity().getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.slide_up_animation, 0)
                        .replace(R.id.activity_sign_main_layout, new SMSFragment(), SMSFragment.TAG).commit();
                break;
            case R.id.tvCallMe:
                callMe();
                break;
        }
    }

    private void callMe() {
        EMLog.d(TAG, "call me was called");
        showDialog(dm.getResourceText(R.string.Loading));
        ProfileManager.PhoneNumberCheck(countryPhoneCode.code, phone, true, new GenericCallback() {
            @Override
            public void onDone(boolean success, Object data) {
                stopDialog();
                try {
                    if (!success) {
                        if (data != null) {
                            ResponsePhoneNumberCheck response = (ResponsePhoneNumberCheck) data;
                            ((BaseActivity) getActivity()).showErrorDialog(response.getErrorMessage());
                            return;
                        }
                    }

                    ResponsePhoneNumberCheck response = (ResponsePhoneNumberCheck) data;
                    if (!response.success) {
                        ((BaseActivity) getActivity()).showErrorDialog(response.getErrorMessage());
                        return;
                    }

                    //call was successfull
                    FragmentEnterCode.this.responsePhoneNumberCheck = response;

                } catch (Exception ex) {
                    EMLog.e(TAG, ex.getMessage());
                }
            }
        });
    }

    private Runnable incrementProgress = new Runnable() {
        @Override
        public void run() {
            progress++;
            pbCallMe.setProgress(progress);

            if (progress >= MAX_PROGRESS) {
                tvCallMe.setTextColor(ds.getIntColor(EMColor.WHITE));
                tvCallMe.setOnClickListener(FragmentEnterCode.this);
                pbCallMe.setVisibility(View.GONE);
                rlCallMeHolder.setBackgroundDrawable(ShapeDrawableUtils.getRoundendButton());
                return;
            }
            handler.postDelayed(this, 250);
        }
    };

    public BroadcastReceiver smsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_SMS)) {
                Bundle bundle = intent.getExtras();
                SmsMessage[] msgs = null;
                String msg_from;
                if (bundle != null) {
                    try {
                        Object[] pdus = (Object[]) bundle.get("pdus");
                        msgs = new SmsMessage[pdus.length];
                        for (int i = 0; i < msgs.length; i++) {
                            msgs[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                            msg_from = msgs[i].getOriginatingAddress();
                            try {
                                if (msg_from.equals(ds.getApplicationData().sms_phone_number)) {
                                    String message = msgs[i].getMessageBody();
                                    if (message != null && message.trim().length() > 6) {
                                        message = message.trim();
                                        String code = message.substring(message.length() - 6, message.length());
                                        etCode.setText(code);
                                    }
                                }
                            } catch (Exception ex) {
                                EMLog.e(TAG, ex.getMessage());
                            }

                        }
                    } catch (Exception e) {
                        EMLog.e(TAG, e.getMessage());
                    }
                }
            }
        }
    };

    @Override
    public void onThreeIconClicked() {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
