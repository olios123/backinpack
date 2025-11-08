package me.olios.backinpack.Managers;

import me.olios.backinpack.Data;
import me.olios.backinpack.Library.Numeric;
import me.olios.backinpack.Library.Replace.StringReplace;
import me.olios.backinpack.Main;
import me.olios.backinpack.Objects.Cache;
import me.olios.backinpack.Objects.Config;
import me.olios.backinpack.Recipes.Backpack;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class ConfigManager {

    // Config object
    public static Config config = new Config();

    private static final YamlConfiguration cnf = FilesManager.getConfigYml();

    public static void loadConfig()
    {
        // General
        config.CHECK_UPDATES = cnf.getBoolean("check-updates");
        config.LANGUAGE = cnf.getString("language");
        config.CLEARER_LOGS = cnf.getBoolean("clearer-logs");
        config.BACKUP = cnf.getBoolean("backup");
        config.DATABASE = cnf.getBoolean("database");
        config.DATABASE_TYPE = cnf.getString("database-type");

        // Backpack
        config.MAX_BACKPACKS = cnf.getInt("max-backpacks");
        config.DEFAULT_BACKPACK = cnf.getString("default-backpack");
        config.DEFAULT_BACKPACK_GIVE_AS_ITEM_ENABLE = cnf.getBoolean("default-backpack-give-as-item.enable");
        config.DEFAULT_BACKPACK_GIVE_AS_ITEM_SLOT = cnf.getInt("default-backpack-give-as-item.slot");

        config.BACKPACK_ACCESS_GUI = cnf.getBoolean("backpack-access.gui");
        config.BACKPACK_ACCESS_ITEM = cnf.getBoolean("backpack-access.item");
        config.BACKPACK_PERMISSION = cnf.getBoolean("backpack-permission");
        config.PLAYER_DEAD = cnf.getString("player-dead");
        config.CREATE_BACKPACK_CRAFTING = cnf.getBoolean("create-backpack.crafting");
        config.CREATE_BACKPACK_BUY = cnf.getBoolean("create-backpack.buy");
        config.DEFAULT_BACKPACK_NAME = cnf.getString("default-backpack-name");
        config.CREATED_BACKPACK = cnf.getString("created-backpack");
        config.OTHER_PLAYERS_OPEN_BACKPACK = cnf.getBoolean("other-players-open-backpack");

        // Crafting
        config.CRAFTING_ENABLE = cnf.getBoolean("crafting.enable");
        config.CRAFTING_1 = Material.getMaterial(cnf.getString("crafting.1"));
        config.CRAFTING_2 = Material.getMaterial(cnf.getString("crafting.2"));
        config.CRAFTING_3 = Material.getMaterial(cnf.getString("crafting.3"));
        config.CRAFTING_4 = Material.getMaterial(cnf.getString("crafting.4"));
        config.CRAFTING_5 = Material.getMaterial(cnf.getString("crafting.5"));
        config.CRAFTING_6 = Material.getMaterial(cnf.getString("crafting.6"));
        config.CRAFTING_7 = Material.getMaterial(cnf.getString("crafting.7"));
        config.CRAFTING_8 = Material.getMaterial(cnf.getString("crafting.8"));
        config.CRAFTING_9 = Material.getMaterial(cnf.getString("crafting.9"));

        // Removing backpack
        config.REMOVING_BACKPACKS_ENABLE = cnf.getBoolean("removing-backpacks.enable");
        config.REMOVING_BACKPACKS_REFUND = cnf.getInt("removing-backpacks.refund");

        // Blacklist
        config.BLACKLIST_ITEMS = cnf.getStringList("blacklist-items");

        // Commands
        config.DISABLED_COMMANDS = cnf.getStringList("disabled-commands");

        // Admin
        config.ADMIN_OPEN_PLAYER_BACKPACK = cnf.getBoolean("admin-open-player-backpack");

        // Sounds
        // open-sound
        config.OPEN_SOUND_ENABLE = cnf.getBoolean("open-sound.enable");
        config.OPEN_SOUND_SOUND = cnf.getString("open-sound.sound");

        // close-sound
        config.CLOSE_SOUND_ENABLE = cnf.getBoolean("close-sound.enable");
        config.CLOSE_SOUND_SOUND = cnf.getString("close-sound.sound");

        // buy-backpack-sound
        config.BUY_BACKPACK_SOUND_ENABLE = cnf.getBoolean("buy-backpack-sound.enable");
        config.BUY_BACKPACK_SOUND_SOUND = cnf.getString("buy-backpack-sound.sound");

        // no-enough-money-sound
        config.NO_ENOUGH_MONEY_SOUND_ENABLE = cnf.getBoolean("no-enough-money-sound.enable");
        config.NO_ENOUGH_MONEY_SOUND_SOUND = cnf.getString("no-enough-money-sound.sound");

        // remove-backpack-sound
        config.REMOVE_BACKPACK_SOUND_ENABLE = cnf.getBoolean("remove-backpack-sound.enable");
        config.REMOVE_BACKPACK_SOUND_SOUND = cnf.getString("remove-backpack-sound.sound");
    }

    public static void reload()
    {
        // Save all backpacks
        BackpacksManager.saveBackpacks();

        // Remove old recipe
        Bukkit.removeRecipe(new NamespacedKey(Data.plugin, "backpack"));

        // Create files
        FilesManager.manageFiles();

        // Read files
        FilesManager.loadYmlFiles();

        // Change language
        MessagesManager.setLanguageFile();

        // Update files
//        ConfigManager.versionUpdater();

//         Load config
//        ConfigManager.readLogic();

        // Load backpacks
        BackpacksManager.loadBackpacks();

        // Add backpack crafting recipe if config allows to create backpacks through crafting
        if (ConfigManager.config.CREATE_BACKPACK_CRAFTING) Bukkit.addRecipe(Backpack.getRecipe());

        // Clear all cache data
        CacheManager.cache = new Cache();
//        GUIManager.openedBackpacks.clear();
//        GUIManager.openedBackpackList.clear();
    }


    private static int getConfigLine(String key)
    {
        File config = FilesManager.configFile;

        try
        {
            FileInputStream inputStream = new FileInputStream(config);
            Yaml yaml = new Yaml();
            Map<String, Object> configMap = yaml.load(inputStream);

            String finalKey = key.split("\\.")[0];

            if (configMap.containsKey(finalKey))
            {
                int lineNumber = findLineNumber(config, finalKey);
                return lineNumber;
            } else return -1;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return -1;
    }

    private static int findLineNumber(File file, String targetVariable)
    {
        try
        {
            FileInputStream inputStream = new FileInputStream(file);
            byte[] buffer = new byte[(int) file.length()];
            inputStream.read(buffer);
            inputStream.close();

            String fileContent = new String(buffer, "UTF-8");
            String[] lines = fileContent.split("\n");

            for (int i = 0; i < lines.length; i++)
            {
                if (lines[i].contains(targetVariable))
                {
                    return i + 1;
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }


        return -1; // Not found
    }

    public static void configWarning(String value,
                                     ConfigIncorrect error,
                                     Map<String, Object> ... placeholders)
    {
        Main.warnLog("Incorrect value in config!");

        String reason = error.getReason();
        if (placeholders.length > 0) reason = StringReplace.string(reason, placeholders[0]);

        Main.warnLog(value + ": [" + getConfigLine(value) + "-config.yml] " + reason);
    }

    private static boolean checkBoolean(String str)
    {
        if (str.equals("true") || str.equals("false")) return true;
        else return false;
    }

    private static int checkInteger(String str, int min, int max)
    {
        if (!Numeric.check(str))
        {
            return -1;
        }

        int number = Integer.parseInt(str);

        if (number < min || number > max)
        {
            return 0;
        }

        return 1;
    }

    private enum ConfigIncorrect
    {
        BOOLEAN("A value other than \"true\" or \"false\""),
        NOT_NUMBER("Value is not a number!"),
        NUMBER_RANGE("The given number cannot be less than %min% and greater than %max%"),
        OPTION("Out of range option selected");

        private String reason;

        ConfigIncorrect(String str)
        {
            this.reason = str;
        }

        public String getReason()
        {
            return reason;
        }
    }




}
