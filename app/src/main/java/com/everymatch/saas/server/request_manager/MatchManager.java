package com.everymatch.saas.server.request_manager;

import android.util.Log;

import com.everymatch.saas.server.Data.DataMatchResults;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.server.requests.RequestEvent;
import com.everymatch.saas.server.requests.RequestMatch;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.singeltones.GenericCallback;

/**
 * Created by PopApp_laptop on 18/01/2016.
 */
public class MatchManager {
    public static final String TAG = "MatchManager";

    public static void getMatch(String matchType, String objectId, String activity_client_id, final GenericCallback callback) {
        ServerConnector.getInstance().processRequest(new RequestMatch(matchType, objectId, activity_client_id), new ServerConnector.OnResultListener() {
            @Override
            public void onSuccess(BaseResponse baseResponse) {
                try {
                    DataMatchResults mDataMatchResults = (DataMatchResults) baseResponse;
                    if (mDataMatchResults != null && callback != null) {
                        callback.onDone(true, mDataMatchResults);
                        return;
                    }
                    callback.onDone(false, "Parse Error");
                } catch (Exception ex) {
                    Log.e(TAG, ex.getMessage());
                    if (callback != null) {
                        callback.onDone(false, ex.getMessage());
                    }
                }

            }

            @Override
            public void onFailure(ErrorResponse errorResponse) {
                callback.onDone(false, errorResponse);
            }
        }, TAG + RequestEvent.class.getSimpleName());
    }
}
