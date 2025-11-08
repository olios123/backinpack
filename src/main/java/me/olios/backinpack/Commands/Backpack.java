//
// CC Creative Commons 2022
// Attribution-NoDerivatives 4.0 International
// Author olios
//

package me.olios.backinpack.Commands;

import me.olios.backinpack.Data;
import me.olios.backinpack.GUIs.BackpackGUI;
import me.olios.backinpack.GUIs.BackpackListGUI;
import me.olios.backinpack.Main;
import me.olios.backinpack.Managers.BackpacksManager;
import me.olios.backinpack.Managers.ConfigManager;
import me.olios.backinpack.Managers.MessagesManager;
import me.olios.backinpack.Managers.PermissionsManager;
import me.olios.backinpack.Objects.BackpackObject;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Backpack {

    public static void executeCommand(CommandSender sender,
                                      Command cmd,
                                      String label,
                                      String[] args)
    {
        // If sender is not player
        if (!(sender instanceof Player))
        {
            Main.warnLog(MessagesManager.getConsoleMessage(Data.Message.CONSOLE_COMMAND));
            return;
        }

        Player p = (Player) sender;
        p.recalculatePermissions(); // Refresh permissions
        String uuid = p.getUniqueId().toString();
        BackpackObject backpackObject = BackpacksManager.getInventory(uuid);


        // Check if player can access to backpack by command
        if (!ConfigManager.config.BACKPACK_ACCESS_GUI && !p.isOp())
        {
            // Access through command is disabled
            MessagesManager.sendMessage(p, Data.Message.PLAYER_BACKPACK_ACCESS_THROUGH_COMMAND_DISABLED);
            return;
        }


        // Open specific backpack or target player backpack
        if (args.length > 0)
        {
            String firstArgs = args[0];
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(firstArgs);

            if (offlinePlayer != null && (offlinePlayer.hasPlayedBefore() || offlinePlayer.isOnline()))
            {
                // Provided specific backpack
                if (args.length > 1)
                {
                    String backpackName = "";

                    for (int i = 1; i < args.length; i++)
                    {
                        backpackName += args[i] + " ";
                    }
                    backpackName = backpackName.substring(0, backpackName.length() - 1);

                    BackpackObject inventory = BackpacksManager.getInventory(
                            offlinePlayer.getUniqueId().toString());

                    AtomicReference<String> targetBackpackID = new AtomicReference<>("");
                    String finalBackpackName = backpackName;
                    inventory.backpacks.forEach(backpack ->
                    {
                        if (backpack.name.equals(finalBackpackName))
                        {
                            targetBackpackID.set(backpack.id);
                        }
                    });

                    // Backpack not found
                    if (targetBackpackID.get().length() == 0)
                    {
                        MessagesManager.sendMessage(p, Data.Message.ARGUMENT_BACKPACK_NOT_FOUND);
                        return;
                    }

                    BackpackGUI.openGUI(offlinePlayer.getUniqueId().toString(), targetBackpackID.get(), p);

                    return;
                }
                else
                {
                    // Sender don't have permissions
                    if (!PermissionsManager.checkPermissions(p, Data.Permission.OPEN) ||
                            !ConfigManager.config.ADMIN_OPEN_PLAYER_BACKPACK)
                    {
                        MessagesManager.sendMessage(p, Data.Message.NO_PERMISSION);
                        return;
                    }

                    BackpackListGUI.openGUI(offlinePlayer.getUniqueId().toString(), p);

                    return;
                }
            }
            else
            {
                Map<String, Object> customPlaceholders = new HashMap<>();
                customPlaceholders.put("%player%", firstArgs);

                // Player not found
                MessagesManager.sendMessage(p, Data.Message.PLAYER_NOT_FOUND, customPlaceholders);
                return;

//                // Open specific backpack
//                AtomicBoolean opened = new AtomicBoolean(false);
//
//                backpackObject.backpacks.forEach(backpack ->
//                {
//                    String[] splitName = backpack.name.split(" ");
//
//                    // Check if args and backpack name is the same
//                    boolean sameName = true;
//                    for (int i = 0; i < args.length; i++)
//                    {
//                        if (!splitName[i].equals(args[i])) sameName = false;
//                    }
//
//                    // If yes
//                    if (sameName)
//                    {
//                        BackpackGUI.openGUI(uuid, backpack.id);
//                        opened.set(true);
//                    }
//                });
//
//                // Open backpack list if not found specific backpack
//                if (!opened.get()) BackpackListGUI.openGUI(uuid);
//
//                return;
            }
        }

        BackpackListGUI.openGUI(uuid);
    }
}
