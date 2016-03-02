package com.everymatch.saas.server.requests;

import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;

/**
 * Created by PopApp_laptop on 17/01/2016.
 */
public class RequestMatchData extends BaseRequest {

    @Override
    public String getServiceUrl() {
        return Constants.getAPI_SERVICE_URL();
    }

    @Override
    public String getUrlFunction() {
        return "api/match?app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id)
                + "&hl=" + DataStore.getInstance().getCulture()
               // + "&activity_client_id=" + activity_client_id
                //+ "&action=" + action
               // + "&object_id=" + object_id
                ;
    }

    @Override
    public Class getResponseClass() {
        return null;
    }

    @Override
    public int getType() {
        return 0;
    }
}
