package com.everymatch.saas.server.responses;

import com.everymatch.saas.server.Data.DataEventHolder;
import com.everymatch.saas.server.Data.DataPeopleHolder;

/**
 * Created by dors on 11/17/15.
 */
public class ResponseBestMatches extends BaseResponse {
    public DataPeopleHolder users;
    public DataEventHolder events;
}
