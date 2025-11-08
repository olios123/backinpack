package me.olios.backinpack.Objects;

import me.olios.backinpack.Data;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BackpackContentObject
{
    public String id;
    public String name;
    public String type; // default or another name from multiple backpacks
    public int size;
    public List<ItemStack> items;
    public boolean crafted;
    public List<String> flags;

    public void setItem(int pos, ItemStack item)
    {
        items.set(pos, item);
    }
}
