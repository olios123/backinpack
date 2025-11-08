package me.olios.backinpack.Objects;

import me.olios.backinpack.Data;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class BackpackConfig {

    public String NAME;
    // Settings
    public int BACKPACK_SIZE;
    public int COST;
    public boolean GIVE_AS_ITEM;
    public List<String> FLAGS = new ArrayList<>();

    // Style item
    public ItemStack STYLE_ITEM;

    // Style gui
    public ItemStack STYLE_GUI;

}
