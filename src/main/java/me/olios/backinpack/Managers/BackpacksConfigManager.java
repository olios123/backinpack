package me.olios.backinpack.Managers;

import me.olios.backinpack.Data;
import me.olios.backinpack.Main;
import me.olios.backinpack.Objects.BackpackConfig;
import me.olios.backinpack.Objects.BackpackObject;
import me.olios.backinpack.Objects.Config;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BackpacksConfigManager {

    public static Map<String, BackpackConfig> backpackConfigs = new HashMap<>();

    // Get a list of all backpacks
    public static void backpackConfigs()
    {
        Map<String, BackpackConfig> backpackConfisgMap = new HashMap<>();

        try
        {
            // For in every file in /backpacks
            for (File file : FilesManager.backpacksFolder.listFiles())
            {
                if (file.getName().equalsIgnoreCase("README.yml")) continue;

                String backpackName = file.getName().replace(".yml", "");

                YamlConfiguration backpackConfiguration = new YamlConfiguration();
                backpackConfiguration.load(file);

                // Read backpack as object
                BackpackConfig backpackConfig = getBackpackConfig(backpackName, backpackConfiguration);

                backpackConfisgMap.put(backpackName, backpackConfig);
            }
        }
        catch (IOException | InvalidConfigurationException e)
        {
            e.printStackTrace();
        }


        // Missing backpacks configs
        if (backpackConfisgMap.isEmpty())
        {
            Main.errLog((String) MessagesManager.getMessage(Data.Message.MISSING_BACKPACKS));
        }

        backpackConfigs = backpackConfisgMap;
    }

    public static List<String> getMultipleBackpacksNames()
    {
        List<String> names = new ArrayList<>();
        for (Map.Entry<String, BackpackConfig> entry : backpackConfigs.entrySet())
        {
            names.add(entry.getValue().NAME);
        }

        return names;
    }

    private static BackpackConfig getBackpackConfig(String fileName,
                                                    YamlConfiguration yml)
    {
        BackpackConfig backpackConfig = new BackpackConfig();
        ConfigurationSection settings = yml.getConfigurationSection("settings");


        // Settings
        backpackConfig.NAME = fileName;
        backpackConfig.BACKPACK_SIZE = settings.getInt("backpack-size");
        backpackConfig.COST = settings.getInt("cost");
        backpackConfig.GIVE_AS_ITEM = settings.getBoolean("give-as-item");
        backpackConfig.FLAGS = settings.getStringList("flags");

        // Style item
        backpackConfig.STYLE_ITEM = GUIConfigManager.getItem(
                GUIConfigManager.getSectionData(yml, "style-item"));

        // Style gui
        backpackConfig.STYLE_GUI = GUIConfigManager.getItem(
                GUIConfigManager.getSectionData(yml, "style-gui"));

        return backpackConfig;
    }
}
