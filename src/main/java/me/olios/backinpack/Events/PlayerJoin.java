package me.olios.backinpack.Events;

import me.olios.backinpack.Data;
import me.olios.backinpack.Library.NBTTags;
import me.olios.backinpack.Managers.BackpacksManager;
import me.olios.backinpack.Managers.MessagesManager;
import me.olios.backinpack.Objects.BackpackContentObject;
import me.olios.backinpack.Objects.BackpackObject;
import me.olios.backinpack.UpdateChecker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class PlayerJoin implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerJoinEvent(PlayerJoinEvent e) 
    {
        Player p = e.getPlayer();
        Inventory inv = p.getInventory();

        /**
         * Replacing not existing backpacks in inventories
         */
        for (int i = 0; i < inv.getSize(); i++)
        {
            ItemStack item = inv.getItem(i);

            if (item == null || !BackpacksManager.isBackpack(item)) continue;

            String backpackID = NBTTags.getNBT(item, "backinpack.backpack-id");

            if (backpackID == null) continue; // Is unassigned backpack

            // Assigned backpack
            String backpackOwner = NBTTags.getNBT(item, "backinpack.backpack-owner");

            // Check if player's inventory exists
            BackpackObject inventory = BackpacksManager.getInventory(backpackOwner);

            // Player's inventory doesn't exists
            if (inventory == null)
            {
                ItemStack unassignedBackpack = BackpacksManager.createUnassignedBackpackItem();
                inv.setItem(i, unassignedBackpack);
                continue;
            }

            // Inventory exists and gets backpack
            BackpackContentObject backpack = BackpacksManager.getInventoryByBackpackID(backpackOwner, backpackID);

            // Backpack not found in player inventory
            if (backpack == null)
            {
                ItemStack unassignedBackpack = BackpacksManager.createUnassignedBackpackItem();
                inv.setItem(i, unassignedBackpack);
                continue;
            }
        }

        // Create first backpack
        BackpacksManager.createFirstBackpack(p.getUniqueId().toString());

        // Update info
        new UpdateChecker((JavaPlugin) Data.plugin, Data.resourceId).getVersion(version ->
        {
            if (!Data.plugin.getDescription().getVersion().equals(version))
            {
                // Plugin can be updated
                // Info about update to player
                if (p.isOp()) MessagesManager.sendUpdateInfo(p);
            }
        });
    }

}
