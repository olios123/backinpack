package me.olios.backinpack.Objects;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Cache {

    public List<OpenedBackpack> openedBackpacks = new ArrayList<>();
    public List<ChangeBackpackName> changeBackpackNames = new ArrayList<>();
    public List<AssigningBackpack> assigningBackpacks = new ArrayList<>();

    public static class OpenedBackpack
    {
        public String owner;
        public String sender;
        public int size;
        public String backpackID;
        public Inventory inventory;

        public OpenedBackpack(String owner,
                              String sender,
                              int size,
                              String backpackID,
                              Inventory inventory)
        {
            this.owner = owner;
            this.sender = sender;
            this.size = size;
            this.backpackID = backpackID;
            this.inventory = inventory;
        }
    }

    public static class ChangeBackpackName
    {
        public String owner;
        public String backpackID;
    }

    public static class AssigningBackpack
    {
        public String owner;
        public ItemStack item;
    }

}
