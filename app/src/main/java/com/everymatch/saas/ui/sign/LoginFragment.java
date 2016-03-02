package com.everymatch.saas.ui.sign;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.server.requests.RequestRegister;
import com.everymatch.saas.server.requests.RequestRegisterWithEmail;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.server.responses.ResponseApplication;
import com.everymatch.saas.server.responses.ResponseRegisterWithEmail;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.NotifierPopup;
import com.everymatch.saas.view.EventHeader;

/**
 * Created by Dacid on 16/06/2015.
 */
public class LoginFragment extends BaseSignFragment implements View.OnClickListener {

    public static final String TAG = LoginFragment.class.getSimpleName();

    private static final String EXTRA_HIDE_PROVIDERS = "extra.hide.providers";
    public static final String ARG_COUNTRY_PHONE_CODE = "arg.country.phone.code";
    public static final String ARG_PHONE = "arg.phone";
    public static final String ARG_PASSWORD = "arg.password";

    //Views
    private EditText etFirstName, etLastName;

    //DATA
    private String phone;


    public static Fragment newInstance(ResponseApplication.DataCountryPhoneCode countryPhoneCode, String phone, String password) {
        LoginFragment newFragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PHONE, phone);
        args.putSerializable(ARG_COUNTRY_PHONE_CODE, countryPhoneCode);
        args.putString(ARG_PASSWORD, password);
        newFragment.setArguments(args);
        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHideProviders = getArguments().getBoolean(EXTRA_HIDE_PROVIDERS);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        countryPhoneCode = (ResponseApplication.DataCountryPhoneCode) getArguments().getSerializable(ARG_COUNTRY_PHONE_CODE);
        phone = getArguments().getString(ARG_PHONE);
        password = getArguments().getString(ARG_PASSWORD);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        etFirstName = (EditText) view.findViewById(R.id.etFirstName);
        etLastName = (EditText) view.findViewById(R.id.etLastName);

        etLastName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView view, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    register();
                }
                return false;
            }
        });

    }

    @Override
    public void setHeader(EventHeader header) {
        super.setHeader(header);

        header.getCenterText().setText(dm.getResourceText(R.string.Account_setup));
        header.getBackButton().setVisibility(View.GONE);
        header.getTitle().setVisibility(View.GONE);
        header.getIconOne().setVisibility(View.GONE);
        header.getIconTwo().setVisibility(View.GONE);
        header.getIconThree().setVisibility(View.GONE);
    }

    private void showSuccessPopup() {
        NotifierPopup.Builder builder = new NotifierPopup.Builder(getActivity());
        builder.setDuration(6000);
        builder.setMessage(R.string.AcountCreated);
        builder.setGravity(Gravity.TOP);
        builder.setType(NotifierPopup.TYPE_SUCCESS);
        builder.setView(getView());
        builder.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.fragment_login_button_login:
                register();
                break;
        }
    }

    private void register() {
        //final Dialog dialog = Utils.createBlockingEmptyDialog(getActivity());

        final String userName = countryPhoneCode.code + phone;
        String countryCode = countryPhoneCode.code;
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        ServerConnector.getInstance().processRequest
                (new RequestRegister(userName, countryCode, phone, password, firstName, lastName, "phone"), new ServerConnector.OnResultListener() {

                    @Override
                    public void onSuccess(BaseResponse baseResponse) {
                        Log.i(TAG, "RequestEmailRegister onSuccess");
                        ResponseRegisterWithEmail responseRegisterWithEmail = (ResponseRegisterWithEmail) baseResponse;

                        if (responseRegisterWithEmail.isSucceeded()) {
                            //mCallbacks.onRegistrationComplete("");
                            //mCallbacks.onLoginCompleted();
                            getToken(userName, password);
                        }
                    }

                    @Override
                    public void onFailure(ErrorResponse errorResponse) {
                        //dialog.dismiss();
                        EMLog.e(TAG, errorResponse.getMessage());
                    }
                }, TAG + RequestRegisterWithEmail.class.getSimpleName());

        //mButtonLoading.setVisibility(View.VISIBLE);
        //mButtonLogin.setText("");
        //tvLoading.setVisibility(View.VISIBLE);
    }

    @Override
    public void onThreeIconClicked() {
        mCallbacks.onActionClick(false);
    }

    @Override
    public void afterTextChanged(Editable s) {
       /* if (TextUtils.isEmpty(mEditTextEmail.getText()) || TextUtils.isEmpty(mEditTextPassword.getText())) {
            //mButtonLogin.getBackground().setAlpha(50);
            //mButtonLogin.setClickable(false);
        } else {
            //mButtonLogin.getBackground().setAlpha(255);
            //mButtonLogin.setClickable(true);
        }*/

        // ((GradientDrawable)mEditTextPassword.getBackground()).setStroke(Utils.dpToPx(1), ds.getIntColor(EMColor.FOG));
        // ((GradientDrawable)mEditTextEmail.getBackground()).setStroke(Utils.dpToPx(1), ds.getIntColor(EMColor.FOG));
    }
}
