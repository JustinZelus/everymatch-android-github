package com.everymatch.saas.server;


import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataManager;
import com.everymatch.saas.server.requests.BaseRequest;
import com.everymatch.saas.server.requests.GsonRequest;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.ui.dialog.NetworkErrorMessageDialog;
import com.everymatch.saas.util.EMLog;
import com.everymatch.saas.util.Utils;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;

public class ServerConnector {

    private static final String TAG = ServerConnector.class.getSimpleName();
    private static ServerConnector sServerConnector = null;

    public static final int DEFAULT_TIMEOUT = 1000 * 15;
    public static final int DEFAULT_NUMBER_OF_RETRIES = 1;

    private Gson mGson;

    private ServerConnector() {
        mGson = new Gson();
    }

    public static ServerConnector getInstance() {
        if (sServerConnector == null) {
            synchronized (ServerConnector.class) {
                if (sServerConnector == null) {
                    sServerConnector = new ServerConnector();
                }
            }
        }

        return sServerConnector;
    }

    /**
     * add request to queue and process it as soon as possible
     *
     * @param baseRequest      the request containing the json and the url
     * @param onResultListener result listener
     */
    public void processRequest(BaseRequest baseRequest, OnResultListener onResultListener) {
        processRequest(baseRequest, onResultListener, null);
    }

    public void processRequest(final BaseRequest baseRequest, final OnResultListener onResultListener, Object tag) {

        if (!Utils.hasInternetConnection()) {
            Intent intent = new Intent(NetworkErrorMessageDialog.ACTION_NETWORK_ERROR);
            intent.putExtra(NetworkErrorMessageDialog.EXTRA_NETWORK_ERROR_TITLE, DataManager.getInstance().getResourceText(R.string.Connection_Lost));
            LocalBroadcastManager.getInstance(EverymatchApplication.getContext()).sendBroadcast(intent);
            return;
        }

        String url = baseRequest.getServiceUrl() + baseRequest.getUrlFunction();
        String body = null;
        final int type = baseRequest.getType();

        switch (type) {
            case Request.Method.POST:
            case Request.Method.PUT:

                body = baseRequest.getEncodedBody();

                if (body == null) {
                    body = mGson.toJson(baseRequest);
                }

                // url encoded types should contains "=" sign in the begging of the body
                if (body != null && GsonRequest.CONTENT_TYPE_X_URL_ENCODED.equals(baseRequest.getBodyContentType())) {
                    body = "=" + body;
                }

                break;
            case Request.Method.GET:
            case Request.Method.DELETE:
            case Request.Method.PATCH:
            {
                url = url.replaceAll(" ", "%20");
            }
            break;
        }

        Log.i(TAG, "serverConnector processRequest: " + url + "\n" + "with body: " + body);

        GsonRequest gsonRequest = new GsonRequest(type, url, body, baseRequest.getBodyContentType(), baseRequest.getResponseClass(), baseRequest.addExtraHeaders(),
                baseRequest.getGsonDeserializer(), baseRequest.parseResponseAsJson(), new Listener<BaseResponse>() {
            @Override
            public void onResponse(BaseResponse response) {
                Log.i(TAG, "onResponse: " + response.getClass().getSimpleName());
                onResultListener.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.w(TAG, "onErrorResponse: " + error.toString());
                ErrorResponse errorResponse = (ErrorResponse) error;

                if (errorResponse.getStatusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    Log.i(TAG, "Unauthorized - going to refresh token");
                    //TokenRefresher.doTokenRefresh();
                }

                EMLog.e(TAG, "onErrorResponse " + error.toString());

                onResultListener.onFailure(errorResponse);

                String errorStr = "Error";

                if (((ErrorResponse) error).getServerRawResponse() != null) {

                    try {
                        errorStr = DataManager.getInstance().getResourceText(R.string.GeneralError);

                        // Try to parse model state
                        JSONObject rootError = new JSONObject(((ErrorResponse) error).getServerRawResponse());

                        if (rootError.has("ModelState")) {
                            JSONObject jsonObjectModelState = rootError.getJSONObject("ModelState");

                            for (int i = 0; i < jsonObjectModelState.names().length(); i++) {
                                Object modelValue = jsonObjectModelState.get(jsonObjectModelState.names().getString(0));
                                errorStr = ((JSONArray) modelValue).getString(0);
                                break;
                            }
                        } else {
                            errorStr = rootError.getString("error_description");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Intent intent = new Intent(NetworkErrorMessageDialog.ACTION_NETWORK_ERROR);
                intent.putExtra(NetworkErrorMessageDialog.EXTRA_NETWORK_ERROR_TITLE, errorStr);
                LocalBroadcastManager.getInstance(EverymatchApplication.getContext()).sendBroadcast(intent);

                //NetworkErrorMessageDialog.start(EverymatchApplication.getCurrentActivity(), EverymatchApplication.getFragmentManager(), errorStr);
                //NetworkErrorMessageDialog(EverymatchApplication.getContext(), errorStr).show();

            }
        });

        // Set tag
        gsonRequest.setTag(tag == null ? baseRequest.getClass().getSimpleName() : tag);

        // Set retry policy
        gsonRequest.setRetryPolicy(new DefaultRetryPolicy(baseRequest.getConnectionTimeout(), baseRequest.getNumberOfRetries(), 0));

        // Add request to queue
        VolleyHelper.getRequestQueue().add(gsonRequest);
    }

    /**
     * Cancels all pending requests by the specified TAG, it is important
     * to specify a TAG so that the pending/ongoing requests can be cancelled.
     */
    public void cancelPendingRequests(Object tag) {
        RequestQueue requestQueue = VolleyHelper.getRequestQueue();

        if (requestQueue != null && tag != null) {
            requestQueue.cancelAll(tag);
        }
    }

    public interface OnResultListener {
        void onSuccess(BaseResponse baseResponse);

        void onFailure(ErrorResponse errorResponse);
    }

    public static abstract class OnResultAdapter implements OnResultListener {
        public void onSuccess(BaseResponse baseResponse) {
        }

        public void onFailure(ErrorResponse errorResponse) {
        }
    }
}