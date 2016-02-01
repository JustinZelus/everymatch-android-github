package com.everymatch.saas.server.Data;

import java.util.HashMap;

/**
 * Created by dors on 8/2/15.
 */
public class Resources {
    private HashMap<String, Resource> resourcesMap;

    public HashMap<String, Resource> getResourcesMap() {
        if (resourcesMap == null)
            resourcesMap = new HashMap<>();
        return resourcesMap;
    }


    public Resources() {
        resourcesMap = new HashMap<>();
    }
}
