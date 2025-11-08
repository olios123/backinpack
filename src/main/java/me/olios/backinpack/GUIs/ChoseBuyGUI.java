//
// CC Creative Commons 2022
// Attribution-NoDerivatives 4.0 International
// Author olios
//

package me.olios.backinpack.GUIs;

import me.olios.backinpack.Library.GUIPosition;
import me.olios.backinpack.Library.NBTTags;
import me.olios.backinpack.Library.Replace.ItemReplace;
import me.olios.backinpack.Library.Replace.StringReplace;
import me.olios.backinpack.Managers.GUIConfigManager;
import me.olios.backinpack.Objects.GUI.ChoseBuyConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ChoseBuyGUI {

    public static Inventory choseButInventory = null;

    public static void openGUI(Player p)
    {
        // Backpack config
        ChoseBuyConfig config = GUIConfigManager.getChoseBuy(p);


        // GUI Object
        choseButInventory = Bukkit.createInventory(null, config.SIZE * 9,
                StringReplace.string(config.TITLE));

        // Adding backpacks
        config.backpacksToBuy.forEach(bck ->
        {
            int position = GUIPosition.getPosition(bck.positionX, bck.positionY);

            ItemStack backpackToBuy = ItemReplace.item(bck.backpack);

            // Add NBT Tags
            NBTTags.addNBT(backpackToBuy, "backinpack.backpack-buy", bck.name);

            choseButInventory.setItem(position, backpackToBuy);
        });

        // Cancel item
        // Lower center is in use
        if (choseButInventory.getItem(config.SIZE * 9 - 5) != null)
        {
            // Lower right corner
            choseButInventory.setItem(config.SIZE * 9 - 1, config.cancel);
        }
        // Lower center
        else choseButInventory.setItem(config.SIZE * 9 - 5, config.cancel);


        // Open inventory
        p.openInventory(choseButInventory);

//
//        // Complete blocked spaces
//        int backpackSize = backpack.size;
//
//
//        // Check if player is online and if has permission to change backpack size
//        if (offlinePlayer.isOnline())
//        {
//            Player player = offlinePlayer.getPlayer();
//            if (BackpackSizePermission.check(player) != -1)
//                backpackSize = BackpackSizePermission.check(player);
//        }
//
//
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
//
//                if (permissionSize != 0) backpackSize = permissionSize;
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
//
//
//        // Load backpack items
//        if (!backpack.items.isEmpty())
//        {
//            for (int i = 0; i <= 53; i++)
//            {
//                if (backpack.items.get(i) != null) backpackInventory.setItem(i, backpack.items.get(i));
//            }
//        }
//
//        boolean allowBlockedSpaces = true;
//        boolean ownerBlockedSpaces = false;
//        boolean senderBlockedSpaces = false;
//
//        // Check for items in blocked slots
//        for (int i = 53; i >= backpackSize; i--)
//        {
//            // The size of the backpack has been changed
//            if (backpackInventory.getItem(i) != null)
//            {
//                ItemStack item = backpackInventory.getItem(i);
//
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
//            }
//        }
//
//        // Info about items in blocked spaces
//        if (ownerBlockedSpaces) MessagesManager.sendMessage(offlinePlayer.getPlayer(), Data.Message.PLAYER_BACKPACK_CHANGE_SIZE, offlinePlayer);
//        if (senderBlockedSpaces) MessagesManager.sendMessage(sender, Data.Message.PLAYER_BACKPACK_SIZE_CHANGED_BLOCKED_SPACES, sender);
//
//
//        // Blocked spaces
//        if (allowBlockedSpaces)
//        {
//            // Start from end of backpack
//            for (int i = 53; i >= backpackSize; i--)
//            {
//                backpackInventory.setItem(i, blocker);
//            }
//        }
//
//
////        // Other want see player backpack
////        if (sender != null && !sender.getUniqueId().toString().equals(uuid))
////        {
////            sender.recalculatePermissions(); // Refresh permissions for sender
////
////            // Sender has permission
////            if (PermissionsManager.checkPermissions(sender, Data.Permission.OPEN))
////            {
////                sender.openInventory(backpackInventory);
////
////                SoundsManager.playSound(sender, Data.Sound.OPEN);
////
////                CacheManager.createOpenedBackpack(
////                        offlinePlayer.getUniqueId().toString(),
////                        sender.getUniqueId().toString(),
////                        backpack.size,
////                        backpackID,
////                        backpackInventory);
////            }
////            // Sender hasn't required permission
////            else
////            {
////                MessagesManager.sendMessage(sender, Data.Message.NO_PERMISSION);
////            }
////        }
////        // Player wants open his backpack
////        else
////        {
//            // Owner of this backpack
//            Player player = Bukkit.getPlayer(offlinePlayer.getUniqueId());
//
//            player.openInventory(backpackInventory);
//
//            SoundsManager.playSound(player, Data.Sound.OPEN);
//
//            CacheManager.createOpenedBackpack(
//                    player.getUniqueId().toString(),
//                    player.getUniqueId().toString(),
//                    backpack.size,
//                    backpackID,
//                    backpackInventory);
////        }
    }
}
