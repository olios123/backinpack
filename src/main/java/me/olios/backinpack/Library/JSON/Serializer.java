package me.olios.backinpack.Library.JSON;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import me.olios.backinpack.Objects.BackpackContentObject;
import me.olios.backinpack.Objects.BackpackObject;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.*;

public class Serializer extends StdSerializer<BackpackObject> {

    public Serializer()
    {
        this(null);
    }

    public Serializer(Class<BackpackObject> t)
    {
        super(t);
    }

    @Override
    public void serialize(BackpackObject inventory,
                          JsonGenerator jsonGenerator,
                          SerializerProvider serializerProvider)
    {
        try
        {
            // {
            // -> id
            // -> name
            // -> size
            // -> items
            // -> crafted
            // -> flags
            // }

            jsonGenerator.writeStartArray();

            for (BackpackContentObject backpack : inventory.backpacks)
            {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeStringField("id", backpack.id);
                jsonGenerator.writeStringField("name", backpack.name);
                jsonGenerator.writeNumberField("size", backpack.size);

                List<Map<String, Object>> itemsMap = new ArrayList<>();
                for (ItemStack item : backpack.items)
                {
                    if (item != null) itemsMap.add(item.serialize());
                    else itemsMap.add(null);
                }

                jsonGenerator.writeObjectField("items", itemsMap);
                jsonGenerator.writeObjectField("crafted", backpack.crafted);

                // Convert flags to strings
                jsonGenerator.writeObjectField("flags", backpack.flags);
                jsonGenerator.writeEndObject();
            }

            jsonGenerator.writeEndArray();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
