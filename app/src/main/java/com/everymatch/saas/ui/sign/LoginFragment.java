package com.everymatch.saas.ui.sign;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.server.requests.RequestSignInWithEmail;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.server.responses.ResponseSignIn;
import com.everymatch.saas.singeltones.Preferences;
import com.everymatch.saas.util.NotifierPopup;
import com.everymatch.saas.util.ShapeDrawableUtils;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.BaseTextView;
import com.everymatch.saas.view.EventHeader;
import com.wang.avi.AVLoadingIndicatorView;

/**
 * Created by Dacid on 16/06/2015.
 */
public class LoginFragment extends BaseSignFragment implements View.OnClickListener {

    public static final String TAG = LoginFragment.class.getSimpleName();

    private static final String EXTRA_HIDE_PROVIDERS = "extra.hide.providers";
    private static final String EXTRA_LOGIN_EMAIL = "extra.string.email";
    private static final String SHOW_RECOVER_PASSWORD_POPUP = "extra.show.recover.password.popup";

    //Views
    private EditText mEditTextEmail;
    private EditText mEditTextPassword;
    private Button mButtonLogin;
    private String mLoginEmail;
    private TextView mTextForgotPassword, tvLoading;
    //private View mButtonLoading;
    private BaseTextView mTextTermsOfUse1;
    private AVLoadingIndicatorView animLogin, animRegister;

    //DAtA
    private boolean mShowRecoverPasswordPopup;

    public static Fragment newInstance() {
        return newInstance(null, false, false);
    }

