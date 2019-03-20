package com.leidossd.dronecontrollerapp.missions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

class MissionSaveAdapter implements JsonSerializer, JsonDeserializer {
    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Object obj = null;
        JsonObject container = json.getAsJsonObject();
        String type = container.get("Type").getAsString();
        JsonElement mission = container.get("Group");
        try {
            obj = context.deserialize(mission, Class.forName(type));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;
    }

    @Override
    public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject json = new JsonObject();
        json.add("Type", new JsonPrimitive(src.getClass().getName()));
        json.add("Group", context.serialize(src, src.getClass()));
        return json;
    }
}
