package com.everymatch.saas.server.responses;

import com.everymatch.saas.server.Data.DataActivity;

/**
 * Created by Dacid on 30/06/2015.
 */
public class ResponseActivities extends BaseResponse {
    DataActivity array[];

    public DataActivity[] getActivitiesData() {
        return array;
    }
}
