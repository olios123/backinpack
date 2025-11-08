package me.olios.backinpack.Library;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import me.olios.backinpack.Objects.BackpackObject;

public class Json {

    public static String toJson(Object object)
    {
        try
        {
            return new ObjectMapper().writeValueAsString(object);
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public static BackpackObject fromJsonInventory(String json)
    {
        try
        {
            return new ObjectMapper().readValue(json, BackpackObject.class);
        }
        catch (JsonProcessingException e)
        {
            e.printStackTrace();
        }
        return null;
    }

}
