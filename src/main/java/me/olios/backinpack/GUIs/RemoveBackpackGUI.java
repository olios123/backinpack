package me.olios.backinpack.GUIs;

import me.olios.backinpack.API.PAPICustom;
import me.olios.backinpack.Data;
import me.olios.backinpack.Library.Blocker;
import me.olios.backinpack.Library.NBTTags;
import me.olios.backinpack.Library.Replace.ItemReplace;
import me.olios.backinpack.Library.Replace.StringReplace;
import me.olios.backinpack.Managers.*;
import me.olios.backinpack.Objects.BackpackObject;
import me.olios.backinpack.Objects.GUI.RemoveBackpackConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RemoveBackpackGUI {

    public static Inventory removeBackpackInventory = null;

    public static void openGUI(Player player)
    {
        String uuid = player.getUniqueId().toString();
        RemoveBackpackConfig config = GUIConfigManager.getRemoveBackpack(player);

        String guiTitle = StringReplace.string(config.TITLE);

        // GUI Object
        removeBackpackInventory = Bukkit.createInventory(null, 54, guiTitle);

        BackpackObject inventory = BackpacksManager.getInventory(player.getUniqueId().toString());

        // Blocked / unused space
        ItemStack blocker = Blocker.get();

        for (int i = ConfigManager.config.MAX_BACKPACKS; i < 54; i++)
        {
            removeBackpackInventory.setItem(i, blocker);
        }

        // Load backpacks
        List<ItemStack> backpacksList = new ArrayList<>();

        if (inventory != null)
        {
            inventory.backpacks.forEach(backpack ->
            {
                // Check if can refund
                boolean canRefund;
                if (ConfigManager.config.CREATE_BACKPACK_BUY && Data.VAULT && !backpack.crafted) canRefund = true;
                else canRefund = false;

                // Backpack item
                ItemStack backpackItem = new ItemStack(BackpacksConfigManager.backpackConfigs.get(backpack.type).STYLE_GUI);

                // Replace lore
                ItemMeta backpackItemMeta = backpackItem.getItemMeta();

                backpackItemMeta.setLore(config.backpackLORE);
                backpackItem.setItemMeta(backpackItemMeta);

                // Backpack placeholders
                Map<String, Object> placeholders = PAPICustom.getBackpackPlaceholders(uuid,
                        backpack.id);
                if (canRefund)
                    placeholders.put("%refund%", ConfigManager.config.REMOVING_BACKPACKS_REFUND);
                else placeholders.put("%refund%", "0");

                backpackItem = ItemReplace.itemPlayer(backpackItem, player, placeholders);

                // Add NBTTags
                NBTTags.addNBT(backpackItem, "backinpack.backpack-id", backpack.id);

                // Add items to backpacksList
                backpacksList.add(backpackItem);
            });

            // Add backpacks to GUI
            for (int i = 0; i < backpacksList.size(); i++)
            {
                if (i >= ConfigManager.config.MAX_BACKPACKS) break;
                removeBackpackInventory.setItem(i, backpacksList.get(i));
            }
        }

        removeBackpackInventory.setItem(49, config.cancel);


        // Open GUI for player
        player.openInventory(removeBackpackInventory);
    }

}
