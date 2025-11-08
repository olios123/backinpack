package me.olios.backinpack.Library.JSON;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import me.olios.backinpack.Objects.BackpackContentObject;
import me.olios.backinpack.Objects.BackpackObject;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Deserializer extends StdDeserializer<BackpackObject> {

    public Deserializer()
    {
        this(null);
    }

    public Deserializer(Class<?> vc)
    {
        super(vc);
    }

    @Override
    public BackpackObject deserialize(JsonParser jsonParser,
                                      DeserializationContext deserializationContext)
            throws IOException
    {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        BackpackObject inventory = new BackpackObject();
        List<BackpackContentObject> backpacks = new ArrayList<>();

        for (JsonNode backpackNode : node)
        {
            String id = backpackNode.get("id").asText();
            String name = backpackNode.get("name").asText();
            int size = backpackNode.get("size").asInt();
            boolean crafted = backpackNode.get("crafted").asBoolean();

            // Items
            List<ItemStack> items = new ArrayList<>();
            JsonNode itemsNode = backpackNode.get("items");
            for (JsonNode itemNode : itemsNode)
            {
                if (itemNode.isNull()) items.add(null);
                else items.add(deserializeItemStack(itemNode));
            }

            // Flags
            List<String> flags = new ArrayList<>();
            JsonNode flagsNode = backpackNode.get("flags");
            for (JsonNode flagNode : flagsNode)
            {
                flags.add(new ObjectMapper().convertValue(flagNode, new TypeReference<String>() {}));
            }

            BackpackContentObject backpack = new BackpackContentObject();
            backpack.id = id;
            backpack.name = name;
            backpack.size = size;
            backpack.items = items;
            backpack.crafted = crafted;
            backpack.flags = flags;

            backpacks.add(backpack);
        }

        inventory.backpacks = backpacks;

        return inventory;
    }

    private ItemStack deserializeItemStack(JsonNode itemNode)
    {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> itemMap = objectMapper.convertValue(itemNode, new TypeReference<Map<String, Object>>() {});

        return ItemStack.deserialize(itemMap);
    }
}
