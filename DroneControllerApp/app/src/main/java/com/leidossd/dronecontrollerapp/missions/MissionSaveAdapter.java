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

/**
 * MissionSaveAdapter Class:
 * Handles conversion from Class data to a Json File.  A custom type adapter, such as this one
 * is needed, when polymorphism is used to assign different types of subclass, such as Dogs and Cats,
 * to a parent class, such as Animals.  It needs this adapter to be able to determine the actual type.
 */
class MissionSaveAdapter implements JsonSerializer, JsonDeserializer {
    //Converts Json data to an object.
    @Override
    public Object deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        Object obj = null;
        //Object is received as JsonElement, so have to convert it to a JsonObject
        JsonObject container = json.getAsJsonObject();
        //Retrieve data corresponding to each string.  Type is the actual subclass the mission/task is,
        //and group is the data.
        String type = container.get("Type").getAsString();
        JsonElement mission = container.get("Group");
        //Convert the Json data to a mission/task.
        try {
            obj = context.deserialize(mission, Class.forName(type));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return obj;
    }

    //Converts an object to Json data.
    @Override
    public JsonElement serialize(Object src, Type typeOfSrc, JsonSerializationContext context) {
        //Create json object, then write actual class type to "Type" and class data to "Group"
        JsonObject json = new JsonObject();
        json.add("Type", new JsonPrimitive(src.getClass().getName()));
        json.add("Group", context.serialize(src, src.getClass()));
        return json;
    }
}
