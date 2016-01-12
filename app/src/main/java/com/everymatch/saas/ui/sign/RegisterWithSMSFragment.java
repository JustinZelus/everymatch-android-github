package com.everymatch.saas.ui.sign;


import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.everymatch.saas.R;
import com.everymatch.saas.ui.WebViewActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class RegisterWithSMSFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = RegisterWithSMSFragment.class.getSimpleName();

    private ImageButton mClose;
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mPhoneNumberView;
    private TextView mErrorFirst;
    private TextView mErrorLast;
    private TextView mErrorPhoneNumber;
    private TextView mPrivacyLink;
    private TextView mTermsLink;
    private Button mRegisterBtn;
    private String mFirstName;
    private String mLastName;
    private String mPhoneNumber;

    public RegisterWithSMSFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sms_register, container, false);


        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mClose = (ImageButton) view.findViewById(R.id.sms_register_close);
        mClose.setOnClickListener(this);
        mFirstNameView = (EditText) view.findViewById(R.id.sms_register_first_name);
        mLastNameView = (EditText) view.findViewById(R.id.sms_register_last_name);
        mPhoneNumberView = (EditText) view.findViewById(R.id.sms_register_phone_number);
        mErrorFirst = (TextView) view.findViewById(R.id.sms_register_error_first_name);
        mErrorLast = (TextView) view.findViewById(R.id.sms_register_error_last_name);
        mErrorPhoneNumber = (TextView) view.findViewById(R.id.sms_register_error_phone_number);
        mPrivacyLink = (TextView) view.findViewById(R.id.sms_register_privacy);
        mPrivacyLink.setOnClickListener(this);
        mTermsLink = (TextView) view.findViewById(R.id.sms_register_terms);
        mTermsLink.setOnClickListener(this);
        mRegisterBtn = (Button) view.findViewById(R.id.sms_register_register_button);
        mRegisterBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sms_register_close:
                getActivity().onBackPressed();
                break;
            case R.id.sms_register_privacy:
                Intent intent1 = new Intent(getActivity(), WebViewActivity.class);
                intent1.putExtra(WebViewActivity.EXTRA_VIEW_URL, getString(R.string.term_url));
                startActivity(intent1);
                break;
            case R.id.sms_register_terms:
                Intent intent2 = new Intent(getActivity(), WebViewActivity.class);
                intent2.putExtra(WebViewActivity.EXTRA_VIEW_URL, getString(R.string.term_url));
                startActivity(intent2);
                break;
            case R.id.sms_register_register_button:
                mErrorFirst.setText("");
                mErrorLast.setText("");
                mErrorPhoneNumber.setText("");

                mFirstName = mFirstNameView.getText().toString();
                mLastName = mLastNameView.getText().toString();
                mPhoneNumber = mPhoneNumberView.getText().toString();

                final ProgressDialog progressDialog = new ProgressDialog(getActivity());
                progressDialog.setCancelable(false);
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setMessage("Register by SMS");
           //     progressDialog.show();

               /* ServerConnector.getInstance().processRequest(new RequestRegisterWithEmail(mFirstName, mLastName, mEmail), new ServerConnector.OnResultListener() {

                    @Override
                    public void onSuccess(BaseResponse baseResponse) {
                        Log.i(TAG, "RequestEmailRegister onSuccess");
                        ResponseRegisterWithEmail responseRegisterWithEmail = (ResponseRegisterWithEmail) baseResponse;
                        if (responseRegisterWithEmail.isSucceeded()) {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), "Register succeeded", Toast.LENGTH_SHORT).show();
                            getFragmentManager().beginTransaction()
                                    .setCustomAnimations(R.animator.sign_fragment_in_animation, R.animator.sign_fragment_out_animation,
                                            R.animator.sign_fragment_in_animation, R.animator.sign_fragment_out_animation)
                                    .addToBackStack("myFragment")
                                    .replace(R.id.main_layout, new SignInWithEmailFragment())
                                    .commit();
                        }
                    }

                    @Override
                    public void onFailure(ErrorResponse errorResponse) {
                        Log.i(TAG, "RequestEmailRegister onFailure");
                        progressDialog.dismiss();

                        try {
                            if (errorResponse != null && errorResponse.getServerRawResponse() != null && errorResponse.getServerRawResponse() != "") {
                                JSONObject errorObject = new JSONObject(errorResponse.getServerRawResponse());

                                if (errorObject.optString("ModelState") != null) {
                                    JSONObject modelStateJson = new JSONObject(errorObject.optString("ModelState"));

                                    if (modelStateJson.optString("model.FirstName") != null && modelStateJson.optString("model.FirstName") != "") {
                                        JSONArray modelFirstNameArr = modelStateJson.getJSONArray("model.FirstName");
                                        for (int i = 0; i < modelFirstNameArr.length(); i++) {
                                            mErrorFirst.setText(modelFirstNameArr.getString(i));
                                        }
                                    }
                                    if (modelStateJson.optString("model.LastName") != null && modelStateJson.optString("model.LastName") != "") {
                                        JSONArray modelLastNameArr = modelStateJson.getJSONArray("model.LastName");
                                        for (int i = 0; i < modelLastNameArr.length(); i++) {
                                            mErrorLast.setText(modelLastNameArr.getString(i));
                                        }
                                    }
                                    if (modelStateJson.optString("model.Email") != null && modelStateJson.optString("model.Email") != "") {
                                        JSONArray modelEmailArr = modelStateJson.getJSONArray("model.Email");
                                        for (int i = 0; i < modelEmailArr.length(); i++) {
                                            mErrorEmail.setText(modelEmailArr.getString(i));
                                        }
                                    }
                                    if (modelStateJson.optString("") != null && modelStateJson.optString("") != "") {
                                        JSONArray modelEmailArr = modelStateJson.getJSONArray("");
                                        for (int i = 0; i < modelEmailArr.length(); i++) {
                                            mErrorEmail.setText(modelEmailArr.getString(i));
                                        }
                                    }

                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });*/
                break;
        }
    }
}
