package me.olios.backinpack.Objects;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import me.olios.backinpack.Library.JSON.Deserializer;
import me.olios.backinpack.Library.JSON.Serializer;

import java.util.ArrayList;
import java.util.List;

@JsonSerialize(using = Serializer.class)
@JsonDeserialize(using = Deserializer.class)
public class BackpackObject
{
    public String uuid; // Player UUID
    public List<BackpackContentObject> backpacks = new ArrayList<>(); // Backpack list

    public void addItem(BackpackContentObject backpack)
    {
        backpacks.add(backpack);
    }
}