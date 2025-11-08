package me.olios.backinpack.Managers;

import me.olios.backinpack.Main;
import me.olios.backinpack.Objects.Cache;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CacheManager {

    public static Cache cache = new Cache();

    public static Cache getCache()
    {
        return cache;
    }

    public static void addOpenedBackpack(Cache.OpenedBackpack openedBackpack)
    {
        cache.openedBackpacks.add(openedBackpack);
    }

    public static void addChangeBackpackName(Cache.ChangeBackpackName changeBackpackName)
    {
        cache.changeBackpackNames.add(changeBackpackName);
    }

    public static Cache.OpenedBackpack getOpenedBackpackOwner(String owner)
    {
        for (Cache.OpenedBackpack openedBackpack : getCache().openedBackpacks)
        {
            if (openedBackpack.owner.equals(owner)) return openedBackpack;
        }
        return null;
    }

    public static List<Cache.OpenedBackpack> getAllOpenedBackpacksOwner(String owner)
    {
        List<Cache.OpenedBackpack> openedBackpacks = new ArrayList<>();
        for (Cache.OpenedBackpack openedBackpack : getCache().openedBackpacks)
        {
            if (openedBackpack.owner.equals(owner) && openedBackpack.backpackID != null)
                openedBackpacks.add(openedBackpack);
        }
        return openedBackpacks;
    }

    public static Cache.OpenedBackpack getOpenedBackpackSender(String sender)
    {
        for (Cache.OpenedBackpack openedBackpack : getCache().openedBackpacks)
        {
            if (openedBackpack.sender.equals(sender)) return openedBackpack;
        }
        return null;
    }

    public static Cache.OpenedBackpack getOpenedBackpackID(String backpackID)
    {
        for (Cache.OpenedBackpack openedBackpack : getCache().openedBackpacks)
        {
            if (openedBackpack == null || openedBackpack.backpackID == null) continue;
            if (openedBackpack.backpackID.equals(backpackID)) return openedBackpack;
        }
        return null;
    }

    public static void createOpenedBackpack(String owner,
                                            String sender,
                                            int size,
                                            String backpackID,
                                            Inventory inventory)
    {
        // If provided in backpackID null that means player has open backpackGUIList
        // First we have to check if player just switching from list to specified backpack

        // Data
        Cache.OpenedBackpack openedBackpack = new Cache.OpenedBackpack(
                owner,
                sender,
                size,
                backpackID,
                inventory);

        Cache.OpenedBackpack oldOpenedBackpack = getOpenedBackpackSender(sender);
        if (oldOpenedBackpack != null) replaceOpenedBackpack(oldOpenedBackpack, openedBackpack);
        else addOpenedBackpack(openedBackpack);
    }

    public static void createOpenedBackpack(String owner,
                                            String sender)
    {
        // If provided in backpackID null that means player has open backpackGUIList
        // First we have to check if player just switching from list to specified backpack

        // Data
        Cache.OpenedBackpack openedBackpack = new Cache.OpenedBackpack(
                owner,
                sender,
                0,
                null,
                null);

        Cache.OpenedBackpack oldOpenedBackpack = getOpenedBackpackSender(sender);
        if (oldOpenedBackpack != null) replaceOpenedBackpack(oldOpenedBackpack, openedBackpack);
        else addOpenedBackpack(openedBackpack);
    }

    public static void removeOpenedBackpack(Cache.OpenedBackpack openedBackpack)
    {
        cache.openedBackpacks.remove(openedBackpack);
    }

    public static void replaceOpenedBackpack(Cache.OpenedBackpack oldOpenedBackpack,
                                             Cache.OpenedBackpack newOpenedBackpack)
    {
        removeOpenedBackpack(oldOpenedBackpack);
        addOpenedBackpack(newOpenedBackpack);
    }

    public static void createChangeBackpackName(String owner, String backpackID)
    {
        Cache.ChangeBackpackName changeBackpackName = new Cache.ChangeBackpackName();
        changeBackpackName.owner = owner;
        changeBackpackName.backpackID = backpackID;

        addChangeBackpackName(changeBackpackName);
    }

    public static void removeChangeBackpackName(Cache.ChangeBackpackName changeBackpackName)
    {
        cache.changeBackpackNames.remove(changeBackpackName);
    }

    public static Cache.ChangeBackpackName getChangeBackpackName(String owner)
    {
        for (Cache.ChangeBackpackName changeBackpackName : cache.changeBackpackNames)
        {
            if (changeBackpackName.owner.equals(owner)) return changeBackpackName;
        }
        return null;
    }

    public static void createAssigningBackpack(String owner, ItemStack item)
    {
        Cache.AssigningBackpack assigningBackpack = new Cache.AssigningBackpack();
        assigningBackpack.owner = owner;
        assigningBackpack.item = item;

        addAssigningBackpack(assigningBackpack);
    }

    public static void addAssigningBackpack(Cache.AssigningBackpack assigningBackpack)
    {
        cache.assigningBackpacks.add(assigningBackpack);
    }

    public static void removeAssigningBackpack(Cache.AssigningBackpack assigningBackpack)
    {
        cache.assigningBackpacks.remove(assigningBackpack);
    }

    public static Cache.AssigningBackpack getAssigningBackpack(String owner)
    {
        for (Cache.AssigningBackpack assigningBackpack : cache.assigningBackpacks)
        {
            if (assigningBackpack.owner.equals(owner)) return assigningBackpack;
        }
        return null;
    }
}
