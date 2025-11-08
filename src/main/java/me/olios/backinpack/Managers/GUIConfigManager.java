package me.olios.backinpack.Managers;

import me.olios.backinpack.Data;
import me.olios.backinpack.Library.Replace.ItemReplace;
import me.olios.backinpack.Library.Skull;
import me.olios.backinpack.Objects.GUI.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUIConfigManager {

    /**
     *   ___ ___ _    ___ ___
     *  | __|_ _| |  | __/ __|
     *  | _| | || |__| _|\__ \
     *  |_| |___|____|___|___/
     *
     */

    // BackpacksConfig
    public static BackpacksConfig getBackpacks(OfflinePlayer offlinePlayer,
                                               Map<String, Object> placeholders)
    {
        ReturnData returnData = loadFile("backpacks.yml", offlinePlayer, placeholders);

        // Create return object
        BackpacksConfig backpacksConfig = new BackpacksConfig(
                returnData.TITLE,
                ItemReplace.item(returnData.items.get("buyBackpack")),
                ItemReplace.item(returnData.items.get("removeBackpack")));

        return backpacksConfig;
    }
    public static BackpacksConfig getBackpacks(OfflinePlayer offlinePlayer)
    {
        return getBackpacks(offlinePlayer, null);
    }
    public static BackpacksConfig getBackpacks(Player player)
    {
        return getBackpacks(Bukkit.getOfflinePlayer(player.getUniqueId()), null);
    }




    // BackpackConfig
    public static BackpackConfig getBackpack(OfflinePlayer offlinePlayer,
                                               Map<String, Object> placeholders)
    {
        ReturnData returnData = loadFile("backpack.yml", offlinePlayer, placeholders);

        // Create return object
        BackpackConfig backpackConfig = new BackpackConfig(returnData.TITLE);

        return backpackConfig;
    }
    public static BackpackConfig getBackpack(OfflinePlayer offlinePlayer)
    {
        return getBackpack(offlinePlayer, null);
    }
    public static BackpackConfig getBackpack(Player player)
    {
        return getBackpack(Bukkit.getOfflinePlayer(player.getUniqueId()), null);
    }




    // SelectAssignmentConfig
    public static SelectAssignmentConfig getSelectAssignment(OfflinePlayer offlinePlayer,
                                                              Map<String, Object> placeholders)
    {
        ReturnData returnData = loadFile("selectAssignment.yml", offlinePlayer, placeholders);

        // Create return object
        SelectAssignmentConfig assigningBackpackConfig = new SelectAssignmentConfig(
                returnData.TITLE,
                ItemReplace.item(returnData.items.get("createNewBackpack")),
                ItemReplace.item(returnData.items.get("assignToExisting")));

        return assigningBackpackConfig;
    }
    public static SelectAssignmentConfig getSelectAssignment(OfflinePlayer offlinePlayer)
    {
        return getSelectAssignment(offlinePlayer, null);
    }
    public static SelectAssignmentConfig getSelectAssignment(Player player)
    {
        return getSelectAssignment(Bukkit.getOfflinePlayer(player.getUniqueId()), null);
    }




    // RemoveBackpackConfig
    public static RemoveBackpackConfig getRemoveBackpack(OfflinePlayer offlinePlayer,
                                                         Map<String, Object> placeholders)
    {
        ReturnData returnData = loadFile("removeBackpack.yml", offlinePlayer, placeholders);

        // Create return object
        RemoveBackpackConfig removeBackpackConfig = new RemoveBackpackConfig(
                returnData.TITLE,
                (List<String>) getSectionData(new File(Data.guiPath + "removeBackpack.yml"), "gui.items.backpack").get("LORE"),
                ItemReplace.item(returnData.items.get("cancel")));

        return removeBackpackConfig;
    }
    public static RemoveBackpackConfig getRemoveBackpack(OfflinePlayer offlinePlayer)
    {
        return getRemoveBackpack(offlinePlayer, null);
    }
    public static RemoveBackpackConfig getRemoveBackpack(Player player)
    {
        return getRemoveBackpack(Bukkit.getOfflinePlayer(player.getUniqueId()), null);
    }




    // ChoseBuyConfig
    public static ChoseBuyConfig getChoseBuy(Player player)
    {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());

        ReturnData returnData = loadFile("choseBuy.yml", offlinePlayer);
        List<ChoseBuyConfig.Bck> backpacksToBuy = new ArrayList<>();

        for (Map.Entry<String, ItemStack> entry : returnData.items.entrySet())
        {
            // Cancel item
            if (entry.getKey().equals("cancel")) continue;

            ChoseBuyConfig.Bck bck = new ChoseBuyConfig.Bck();

            // Read data from section
            Map<String, Object> data = getSectionData(
                    new File(Data.guiPath + "choseBuy.yml"),
                    "gui.items." + entry.getKey());

            // Insert data to class
            bck.name = entry.getKey();
            bck.backpack = ItemReplace.item(entry.getValue());
            bck.positionX = (int) data.get("POSITION_X");
            bck.positionY = (int) data.get("POSITION_Y");

            backpacksToBuy.add(bck);
        }

        // Create a return object
        ChoseBuyConfig choseBuyConfig = new ChoseBuyConfig(
                returnData.TITLE,
                (returnData.SIZE == 0) ? 6 : returnData.SIZE,
                backpacksToBuy,
                ItemReplace.item(returnData.items.get("cancel")));

        return choseBuyConfig;
    }
    /**
     *   ___ _____ ___ __  __    ___ ___  _  ___   _____ ___ _____ ___ ___
     *  |_ _|_   _| __|  \/  |  / __/ _ \| \| \ \ / / __| _ \_   _| __| _ \
     *   | |  | | | _|| |\/| | | (_| (_) | .` |\ V /| _||   / | | | _||   /
     *  |___| |_| |___|_|  |_|  \___\___/|_|\_| \_/ |___|_|_\ |_| |___|_|_\
     *
     */

    public static ItemStack getItem(Map<String, Object> data,
                                     OfflinePlayer offlinePlayer,
                                     Map<String, Object> placeholders)
    {

        if (data.get("MATERIAL") == null) return null;

        ItemStack itemStack;

        // Skull
        if (data.get("MATERIAL").equals("PLAYER_HEAD") && data.get("TEXTURE") != null)
            itemStack = Skull.createSkull(String.valueOf(data.get("TEXTURE")));
        else itemStack = new ItemStack(Material.getMaterial(String.valueOf(data.get("MATERIAL"))));

        // Get item meta
        ItemMeta itemMeta = itemStack.getItemMeta();

        // Name
        if (data.get("NAME") != null)
            itemMeta.setDisplayName(String.valueOf(data.get("NAME")));
        // Lore
        if (data.get("LORE") != null)
            itemMeta.setLore((List<String>) data.get("LORE"));
        // Custom model data
        if (data.get("CUSTOM_MODEL_DATA") != null)
            itemMeta.setCustomModelData(Integer.getInteger(String.valueOf(data.get("CUSTOM_MODEL_DATA"))));

        // Set item meta
        itemStack.setItemMeta(itemMeta);

        // Replace item
        if (offlinePlayer != null && offlinePlayer.hasPlayedBefore())
            itemStack = ItemReplace.item(itemStack, offlinePlayer);

        if (placeholders != null)
            itemStack = ItemReplace.item(itemStack, placeholders);

        return itemStack;
    }

    public static ItemStack getItem(Map<String, Object> data)
    {
        return getItem(data, null, null);
    }

    public static ItemStack getItem(Map<String, Object> data,
                                    OfflinePlayer offlinePlayer)
    {
        return getItem(data, offlinePlayer, null);
    }

    public static ItemStack getItem(Map<String, Object> data,
                                    @NotNull Player player)
    {
        return getItem(data, Bukkit.getOfflinePlayer(player.getUniqueId()), null);
    }

    public static ItemStack getItem(Map<String, Object> data,
                                    Map<String, Object> placeholders)
    {
        return getItem(data, null, placeholders);
    }

    public static ItemStack getItemPlayer(Map<String, Object> data,
                                    Player player,
                                    Map<String, Object> placeholders)
    {
        return getItem(data, Bukkit.getOfflinePlayer(player.getUniqueId()), placeholders);
    }

    public static boolean isPlayer(OfflinePlayer offlinePlayer)
    {
        if (offlinePlayer != null && offlinePlayer.hasPlayedBefore()) return true;
        else return false;
    }

    /**
     *   _    ___   _   ___ ___ _  _  ___   ___ ___ _    ___
     *  | |  / _ \ /_\ |   \_ _| \| |/ __| | __|_ _| |  | __|
     *  | |_| (_) / _ \| |) | || .` | (_ | | _| | || |__| _|
     *  |____\___/_/ \_\___/___|_|\_|\___| |_| |___|____|___|
     *
     */

    private static class ReturnData
    {
        String TITLE = "";
        int SIZE = 0;
        Map<String, ItemStack> items = new HashMap<>();
    }

    private static ReturnData loadFile(String fileName,
                                       OfflinePlayer offlinePlayer,
                                       Map<String, Object> placeholders)
    {
        YamlConfiguration yml = new YamlConfiguration();
        File file = new File(Data.guiPath + fileName);

        try
        {
            yml.load(file);
        }
        catch (IOException | InvalidConfigurationException e)
        {
            e.printStackTrace();
        }

        String TITLE = yml.getString("gui.title");
        int SIZE = yml.getInt("gui.size");
        Map<String, Map<String, Object>> rawData = new HashMap<>();
        ReturnData returnData = new ReturnData();

        if (yml.get("gui.items") != null)
        {
            // Read items
            yml.getConfigurationSection("gui.items").getKeys(false).forEach(key ->
            {
                ConfigurationSection item = yml.getConfigurationSection("gui.items." + key);
                Map<String, Object> itemData = new HashMap<>();

                item.getKeys(false).forEach(data ->
                {
                    itemData.put(data, item.get(data));
                });

                rawData.put(key, itemData);
            });

            Map<String, ItemStack> convertedData = new HashMap<>();

            for (Map.Entry<String, Map<String, Object>> entry : rawData.entrySet())
            {
                String name = entry.getKey();
                Map<String, Object> data = entry.getValue();

                // Player and placeholders
                if (isPlayer(offlinePlayer) && placeholders != null)
                    convertedData.put(name, getItem(data, offlinePlayer, placeholders));
                    // Player without placeholders
                else if (isPlayer(offlinePlayer) && placeholders == null)
                    convertedData.put(name, getItem(data, offlinePlayer));
                    // Placeholders without player
                else if (!isPlayer(offlinePlayer) && placeholders != null)
                    convertedData.put(name, getItem(data, placeholders));
                    // Nothing
                else
                    convertedData.put(name, getItem(data));
            }

            // Confirm data change
            returnData.TITLE = TITLE;
            returnData.SIZE = SIZE;
            returnData.items = convertedData;
        }
        else
        {
            returnData.TITLE = TITLE;
            returnData.SIZE = SIZE;
            returnData.items = null;
        }


        return returnData;
    }

    private static ReturnData loadFile(String fileName)
    {
        return loadFile(fileName, null, null);
    }

    private static ReturnData loadFile(String fileName,
                                       OfflinePlayer offlinePlayer)
    {
        return loadFile(fileName, offlinePlayer, null);
    }

    private static ReturnData loadFile(String fileName,
                                       Map<String, Object> placeholders)
    {
        return loadFile(fileName, null, placeholders);
    }

    // Read item data from config
    public static Map<String, Object> getData(String configSection)
    {
        Map<String, Object> returnData = new HashMap<>();
        YamlConfiguration cnf = FilesManager.getConfigYml();

        if (cnf.get(configSection) == null) return null;
        ConfigurationSection backpackStyle = cnf.getConfigurationSection(configSection);

        // Read data from backpack in config
        backpackStyle.getKeys(false).forEach(key ->
        {
            returnData.put(key, backpackStyle.get(key));
        });

        return returnData;
    }

    // Read file section
    public static Map<String, Object> getSectionData(YamlConfiguration yml,
                                                     String section)
    {
        Map<String, Object> returnData = new HashMap<>();

        ConfigurationSection sec = yml.getConfigurationSection(section);
        if (sec == null) return returnData;

        sec.getKeys(false).forEach(key ->
        {
            returnData.put(key, sec.get(key));
        });

        return returnData;
    }

    public static Map<String, Object> getSectionData(File file,
                                                     String section)
    {
        YamlConfiguration yml = new YamlConfiguration();

        try
        {
            yml.load(file);
        }
        catch (IOException | InvalidConfigurationException e)
        {
            e.printStackTrace();
        }

        return getSectionData(yml, section);
    }
}
