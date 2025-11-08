package me.olios.backinpack.Library;

import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemsCompare {

    public static boolean isSimilar(ItemStack item1,
                                    ItemStack item2)
    {
        boolean similar = true; // Control boolean

        // Get item meta from every item
        ItemMeta meta1 = item1.getItemMeta();
        ItemMeta meta2 = item2.getItemMeta();

        // Compare by title, lore and material
        if (item1.getType() != item2.getType()) similar = false;
        if (!ChatColor.stripColor(meta1.getDisplayName()).equals(ChatColor.stripColor(meta2.getDisplayName()))) similar = false;
        if (meta1.getLore() != meta2.getLore()) similar = false;

        return similar;
    }

}
