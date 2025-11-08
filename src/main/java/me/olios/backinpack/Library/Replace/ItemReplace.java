package me.olios.backinpack.Library.Replace;

import me.clip.placeholderapi.PlaceholderAPI;
import me.olios.backinpack.API.PAPICustom;
import me.olios.backinpack.Data;
import me.olios.backinpack.Library.NBTTags;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemReplace {

    // Given item, player and placeholders
    public static ItemStack item(ItemStack itemStack,
                                 OfflinePlayer target,
                                 Map<String, Object> additionalPlaceholders)
    {
        ItemStack newItem = new ItemStack(itemStack);
        ItemMeta itemMeta = newItem.getItemMeta();

        Map<String, Object> placeholders = PAPICustom.getStaticPlaceholders();

        // Add additional placeholders if given
        additionalPlaceholders.forEach(placeholders::putIfAbsent);

        String title = itemMeta.getDisplayName();
        List<String> lore = itemMeta.getLore();

        title = StringReplace.string(title, target, placeholders);

        List<String> convertedList = new ArrayList<>();
        if (lore != null)
        {
            String msg = "";

            for (String s : lore)
            {
                msg = s;
                if (target != null && Data.PAPI) msg = PlaceholderAPI.setPlaceholders(target, msg);
                for (Map.Entry<String, Object> entry : placeholders.entrySet())
                {
                    msg = msg.replace(entry.getKey(), String.valueOf(entry.getValue()));
                }

                if (msg.contains("\\n"))
                {
                    String[] newLineStrings = msg.split("\\\\n");
                    for (String x : newLineStrings)
                    {
                        convertedList.add(ChatColor.translateAlternateColorCodes('&', x));
                    }
                    continue;
                }


                msg = ChatColor.translateAlternateColorCodes('&', msg);
                convertedList.add(msg);
            }
        }

        itemMeta.setDisplayName(title);
        itemMeta.setLore(convertedList);
        newItem.setItemMeta(itemMeta);


        // Adding NBTTags
        for (Map.Entry<String, String> entry : NBTTags.getAllValues(itemStack).entrySet())
        {
            String key = entry.getKey().replace("backinpack:", "");
            NBTTags.addNBT(newItem, key, entry.getValue());
        }

        return newItem;
    }

    // Given item, player and placeholders
    public static ItemStack itemPlayer(ItemStack itemStack,
                                 Player target,
                                 Map<String, Object> placeholders)
    {
        return item(itemStack, Bukkit.getOfflinePlayer(target.getUniqueId()), placeholders);
    }
    // Given item and player
    public static ItemStack itemPlayer(ItemStack itemStack,
                                       Player target)
    {
        return item(itemStack, Bukkit.getOfflinePlayer(target.getUniqueId()), new HashMap<>());
    }



    // Given item and target
    public static ItemStack item(ItemStack itemStack,
                                 OfflinePlayer target)
    {
        return item(itemStack, target, new HashMap<>());
    }

    // Given item and placeholders
    public static ItemStack item(ItemStack itemStack,
                                 Map<String, Object> placeholders)
    {
        return item(itemStack, null, placeholders);
    }



    // Given only item
    public static ItemStack item(ItemStack itemStack)
    {
        return item(itemStack, null, new HashMap<>());
    }
}
