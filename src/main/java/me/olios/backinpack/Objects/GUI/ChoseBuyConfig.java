package me.olios.backinpack.Objects.GUI;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ChoseBuyConfig {

    public String TITLE;
    public int SIZE;
    public List<Bck> backpacksToBuy;
    public ItemStack cancel;

    public ChoseBuyConfig(String TITLE,
                           int SIZE,
                           List<Bck> backpacksToBuy,
                           ItemStack cancel)
    {
        this.TITLE = TITLE;
        this.SIZE = SIZE;
        this.backpacksToBuy = backpacksToBuy;
        this.cancel = cancel;
    }

    public static class Bck
    {
        public String name;
        public ItemStack backpack;
        public int positionX = 1;
        public int positionY = 1;
    }

}
