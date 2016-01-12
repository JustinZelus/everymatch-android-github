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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.server.requests.RequestRegisterWithEmail;
import com.everymatch.saas.server.requests.RequestTNC;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.server.responses.ResponseRegisterWithEmail;
import com.everymatch.saas.util.ShapeDrawableUtils;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.BaseTextView;
import com.everymatch.saas.view.EventHeader;

/**
 * Created by Dacid on 16/06/2015.
 */
public class RegistrationFragment extends BaseSignFragment implements View.OnClickListener {

    public static final String TAG = RegistrationFragment.class.getSimpleName();

    private static final int MIN_NAME_LENGTH = 2;
    private static final int MAX_NAME_LENGTH = 16;

    private BaseTextView mTextTermsOfUse1, mTextTermsOfUse2;
    private EditText mEditTextFirstName;
    private EditText mEditTextLastName;
    private EditText mEditTextEmail;
    private TextView mTextErrorFirstName;
    private TextView mTextErrorLastName;
    private TextView mTextErrorEmail;
    private Button mButtonRegister;
    private View mButtonLoading;
    private ScrollView mScrollView;

    public static Fragment newInstance() {
        RegistrationFragment newFragment = new RegistrationFragment();
        Bundle args = new Bundle();
        newFragment.setArguments(args);
        return newFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mScrollView = (ScrollView) view.findViewById(R.id.fragment_registration_scroll_view);
        mTextTermsOfUse1 = (BaseTextView) view.findViewById(R.id.fragment_registration_terms_of_use_1);
        mTextTermsOfUse2 = (BaseTextView) view.findViewById(R.id.fragment_registration_terms_of_use_2);
        mEditTextFirstName = (EditText) view.findViewById(R.id.fragment_registration_edit_text_first_name);
        mEditTextLastName = (EditText) view.findViewById(R.id.fragment_registration_edit_text_last_name);
        mEditTextEmail = (EditText) view.findViewById(R.id.fragment_registration_edit_text_email);
        mTextErrorFirstName = (TextView) view.findViewById(R.id.email_register_error_first_name);
        mTextErrorLastName = (TextView) view.findViewById(R.id.email_register_error_last_name);
        mTextErrorEmail = (TextView) view.findViewById(R.id.email_register_error_email);
        mButtonRegister = (Button) view.findViewById(R.id.fragment_registration_button_register);
        mEditTextLastName.setNextFocusDownId(R.id.fragment_registration_edit_text_email);
        mEditTextEmail.setNextFocusDownId(R.id.fragment_registration_button_register);
        mButtonLoading = view.findViewById(R.id.fragment_registration_button_loading);

        mButtonRegister.setOnClickListener(this);
        mButtonRegister.setBackgroundDrawable(ShapeDrawableUtils.getRoundendButton(ds.getIntColor(EMColor.PRIMARY), Utils.dpToPx(3)));

        mEditTextFirstName.setBackgroundDrawable(ShapeDrawableUtils.getRoundendButton(ds.getIntColor(EMColor.WHITE),
                Utils.dpToPx(3), Utils.dpToPx(1), ds.getIntColor(EMColor.FOG)));
        mEditTextFirstName.setHintTextColor(ds.getIntColor(EMColor.MOON));
        mEditTextFirstName.setHint(DataManager.getInstance().getResourceText(R.string.FirstName, true));
        mEditTextLastName.setBackgroundDrawable(ShapeDrawableUtils.getRoundendButton(ds.getIntColor(EMColor.WHITE),
                Utils.dpToPx(3), Utils.dpToPx(1), ds.getIntColor(EMColor.FOG)));
        mEditTextLastName.setHintTextColor(ds.getIntColor(EMColor.MOON));
        mEditTextLastName.setHint(DataManager.getInstance().getResourceText(R.string.LastName, true));
        mEditTextEmail.setBackgroundDrawable(ShapeDrawableUtils.getRoundendButton(ds.getIntColor(EMColor.WHITE),
                Utils.dpToPx(3), Utils.dpToPx(1), ds.getIntColor(EMColor.FOG)));
        mEditTextEmail.setHintTextColor(ds.getIntColor(EMColor.MOON));
        mEditTextEmail.setHint(DataManager.getInstance().getResourceText(R.string.Email, true));

        setTextViewHTML(mTextTermsOfUse1, DataManager.getInstance().getResourceText(R.string.RegisterTnc));
        mTextTermsOfUse1.setMovementMethod(LinkMovementMethod.getInstance());
        setTextViewHTML(mTextTermsOfUse2, DataManager.getInstance().getResourceText(R.string.RegisterTnc));
        mTextTermsOfUse2.setMovementMethod(LinkMovementMethod.getInstance());

        mEditTextFirstName.addTextChangedListener(this);
        mEditTextLastName.addTextChangedListener(this);
        mEditTextEmail.addTextChangedListener(this);
        mButtonRegister.getBackground().setAlpha(50);
        mButtonRegister.setClickable(false);

        mEditTextEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    mEditTextEmail.clearFocus();
                    InputMethodManager imm =  (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    mScrollView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mScrollView.smoothScrollTo(0, mScrollView.getBottom());
                        }
                    }, 300);

                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public void seatHeader(EventHeader header) {
        super.seatHeader(header);
        header.setTitle(DataManager.getInstance().getResourceText(R.string.Em2_register_title));
        header.getIconThree().setText(DataManager.getInstance().getResourceText(R.string.Em2_login_title).toUpperCase());
    }



    @Override
    protected void cancelPendingRequests(String... tags) {
        super.cancelPendingRequests(TAG + RequestTNC.class.getSimpleName(), TAG + RequestRegisterWithEmail.class.getSimpleName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.fragment_registration_button_register:
                register();
                break;

        }
    }

    private void register() {

        if (mEditTextFirstName.length() < MIN_NAME_LENGTH || mEditTextFirstName.getText().length() > MAX_NAME_LENGTH) {
            ((GradientDrawable)mEditTextFirstName.getBackground()).setStroke(Utils.dpToPx(2), ds.getIntColor(EMColor.ERROR));
            showError(R.string.Buy_Error_FNameLength);
            return;
        }
        if (mEditTextLastName.length() < MIN_NAME_LENGTH || mEditTextLastName.getText().length() > MAX_NAME_LENGTH){
            ((GradientDrawable)mEditTextLastName.getBackground()).setStroke(Utils.dpToPx(2), ds.getIntColor(EMColor.ERROR));
            showError(R.string.Buy_Error_LNameLength);
            return;
        }

        if (!Utils.isEmailValid(mEditTextEmail.getText())){
            ((GradientDrawable)mEditTextEmail.getBackground()).setStroke(Utils.dpToPx(2), ds.getIntColor(EMColor.ERROR));
            showError(R.string.EmailErrorMessages);
            return;
        }

        mTextErrorFirstName.setText("");
        mTextErrorLastName.setText("");
        mTextErrorEmail.setText("");

        final Dialog dialog = Utils.createBlockingEmptyDialog(getActivity());
        mButtonLoading.setVisibility(View.VISIBLE);
        mButtonRegister.setText("");

        ServerConnector.getInstance().processRequest(new RequestRegisterWithEmail(mEditTextFirstName.getText().toString(),
                mEditTextLastName.getText().toString(), mEditTextEmail.getText().toString()), new ServerConnector.OnResultListener() {

            @Override
            public void onSuccess(BaseResponse baseResponse) {
                Log.i(TAG, "RequestEmailRegister onSuccess");
                dialog.dismiss();

                ResponseRegisterWithEmail responseRegisterWithEmail = (ResponseRegisterWithEmail) baseResponse;

                if (responseRegisterWithEmail.isSucceeded()) {
                    mCallbacks.onRegistrationComplete(mEditTextEmail.getText().toString());
                }
            }

            @Override
            public void onFailure(ErrorResponse errorResponse) {
                Log.i(TAG, "RequestEmailRegister onFailure");
                mButtonRegister.setText(dm.getResourceText(R.string.Em2_register_title));
                mButtonLoading.setVisibility(View.GONE);
                dialog.dismiss();
            }
        }, TAG + RequestRegisterWithEmail.class.getSimpleName());
    }

    @Override
    public void onThreeIconClicked() {
        mCallbacks.onActionClick(true);
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(mEditTextEmail.getText()) || TextUtils.isEmpty(mEditTextFirstName.getText())
                || TextUtils.isEmpty(mEditTextLastName.getText())) {
            mButtonRegister.getBackground().setAlpha(50);
            mButtonRegister.setClickable(false);
        } else {
            mButtonRegister.getBackground().setAlpha(255);
            mButtonRegister.setClickable(true);
        }

        ((GradientDrawable)mEditTextFirstName.getBackground()).setStroke(Utils.dpToPx(1), ds.getIntColor(EMColor.FOG));
        ((GradientDrawable)mEditTextLastName.getBackground()).setStroke(Utils.dpToPx(1), ds.getIntColor(EMColor.FOG));
        ((GradientDrawable)mEditTextEmail.getBackground()).setStroke(Utils.dpToPx(1), ds.getIntColor(EMColor.FOG));
    }
}