    public static Fragment newInstance(String loginEmail, boolean hideProviders, boolean showRecoverPasswordPopup) {
        LoginFragment newFragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putBoolean(EXTRA_HIDE_PROVIDERS, hideProviders);
        args.putString(EXTRA_LOGIN_EMAIL, loginEmail);
        args.putBoolean(SHOW_RECOVER_PASSWORD_POPUP, showRecoverPasswordPopup);
        newFragment.setArguments(args);
        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHideProviders = getArguments().getBoolean(EXTRA_HIDE_PROVIDERS);
        mLoginEmail = getArguments().getString(EXTRA_LOGIN_EMAIL);
        mShowRecoverPasswordPopup = getArguments().getBoolean(SHOW_RECOVER_PASSWORD_POPUP);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mEditTextEmail = (EditText) view.findViewById(R.id.fragment_login_edit_text_email);
        mEditTextPassword = (EditText) view.findViewById(R.id.fragment_login_edit_text_password);
        mButtonLogin = (Button) view.findViewById(R.id.fragment_login_button_login);
        mTextForgotPassword = (TextView) view.findViewById(R.id.fragment_login_text_forgot_password);
        //mButtonLoading = view.findViewById(R.id.fragment_login_button_loading);
        mTextTermsOfUse1 = (BaseTextView) view.findViewById(R.id.fragment_login_terms_of_use);
        animLogin = (AVLoadingIndicatorView) view.findViewById(R.id.animLogin);
        mTextTermsOfUse1 = (BaseTextView) view.findViewById(R.id.fragment_login_terms_of_use);
        tvLoading = (BaseTextView) view.findViewById(R.id.tvLoading);

        mTextForgotPassword.setOnClickListener(this);
        mButtonLogin.setOnClickListener(this);
        mButtonLogin.setBackgroundDrawable(ShapeDrawableUtils.getRoundendButton(ds.getIntColor(EMColor.PRIMARY), Utils.dpToPx(3)));

        mEditTextPassword.setNextFocusDownId(R.id.fragment_login_button_login);

        mEditTextEmail.setBackgroundDrawable(ShapeDrawableUtils.getRoundendButton(ds.getIntColor(EMColor.WHITE), Utils.dpToPx(3), Utils.dpToPx(1), ds.getIntColor(EMColor.FOG)));
        mEditTextEmail.setHintTextColor(ds.getIntColor(EMColor.MOON));
        mEditTextEmail.setHint(dm.getResourceText(R.string.Email, true));
        mEditTextPassword.setBackgroundDrawable(ShapeDrawableUtils.getRoundendButton(ds.getIntColor(EMColor.WHITE), Utils.dpToPx(3), Utils.dpToPx(1), ds.getIntColor(EMColor.FOG)));
        mEditTextPassword.setHintTextColor(ds.getIntColor(EMColor.MOON));
        mEditTextPassword.setHint(dm.getResourceText(R.string.Password, true));


        mEditTextEmail.addTextChangedListener(this);
        mEditTextPassword.addTextChangedListener(this);

        mButtonLogin.getBackground().setAlpha(50);
        mButtonLogin.setClickable(false);

        if (!TextUtils.isEmpty(mLoginEmail)) {
            mEditTextEmail.setText(mLoginEmail);
        }

        mEditTextPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mEditTextPassword.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
                return false;
            }
        });

        setTextViewHTML(mTextTermsOfUse1, DataManager.getInstance().getResourceText(R.string.RegisterTnc));
        mTextTermsOfUse1.setMovementMethod(LinkMovementMethod.getInstance());
    }

    @Override
    public void seatHeader(EventHeader header) {
        super.seatHeader(header);

        if (!TextUtils.isEmpty(mLoginEmail)) {

            // Coming from recover password
            if (mShowRecoverPasswordPopup) {
                header.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showRecoverPasswordPopup();
                    }
                }, 250);

                header.setTitle(DataManager.getInstance().getResourceText(R.string.Em2_login_title));
                header.getIconThree().setText(DataManager.getInstance().getResourceText(R.string.Em2_register_title).toUpperCase());

            } else { // Coming from registration
                header.setTitle(DataManager.getInstance().getResourceText(R.string.Activate_Account));
                header.getIconThree().setVisibility(View.GONE);

                header.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showSuccessPopup();
                    }
                }, 250);
            }

        } else {
            header.setTitle(DataManager.getInstance().getResourceText(R.string.Em2_login_title));
            header.getIconThree().setText(DataManager.getInstance().getResourceText(R.string.Em2_register_title).toUpperCase());
        }
    }

    private void showRecoverPasswordPopup() {
        NotifierPopup.Builder builder = new NotifierPopup.Builder(getActivity());
        builder.setDuration(6000);
        builder.setMessage(R.string.Sent_new_password);
        builder.setGravity(Gravity.TOP);
        builder.setType(NotifierPopup.TYPE_INFO);
        builder.setView(getView());
        builder.show();
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
                login();
                break;

            case R.id.fragment_login_text_forgot_password:
                mCallbacks.onForgotPasswordClick();
                break;

        }
    }

    private void login() {

        if (!Utils.isEmailValid(mEditTextEmail.getText())) {
            ((GradientDrawable) mEditTextEmail.getBackground()).setStroke(Utils.dpToPx(2), ds.getIntColor(EMColor.ERROR));
            showError(R.string.EmailErrorMessages);
            return;
        }

        if (mEditTextPassword.length() < 6) {
            ((GradientDrawable) mEditTextPassword.getBackground()).setStroke(Utils.dpToPx(2), ds.getIntColor(EMColor.ERROR));
            showError(R.string.PasswordErrorMessages);
            return;
        }


        final Dialog dialog = Utils.createBlockingEmptyDialog(getActivity());
        //mButtonLoading.setVisibility(View.VISIBLE);
        mButtonLogin.setText("");
        animLogin.setVisibility(View.VISIBLE);
        tvLoading.setVisibility(View.VISIBLE);
        ServerConnector.getInstance().processRequest(new RequestSignInWithEmail(mEditTextEmail.getText().toString(),
                mEditTextPassword.getText().toString()), new ServerConnector.OnResultListener() {

            @Override
            public void onSuccess(BaseResponse baseResponse) {
                animLogin.setVisibility(View.INVISIBLE);
                tvLoading.setVisibility(View.INVISIBLE);

                Log.i(TAG, "RequestEmailLogin onSuccess");

                dialog.dismiss();

                ResponseSignIn responseSignIn = (ResponseSignIn) baseResponse;

                Preferences.getInstance().setAccessToken(responseSignIn.getAccess_token());
                Preferences.getInstance().setTokenType(responseSignIn.getToken_type());
                Preferences.getInstance().setExpireIn(responseSignIn.getExpires_in());
                Preferences.getInstance().setUsername(responseSignIn.getUserName());
                Preferences.getInstance().setExpires(responseSignIn.getExpires());

                //Toast.makeText(getActivity(), "LOGIN succeeded", Toast.LENGTH_SHORT).show();

                mCallbacks.onLoginCompleted();
            }

            @Override
            public void onFailure(ErrorResponse errorResponse) {
                animLogin.setVisibility(View.INVISIBLE);
                tvLoading.setVisibility(View.INVISIBLE);
                Log.i(TAG, "RequestEmailLogin onFailure");
                mButtonLogin.setText(dm.getResourceText(R.string.SignIn_Login));
                // mButtonLoading.setVisibility(View.GONE);
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onThreeIconClicked() {
        mCallbacks.onActionClick(false);
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(mEditTextEmail.getText()) || TextUtils.isEmpty(mEditTextPassword.getText())) {
            mButtonLogin.getBackground().setAlpha(50);
            mButtonLogin.setClickable(false);
        } else {
            mButtonLogin.getBackground().setAlpha(255);
            mButtonLogin.setClickable(true);
        }

        // ((GradientDrawable)mEditTextPassword.getBackground()).setStroke(Utils.dpToPx(1), ds.getIntColor(EMColor.FOG));
        // ((GradientDrawable)mEditTextEmail.getBackground()).setStroke(Utils.dpToPx(1), ds.getIntColor(EMColor.FOG));
    }
}
