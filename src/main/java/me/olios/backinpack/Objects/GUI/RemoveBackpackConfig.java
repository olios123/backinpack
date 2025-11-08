package me.olios.backinpack.Objects.GUI;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class RemoveBackpackConfig {

    public String TITLE = "";
    public List<String> backpackLORE;
    public ItemStack cancel;

    public RemoveBackpackConfig(String TITLE,
                                   List<String> backpackLORE,
                                   ItemStack cancel)
    {
        this.TITLE = TITLE;
        this.backpackLORE = backpackLORE;
        this.cancel = cancel;
    }

}
