package me.olios.backinpack.Commands;

import me.olios.backinpack.Data;
import me.olios.backinpack.Main;
import me.olios.backinpack.Managers.*;
import me.olios.backinpack.Objects.BackpackConfig;
import me.olios.backinpack.Objects.BackpackObject;
import me.olios.backinpack.Objects.Config;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackpackGive {

    public static void executeCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        // backpack-give <player> <true/false - give as item>
        boolean isPlayer = true;

        if (sender instanceof Player) isPlayer = true;
        else isPlayer = false;


        // Check permissions
        if (isPlayer)
        {
            Player p = ((Player) sender).getPlayer();
            p.recalculatePermissions(); // Refresh permissions

            if (!PermissionsManager.checkPermissions(p, Data.Permission.GIVE))
            {
                MessagesManager.sendMessage(p, Data.Message.NO_PERMISSION);
                return;
            }
        }


        if (args.length < 2)
        {
            Map<String, Object> placeholders = new HashMap<>();
            placeholders.put("%command%", "backpack-give <player> <backpack> [true/false - give as item]");

            if (isPlayer) // For player
            {
                Player p = ((Player) sender).getPlayer();
                MessagesManager.sendMessage(p, Data.Message.ARGUMENT_MISSING, placeholders);
            }
            else // For console
            {
                MessagesManager.sendLogMessage(Data.Message.ARGUMENT_MISSING, placeholders);
            }

            return;
        }


        String arg1 = args[0];
        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(arg1);

        // This player never played on this server or not exists
        if (targetPlayer == null || !targetPlayer.hasPlayedBefore())
        {
            if (isPlayer) // For player
            {
                Player p = ((Player) sender).getPlayer();
                MessagesManager.sendMessage(p, Data.Message.ARGUMENT_PLAYER_MISSING);
            }
            else // For console
            {
                MessagesManager.sendLogMessage(Data.Message.ARGUMENT_PLAYER_MISSING);
            }
            return;
        }


        // Get target player backpack
        String targetUUID = targetPlayer.getUniqueId().toString();
        BackpackObject backpack = BackpacksManager.getInventory(targetUUID);

        if (backpack == null || backpack.backpacks == null)
        {
            if (isPlayer) // For player
            {
                Player p = ((Player) sender).getPlayer();
                MessagesManager.sendMessage(p, Data.Message.ARGUMENT_PLAYER_MISSING);
            }
            else // For console
            {
                MessagesManager.sendLogMessage(Data.Message.ARGUMENT_PLAYER_MISSING);
            }
            return;
        }

        // Check if player is not having maximum backpacks
        if (ConfigManager.config.MAX_BACKPACKS <= backpack.backpacks.size())
        {
            if (isPlayer) // For player
            {
                Player p = ((Player) sender).getPlayer();
                MessagesManager.sendMessage(p, Data.Message.PLAYER_HAS_MAX_BACKPACKS);
            }
            else // For console
                MessagesManager.sendLogMessage(Data.Message.PLAYER_HAS_MAX_BACKPACKS);

            return;
        }

        // Check if indicated backpack exists
        String backpackName = args[1];

        // Check if player indicated existing backpack
        if (BackpacksConfigManager.backpackConfigs.get(backpackName) == null)
        {
            if (isPlayer) // For player
            {
                Player p = ((Player) sender).getPlayer();
                MessagesManager.sendMessage(p, Data.Message.ARGUMENT_BACKPACK_NOT_FOUND);
            }
            else // For console
                MessagesManager.sendLogMessage(Data.Message.ARGUMENT_BACKPACK_NOT_FOUND);

            return;
        }

        BackpackConfig backpackConfig = BackpacksConfigManager.backpackConfigs.get(backpackName);

        // Create backpack for player
        String id = BackpacksManager.createBackpack(targetUUID, false, backpackConfig);


        // Check if command have to give backpack as item
        if (args.length >= 2)
        {
            String arg2 = (args.length == 2) ? "false" : args[2];


            switch (arg2)
            {
                // Create backpack item and give it to player
                case "true":
                    if (targetPlayer.isOnline())
                    {
                        Player targetOnlinePlayer = Bukkit.getPlayer(targetPlayer.getUniqueId());

                        boolean assigned = false;
                        if (ConfigManager.config.CREATE_BACKPACK_CRAFTING) assigned = true;

                        ItemStack backpackItem = BackpacksManager.createBackpackItem(targetUUID, id, BackpacksConfigManager.backpackConfigs.get(backpackName).STYLE_ITEM);

                        targetOnlinePlayer.getInventory().addItem(backpackItem);
                    }
                    else
                    {
                        // Player is offline
                        if (isPlayer)
                        {
                            Player p = ((Player) sender).getPlayer();
                            MessagesManager.sendMessage(p, Data.Message.PLAYER_BACKPACK_GIVE_OFFLINE);
                        }
                        else
                        {
                            MessagesManager.sendLogMessage(Data.Message.PLAYER_BACKPACK_GIVE_OFFLINE);
                        }
                    }
                    break;

                // Given backpack can be assigned
                case "false":

                    break;

                // Not true or false
                default:
                    Map<String, Object> placeholders = new HashMap<>();
                    placeholders.put("%command%", "backpack-give <player> <backpack> [true/false - give as item]");

                    if (isPlayer) // For player
                    {
                        Player p = ((Player) sender).getPlayer();
                        MessagesManager.sendMessage(p, Data.Message.ARGUMENT_MISSING, placeholders);
                    }
                    else // For console
                    {
                        MessagesManager.sendLogMessage(Data.Message.ARGUMENT_MISSING, placeholders);
                    }
                    break;
            }

        }

        if (isPlayer) // For player
        {
            Player p = ((Player) sender).getPlayer();
            MessagesManager.sendMessage(p,
                    Data.Message.PLAYER_BACKPACK_ADD,
                    targetPlayer);
        }
        else // For console
        {
            MessagesManager.sendLogMessage(Data.Message.PLAYER_BACKPACK_ADD, targetPlayer);
        }

    }
}
