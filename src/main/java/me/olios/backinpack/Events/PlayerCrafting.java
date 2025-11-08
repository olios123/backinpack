package me.olios.backinpack.Events;

import me.olios.backinpack.Managers.BackpacksManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerCrafting implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    private static void onPlayerCraftingEvent(CraftItemEvent e)
    {
        Player p = (Player) e.getWhoClicked();
        String uuid = p.getUniqueId().toString();

        // Check if backpacks were crafted
        ItemStack craftedItem = e.getInventory().getResult();

        // Item has ben crafted
        if (craftedItem != null && craftedItem.getType() != Material.AIR)
        {
            // Item is a backpack
            if (BackpacksManager.isBackpack(craftedItem))
            {
                // Check if used with SHIFT
                if (e.isShiftClick())
                {
                    e.setCancelled(true);
                    return;
                }

                // New unassigned backpack item
                ItemStack unassignedBackpackItem = BackpacksManager.createUnassignedBackpackItem();

                // Replace old item
                e.setCurrentItem(unassignedBackpackItem);
            }
        }
    }

}
