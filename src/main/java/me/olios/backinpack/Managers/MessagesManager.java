//
// CC Creative Commons 2022
// Attribution-NoDerivatives 4.0 International
// Author olios
//

package me.olios.backinpack.Managers;

import me.clip.placeholderapi.PlaceholderAPI;
import me.olios.backinpack.API.PAPICustom;
import me.olios.backinpack.Data;
import me.olios.backinpack.Library.Replace.ListReplace;
import me.olios.backinpack.Library.Replace.StringReplace;
import me.olios.backinpack.Main;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class MessagesManager {

    public static YamlConfiguration languageYml = new YamlConfiguration();
    public static String languageCode = "";

    public static void setLanguageFile()
    {
        languageCode = FilesManager.getConfigYml().getString("language");
        File languageFile = new File(Data.languagesPath + languageCode + ".yml");

        try
        {
            if (languageFile.exists())
            {
                languageYml.load(languageFile);
                assert languageCode != null;
                Main.log(getConsoleMessage(Data.Message.LOADED_LANGUAGE_FILE));
            }
            else
            {
                languageYml.load(FilesManager.enFile);
                assert languageCode != null;
                Main.log(getConsoleMessage(Data.Message.LANGUAGE_FILE_NOT_FOUND));
            }
        }
        catch (IOException | InvalidConfigurationException e)
        {
            e.printStackTrace();
        }
    }



    // Given player, message, target, placeholders
    public static void sendMessage(Player p,
                                   Data.Message message,
                                   OfflinePlayer target,
                                   Map<String, Object> additionalPlaceholders)
    {
        Object msg = languageYml.get(message.toString().toLowerCase().replace("_", "-"));
        Map<String, Object> placeholders = PAPICustom.getStaticPlaceholders();

        // Add additional placeholders if given
        additionalPlaceholders.forEach(placeholders::putIfAbsent);

        // Message is list
        if (msg instanceof List<?>)
        {
            List<String> strings = (List<String>) msg;

            // Placeholders
            if (target != null) strings = ListReplace.list(strings, target, placeholders);
            else strings = ListReplace.listPlayer(strings, p, placeholders);

            strings.forEach(p::sendMessage);
        }
        // Message is single string
        else if (msg instanceof String)
        {
            String string = (String) msg;

            // Placeholders
            if (target != null) string = StringReplace.string(string, target, placeholders);
            else string = StringReplace.stringPlayer(string, p, placeholders);

            p.sendMessage(string);
        }
    }

    // Given player, message, player, placeholders
    public static void sendMessagePlayer(Player p,
                                   Data.Message message,
                                   Player target,
                                   Map<String, Object> placeholders)
    {
        sendMessage(p, message, Bukkit.getOfflinePlayer(target.getUniqueId()), placeholders);
    }



    // Given player, message and target
    public static void sendMessage(Player p,
                                   Data.Message message,
                                   OfflinePlayer target)
    {
        sendMessage(p, message, target, new HashMap<>());
    }

    // Given player, message and player
    public static void sendMessage(Player p,
                                   Data.Message message,
                                   Player target)
    {
        sendMessage(p, message, Bukkit.getOfflinePlayer(target.getUniqueId()), new HashMap<>());
    }



    // Given player, message and placeholders
    public static void sendMessage(Player p,
                                   Data.Message message,
                                   Map<String, Object> placeholders)
    {
        sendMessage(p, message, null, placeholders);
    }



    // Given player and message
    public static void sendMessage(Player p,
                                   Data.Message message)
    {
        sendMessage(p, message, null, new HashMap<>());
    }



    public static void sendLogMessage(Data.Message message,
                                      OfflinePlayer target,
                                      Map<String, Object> additionalPlaceholders)
    {
        Object msg = languageYml.get(message.toString().toLowerCase().replace("_", "-"));
        Map<String, Object> placeholders = PAPICustom.getStaticPlaceholders();

        // Add additional placeholders if given
        additionalPlaceholders.forEach(placeholders::putIfAbsent);

        // Message is list
        if (msg instanceof List<?>)
        {
            List<String> strings = (List<String>) msg;
            List<String> convertedList = new ArrayList<>();

            // Set placeholders for target
            if (target != null && Data.PAPI) strings = PlaceholderAPI.setPlaceholders(target, strings);

            // Placeholders
            String m = "";
            for (String x : strings)
            {
                m = x;
                if (target != null && Data.PAPI) m = PlaceholderAPI.setPlaceholders(target, m);
                for (Map.Entry<String, Object> entry : placeholders.entrySet())
                {
                    m = m.replace(entry.getKey(), String.valueOf(entry.getValue()));
                }

                // Colors
                m = StringReplace.replaceCommandColors(m);

                convertedList.add(m);
            }

            convertedList.forEach(Main::log);
        }
        // Message is single string
        else if (msg instanceof String)
        {
            String string = (String) msg;

            // Set placeholders for target
            if (target != null && Data.PAPI) string = PlaceholderAPI.setPlaceholders(target, string);

            // Placeholders
            for (Map.Entry<String, Object> entry : placeholders.entrySet())
            {
                string = string.replace(entry.getKey(), String.valueOf(entry.getValue()));
            }

            // Colors
            string = StringReplace.replaceCommandColors(string);

            Main.log(string);
        }
    }

    // Given message and target
    public static void sendLogMessage(Data.Message message,
                                      OfflinePlayer target)
    {
        sendLogMessage(message, target, new HashMap<>());
    }

    // Given message
    public static void sendLogMessage(Data.Message message)
    {
        sendLogMessage(message, null, new HashMap<>());
    }

    // Given message and placeholders
    public static void sendLogMessage(Data.Message message,
                                      Map<String, Object> placeholders)
    {
        sendLogMessage(message, null, placeholders);
    }

    public static Object getMessage(Data.Message message, Map<String, Object> ... additionalPlaceholders)
    {
        Object msg = languageYml.get(message.toString().toLowerCase().replace("_", "-"));
        Map<String, Object> placeholders = PAPICustom.getStaticPlaceholders();

        // Add additional placeholders if given
        if (additionalPlaceholders.length > 0) additionalPlaceholders[0].forEach(placeholders::putIfAbsent);

        // Message is list
        if (msg instanceof List<?>)
        {
            List<String> strings = (List<String>) msg;

            // Placeholders
            strings = ListReplace.list(strings, placeholders);

            return strings;
        }
        // Message is single string
        else if (msg instanceof String)
        {
            String string = (String) msg;

            // Placeholders
            string = StringReplace.string(string, placeholders);

            return string;
        }

        return null;
    }

    public static String getConsoleMessage(Data.Message message)
    {
        Object msg = languageYml.get(message.toString().toLowerCase().replace("_", "-"));

        // List
        if (msg instanceof List<?>)
        {
            List<String> strings = (List<String>) msg;

            String m = "";
            for (String x : strings) m += x + "\n";
            m = m.substring(-2);

            // Replace colors
            m = StringReplace.cmd(m);

            return m;
        }
        // Single message
        else if (msg instanceof String)
        {
            String m = (String) msg;

            // Replace colors
            m = StringReplace.cmd(m);

            return m;
        }

        return null;
    }

    public static Object getRawMessage(Data.Message message)
    {
        Object msg = languageYml.get(message.toString().toLowerCase().replace("_", "-"));

        // Message is list
        if (msg instanceof List<?>)
        {
            List<String> strings = (List<String>) msg;
            return strings;
        }
        // Message is single string
        else if (msg instanceof String)
        {
            return msg;
        }

        return null;
    }

    private static List<String> getMessages(String messageCode)
    {
        return languageYml.getStringList(messageCode);
    }

    public static String getLoadedLanguage()
    {
        return FilesManager.getConfigYml().getString("language");
    }

    public static String getFlagsMessage(List<String> flags)
    {
        if (flags.isEmpty()) return (String) getRawMessage(Data.Message.FLAG_NONE);
        else
        {
            String flagsMessage = "\\n";
            for (String flag : flags)
            {
                flagsMessage += getRawMessage(Data.Message.valueOf("FLAG_" + flag)) + "\\n";
            }

            flagsMessage = flagsMessage.substring(0, flagsMessage.length() - 2);
            return flagsMessage;
        }
    }

    public static void sendUpdateInfo(Player p)
    {
        TextComponent link = new TextComponent();
        link.setText(StringReplace.string("&a&nspigotmc.org BackInPack (click)"));
        link.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, Data.resourceURL));
        link.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(Data.resourceURL)));

        p.spigot().sendMessage(new TextComponent("There is a newer version of BackInPack plugin available, you can download it from "), link);
    }

    public static void sendUpdateInfo()
    {
        List<String> msg = new ArrayList<>();
        msg.add("&6  ___ &9___&6 ___   _   _ ___ ___   _ _____ ___ ");
        msg.add("&6 | _ )&9_ _&6| _ \\ | | | | _ \\   \\ /_\\_   _| __|");
        msg.add("&6 | _ \\&9| |&6|  _/ | |_| |  _/ |) / _ \\| | | _| ");
        msg.add("&6 |___/&9___&6|_|    \\___/|_| |___/_/ \\_\\_| |___|");
        msg.add("");
        msg.add("&rThe plugin BackInPack can be updated. Use this link to download the latest version");
        msg.add("&a" + Data.resourceURL);
        msg.add("");

        String m = "";

        for (String x : msg)
        {
            m += "\n" + x;
        }

        m = StringReplace.cmd(m);

        Main.log(m);
    }

}
