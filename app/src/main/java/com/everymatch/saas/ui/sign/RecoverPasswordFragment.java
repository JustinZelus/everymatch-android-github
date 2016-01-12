package com.everymatch.saas.ui.sign;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.everymatch.saas.R;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.server.requests.RequestRecoverPassword;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.server.responses.ResponseRecoverPassword;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.ui.base.BaseFragment;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.view.EventHeader;

import org.json.JSONObject;

import java.net.HttpURLConnection;


public class RecoverPasswordFragment extends BaseFragment implements View.OnClickListener {

    public static final String TAG = RecoverPasswordFragment.class.getSimpleName();

    private EventHeader mHeader;
    private EditText mEmailView;
    private String mEmail;
    private Button mSubmitBtn;
    private TextView mTextPasswordError;
    private Callbacks mCallbacks;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof Callbacks) {
            mCallbacks = (Callbacks) context;
        } else {
            throw new IllegalStateException(context + " must implements " + Callbacks.class.getName());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recover_password, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mEmailView = (EditText) view.findViewById(R.id.recover_password_email);
        mEmailView.setHint(dm.getResourceText(getResources().getString(R.string.EmailPlaceHolder)));
        mSubmitBtn = (Button) view.findViewById(R.id.recover_password_submit_button);
        mTextPasswordError = (TextView) view.findViewById(R.id.fragment_recover_password_error);
        mSubmitBtn.setOnClickListener(this);
        setHeader(view);
    }

    private void setHeader(View v) {
        mHeader = (EventHeader) v.findViewById(R.id.eventHeader);
        mHeader.setListener(new EventHeader.OnEventHeader() {
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

            }
        });
        mHeader.getBackButton().setText(Consts.Icons.icon_ArrowBack);
        mHeader.getIconOne().setVisibility(View.GONE);
        mHeader.getIconTwo().setVisibility(View.GONE);
        mHeader.getIconThree().setVisibility(View.GONE);
        mHeader.setTitle(dm.getResourceText(getString(R.string.Forgot_Password)));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.recover_password_submit_button:
                mEmail = mEmailView.getText().toString();

                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("Submit...");
                progressDialog.show();

                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                ServerConnector.getInstance().processRequest(new RequestRecoverPassword(mEmail), new ServerConnector.OnResultListener() {

                    @Override
                    public void onSuccess(BaseResponse baseResponse) {
                        Log.i(TAG, "RequestRecoverPassword onSuccess");
                        ResponseRecoverPassword responseRecoverPassword = (ResponseRecoverPassword) baseResponse;
                        progressDialog.dismiss();
                        mCallbacks.onPasswordSent(mEmail);
                    }

                    @Override
                    public void onFailure(ErrorResponse errorResponse) {
                        Log.i(TAG, "RequestRecoverPassword onFailure");
                        progressDialog.dismiss();

                        mTextPasswordError.setVisibility(View.VISIBLE);

                        try {
                            if (errorResponse.getStatusCode() == HttpURLConnection.HTTP_BAD_REQUEST) {
                                JSONObject jsonObject = new JSONObject(errorResponse.getServerRawResponse());
                                mTextPasswordError.setText((String) jsonObject.getJSONObject("ModelState").getJSONArray("model.Email").get(0));
                            }

                        } catch (Exception e) {
                            EMLog.e(TAG, "onFailure");
                        }

                    }
                }, RequestRecoverPassword.class.getSimpleName());
                break;
        }
    }

    @Override
    public void onDetach() {
        ServerConnector.getInstance().cancelPendingRequests(RequestRecoverPassword.class.getSimpleName());
        super.onDetach();
    }

    public interface Callbacks {
        void onPasswordSent(String toEmail);
    }
}
