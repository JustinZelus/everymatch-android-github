package com.everymatch.saas.server.serialization;

import com.everymatch.saas.server.Data.Resource;
import com.everymatch.saas.server.Data.Resources;
import com.everymatch.saas.server.responses.ResponseResources;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

/**
 * Created by dors on 8/2/15.
 */
public class ResourcesDeserializer implements JsonDeserializer<ResponseResources> {


    @Override
    public ResponseResources deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

        // Create response class
        ResponseResources responseResources = new ResponseResources();
        responseResources.dataResources = new Resources();

        JsonArray resourcesJsonArray = json.getAsJsonArray();
        final int size = resourcesJsonArray.size();

        for (int i = 0; i < size; i++) {
            JsonElement jsonElement = resourcesJsonArray.get(i);
            Resource resource = context.deserialize(jsonElement, Resource.class);
            responseResources.dataResources.getResourcesMap().put(resource.key, resource);
        }

        return responseResources;
    }
}
