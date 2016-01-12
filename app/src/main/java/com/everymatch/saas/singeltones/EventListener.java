package com.everymatch.saas.singeltones;

import com.everymatch.saas.server.Data.DataEvent;
import com.everymatch.saas.server.Data.DataEventHolder;

/**
 * Created by Dor on 10/29/15.
 */
public interface EventListener {
    void onEventClick(DataEvent event);

    void onViewAllEventsClick(DataEventHolder dataEventHolder);
}
