package me.olios.backinpack.Commands;

import me.olios.backinpack.Data;
import me.olios.backinpack.Main;
import me.olios.backinpack.Managers.BackpacksManager;
import me.olios.backinpack.Managers.MessagesManager;
import me.olios.backinpack.Managers.PermissionsManager;
import me.olios.backinpack.Objects.BackpackContentObject;
import me.olios.backinpack.Objects.BackpackObject;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class BackpackUserInfo {

    public static void executeCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        // If sender is not player
        boolean isPlayer = true;

        if (sender instanceof Player) isPlayer = true;
        else isPlayer = false;

        // Check permissions
        if (isPlayer)
        {
            Player p = ((Player) sender).getPlayer();
            p.recalculatePermissions(); // Refresh permissions

            if (!PermissionsManager.checkPermissions(p, Data.Permission.USERINFO))
            {
                MessagesManager.sendMessage(p, Data.Message.NO_PERMISSION);
                return;
            }
        }

        if (args.length == 0 || args[0] == null)
        {
            Map<String, Object> placeholders = new HashMap<>();
            placeholders.put("%command%", "backpack-userinfo <player>");

            if (isPlayer)
            {
                Player p = ((Player) sender).getPlayer();
                MessagesManager.sendMessage(p, Data.Message.ARGUMENT_MISSING, placeholders);
            }
            else MessagesManager.sendLogMessage(Data.Message.ARGUMENT_MISSING, placeholders);
            return;
        }

        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

        if (target == null || !target.hasPlayedBefore() || BackpacksManager.getInventory(target.getUniqueId().toString()) == null)
        {
            Map<String, Object> placeholders = new HashMap<>();
            placeholders.put("%player%", args[0]);

            if (isPlayer)
            {
                Player p = ((Player) sender).getPlayer();
                MessagesManager.sendMessage(p, Data.Message.PLAYER_NOT_FOUND, placeholders);
            }
            else MessagesManager.sendLogMessage(Data.Message.PLAYER_NOT_FOUND, placeholders);
            return;
        }

        if (isPlayer)
        {
            Player p = ((Player) sender).getPlayer();
            MessagesManager.sendMessage(p, Data.Message.PLAYER_USERINFO, target);
        }
        else MessagesManager.sendLogMessage(Data.Message.PLAYER_USERINFO, target);

    }

}
