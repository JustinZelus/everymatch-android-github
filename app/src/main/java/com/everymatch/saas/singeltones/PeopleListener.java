package com.everymatch.saas.singeltones;

import com.everymatch.saas.server.Data.DataPeople;
import com.everymatch.saas.server.Data.DataPeopleHolder;

public interface PeopleListener {
    void onUserClick(DataPeople user);

    void onViewAllUsersClick(DataPeopleHolder holder);
}