package me.olios.backinpack.GUIs;

import me.olios.backinpack.API.PAPICustom;
import me.olios.backinpack.Library.NBTTags;
import me.olios.backinpack.Library.Replace.ItemReplace;
import me.olios.backinpack.Library.Replace.StringReplace;
import me.olios.backinpack.Managers.*;
import me.olios.backinpack.Objects.BackpackObject;
import me.olios.backinpack.Objects.GUI.BackpacksConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class AssigningBackpackGUI {

    public static Inventory assigningBackpackInventory = null;

    public static void openGUI(Player player, ItemStack unassignedBackpack)
    {
        String uuid = player.getUniqueId().toString();
        BackpacksConfig config = GUIConfigManager.getBackpacks(player);

        String guiTitle = StringReplace.string(config.TITLE, player);

        // GUI Object
        assigningBackpackInventory = Bukkit.createInventory(null, 54, guiTitle);


        // Add cache about assigning backpack
        CacheManager.createAssigningBackpack(uuid, unassignedBackpack);

        BackpackObject inventory = BackpacksManager.getInventory(player.getUniqueId().toString());

        // Load backpacks
        List<ItemStack> backpacksList = new ArrayList<>();

        if (inventory != null)
        {
            inventory.backpacks.forEach(backpack ->
            {
                ItemStack backpackItem = BackpacksConfigManager.backpackConfigs.get(backpack.type).STYLE_GUI;

                backpackItem = ItemReplace.item(backpackItem, PAPICustom.getBackpackPlaceholders(uuid, backpack.id));

                // Add information about an owner of this backpack
                NBTTags.addNBT(backpackItem, "backinpack.backpack-owner", uuid);
                // Add backpack ID to item
                NBTTags.addNBT(backpackItem, "backinpack.backpack-id", backpack.id);

                // Add items to backpacksList
                backpacksList.add(backpackItem);
            });

            // Add backpacks to GUI
            for (int i = 0; i < backpacksList.size(); i++)
            {
                if (i >= ConfigManager.config.MAX_BACKPACKS) break;
                assigningBackpackInventory.setItem(i, backpacksList.get(i));
            }
        }


        // Open GUI for player
        player.openInventory(assigningBackpackInventory);
    }

}
