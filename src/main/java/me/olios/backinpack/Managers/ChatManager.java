package me.olios.backinpack.Managers;

import me.olios.backinpack.API.PAPICustom;
import me.olios.backinpack.Data;
import me.olios.backinpack.Objects.BackpackContentObject;
import me.olios.backinpack.Objects.BackpackObject;
import me.olios.backinpack.Objects.Cache;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;

public class ChatManager implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerChatEvent(AsyncPlayerChatEvent e) 
    {
        Player p = e.getPlayer();
        String uuid = p.getUniqueId().toString();
        p.recalculatePermissions(); // Refresh permissions
        String msg = e.getMessage();

        // Player is changing backpack name
        Cache.ChangeBackpackName changeBackpackName = CacheManager.getChangeBackpackName(uuid);
        if (changeBackpackName == null) return;

        String backpackID = changeBackpackName.backpackID;

        BackpackObject inventory = BackpacksManager.getInventory(uuid);
        BackpackContentObject backpack = BackpacksManager.getInventoryByBackpackID(uuid, backpackID);

        // Placeholders
        Map<String, Object> placeholders = PAPICustom.getBackpackPlaceholders(uuid, backpackID);
        placeholders.put("%old-backpack-name%", backpack.name);
        placeholders.put("%new-backpack-name%", msg);

        // Change backpack name and save changes
        backpack.name = msg;
        BackpacksManager.replaceBackpackInInventory(inventory, backpack);


        // Send info to player
        MessagesManager.sendMessage(p, Data.Message.PLAYER_BACKPACK_NAME_CHANGED, placeholders);

        // Remove from list
        CacheManager.removeChangeBackpackName(changeBackpackName);

        // Refresh placeholders
        BackpacksManager.refreshPlaceholders(p);

        // Hide message from chat
        e.setCancelled(true);
    }

}
