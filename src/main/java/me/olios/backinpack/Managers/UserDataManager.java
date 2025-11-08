//
// CC Creative Commons 2022
// Attribution-NoDerivatives 4.0 International
// Author olios
//

package me.olios.backinpack.Managers;

import me.olios.backinpack.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UserDataManager implements Listener
{
//    private static YamlConfiguration userdataFileYml = new YamlConfiguration();
//    private static File userdataFile = null;

//    public static List<OfflinePlayer> getUserDataPlayers() throws IOException, InvalidConfigurationException
//    {
//        List<OfflinePlayer> players = new ArrayList<>();
//        String[] userDataFiles = FilesManager.userdataFolder.list();
//
//        for (String f : userDataFiles)
//        {
//            File file = new File(FilesManager.userdataFolder, f);
//            YamlConfiguration yamlConfiguration = new YamlConfiguration();
//
//            yamlConfiguration.load(file);
//
//            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(
//                    yamlConfiguration.getString("uuid")));
//
//            players.add(offlinePlayer);
//        }
//
//        return players;
//    }
//
//    public static void createUserData(Player p)
//    {
//        userdataFile = new File(FilesManager.userdataFolder, p.getUniqueId() + ".yml");
//
//        int standardBackpackSize = FilesManager.getConfigYml().getInt("standard-backpack-size");
//
//        try
//        {
//            if (!userdataFile.exists())
//            {
//                if (userdataFile.createNewFile())
//                {
//                    userdataFileYml = new YamlConfiguration();
//
//                    userdataFileYml.set("uuid", p.getUniqueId().toString());
//                    userdataFileYml.set("username", p.getName());
//                    userdataFileYml.set("backpack-size", standardBackpackSize);
//
//                    save(p);
//                }
//
//                BackpacksManager.createBackpack(p.getUniqueId().toString());
//            }
//
//            if (MySQL.isConnected)
//            {
//                ResultSet resultSet = MySQL.queryGet("SELECT * FROM `userdata` WHERE `uuid`='"+p.getUniqueId()+"'");
//
//                if (!resultSet.next())
//                {
//                    MySQL.querySet("INSERT INTO `userdata`(`uuid`, `username`, `backpack-size`) VALUES " +
//                            "('"+p.getUniqueId()+"','"+p.getName()+"',"+standardBackpackSize+")");
//                }
//            }
//
//        }
//        catch (IOException | SQLException e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//
//    public static YamlConfiguration getUserDataYml(String uuid)
//    {
//        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
//        createUserData(offlinePlayer);
//        userdataFile = new File(FilesManager.userdataFolder, uuid + ".yml");
//
//        try
//        {
//            userdataFileYml.load(userdataFile);
//        }catch(IOException | InvalidConfigurationException e)
//        {
//            e.printStackTrace();
//        }
//
//        return userdataFileYml;
//    }
//
//    public static void setBackpackSize(Player p, int size) throws SQLException {
//        createUserData(p);
//        userdataFile = new File(FilesManager.userdataFolder, p.getUniqueId() + ".yml");
//
//        getUserDataYml(p).set("backpack-size", size);
//
//        if (MySQL.isConnected)
//        {
//            MySQL.querySet("UPDATE `userdata` SET `backpack-size`=" + size + " WHERE `uuid`='"+p.getUniqueId()+"'");
//        }
//
//        save(p);
//    }
//
//    private static void save(Player p)
//    {
//        userdataFile = new File(FilesManager.userdataFolder, p.getUniqueId() + ".yml");
//
//        try
//        {
//            userdataFileYml.save(userdataFile);
//        }catch(IOException e)
//        {
//            e.printStackTrace();
//        }
//    }
//
////    @EventHandler(priority = EventPriority.HIGH)
////    private void playerJoinServer(PlayerJoinEvent e)
////    {
////        Player p = e.getPlayer();
////        userdataFile = new File(FilesManager.userdataFolder, p.getUniqueId() + ".yml");
////
////        createUserData(p);
////    }
//
//    // OfflinePlayer
//    public static void createUserData(OfflinePlayer p)
//    {
//        userdataFile = new File(FilesManager.userdataFolder, p.getUniqueId() + ".yml");
//
//        int standardBackpackSize = FilesManager.getConfigYml().getInt("standard-backpack-size");
//
//        try
//        {
//            if (!userdataFile.exists())
//            {
//                if (userdataFile.createNewFile())
//                {
//                    userdataFileYml = new YamlConfiguration();
//
//                    userdataFileYml.set("uuid", p.getUniqueId().toString());
//                    userdataFileYml.set("username", p.getName());
//                    userdataFileYml.set("backpack-size", FilesManager.getConfigYml().getInt("standard-backpack-size"));
//
//                    save(p);
//                }
//
//                BackpacksManager.createBackpack(p.getUniqueId().toString());
//            }
//
//            if (MySQL.isConnected)
//            {
//                ResultSet resultSet = MySQL.queryGet("SELECT * FROM `userdata` WHERE `uuid`='"+p.getUniqueId()+"'");
//
//                if (!resultSet.next())
//                {
//                    MySQL.querySet("INSERT INTO `userdata`(`uuid`, `username`, `backpack-size`) VALUES " +
//                            "('"+p.getUniqueId()+"','"+p.getName()+"',"+standardBackpackSize+")");
//                }
//            }
//
//        } catch (IOException | SQLException e)
//        {
//            e.printStackTrace();
//        }
//    }
//
//    public static YamlConfiguration getUserDataYml(OfflinePlayer p)
//    {
//        createUserData(p);
//        userdataFile = new File(FilesManager.userdataFolder, p.getUniqueId() + ".yml");
//
//        try
//        {
//            userdataFileYml.load(userdataFile);
//        }catch (IOException | InvalidConfigurationException e)
//        {
//            e.printStackTrace();
//        }
//
//        return userdataFileYml;
//    }
//
//    public static void setBackpackSize(OfflinePlayer p, int size) throws SQLException {
//        createUserData(p);
//        userdataFile = new File(FilesManager.userdataFolder, p.getUniqueId() + ".yml");
//
//        getUserDataYml(p).set("backpack-size", size);
//
//        if (MySQL.isConnected)
//        {
//            MySQL.querySet("UPDATE `userdata` SET `backpack-size`=" + size + " WHERE `uuid`='"+p.getUniqueId()+"'");
//        }
//
//        save(p);
//    }
//
//    private static void save(OfflinePlayer p)
//    {
//        userdataFile = new File(FilesManager.userdataFolder, p.getUniqueId() + ".yml");
//
//        try
//        {
//            userdataFileYml.save(userdataFile);
//        }catch(IOException e)
//        {
//            e.printStackTrace();
//        }
//    }
}
