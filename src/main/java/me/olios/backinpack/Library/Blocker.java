package me.olios.backinpack.Library;

import me.olios.backinpack.Library.Replace.ItemReplace;
import me.olios.backinpack.Managers.FilesManager;
import me.olios.backinpack.Managers.GUIConfigManager;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class Blocker {

    private static YamlConfiguration cnf = FilesManager.getConfigYml();

    public static ItemStack get()
    {
        Map<String, Object> blockerData = GUIConfigManager.getData("backpack-inaccessible-place");

        // Blocked place ItemStack
        return ItemReplace.item(GUIConfigManager.getItem(blockerData));
    }

}
