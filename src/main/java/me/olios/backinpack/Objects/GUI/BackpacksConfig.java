package me.olios.backinpack.Objects.GUI;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BackpacksConfig {

    public String TITLE = "";

    // Buy section
    public ItemStack buyBackpack;
    public ItemStack removeBackpack;

    public BackpacksConfig(String TITLE,
                           ItemStack buyBackpack,
                           ItemStack removeBackpack)
    {
        this.TITLE = TITLE;
        this.buyBackpack = buyBackpack;
        this.removeBackpack = removeBackpack;
    }

}
