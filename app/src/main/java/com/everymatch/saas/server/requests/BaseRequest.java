package com.everymatch.saas.server.requests;


import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.ServerConnector;
import com.google.gson.Gson;

import java.util.Map;

/**
 * Base Request
 */
public abstract class BaseRequest {

    protected DataStore dm = DataStore.getInstance();

    /**
     * Get the service url of which this request belong to
     */
    public abstract String getServiceUrl();

    /**
     * Get the url function corresponding to this specific request
     */
    public abstract String getUrlFunction();

    /**
     * Create the corresponding class that will be used to parse the response
     *
     * @return Base response
     */
    public abstract Class getResponseClass();

    /**
     * Get method type
     *
     * @return method type
     * @see com.android.volley.Request.Method
     */
    public abstract int getType();

    /**
     * Override this method if you need to specify a unique timeout (in milliseconds) for the request
     */
    public int getConnectionTimeout() {
        return ServerConnector.DEFAULT_TIMEOUT;
    }

    /**
     * Override this method if you need to specify a unique retry number for the request
     */
    public int getNumberOfRetries() {
        return ServerConnector.DEFAULT_NUMBER_OF_RETRIES;
    }

    /**
     * Override this method if you want to do the encoding of the body by yourself (necessary only for post/put methods)
     */
    public String getEncodedBody() {
        return null;
    }

    /**
     * Override this method if you want to set your own encoding type (and not the default application/json)
     */
    public String getBodyContentType() {
        return null;
    }

    /**
     * Override this method if you need to specify custom headers
     */
    public Map<String, String> addExtraHeaders() {
        return null;
    }

    /**
     * Override this method if you need to specify custom implementation for parsing
     */
    public Gson getGsonDeserializer() {
        return null;
    }

    public boolean parseResponseAsJson() {
        return true;
    }
}