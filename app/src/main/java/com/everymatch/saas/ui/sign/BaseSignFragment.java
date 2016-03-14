package com.everymatch.saas.ui.sign;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.everymatch.saas.Constants;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.EMColor;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.server.request_manager.ProfileManager;
import com.everymatch.saas.server.requests.RequestProviderLogin;
import com.everymatch.saas.server.requests.RequestTNC;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.server.responses.ResponseApplication;
import com.everymatch.saas.server.responses.ResponseLoadProviders;
import com.everymatch.saas.server.responses.ResponseSignIn;
import com.everymatch.saas.server.responses.ResponseString;
import com.everymatch.saas.singeltones.Consts;
import com.everymatch.saas.singeltones.GenericCallback;
import com.everymatch.saas.singeltones.Preferences;
import com.everymatch.saas.ui.WebViewActivity;
import com.everymatch.saas.ui.base.BaseFragment;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.NotifierPopup;
import com.everymatch.saas.util.ShapeDrawableUtils;
import com.everymatch.saas.util.TypeFaceProvider;
import com.everymatch.saas.util.Utils;
import com.everymatch.saas.view.BaseButton;
import com.everymatch.saas.view.EventHeader;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by dors on 11/23/15.
 */
public abstract class BaseSignFragment extends BaseFragment implements EventHeader.OnEventHeader, TextWatcher {

    protected static final String EXTRA_ACCESS_TOKEN = "key";
    protected static final int REQUEST_CODE_GET_TOKEN = 1;
    private final String TAG = this.getClass().getSimpleName();
    //DATA
    protected ResponseApplication.DataCountryPhoneCode countryPhoneCode;
    protected String password;
    protected boolean isUpdate;


    protected Callbacks mCallbacks;
    protected boolean mHideProviders;
    //VIEWS
    private TextView mTextTitle;
    private TextView mTextOr;
    private View mOrContainer;
    private LinearLayout mProvidersContainer;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        isUpdate = !(ds.getUser()==null);
        if (isUpdate)
            return;
        if (context instanceof Callbacks) {
            mCallbacks = (Callbacks) context;
        } else {
            throw new IllegalStateException(context + " must implements " + Callbacks.class.getName());
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (this instanceof FragmentEnterCode)
            return;

        setHeader((EventHeader) view.findViewById(R.id.fragment_base_sign_header));

        mProvidersContainer = (LinearLayout) view.findViewById(R.id.fragment_base_sign_provider_button_container);
        mTextOr = (TextView) view.findViewById(R.id.fragment_base_sign_text_or);
        mOrContainer = view.findViewById(R.id.fragment_sign_base_or_container);
        mTextTitle = (TextView) view.findViewById(R.id.fragment_base_sign_text_title);


        if (mHideProviders) {
            mTextTitle.setVisibility(View.GONE);
            mOrContainer.setVisibility(View.GONE);
        } else {
            setProviders();
            setOrButton();
        }
    }

    protected void showError(int resource) {
        NotifierPopup.Builder builder = new NotifierPopup.Builder(getActivity());
        builder.setMessage(resource);
        builder.setType(NotifierPopup.TYPE_ERROR);
        builder.setView(getView());
        builder.show();
    }

    private void setOrButton() {

        Drawable drawable = getResources().getDrawable(R.drawable.or_background);

        if (drawable instanceof ShapeDrawable) {
            ShapeDrawable shapeDrawable = (ShapeDrawable) drawable;
            shapeDrawable.getPaint().setColor(ds.getIntColor(EMColor.BACKGROUND));
            shapeDrawable.setColorFilter(ds.getIntColor(EMColor.FOG), PorterDuff.Mode.SRC_ATOP);

        } else {
            GradientDrawable gradientDrawable = (GradientDrawable) drawable;
            gradientDrawable.setColor(ds.getIntColor(EMColor.BACKGROUND));
            gradientDrawable.setStroke(Utils.dpToPx(1), ds.getIntColor(EMColor.FOG));
        }

        mTextOr.setBackgroundDrawable(drawable);
    }

