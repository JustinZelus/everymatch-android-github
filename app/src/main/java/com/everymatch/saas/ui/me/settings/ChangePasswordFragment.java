package com.everymatch.saas.ui.me.settings;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.server.requests.BaseRequest;
import com.everymatch.saas.server.requests.RequestLoadProviders;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.server.responses.ResponseString;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.singeltones.DialogFactory;
import com.everymatch.saas.singeltones.Preferences;
import com.everymatch.saas.ui.base.BaseFragment;
import com.everymatch.saas.util.ShapeDrawableUtils;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.BaseEditText;
import com.everymatch.saas.view.EventHeader;
import com.everymatch.ucpa.SplashActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PopApp_laptop on 11/10/2015.
 */
public class ChangePasswordFragment extends BaseFragment implements EventHeader.OnEventHeader, View.OnClickListener {
    public static java.lang.String TAG = "ChangePasswordFragment";
    private EventHeader mHeader;
    BaseEditText etOd, etNew, etConfirm;
    Button btnSend;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_change_password, container, false);

        etOd = (BaseEditText) v.findViewById(R.id.etChangePasswordOld);
        etNew = (BaseEditText) v.findViewById(R.id.etChangePasswordNew);
        etConfirm = (BaseEditText) v.findViewById(R.id.etChangePasswordNewAgain);

        etOd.setBackgroundDrawable(ShapeDrawableUtils.getRoundendButton(ds.getIntColor(EMColor.WHITE), Utils.dpToPx(3), Utils.dpToPx(1), ds.getIntColor(EMColor.FOG)));
        etNew.setBackgroundDrawable(ShapeDrawableUtils.getRoundendButton(ds.getIntColor(EMColor.WHITE), Utils.dpToPx(3), Utils.dpToPx(1), ds.getIntColor(EMColor.FOG)));
        etConfirm.setBackgroundDrawable(ShapeDrawableUtils.getRoundendButton(ds.getIntColor(EMColor.WHITE), Utils.dpToPx(3), Utils.dpToPx(1), ds.getIntColor(EMColor.FOG)));


        v.findViewById(R.id.btnChangePassword).setOnClickListener(this);
        v.findViewById(R.id.btnChangePassword).setBackgroundDrawable(ShapeDrawableUtils.getRoundendButton());
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mHeader = (EventHeader) view.findViewById(R.id.eventHeader);
        mHeader.setListener(this);
        mHeader.getBackButton().setText(Consts.Icons.icon_ArrowBack);
        mHeader.getIconOne().setVisibility(View.GONE);
        mHeader.getIconTwo().setVisibility(View.GONE);
        mHeader.getIconThree().setVisibility(View.GONE);
        mHeader.setTitle("Change Password");

    }

    @Override
    public void onClick(View v) {
        final String old, New, Confirm;
        old = etOd.getText().toString().trim();
        New = etNew.getText().toString().trim();
        Confirm = etConfirm.getText().toString().trim();

        switch (v.getId()) {
            case R.id.btnChangePassword:
                ServerConnector.getInstance().processRequest(new BaseRequest() {
                    @Override
                    public String getServiceUrl() {
                        return Constants.getOAUTH2_SERVICE_URL();
                        //https://oauth2.everymatch.me/api/Account/ChangePassword?hl=en-US

                    }

                    @Override
                    public String getUrlFunction() {
                        //https://oauth2.everymatch.me/api/Account/ChangePassword?hl=en-US
                        return "api/Account/ChangePassword?hl="
                                + DataStore.getInstance().getCulture();
                    }

                    @Override
                    public String getEncodedBody() {
                        Map m = new HashMap<String, String>();
                        m.put("OldPassword", old);
                        m.put("NewPassword", New);
                        m.put("ConfirmPassword", Confirm);

                        JSONObject obj = new JSONObject(m);
                        String str = obj.toString();
                        return str;
                    }

                    @Override
                    public Class getResponseClass() {
                        return null;
                    }

                    @Override
                    public boolean parseResponseAsJson() {
                        return false;
                    }

                    @Override
                    public int getType() {
                        return Request.Method.POST;
                    }

                    @Override
                    public Map<String, String> addExtraHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Authorization", "Bearer " + Preferences.getInstance().getTokenType());
                        return headers;
                    }
                }, new ServerConnector.OnResultListener() {
                    @Override
                    public void onSuccess(BaseResponse baseResponse) {
                        Log.i(TAG, "RequestChangePassword onSuccess");
                        try {
                            JSONObject jsonObject = new JSONObject(((ResponseString) baseResponse).responseStr);
                            if (jsonObject.has("message")) {
                                DialogFactory.getDialogBuilder(getActivity(), "Success!", jsonObject.getString("message"))
                                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                ServerConnector.getInstance().cancelPendingRequests(RequestLoadProviders.class.getName());
                                                Utils.doLogoutOperation();
                                                Intent intent = new Intent(getActivity(), SplashActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                getActivity().finish();
                                            }
                                        }).create().show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(ErrorResponse errorResponse) {
                        Log.i(TAG, "RequestChangePassword onFailure");
                        try {
                            DialogFactory.getDialogOkButton(getActivity(), "Error!", errorResponse.getMessage()).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
                break;
        }
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

    }
}
