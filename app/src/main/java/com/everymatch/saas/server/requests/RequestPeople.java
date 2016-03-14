package com.everymatch.saas.server.requests;

import com.android.volley.Request;
import com.everymatch.saas.Constants;
import com.everymatch.saas.EverymatchApplication;
import com.everymatch.saas.R;
import com.everymatch.saas.client.data.DataStore;
import com.everymatch.saas.server.responses.ResponsePeople;
import com.everymatch.saas.singeltones.Preferences;
import com.everymatch.saas.ui.event.MyPeopleListFragment;

import java.util.HashMap;
import java.util.Map;

public class RequestPeople extends BaseRequest {


    // For pagination
    private transient String type;
    private transient int start;
    private transient int count;

    @Override
    public String getServiceUrl() {
        return Constants.getAPI_SERVICE_URL();
    }

    public RequestPeople(String type, int start, int count) {
        this.type = type;
        this.start = start;
        this.count = count;
    }

    @Override
    public String getUrlFunction() {
        StringBuilder request = new StringBuilder();

        //https://api.everymatch.me/api/friends?
        // start=10&count=10&search_phrase=&collection_name=&object_id=&app_id=20150208103953&hl=en-US&_=1457277752282
        if (type.equals(MyPeopleListFragment.TYPE_MY_FRIENDS)){
            request.append("api/friends?hl=" + DataStore.getInstance().getCulture() +
                    "&app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id) +
                    "&search_phrase=&collection_name=&object_id=");
        } else{
            request.append("api/recentlyviewed?hl=" + DataStore.getInstance().getCulture() +
                    "&app_id=" + EverymatchApplication.getContext().getResources().getString(R.string.app_id) +
                    "&search_phrase=&collection_name=users&object_id=");
        }

        request.append("&start=" + start + "&count=" + count);

        return request.toString();
    }

    @Override
    public Class getResponseClass() {
        return ResponsePeople.class;
    }

    @Override
    public int getType() {
        return Request.Method.GET;
    }

    @Override
    public Map<String, String> addExtraHeaders() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Authorization", "Bearer " + Preferences.getInstance().getTokenType());
        return headers;
    }
}
