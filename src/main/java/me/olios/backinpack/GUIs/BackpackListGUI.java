package me.olios.backinpack.GUIs;

import me.olios.backinpack.API.PAPICustom;
import me.olios.backinpack.Data;
import me.olios.backinpack.Library.Blocker;
import me.olios.backinpack.Library.NBTTags;
import me.olios.backinpack.Library.Replace.ItemReplace;
import me.olios.backinpack.Library.Replace.StringReplace;
import me.olios.backinpack.Managers.*;
import me.olios.backinpack.Objects.BackpackObject;
import me.olios.backinpack.Objects.GUI.BackpacksConfig;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class BackpackListGUI {

    public static Inventory backpackListInventory = null;

    public static void openGUI(String uuid, Player sender)
    {
        // Player object
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));

        // Check if player requires permission to show his own backpacks
        if (!PermissionsManager.checkBackpackAccessPermission(uuid, sender)) return;

        // Checking if GUI is opening other player than sender
        boolean isSender = false;
        if (sender != null && !uuid.equals(sender.getUniqueId().toString())) isSender = true;


        // GUI Object
        backpackListInventory = Bukkit.createInventory(null, 54, StringReplace.string("&e%backinpack_player_username%", offlinePlayer));


        BackpackObject backpacks = BackpacksManager.getInventory(uuid);
        BackpacksConfig config = GUIConfigManager.getBackpacks(offlinePlayer);


        // Blocked / unused space
        ItemStack blocker = Blocker.get();

        for (int i = ConfigManager.config.MAX_BACKPACKS; i < 54; i++)
        {
            backpackListInventory.setItem(i, blocker);
        }

        // Check if all requirements have been set
        if (ConfigManager.config.CREATE_BACKPACK_BUY &&
                EconomyManager.registered &&
                !isSender)
        {
            if (ConfigManager.config.REMOVING_BACKPACKS_ENABLE)
            {
                // Buy backpack
                backpackListInventory.setItem(48, config.buyBackpack);

                // Delete backpack
                backpackListInventory.setItem(50, config.removeBackpack);
            }
            else
            {
                // Buy backpack
                backpackListInventory.setItem(48, config.buyBackpack);
            }
        }
        else if (!isSender)
        {
            // Delete backpack if buy backpack item is not visible
            if (ConfigManager.config.REMOVING_BACKPACKS_ENABLE)
                backpackListInventory.setItem(49, config.removeBackpack);
        }


        // Load backpacks
        List<ItemStack> backpacksList = new ArrayList<>();

        if (backpacks != null)
        {
            backpacks.backpacks.forEach(backpack ->
            {
                // Backpack item
                ItemStack backpackItem = ItemReplace.item(BackpacksConfigManager.backpackConfigs.get(backpack.type).STYLE_GUI);

                // Backpack placeholders
                Map<String, Object> placeholders = PAPICustom.getBackpackPlaceholders(uuid,
                        backpack.id);

                // Add backpack ID to item
                NBTTags.addNBT(backpackItem, "backinpack.backpack-id", backpack.id);
                NBTTags.addNBT(backpackItem, "backinpack.backpack-owner", uuid);
                NBTTags.addNBT(backpackItem, "backinpack.assigned", "true");

                // Set placeholders and replace strings
                backpackItem = ItemReplace.item(backpackItem, placeholders);

                // Add items to backpacksList
                backpacksList.add(backpackItem);
            });

            // Add backpacks to GUI
            for (int i = 0; i < backpacksList.size(); i++)
            {
                if (i >= ConfigManager.config.MAX_BACKPACKS) break;
                backpackListInventory.setItem(i, backpacksList.get(i));
            }
        }


        // Owner
        if (sender == null || sender.getUniqueId().toString().equals(uuid))
        {
            Player player = Bukkit.getPlayer(offlinePlayer.getUniqueId());

            player.openInventory(backpackListInventory);

            SoundsManager.playSound(player, Data.Sound.OPEN);

            CacheManager.createOpenedBackpack(player.getUniqueId().toString(),
                    player.getUniqueId().toString());
        }
        // Sender
        else if (!sender.getUniqueId().toString().equals(uuid))
        {
            sender.openInventory(backpackListInventory);

            SoundsManager.playSound(sender, Data.Sound.OPEN);

            CacheManager.createOpenedBackpack(uuid,
                    sender.getUniqueId().toString());
        }
    }

    public static void openGUI(String uuid)
    {
        openGUI(uuid, null);
    }

}
