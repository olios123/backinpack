package me.olios.backinpack.Library.Replace;

import me.clip.placeholderapi.PlaceholderAPI;
import me.olios.backinpack.API.PAPICustom;
import me.olios.backinpack.Data;
import me.olios.backinpack.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class StringReplace {

    // Given string, player and placeholders
    public static String string(String string,
                                OfflinePlayer target,
                                Map<String, Object> additionalPlaceholders)
    {
        String stringToReplace = "";

        Map<String, Object> placeholders = PAPICustom.getStaticPlaceholders();

        // Add additional placeholders if given
        additionalPlaceholders.forEach(placeholders::putIfAbsent);

        String temp = string;
        if (target != null && Data.PAPI) temp = PlaceholderAPI.setPlaceholders(target, temp);
        for (Map.Entry<String, Object> entry : placeholders.entrySet())
        {
            temp = temp.replace(entry.getKey(), String.valueOf(entry.getValue()));
        }
        stringToReplace = ChatColor.translateAlternateColorCodes('&', temp);

        return stringToReplace;
    }

    // Given string, player and placeholders
    public static String stringPlayer(String string,
                                Player target,
                                Map<String, Object> placeholders)
    {
        return string(string, Bukkit.getOfflinePlayer(target.getUniqueId()), placeholders);
    }



    // Given string and target
    public static String string(String string,
                                OfflinePlayer target)
    {
        return string(string, target, new HashMap<>());
    }

    // Given string and player
    public static String string(String string,
                                Player target)
    {
        return string(string, Bukkit.getOfflinePlayer(target.getUniqueId()), new HashMap<>());
    }



    // Given string and placeholders
    public static String string(String string,
                                Map<String, Object> placeholders)
    {
        return string(string, null, placeholders);
    }



    // Given only string
    public static String string(String string)
    {
        return string(string, null, new HashMap<>());
    }


    // Replace for consoles
    public static String cmd(String string,
                             OfflinePlayer target,
                             Map<String, Object> additionalPlaceholders)
    {
        String stringToReplace = "";

        Map<String, Object> placeholders = PAPICustom.getStaticPlaceholders();

        // Add additional placeholders if given
        additionalPlaceholders.forEach(placeholders::putIfAbsent);

        String temp = string;
        if (target != null && Data.PAPI) temp = PlaceholderAPI.setPlaceholders(target, temp);
        for (Map.Entry<String, Object> entry : placeholders.entrySet())
        {
            temp = temp.replace(entry.getKey(), String.valueOf(entry.getValue()));
        }

        // Replace colors
        stringToReplace = replaceCommandColors(temp);

        return stringToReplace;
    }

    public static String cmd(String string)
    {
        return cmd(string, null, new HashMap<>());
    }

    public static String replaceCommandColors(String string)
    {
        String tmp = string;

        tmp = tmp.replace("&0", Main.ANSI_BLACK);
        tmp = tmp.replace("&1", Main.ANSI_DARK_BLUE);
        tmp = tmp.replace("&2", Main.ANSI_DARK_GREEN);
        tmp = tmp.replace("&3", Main.ANSI_DARK_AQUA);
        tmp = tmp.replace("&4", Main.ANSI_DARK_RED);
        tmp = tmp.replace("&5", Main.ANSI_DARK_PURPLE);
        tmp = tmp.replace("&6", Main.ANSI_GOLD);
        tmp = tmp.replace("&f", Main.ANSI_WHITE);
        tmp = tmp.replace("&r", Main.ANSI_RESET);

        String serverVersion = Bukkit.getServer().getBukkitVersion();

        // For versions > 1.19
        if (Integer.parseInt(serverVersion.split("\\.")[1]) >= 19)
        {
            tmp = tmp.replace("&7", Main.ANSI_WHITE); // No replace
            tmp = tmp.replace("&8", Main.ANSI_WHITE); // No replace
            tmp = tmp.replace("&9", Main.ANSI_DARK_BLUE); // To dark
            tmp = tmp.replace("&a", Main.ANSI_DARK_GREEN); // To dark
            tmp = tmp.replace("&b", Main.ANSI_DARK_AQUA); // To dark
            tmp = tmp.replace("&c", Main.ANSI_DARK_RED); // To dark
            tmp = tmp.replace("&d", Main.ANSI_DARK_PURPLE); // To dark
            tmp = tmp.replace("&e", Main.ANSI_GOLD); // To dark
            tmp = tmp.replace("&k", ""); // Remove replace
            tmp = tmp.replace("&l", ""); // Remove replace
            tmp = tmp.replace("&m", ""); // Remove replace
            tmp = tmp.replace("&n", ""); // Remove replace
            tmp = tmp.replace("&o", ""); // Remove replace
        }
        else // For versions < 1.19
        {
            tmp = tmp.replace("&7", Main.ANSI_GRAY);
            tmp = tmp.replace("&8", Main.ANSI_DARK_GRAY);
            tmp = tmp.replace("&9", Main.ANSI_BLUE);
            tmp = tmp.replace("&a", Main.ANSI_GREEN);
            tmp = tmp.replace("&b", Main.ANSI_AQUA);
            tmp = tmp.replace("&c", Main.ANSI_RED);
            tmp = tmp.replace("&d", Main.ANSI_LIGHT_PURPLE);
            tmp = tmp.replace("&e", Main.ANSI_YELLOW);
            tmp = tmp.replace("&k", Main.ANSI_OBFUSCATED);
            tmp = tmp.replace("&l", Main.ANSI_BOLD);
            tmp = tmp.replace("&m", Main.ANSI_STRIKETHROUGH);
            tmp = tmp.replace("&n", Main.ANSI_UNDERLINE);
            tmp = tmp.replace("&o", Main.ANSI_ITALIC);
        }

        return tmp;
    }
}
