package me.olios.backinpack.Commands;

import me.olios.backinpack.API.PAPICustom;
import me.olios.backinpack.Data;
import me.olios.backinpack.Library.Numeric;
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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class BackpackSetSize {

    private static String command = "/backpack-set-size <player/all> <backpack/all> <size>";

    // /backpack-set-size <player/all> <backpack/all> <size>
    public static void executeCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        boolean player = sender instanceof Player;
        OfflinePlayer target = null;

        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put("%command%", command);

        // Check permissions if player
        if (player)
        {
            Player p = ((Player) sender).getPlayer();
            p.recalculatePermissions(); // Refresh permissions

            if (!PermissionsManager.checkPermissions(p, Data.Permission.SETSIZE))
            {
                MessagesManager.sendMessage(p, Data.Message.NO_PERMISSION);
                return;
            }
        }

        if (args.length < 3)
        {
            if (player) MessagesManager.sendMessage(
                    ((Player) sender).getPlayer(),
                    Data.Message.ARGUMENT_MISSING,
                    placeholders);
            else MessagesManager.sendLogMessage(
                    Data.Message.ARGUMENT_MISSING,
                    placeholders);
            return;
        }

        // For all players
        if (args[0].equals("all"))
        {
            // Need change for all backpacks
            if (!args[1].equals("all"))
            {
                if (player) MessagesManager.sendMessage(
                        ((Player) sender).getPlayer(),
                        Data.Message.ARGUMENT_NEED_CHANGE_FOR_ALL_PLAYERS);
                else MessagesManager.sendLogMessage(
                        Data.Message.ARGUMENT_NEED_CHANGE_FOR_ALL_PLAYERS);
                return;
            }

            // Check size
            int size;
            if (player)
            {
                size = checkSize(args[2], ((Player) sender).getPlayer());
                if (size == 0) return;
            }
            else
            {
                size = checkSize(args[2]);
                if (size == 0) return;
            }

//
//            AtomicReference<String> lastUUID = new AtomicReference<>("");
//            AtomicReference<BackpackContentObject> lastBackpack = new AtomicReference<>();
//            for (Map.Entry<String, BackpackObject> entry : BackpacksManager.inventory.entrySet())
//            {
//                String uuid = entry.getKey();
//                BackpackObject inventory = entry.getValue();
//
//                inventory.backpacks.forEach(backpack ->
//                {
//                    backpack.size = size;
//                    BackpacksManager.replaceBackpackInInventory(inventory, backpack);
//
//                    lastBackpack.set(backpack);
//                    lastUUID.set(uuid);
//                });
//            }

            // Everything is OK
            String lastUUID = null;
            BackpackContentObject lastBackpack = null;

            for (Map.Entry<String, BackpackObject> entry : BackpacksManager.inventory.entrySet())
            {
                BackpackObject inventory = entry.getValue();

                for (BackpackContentObject backpack : inventory.backpacks)
                {
                    backpack.size = size;
                    BackpacksManager.replaceBackpackInInventory(inventory, backpack);

                    lastUUID = entry.getKey();
                    lastBackpack = backpack;
                }
            }

            if (lastUUID != null && lastBackpack != null) PAPICustom.getBackpackPlaceholders(lastUUID, lastBackpack).forEach(placeholders::putIfAbsent);

            placeholders.replace("%backinpack_backpack_size%", size);

            if (player) MessagesManager.sendMessage(
                    ((Player) sender).getPlayer(),
                    Data.Message.PLAYER_SET_ALL_BACKPACK_SIZE,
                    placeholders);
            else MessagesManager.sendLogMessage(
                    Data.Message.PLAYER_SET_ALL_BACKPACK_SIZE,
                    placeholders);
        }
        // For specific player
        else
        {
            target = Bukkit.getOfflinePlayer(args[0]);

            if (target == null || !target.hasPlayedBefore())
            {
                if (player) MessagesManager.sendMessage(
                        ((Player) sender).getPlayer(),
                        Data.Message.ARGUMENT_PLAYER_MISSING);
                else MessagesManager.sendLogMessage(
                        Data.Message.ARGUMENT_PLAYER_MISSING);
                return;
            }

            // All backpacks
            if (args[1].equals("all"))
            {
                // Check size
                int size;
                if (player)
                {
                    size = checkSize(args[2], ((Player) sender).getPlayer());
                    if (size == 0) return;
                }
                else
                {
                    size = checkSize(args[2]);
                    if (size == 0) return;
                }

                // Everything is OK
                BackpackObject inventory = BackpacksManager.getInventory(target.getUniqueId().toString());

                AtomicReference<BackpackContentObject> lastBackpack = new AtomicReference<>();
                inventory.backpacks.forEach(backpack ->
                {
                    backpack.size = size;
                    BackpacksManager.replaceBackpackInInventory(inventory, backpack);

                    lastBackpack.set(backpack);
                });

                PAPICustom.getBackpackPlaceholders(inventory.uuid, lastBackpack.get())
                        .forEach(placeholders::putIfAbsent);

                placeholders.replace("%backinpack_backpack_size%", size);

                if (player) MessagesManager.sendMessage(
                        ((Player) sender).getPlayer(),
                        Data.Message.PLAYER_SET_PLAYER_ALL_BACKPACK_SIZE,
                        target,
                        placeholders);
                else MessagesManager.sendLogMessage(
                        Data.Message.PLAYER_SET_PLAYER_ALL_BACKPACK_SIZE,
                        target,
                        placeholders);
            }
            // Specific backpack
            else
            {
                // Read backpack name
                String tempBackpackName = "";
                BackpackObject inventory = BackpacksManager.getInventory(target.getUniqueId().toString());
                AtomicBoolean backpackFound = new AtomicBoolean(false);
                AtomicReference<BackpackContentObject> foundBackpack = new AtomicReference<>();

                int i = 1;
                while (i < args.length)
                {
                    tempBackpackName += args[i];

                    String finalTempBackpackName = tempBackpackName;
                    inventory.backpacks.forEach(backpack ->
                    {
                        if (backpack.name.equals(finalTempBackpackName))
                        {
                            backpackFound.set(true);
                            foundBackpack.set(backpack);
                        }
                    });

                    if (backpackFound.get()) break;
                    else tempBackpackName += " ";

                    // Repeat
                    i++;
                }

                // Backpack was not found
                if (!backpackFound.get())
                {
                    if (player) MessagesManager.sendMessage(
                            ((Player) sender).getPlayer(),
                            Data.Message.ARGUMENT_BACKPACK_NOT_FOUND);
                    else MessagesManager.sendLogMessage(Data.Message.ARGUMENT_BACKPACK_NOT_FOUND);
                    return;
                }

                // Check size
                int size;
                if (player)
                {
                    size = checkSize(args[i + 1], ((Player) sender).getPlayer());
                    if (size == 0) return;
                }
                else
                {
                    size = checkSize(args[i + 1]);
                    if (size == 0) return;
                }

                // Everything is OK
                foundBackpack.get().size = size;

                BackpacksManager.replaceBackpackInInventory(inventory, foundBackpack.get());

                PAPICustom.getBackpackPlaceholders(
                        target.getUniqueId().toString(),
                        foundBackpack.get()).forEach(placeholders::putIfAbsent);

                placeholders.replace("%backinpack_backpack_size%", size);

                if (player) MessagesManager.sendMessage(
                        ((Player) sender).getPlayer(),
                        Data.Message.PLAYER_SET_BACKPACK_SIZE,
                        target,
                        placeholders);
                else MessagesManager.sendLogMessage(
                        Data.Message.PLAYER_SET_BACKPACK_SIZE,
                        target,
                        placeholders);

            }

        }
    }

    private static int checkSize(String args, Player ... player)
    {
        // <size> is not number
        if (!Numeric.check(args))
        {
            Map<String, Object> placeholders = new HashMap<>();
            placeholders.put("%args%", args);

            if (player != null && player.length > 0) MessagesManager.sendMessage(player[0], Data.Message.ARGUMENT_NOT_NUMBER, placeholders);
            else MessagesManager.sendLogMessage(Data.Message.ARGUMENT_NOT_NUMBER, placeholders);

            return 0;
        }

        int size = Integer.parseInt(args);

        // <size> is off scale
        if (size < 1 || size > 54)
        {
            if (player != null && player.length > 0) MessagesManager.sendMessage(player[0], Data.Message.ARGUMENT_INVALID_NUMBER);
            else MessagesManager.sendLogMessage(Data.Message.ARGUMENT_INVALID_NUMBER);
            return 0;
        }

        return size;
    }
}
