//
// CC Creative Commons 2022
// Attribution-NoDerivatives 4.0 International
// Author olios
//

package me.olios.backinpack.Managers;

import me.olios.backinpack.Commands.*;
import me.olios.backinpack.Data;
import me.olios.backinpack.Objects.BackpackObject;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommandsManager {

    public static void manageCommand(CommandSender sender, Command cmd, String label, String[] args) throws SQLException, IOException, InvalidConfigurationException
    {
        String command = cmd.getName().toLowerCase();

        // Check if command is disabled
        if (ConfigManager.config.DISABLED_COMMANDS != null &&
                ConfigManager.config.DISABLED_COMMANDS.contains(command))
        {
            if (sender instanceof Player)
            {
                Player p = ((Player) sender).getPlayer();
                assert p != null;
                MessagesManager.sendMessage(p, Data.Message.COMMAND_DISABLED, p);
            }
            else MessagesManager.sendLogMessage(Data.Message.COMMAND_DISABLED);
            return;
        }

        switch (command)
        {
            case "b":
            case "bck":
            case "backpack":
            case "data":
                Backpack.executeCommand(sender, cmd, label, args);
                break;
            case "backpack-name-edit":
                BackpackNameEdit.executeCommand(sender, cmd, label, args);
                break;
            case "backpack-set-size":
                BackpackSetSize.executeCommand(sender, cmd, label, args);
                break;
            case "backpack-give":
                BackpackGive.executeCommand(sender, cmd, label, args);
                break;
            case "backpack-userinfo":
                BackpackUserInfo.executeCommand(sender, cmd, label, args);
                break;
            case "bip":
            case "backinpack":
                BackInPack.executeCommand(sender, cmd, label, args);
                break;
        }
    }

    public static List<String> manageCompleter(CommandSender sender,
                                               Command cmd,
                                               String label,
                                               String[] args)
    {
        String command = cmd.getName().toLowerCase();
        List<String> completions = new ArrayList<>();

        if (!(sender instanceof Player)) return completions;

        // Sender
        Player p = ((Player) sender).getPlayer();
        p.recalculatePermissions(); // Refresh permissions
        String uuid = p.getUniqueId().toString();

        switch (command)
        {
            case "bip":
            case "backinpack":
                switch (args.length)
                {
                    case 1:
                        completions.add("help");
                        completions.add("about");

                        if (PermissionsManager.checkPermissions(p, Data.Permission.ADMIN))
                        {
                            completions.add("commands");
                            completions.add("permissions");
                            completions.add("placeholders");
                        }

                        if (PermissionsManager.checkPermissions(p, Data.Permission.RELOAD))
                        {
                            completions.add("reload");
                        }

                        if (PermissionsManager.checkPermissions(p, Data.Permission.SYNC))
                        {
                            completions.add("sync");
                        }
                }
                return completions;

            case "b":
            case "bck":
            case "backpack":
            case "data":
            case "backpack-name-edit":
                switch (args.length)
                {
                    case 1:
                        BackpackObject backpackObject = BackpacksManager.getInventory(uuid);

                        // Add backpacks names to completer
                        if (!backpackObject.backpacks.isEmpty())
                        {
                            backpackObject.backpacks.forEach(backpack ->
                            {
                                completions.add(backpack.name);
                            });
                        }

                        break;
                    case 2:
                        // Check permissions
                        if (!ConfigManager.config.ADMIN_OPEN_PLAYER_BACKPACK ||
                                !PermissionsManager.checkPermissions(p, Data.Permission.OPEN))  break;

                        OfflinePlayer target = Bukkit.getOfflinePlayer(args[0]);

                        if (target != null && target.hasPlayedBefore())
                        {
                            BackpackObject inventory = BackpacksManager.getInventory(target.getUniqueId().toString());

                            inventory.backpacks.forEach(backpack ->
                            {
                                completions.add(backpack.name);
                            });
                        }

                        break;

                }
                return completions;

            case "backpack-set-size":
                // /backpack-set-size <player/all> <backpack/all> <size>

                // Check permissions
                if (!PermissionsManager.checkPermissions(p, Data.Permission.SETSIZE))
                    return completions;

                switch (args.length)
                {
                    // args[0]: <player/all>
                    case 1:
                        Bukkit.getServer().getOnlinePlayers().forEach(player ->
                        {
                            completions.add(player.getName());
                        });
                        completions.add("all");
                        break;

                    // args[1]: <backpack/all>
                    case 2:
                        if (!args[0].equals("all"))
                        {
                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(args[0]);
                            BackpackObject inventory = BackpacksManager.getInventory(offlinePlayer.getUniqueId().toString());

                            if (inventory != null)
                            {
                                inventory.backpacks.forEach(backpack ->
                                {
                                    completions.add(backpack.name);
                                });
                            }
                        }

                        completions.add("all");

                        break;
                }
                return completions;
            case "backpack-give":
                // /backpack-give <player> [true/false - give as item]

                // Check permissions
                if (!PermissionsManager.checkPermissions(p, Data.Permission.GIVE))
                    return completions;

                switch (args.length)
                {
                    // args[0]: <player>
                    case 1:
                        Bukkit.getServer().getOnlinePlayers().forEach(player ->
                        {
                            completions.add(player.getName());
                        });
                        break;

                    // args[1]: <backpack>
                    case 2:
                        completions.add("default");
                        completions.addAll(BackpacksConfigManager.getMultipleBackpacksNames());

                        break;

                    case 3:
                        completions.add("true");
                        completions.add("false");
                        break;
                }
                return completions;
            case "backpack-userinfo":
                // /backpack-userinfo <player>

                // Check permissions
                if (!PermissionsManager.checkPermissions(p, Data.Permission.USERINFO))
                    return completions;

                switch (args.length)
                {
                    // args[0]: <player>
                    case 1:
                        Bukkit.getServer().getOnlinePlayers().forEach(player ->
                        {
                            completions.add(player.getName());
                        });
                        break;
                }
                return completions;
        }

        return null;
    }
}
