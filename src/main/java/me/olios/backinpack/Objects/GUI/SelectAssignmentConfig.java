package me.olios.backinpack.Objects.GUI;

import org.bukkit.inventory.ItemStack;

public class SelectAssignmentConfig {

    public String TITLE = "";
    public ItemStack createNewBackpack;
    public ItemStack assignToExisting;

    public SelectAssignmentConfig(String TITLE,
                                  ItemStack createNewBackpack,
                                  ItemStack assignToExisting)
    {
        this.TITLE = TITLE;
        this.createNewBackpack = createNewBackpack;
        this.assignToExisting = assignToExisting;
    }
}
