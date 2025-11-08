package me.olios.backinpack.Managers;

import me.olios.backinpack.Data;
import me.olios.backinpack.Main;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PermissionsManager {

    public static boolean checkPermissions(Player p, Data.Permission permission)
    {
        p.recalculatePermissions(); // Refresh permissions

        String properPermission = "backinpack." +  permission.toString().toLowerCase().replace("_", "-");

        if (p.hasPermission(properPermission)) return true;
        else return false;
    }

    public static boolean checkBackpackAccessPermission(String uuid, Player sender)
    {
        // Player object
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));

        // Check if player requires permission to show his own backpacks
        if (ConfigManager.config.BACKPACK_PERMISSION)
        {
//            if (offlinePlayer.isOnline() &&
//                    sender != null &&
//                    !uuid.equals(sender.getUniqueId().toString()))
//            {
//                Player player = offlinePlayer.getPlayer();
//                player.recalculatePermissions(); // Refresh permissions
//
//                if (!PermissionsManager.checkPermissions(sender, Data.Permission.BACKPACK_ACCESS))
//                {
//                    MessagesManager.sendMessage(sender, Data.Message.NO_PERMISSION_BACKPACK_ACCESS);
//                    return false;
//                }
//            }
            if (offlinePlayer.isOnline())
            {
                Player player = offlinePlayer.getPlayer();
                player.recalculatePermissions(); // Refresh permissions

                if (!PermissionsManager.checkPermissions(player, Data.Permission.BACKPACK_ACCESS))
                {
                    MessagesManager.sendMessage(player, Data.Message.NO_PERMISSION_BACKPACK_ACCESS);
                    return false;
                }
            }
        }
        return true;
    }


    public static int checkBackpackPermission(Player p)
    {
        p.recalculatePermissions(); // Refresh permissions

        if (!p.isOp())
        {
            for (int i = 0; i <= 54; i++)
            {
                if (p.hasPermission("backpack.size." + i)) return i;
            }
        }

        return -1;
    }

}
