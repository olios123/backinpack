//
// CC Creative Commons 2022
// Attribution-NoDerivatives 4.0 International
// Author olios
//

package me.olios.backinpack.GUIs;

import me.olios.backinpack.API.PAPICustom;
import me.olios.backinpack.Data;
import me.olios.backinpack.Library.Blocker;
import me.olios.backinpack.Library.Replace.StringReplace;
import me.olios.backinpack.Managers.*;
import me.olios.backinpack.Objects.BackpackContentObject;
import me.olios.backinpack.Objects.BackpackObject;
import me.olios.backinpack.Objects.GUI.BackpackConfig;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;

public class BackpackGUI {

    public static Inventory backpackInventory = null;

    public static void openGUI(String uuid, String backpackID, Player sender)
    {
        // Player object
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
        // Backpack config
        BackpackConfig config = GUIConfigManager.getBackpack(offlinePlayer);

        // Check if player requires permission to show his own backpacks
//        if (!PermissionsManager.checkBackpackAccessPermission(uuid, sender)) return;

        // Backpack placeholders
        Map<String, Object> placeholders = PAPICustom.getBackpackPlaceholders(uuid, backpackID);


        // GUI Object
        backpackInventory = Bukkit.createInventory(null, 54,
                StringReplace.string(config.TITLE, placeholders));


        // Backpack data
        BackpackObject userBackpacks = BackpacksManager.getInventory(uuid);
        BackpackContentObject backpack = BackpacksManager.getInventoryByBackpackID(uuid, backpackID);


        // Blocked place ItemStack
        ItemStack blocker = Blocker.get();


        // Complete blocked spaces
        int backpackSize = backpack.size;


        // Check if player is online and if has permission to change backpack size
        if (offlinePlayer.isOnline() &&
                !sender.getUniqueId().toString().equals(offlinePlayer.getUniqueId().toString()))
        {
            Player player = Bukkit.getPlayer(UUID.fromString(uuid));

            if (PermissionsManager.checkBackpackPermission(player) != -1)
                backpackSize = PermissionsManager.checkBackpackPermission(player);
        }


//        // Check if player has permission for bigger backpack
//        if (sender == null)
//        {
//            // Player opening backpack is his owner
//            Player player = Bukkit.getPlayer(offlinePlayer.getUniqueId());
//
//            // Player exists
//            if (player != null)
//            {
//                player.recalculatePermissions(); // Refresh permissions
//                int permissionSize = PermissionsManager.checkBackpackPermission(player);
//                Main.l(permissionSize);
//                if (permissionSize != 0) backpackSize = permissionSize;
//                Main.l("0" + permissionSize);
//                Main.l("1" + backpackSize);
//            }
//        }
//        else if (sender != null)
//        {
//            if (offlinePlayer.isOnline())
//            {
//                // Target is online
//                Player player = Bukkit.getPlayer(offlinePlayer.getUniqueId());
//
//                player.recalculatePermissions(); // Refresh permissions
//                int permissionSize = PermissionsManager.checkBackpackPermission(player);
//
//                if (permissionSize != 0) backpackSize = permissionSize;
//            }
//            else
//            {
//                // Send from player and target is offline
//                MessagesManager.sendMessage(
//                        sender,
//                        Data.Message.PLAYER_BACKPACK_USERINFO_OFFLINE,
//                        placeholders);
//            }
//        }


        // Load backpack items
        if (!backpack.items.isEmpty())
        {
            for (int i = 0; i <= 53; i++)
            {
                if (backpack.items.get(i) != null) backpackInventory.setItem(i, backpack.items.get(i));
            }
        }

        boolean allowBlockedSpaces = true;
        boolean ownerBlockedSpaces = false;
        boolean senderBlockedSpaces = false;

        int size = backpackSize;

        // Check for items in blocked slots
        for (int i = 53; i >= size; i--)
        {
            // The size of the backpack has been changed
            if (backpackInventory.getItem(i) != null)
            {
                ItemStack item = backpackInventory.getItem(i);

//                // Opened by owner
//                if (sender == null && offlinePlayer.isOnline())
//                {
//                    // Drop item
//                    offlinePlayer.getPlayer().getWorld().dropItemNaturally(
//                            offlinePlayer.getPlayer().getLocation(),
//                            item);
//                    backpackInventory.setItem(i, null);
//                    ownerBlockedSpaces = true;
//                }
//                // Opened by player other than owner
//                else if (sender != null)
//                {
//                    allowBlockedSpaces = false;
//                    senderBlockedSpaces = true;
//                }

                offlinePlayer.getPlayer().getWorld().dropItemNaturally(
                        offlinePlayer.getPlayer().getLocation(),
                        item);
                backpackInventory.setItem(i, null);
                ownerBlockedSpaces = true;
            }
        }


        // Info about items in blocked spaces
        if (ownerBlockedSpaces) MessagesManager.sendMessage(offlinePlayer.getPlayer(), Data.Message.PLAYER_BACKPACK_CHANGE_SIZE, offlinePlayer);
//        if (senderBlockedSpaces) MessagesManager.sendMessage(sender, Data.Message.PLAYER_BACKPACK_SIZE_CHANGED_BLOCKED_SPACES, sender);


        // Blocked spaces
        if (allowBlockedSpaces)
        {
            // Start from end of backpack
            for (int i = 53; i >= backpackSize; i--)
            {
                backpackInventory.setItem(i, blocker);
            }
        }


        // Owner
        if (sender == null || sender.getUniqueId().toString().equals(uuid))
        {
            Player player = Bukkit.getPlayer(UUID.fromString(uuid));

            player.openInventory(backpackInventory);

            SoundsManager.playSound(player, Data.Sound.OPEN);

            CacheManager.createOpenedBackpack(
                    player.getUniqueId().toString(),
                    player.getUniqueId().toString(),
                    backpack.size,
                    backpackID,
                    backpackInventory);
        }
        // Sender
        else if (sender != null && !sender.getUniqueId().toString().equals(uuid))
        {
            // Refresh permissions for sender
            sender.recalculatePermissions();

            sender.openInventory(backpackInventory);

            SoundsManager.playSound(sender, Data.Sound.OPEN);

            CacheManager.createOpenedBackpack(
                    offlinePlayer.getUniqueId().toString(),
                    sender.getUniqueId().toString(),
                    backpack.size,
                    backpackID,
                    backpackInventory);
        }
    }
}
