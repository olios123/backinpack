//
// CC Creative Commons 2022
// Attribution-NoDerivatives 4.0 International
// Author olios
//

package me.olios.backinpack.Events;

import me.olios.backinpack.Data;
import me.olios.backinpack.Main;
import me.olios.backinpack.Managers.BackpacksManager;
import me.olios.backinpack.Managers.ConfigManager;
import me.olios.backinpack.Managers.MessagesManager;
import me.olios.backinpack.Managers.PermissionsManager;
import me.olios.backinpack.Objects.BackpackContentObject;
import me.olios.backinpack.Objects.BackpackObject;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PlayerDeath implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerDeath(PlayerDeathEvent e)
    {
        Player p = e.getEntity().getPlayer();
        p.recalculatePermissions(); // Refresh permissions
        String uuid = p.getUniqueId().toString();
        Location deathLocation = e.getEntity().getLocation();
        BackpackObject inventory = BackpacksManager.getInventory(uuid);


        // Player doesn't have backpacks
        if (inventory == null || inventory.backpacks.isEmpty()) return;


        // Player has permission protecting his items after death
        if (PermissionsManager.checkPermissions(p, Data.Permission.BACKPACK_SAVE))
        {
            MessagesManager.sendMessage(p, Data.Message.PLAYER_DEATH_SAVE);
            return;
        }


        // Decide what is going to do with items after death
        switch (ConfigManager.config.PLAYER_DEAD)
        {
            // Items stays in player backpack
            case "save":
                // Send message to player
                MessagesManager.sendMessage(p, Data.Message.PLAYER_DEATH_SAVE);
                break;

            // All items are deleted after death
            case "delete":

                // Delete all items from all backpacks
                inventory.backpacks.forEach(bck ->
                {
                    if (bck.flags.contains(Data.BackpackFlags.ITEMS_SAVE.toString())) return;

                    bck.items = new ArrayList<>();
                    BackpacksManager.replaceBackpackInInventory(inventory, bck);
                });

                // Send message to player
                MessagesManager.sendMessage(p, Data.Message.PLAYER_DEATH_DELETE);
                break;

            // All items are dropped after death
            case "drop":

                // Drop items in death location
                inventory.backpacks.forEach(bck ->
                {
                    if (bck.flags.contains(Data.BackpackFlags.ITEMS_SAVE.toString())) return;

                    bck.items.forEach(item ->
                    {
                        if (item != null) p.getWorld().dropItemNaturally(deathLocation, item);
                    });
                    bck.items = new ArrayList<>();
                    BackpacksManager.replaceBackpackInInventory(inventory, bck);
                });

                // Send message to player
                MessagesManager.sendMessage(p, Data.Message.PLAYER_DEATH_DROP);
                break;
        }

        // Delete player backpack with flag
        Iterator<BackpackContentObject> backpacks = inventory.backpacks.iterator();
        while (backpacks.hasNext())
        {
            BackpackContentObject backpack = backpacks.next();
            if (backpack.flags.contains(Data.BackpackFlags.BACKPACK_DELETE.toString()))
            {
                backpacks.remove();
            }
        }
    }
}
