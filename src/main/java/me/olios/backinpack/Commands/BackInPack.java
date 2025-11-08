//
// CC Creative Commons 2022
// Attribution-NoDerivatives 4.0 International
// Author olios
//

package me.olios.backinpack.Commands;

import me.olios.backinpack.Data;
import me.olios.backinpack.Library.Replace.StringReplace;
import me.olios.backinpack.Library.TextCreator;
import me.olios.backinpack.Main;
import me.olios.backinpack.Managers.*;
import me.olios.backinpack.MySQL;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class BackInPack {

    public static void executeCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        TextCreator back = new TextCreator("[Back]");
        back.COLOR = ChatColor.GOLD;
        back.addClickEvent(ClickEvent.Action.RUN_COMMAND, "/backinpack");
        back.addHoverEvent(HoverEvent.Action.SHOW_TEXT, "&fReturn to BackInPack menu");

        // Sender is player
        if (sender instanceof Player)
        {
            Player p = ((Player) sender).getPlayer();
            p.recalculatePermissions(); // Refresh permissions

            // Help
            clearChat(p);
            if (args.length == 0 || args[0].equals("help"))
            {
                msg(p, "&6Back&9In&6Pack &f(" + Data.pluginVersion + ")");
                msg(p, "&fAuthor: &aolios");
                msg(p, "");

                TextCreator commands = new TextCreator("[Commands]");
                commands.COLOR = ChatColor.GOLD;
                commands.addClickEvent(ClickEvent.Action.RUN_COMMAND, "/backinpack commands");
                commands.addHoverEvent(HoverEvent.Action.SHOW_TEXT, "&f/backinpack commands");
                p.spigot().sendMessage(new TextComponent("     "), commands.get());

                TextCreator permissions = new TextCreator("[Permissions]");
                permissions.COLOR = ChatColor.GOLD;
                permissions.addClickEvent(ClickEvent.Action.RUN_COMMAND, "/backinpack permissions");
                permissions.addHoverEvent(HoverEvent.Action.SHOW_TEXT, "&f/backinpack permissions");
                p.spigot().sendMessage(new TextComponent("     "), permissions.get());

                TextCreator placeholders = new TextCreator("[Placeholders]");
                placeholders.COLOR = ChatColor.GOLD;
                placeholders.addClickEvent(ClickEvent.Action.RUN_COMMAND, "/backinpack placeholders");
                placeholders.addHoverEvent(HoverEvent.Action.SHOW_TEXT, "&f/backinpack placeholders");
                p.spigot().sendMessage(new TextComponent("     "), placeholders.get());

                TextCreator about = new TextCreator("[About]");
                about.COLOR = ChatColor.GOLD;
                about.addClickEvent(ClickEvent.Action.RUN_COMMAND, "/backinpack about");
                about.addHoverEvent(HoverEvent.Action.SHOW_TEXT, "&f/backinpack about");
                p.spigot().sendMessage(new TextComponent("     "), about.get());

                msg(p, "");

                TextCreator spigot = new TextCreator("[SpigotMC]");
                spigot.COLOR = ChatColor.YELLOW;
                spigot.addClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/backinpack-portable-backpack.102384/");
                spigot.addHoverEvent(HoverEvent.Action.SHOW_TEXT, "&eSpigotMC plugin site");

                TextCreator built = new TextCreator("[BuiltByBit]");
                built.COLOR = ChatColor.BLUE;
                built.addClickEvent(ClickEvent.Action.OPEN_URL, "https://builtbybit.com/resources/backinpack-portable-backpack.32141/");
                built.addHoverEvent(HoverEvent.Action.SHOW_TEXT, "&9BuiltByBit plugin site");

                TextCreator polymart = new TextCreator("[Polymart]");
                polymart.COLOR = ChatColor.GREEN;
                polymart.addClickEvent(ClickEvent.Action.OPEN_URL, "https://polymart.org/resource/backinpack-portable-backpack.4808");
                polymart.addHoverEvent(HoverEvent.Action.SHOW_TEXT, "&aPolymart plugin site");

                TextCreator discord = new TextCreator("[Discord]");
                discord.COLOR = ChatColor.AQUA;
                discord.addClickEvent(ClickEvent.Action.OPEN_URL, Data.discord);
                discord.addHoverEvent(HoverEvent.Action.SHOW_TEXT, "&bDiscord support server");

                p.spigot().sendMessage(new TextComponent("     "), spigot.get(),
                        new TextComponent("  "), built.get(),
                        new TextComponent("  "), polymart.get(),
                        new TextComponent("  "), discord.get());

                msg(p, "");
            }

            // About
            else if (args[0].equals("about"))
            {
                p.sendMessage(StringReplace.string("&6Back&9In&6Pack &f- About"));
                p.sendMessage("");
                MessagesManager.sendMessage(p, Data.Message.CMD_ABOUT);
            }

            // Commands
            else if (args[0].equals("commands"))
            {
                if (!PermissionsManager.checkPermissions(p, Data.Permission.ADMIN))
                {
                    MessagesManager.sendMessage(p, Data.Message.NO_PERMISSION);
                    return;
                }

                p.sendMessage(StringReplace.string("&6Back&9In&6Pack &f- Commands"));
                p.sendMessage("");

                for (Map.Entry<String, Map<String, Object>> entry :
                        Data.plugin.getDescription().getCommands().entrySet())
                {
                    Map<String, Object> description = entry.getValue();

                    String usage = description.get("usage").toString();

                    p.sendMessage(StringReplace.string("&a" + usage));
                    p.sendMessage(StringReplace.string("  &6" +
                            description.get("description").toString()));
                }
                p.sendMessage("");
                p.spigot().sendMessage(back.get());
            }

            // Permissions
            else if (args[0].equals("permissions"))
            {
                if (!PermissionsManager.checkPermissions(p, Data.Permission.ADMIN))
                {
                    MessagesManager.sendMessage(p, Data.Message.NO_PERMISSION);
                    return;
                }

                p.sendMessage(StringReplace.string("&6Back&9In&6Pack &f- Permissions"));
                p.sendMessage("");

                Data.plugin.getDescription().getPermissions().forEach(permission ->
                {
                    p.sendMessage(StringReplace.string("&a" + permission.getName()));
                    p.sendMessage(StringReplace.string("  &6" + permission.getDescription()));
                });
                p.sendMessage("");
                p.spigot().sendMessage(back.get());
            }

            // Placeholders
            else if (args[0].equals("placeholders"))
            {
                if (!PermissionsManager.checkPermissions(p, Data.Permission.ADMIN))
                {
                    MessagesManager.sendMessage(p, Data.Message.NO_PERMISSION);
                    return;
                }

                p.sendMessage(StringReplace.string("&6The list of placeholders is available at this link:"));
                p.sendMessage(StringReplace.string("&a" + Data.resourceDocs));
                p.sendMessage("");
                p.spigot().sendMessage(back.get());
            }

            // Reload plugin
            else if (args[0].equals("reload"))
            {
                // Check permissions
                if (!PermissionsManager.checkPermissions(p, Data.Permission.RELOAD))
                {
                    MessagesManager.sendMessage(p, Data.Message.NO_PERMISSION);
                    return;
                }

                ConfigManager.reload();

                MessagesManager.sendMessage(p, Data.Message.PLAYER_RELOAD_COMPLETE);
            }

            // Sync plugin with database
            else if (args[0].equals("sync"))
            {
                // Check permissions
                if (!PermissionsManager.checkPermissions(p, Data.Permission.SYNC))
                {
                    MessagesManager.sendMessage(p, Data.Message.NO_PERMISSION);
                    return;
                }

                if (!MySQL.isConnected)
                {
                    MessagesManager.sendMessage(p, Data.Message.PLAYER_DATABASE_NOT_CONNECTED);
                    return;
                }

                BackpacksManager.updateMySQLBackpacks();
                MessagesManager.sendMessage(p, Data.Message.PLAYER_DATABASE_SYNCHRONIZED);
            }

            // Argument not found
            else
            {
                Map<String, Object> placeholders = new HashMap<>();
                placeholders.put("%command%", "backinpack [help/commands/permissions/placeholders/reload/sync/about]");

                MessagesManager.sendMessage(p, Data.Message.ARGUMENT_MISSING, placeholders);
            }
        }
        else // Console
        {
            // Default help
            if (args.length == 0 || args[0].equals("help"))
            {
                Main.log(StringReplace.cmd("&6Back&9In&6Pack &r&f(" + Data.pluginVersion + "&f)"));
                Main.log(StringReplace.cmd("&fAuthor: &aolios"));
                Main.log(StringReplace.cmd("&fCommands: &a/backinpack commands"));
                Main.log(StringReplace.cmd("&fAbout: &a/backinpack about"));

            }

            // About
            else if (args[0].equals("about"))
            {
                Main.log(StringReplace.cmd("&6Back&9In&6Pack &r&f- About"));
                Main.log("");
                MessagesManager.sendLogMessage(Data.Message.CMD_ABOUT);
            }

            // Commands
            else if (args[0].equals("commands"))
            {
                Main.log(StringReplace.cmd("&6Back&9In&6Pack &r&f- Commands"));
                Main.log("");

                for (Map.Entry<String, Map<String, Object>> entry :
                        Data.plugin.getDescription().getCommands().entrySet())
                {
                    Map<String, Object> description = entry.getValue();

                    String usage = description.get("usage").toString();

                    Main.log(StringReplace.cmd("&a" + usage));
                    Main.log(StringReplace.cmd("  &f" +
                            description.get("description").toString()));
                }

            }

            // Permissions
            else if (args[0].equals("permissions"))
            {
                Main.log(StringReplace.cmd("&6Back&9In&6Pack &r&f- Permissions"));
                Main.log("");

                Data.plugin.getDescription().getPermissions().forEach(permission ->
                {
                    Main.log(StringReplace.cmd("&a" + permission.getName()));
                    Main.log(StringReplace.cmd("  &f" + permission.getDescription()));
                });
            }


            // Placeholders
            else if (args[0].equals("placeholders"))
            {
                Main.log(StringReplace.cmd("&6The list of placeholders is available at this link:"));
                Main.log(StringReplace.cmd("&a" + Data.resourceDocs));
            }


            // Reload plugin
            else if (args[0].equals("reload"))
            {
                ConfigManager.reload();

                MessagesManager.sendLogMessage(Data.Message.RELOAD_COMPLETE);
            }


            // Sync plugins
            else if (args[0].equals("sync"))
            {
                if (!MySQL.isConnected)
                {
                    MessagesManager.sendLogMessage(Data.Message.DATABASE_NOT_CONNECTED);
                    return;
                }

                BackpacksManager.updateMySQLBackpacks();
                MessagesManager.sendLogMessage(Data.Message.DATABASE_SYNCHRONIZED);
            }


            // Argument not found
            else
            {
                Map<String, Object> placeholders = new HashMap<>();
                placeholders.put("%command%", "backinpack [help/commands/permissions/placeholders/reload/about]");

                MessagesManager.sendLogMessage(Data.Message.ARGUMENT_MISSING, placeholders);
            }
        }
    }

    private static void clearChat(Player p)
    {
        for (int i = 0; i <= 30; i++)
        {
            p.sendMessage("");
        }
    }

    private static void msg(Player p, String msg)
    {
        p.sendMessage("     " + StringReplace.string(msg));
    }
}
