package com.everymatch.saas.server.requests;

import android.text.TextUtils;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonRequest;
import com.everymatch.saas.server.responses.BaseResponse;
import com.everymatch.saas.server.responses.ErrorResponse;
import com.everymatch.saas.server.responses.ResponseString;
import com.everymatch.saas.util.EMLog;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Map;

public class GsonRequest<T extends BaseResponse> extends JsonRequest<BaseResponse> {

    private static final String TAG = GsonRequest.class.getSimpleName();

    public static final String CONTENT_TYPE_X_URL_ENCODED = "application/x-www-form-urlencoded; charset=UTF-8";

    public static final String HTTP_STATUS_KEY = "http_status";
    public static final String JSON_ARRAY_RESPONSE = "array";

    private final Gson gson;
    private final Class<T> clazz;
    private final Map<String, String> headers;
    private final String bodyContentType;
    private final Response.Listener<BaseResponse> listener;
    private final Response.ErrorListener errorListener;
    private boolean parseJsonUsingCustomDeserializer, parseResponseAsJson;

    /**
     * Make a request and return a parsed object from JSON.
     *
     * @param url     URL of the request to make
     * @param clazz   Relevant class object, for Gson's reflection
     * @param headers Map of request headers
     */
    public GsonRequest(int method, String url, String body, String bodyContentType, Class<T> clazz, Map<String, String> headers,
                       Gson gsonDeserializer, boolean parseResponseAsJson, Response.Listener<BaseResponse> listener, Response.ErrorListener errorListener) {
        super(method, url, body, listener, errorListener);
        this.clazz = clazz;
        this.bodyContentType = bodyContentType;
        this.headers = headers;
        this.listener = listener;
        this.errorListener = errorListener;
        this.parseResponseAsJson = parseResponseAsJson;

        if (gsonDeserializer == null) {
            this.gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
        } else {
            this.gson = gsonDeserializer;
            parseJsonUsingCustomDeserializer = true;
        }
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return headers != null ? headers : super.getHeaders();
    }

    @Override
    public String getBodyContentType() {
        return bodyContentType != null ? bodyContentType : super.getBodyContentType();
    }

    @Override
    protected Response<BaseResponse> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Log.d("Response", json);

            BaseResponse baseResponse;

            if (clazz == null) {
                baseResponse = new ResponseString(json);
                baseResponse.setHttpStatus(response.statusCode);
            } else {
                // A success response - but has no body - pass an empty base response
                if (TextUtils.isEmpty(json)) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put(HTTP_STATUS_KEY, response.statusCode);
                    baseResponse = gson.fromJson(jsonObject.toString(), clazz);
                } else if (parseResponseAsJson) {
                    if (!parseJsonUsingCustomDeserializer && json.charAt(0) == '[') { // handle array
                        JSONObject jsonObject = new JSONObject();
                        JSONArray jsonArray = new JSONArray(json);
                        jsonObject.put(JSON_ARRAY_RESPONSE, jsonArray);
                        json = jsonObject.toString();
                    }

                    baseResponse = gson.fromJson(json, clazz);
                    baseResponse.setHttpStatus(response.statusCode);
                } else {
                    baseResponse = new ResponseString(json);
                    baseResponse.setHttpStatus(response.statusCode);
                }
            }

            return Response.success(baseResponse, HttpHeaderParser.parseCacheHeaders(response));
        } catch (Exception e) {
            EMLog.e(TAG, e.getMessage());
            return Response.error(new ParseError(e));
        }
    }

    @Override
    protected ErrorResponse parseNetworkError(VolleyError volleyError) {

        ErrorResponse errorResponse = new ErrorResponse();
        try {
            errorResponse.setStatusCode(volleyError.networkResponse.statusCode);
            String error = new String(volleyError.networkResponse.data);
            errorResponse.setServerRawResponse(error);
            EMLog.i(TAG, "Server error response -> " + error);
            EMLog.i(TAG, "Status code -> " + volleyError.networkResponse.statusCode);
        } catch (Exception ignored) {
            // Nothing in here
        }
        return errorResponse;
    }


    @Override
    protected void deliverResponse(BaseResponse response) {
        listener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        if (!(error instanceof ErrorResponse)) {
            errorListener.onErrorResponse(new ErrorResponse());
            return;
        }
        errorListener.onErrorResponse(error);
    }
}