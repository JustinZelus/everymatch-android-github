package com.everymatch.saas.ui.base;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.Data.DataActivity;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.server.requests.RequestApplication;
import com.everymatch.saas.server.requests.RequestGetUser;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.server.responses.ResponseApplication;
import com.everymatch.saas.server.responses.ResponseGetUser;
import com.everymatch.saas.singeltones.PusherManager;
import com.everymatch.saas.ui.BaseActivity;
import com.everymatch.saas.ui.discover.DiscoverActivity;
import com.everymatch.saas.ui.questionnaire.QuestionnaireActivity;
import com.everymatch.saas.ui.sign.SignActivity;
import com.everymatch.saas.util.EMLog;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by PopApp_laptop on 16/11/2015.
 */
public abstract class BaseLoginActivity extends BaseActivity {
    public final String TAG = getClass().getName();

    protected void fetchApplicationData() {
        ServerConnector.getInstance().processRequest(new RequestApplication(), new ServerConnector.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                Log.i(TAG, "RequestApplication onSuccess");
                ResponseApplication responseApplication = (ResponseApplication) baseResponse;
                if (responseApplication.timestamp != null) {
                    ds.setApplicationData(responseApplication);
                }
                onFetchApplicationFinished();
            }

            @Override
            public void onFailure(ErrorResponse errorResponse) {
                Log.i(TAG, "RequestApplication onFailure");
                //TODO take care of 403 code
                if (errorResponse.getStatusCode() == HttpsURLConnection.HTTP_UNAUTHORIZED) {
                    // lets delete our token and try again
                    onGetApplicationFailure();
                } else {
                    //TODO show error dialog
                }
            }
        });
    }

    public void fetchUser() {
        ServerConnector.getInstance().processRequest(new RequestGetUser(), new ServerConnector.OnResultAdapter() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                ResponseGetUser responseGetUser = (ResponseGetUser) baseResponse;
                if (responseGetUser != null) {
                    DataStore.getInstance().setUserData(responseGetUser);
                    onGetUserFinished();
                    return;
                } else {
                }
            }

            @Override
            public void onFailure(ErrorResponse errorResponse) {
                super.onFailure(errorResponse);
                EMLog.e(TAG, "Request getUser onFailure" + errorResponse.getServerRawResponse());
                //check if status code == 403 (authorisation)
                //performLoginOperation();
                goToLogin();
            }
        }, TAG + "RequestGetUser");
    }

    protected void goToLogin() {
        //go to login screen
        Intent intent = new Intent(BaseLoginActivity.this, SignActivity.class);
        BaseLoginActivity.this.finish();
        BaseLoginActivity.this.startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    public void performLoginOperation() {
        ResponseGetUser responseGetUser = ds.getUser();
        ds.getUser().user_settings.setDefaultData();
        registerToPusher();
        if (responseGetUser.user_settings.user_activity_profile_id_list != null && responseGetUser.user_settings.user_activity_profile_id_list.length() > 0) {
            // user already filled profile
            goToDiscover(responseGetUser.user_settings.last_activity_id);
            return;
        }
        // user has not answer any profie
        else {
            // here we start QuestionnaireActivity and pass it default_activity parameter.
            // QuestionnaireActivity will check if default_activity is null and will go to select activity screen.
            // else the user will be answering default acivity
            final Intent intent = new Intent(BaseLoginActivity.this, QuestionnaireActivity.class);
            // set the question flag to activity creation
            int defaultActivity = 0;
            for (DataActivity ac : ds.getApplicationData().getActivities()) {
                if (ac.is_default == true) {
                    defaultActivity = ac._id;
                    break;
                }
            }

            QuestionnaireActivity.create_mode = QuestionnaireActivity.CREATE_MODE.CREATE_ACTIVITY;
            //intent.putExtra(QuestionnaireActivity.EXTRA_ACTIVITY_ID, responseGetUser.user_settings.application_default_activity);
            intent.putExtra(QuestionnaireActivity.EXTRA_ACTIVITY_ID, defaultActivity);
            finish();
            startActivity(intent);
        }
    }

    protected void goToDiscover(String last_activity_id) {
        finish();

        if (TextUtils.isEmpty(last_activity_id))
            BaseLoginActivity.this.startActivity(new Intent(BaseLoginActivity.this, DiscoverActivity.class));
        else
            DiscoverActivity.startActivity(BaseLoginActivity.this, DiscoverActivity.EXTRA_ACTIVITY_ID, last_activity_id);
    }

    private void registerToPusher() {
        PusherManager.getInstance().registerAll();
    }

    protected abstract void onGetUserFinished();

    protected abstract void onFetchApplicationFinished();

    protected abstract void onGetApplicationFailure();
}
