package me.olios.backinpack.Commands;

import me.olios.backinpack.Data;
import me.olios.backinpack.Main;
import me.olios.backinpack.Managers.BackpacksManager;
import me.olios.backinpack.Managers.MessagesManager;
import me.olios.backinpack.Objects.BackpackContentObject;
import me.olios.backinpack.Objects.BackpackObject;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class BackpackNameEdit {

    public static void executeCommand(CommandSender sender, Command cmd, String label, String[] args) throws SQLException
    {
        // If sender is not player
        if (!(sender instanceof Player))
        {
            Main.warnLog("You can't execute this command from console!");
            return;
        }

        Player p = (Player) sender;
        p.recalculatePermissions(); // Refresh permissions
        String uuid = p.getUniqueId().toString();
        BackpackObject backpackObject = BackpacksManager.getInventory(uuid);

        // Change backpack name
        boolean constainNameEdit = false;

        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("name-edit"))
            {
                constainNameEdit = true;
            }
        }

        // Contains name-edit but not given all arguments
        if (constainNameEdit && args.length < 3)
        {
            Map<String, Object> placeholders = new HashMap<>();
            placeholders.put("%command%", "backpack-name-edit <old name> <new name>");

            MessagesManager.sendMessage(p, Data.Message.ARGUMENT_MISSING, placeholders);
            return;
        }

        // Read backpack name
        String tempBackpackName = "";
        AtomicBoolean backpackFound = new AtomicBoolean(false);
        AtomicReference<BackpackContentObject> foundBackpack = new AtomicReference<>();

        int i = 0;
        while (i < args.length)
        {
            tempBackpackName += args[i];

            String finalTempBackpackName = tempBackpackName;
            backpackObject.backpacks.forEach(backpack ->
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
            MessagesManager.sendMessage(p, Data.Message.ARGUMENT_BACKPACK_NOT_FOUND);
            return;
        }
        
        // Not given new backpack name in args
        if (i + 1 == args.length)
        {
            MessagesManager.sendMessage(p, Data.Message.ARGUMENT_BACKPACK_NAME_NOT_GIVEN);
            return;
        }

        String newBackpackName = "";

        for (int j = i + 1; j < args.length; j++)
        {
            newBackpackName += args[j];
            newBackpackName += " ";
        }

        // Remove last space from string
        newBackpackName = newBackpackName.substring(0, newBackpackName.length() - 1);


        // Set new name for backpack
        foundBackpack.get().name = newBackpackName;


        // Save changes to inventory variable
        BackpacksManager.replaceBackpackInInventory(backpackObject, foundBackpack.get());


        // Send info to player about changed name
        Map<String, Object> placeholders = new HashMap<>();
        placeholders.put("%old-backpack-name%", tempBackpackName);
        placeholders.put("%new-backpack-name%", newBackpackName);

        MessagesManager.sendMessage(p, Data.Message.PLAYER_BACKPACK_NAME_CHANGED, placeholders);
    }
}