    protected void makeLinkClickable(SpannableStringBuilder strBuilder, final URLSpan span) {
        int start = strBuilder.getSpanStart(span);
        int end = strBuilder.getSpanEnd(span);
        int flags = strBuilder.getSpanFlags(span);
        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(View view) {
                String url = span.getURL();

                startProgressDialog();

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
                            stopProgressDialog();
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
                        stopProgressDialog();
                    }
                }, TAG + RequestTNC.class.getSimpleName());

            }
        };
        strBuilder.setSpan(clickable, start, end, flags);
        strBuilder.removeSpan(span);
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

    private void setProviders() {
        if (ds.responseLoadProviders == null)
            return;
        final ArrayList<ResponseLoadProviders.Provider> providers = ds.responseLoadProviders.getProviders();
        for (int i = 0; i < providers.size(); ++i) {
            BaseButton button = new BaseButton(getActivity());
            button.setText(providers.get(i).text_title.toUpperCase());
            button.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Utils.pxToDp((int) getResources().getDimension(R.dimen.text_size_normal)));
            button.setTextColor(ds.getIntColor(EMColor.WHITE));
            button.setTypeface(TypeFaceProvider.getTypeFace(TypeFaceProvider.FONT_LATO));
            int color;
            try {
                color = Color.parseColor(providers.get(i).background_color);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }

            mProvidersContainer.addView(button);
            button.setBackgroundDrawable(ShapeDrawableUtils.getRoundendButton(color, Utils.dpToPx(6)));
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) button.getLayoutParams();
            params.height = Utils.dpToPx(44);
            params.setMargins(0, getResources().getDimensionPixelSize(R.dimen.margin_s), 0, 0);
            button.setLayoutParams(params);

            final int providerIndex = i;

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), WebViewActivity.class);
                    intent.putExtra(WebViewActivity.EXTRA_VIEW_URL, providers.get(providerIndex).client_url.
                            replace("[culture_name]", ds.getCulture()).
                            //replace("[culture_name]", getString(R.string.host_language)).
                                    //replace("[app_host]", "/").
                                    replace("[app_code]", password).
                            replace("[app_id]", getString(R.string.app_id)));
                    //intent.putExtra(WebViewActivity.EXTRA_RETURN_URL, Constants.getOAUTH2_SERVICE_URL() + "?");
                    intent.putExtra(WebViewActivity.EXTRA_RETURN_DATA_NAME, EXTRA_ACCESS_TOKEN);
                    startActivityForResult(intent, REQUEST_CODE_GET_TOKEN);
                }
            });
        }
    }

    public void setHeader(EventHeader header) {
        header.setListener(this);
        header.getBackButton().setText(Consts.Icons.icon_Arrowdown);
        header.getIconOne().setVisibility(View.GONE);
        header.getIconTwo().setVisibility(View.GONE);
        header.getIconThree().setTypeface(TypeFaceProvider.getTypeFace(TypeFaceProvider.FONT_LATO));
        header.getIconThree().setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
    }

    @Override
    public void onBackButtonClicked() {
        getActivity().onBackPressed();
    }

    @Override
    public void onOneIconClicked() {
    }

    protected void getToken(String userName, String password) {
        showDialog(dm.getResourceText(R.string.Loading));
        ProfileManager.Login(userName, password, new GenericCallback() {
            @Override
            public void onDone(boolean success, Object data) {
                if (!success) {
                    stopDialog();
                    return;
                }
                ResponseSignIn responseSignIn = (ResponseSignIn) data;
                Preferences.getInstance().setAccessToken(responseSignIn.getAccess_token());
                Preferences.getInstance().setTokenType(responseSignIn.getToken_type());
                Preferences.getInstance().setExpireIn(responseSignIn.getExpires_in());
                Preferences.getInstance().setUsername(responseSignIn.getUserName());
                Preferences.getInstance().setExpires(responseSignIn.getExpires());

                mCallbacks.onLoginCompleted();
            }
        });
    }

    @Override
    public void onTwoIconClicked() {
    }

    public void startProgressDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            return;
        }

        mDialog = new ProgressDialog(getActivity());
        mDialog.setCancelable(false);
        mDialog.setCanceledOnTouchOutside(false);
        ((ProgressDialog) mDialog).setMessage("Loading...");
        mDialog.show();
    }

    public void stopProgressDialog() {
        if (mDialog == null || !mDialog.isShowing()) {
            return;
        }

        mDialog.dismiss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_GET_TOKEN:
                if (resultCode == Activity.RESULT_OK) {
                    String token = data.getStringExtra(EXTRA_ACCESS_TOKEN);
                    EMLog.d(TAG, "result token is: " + token);

                    startProgressDialog();

                    // login with token
                    ServerConnector.getInstance().processRequest(new RequestProviderLogin(token), new ServerConnector.OnResultListener() {
                        @Override
                        public void onSuccess(BaseResponse baseResponse) {
                            EMLog.i(TAG, "RequestProviderLogin onSuccess");
                            ResponseSignIn responseSignIn = (ResponseSignIn) baseResponse;

                            Preferences.getInstance().setAccessToken(responseSignIn.getAccess_token());
                            Preferences.getInstance().setTokenType(responseSignIn.getToken_type());
                            Preferences.getInstance().setExpireIn(responseSignIn.getExpires_in());
                            Preferences.getInstance().setUsername(responseSignIn.getUserName());
                            Preferences.getInstance().setExpires(responseSignIn.getExpires());

                            mCallbacks.onLoginCompleted();
                        }

                        @Override
                        public void onFailure(ErrorResponse errorResponse) {
                            EMLog.i(TAG, "RequestProviderLogin onFailure " + errorResponse);

                            stopProgressDialog();
                        }
                    });
                }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    public interface Callbacks {
        void onActionClick(boolean isLoginClick);

        void onRegistrationComplete(String email);

        void onLoginCompleted();

        void onForgotPasswordClick();
    }
}
