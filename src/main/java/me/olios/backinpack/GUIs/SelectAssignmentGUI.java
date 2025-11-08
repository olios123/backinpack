package me.olios.backinpack.GUIs;

import me.olios.backinpack.Library.Replace.StringReplace;
import me.olios.backinpack.Managers.CacheManager;
import me.olios.backinpack.Managers.GUIConfigManager;
import me.olios.backinpack.Objects.GUI.SelectAssignmentConfig;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class SelectAssignmentGUI {

    public static Inventory selectAssignmentInventory = null;

    public static void openGUI(Player player, ItemStack unassignedBackpack)
    {
        String uuid = player.getUniqueId().toString();
        SelectAssignmentConfig config = GUIConfigManager.getSelectAssignment(player);

        String guiTitle = StringReplace.string(config.TITLE);

        // GUI Object
        selectAssignmentInventory = Bukkit.createInventory(null, 27, guiTitle);

        // Add cache about assigning backpack
        CacheManager.createAssigningBackpack(uuid, unassignedBackpack);

        SelectAssignmentConfig selectAssignmentObject = GUIConfigManager.getSelectAssignment(player);

        // Items
        ItemStack createNewBackpackItem = selectAssignmentObject.createNewBackpack;
        ItemStack assignToExistingItem = selectAssignmentObject.assignToExisting;
        
        // Set items in inventory
        selectAssignmentInventory.setItem(11, createNewBackpackItem);
        selectAssignmentInventory.setItem(15, assignToExistingItem);

        // Open GUI for player
        player.openInventory(selectAssignmentInventory);
    }

}
