package com.everymatch.saas.server.request_manager;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.ServerConnector;
import com.everymatch.saas.server.requests.BaseRequest;
import com.everymatch.saas.server.requests.GsonRequest;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.server.responses.ResponseString;
import com.everymatch.saas.singeltones.GenericCallback;
import com.everymatch.saas.singeltones.Preferences;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by PopApp_laptop on 25/01/2016.
 */
public class NotificationManager {

    public static void markNotificationsAsReadOrSeen(final String status, final String ids, final GenericCallback callback) {
        //PUT: /api/notifications?app_id=&hl=
        ServerConnector.getInstance().processRequest(new BaseRequest() {
            @Override
            public String getServiceUrl() {
                return Constants.getAPI_SERVICE_URL();
            }

            @Override
            public String getUrlFunction() {
                return "api/notifications?app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id) +
                        "&hl=" + DataStore.getInstance().getCulture();

            }

            @Override
            public Class getResponseClass() {
                HashMap<String, Object> hashMap = new HashMap<>();
                return hashMap.getClass();
            }

            @Override
            public String getEncodedBody() {
                Map m = new HashMap<String, String>();
                m.put("field_name", status);
                m.put("ids", ids);
                JSONObject obj = new JSONObject(m);
                String str = obj.toString();
                return str;
            }

            @Override
            public int getType() {
                return Request.Method.PUT;
            }

            @Override
            public boolean parseResponseAsJson() {
                return false;
            }

            @Override
            public String getBodyContentType() {
                return GsonRequest.CONTENT_TYPE_X_URL_ENCODED;
            }

            @Override
            public Map<String, String> addExtraHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                String token = Preferences.getInstance().getTokenType();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        }, new ServerConnector.OnResultListener() {

            @Override
            public void onSuccess(BaseResponse baseResponse) {
                String responseStr = ((ResponseString) baseResponse).responseStr;
                if (callback != null)
                    callback.onDone(true, null);
            }

            @Override
            public void onFailure(ErrorResponse errorResponse) {
                if (callback != null)
                    callback.onDone(false, errorResponse.toString());
            }
        });
    }
}
