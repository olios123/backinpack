package me.olios.backinpack;

import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Data {

    public static Plugin plugin;
    public static int resourceId = 102384;
    public static String resourceURL = "https://www.spigotmc.org/resources/backinpack.102384/";
    public static String resourceDocs = "https://www.spigotmc.org/resources/backinpack.102384/field?field=documentation";
    public static String version = "4.0-BETA";
    public static String pluginVersion = "4.0-BETA";
    public static String discord = "https://discord.gg/kE6eptDu3W";
    public static boolean canUpdate = false;
    public static String SSID;

    // Paths
    public static String REPORT_URL = "http://localhost/backinpack/error.php";
    public static String pluginPath = "plugins/BackInPack/";
    public static String backupPath = pluginPath + "backup/";
    public static String languagesPath = pluginPath + "languages/";
    public static String dataPath = pluginPath + "data/";
    public static String backpacksPath = pluginPath + "backpacks/";
    public static String guiPath = pluginPath + "GUI/";

    // Additional plugins
    public static boolean PAPI = false;
    public static boolean VAULT = false;

    public static String SSID()
    {
        return UUID.randomUUID().toString();
    }

    public enum Message
    {
        // Plugin
        ENABLE_PLUGIN,
        DISABLE_PLUGIN,
        LOADED_LANGUAGE_FILE,
        LANGUAGE_FILE_NOT_FOUND,
        LOADING_SINGLE_BACKPACK,
        RELOAD_COMPLETE,
        MISSING_BACKPACKS,

        // Database
        DATABASE_NOT_CONNECTED,
        DATABASE_SYNCHRONIZED,

        // Player
        PLAYER_DEATH_DELETE,
        PLAYER_DEATH_DROP,
        PLAYER_DEATH_SAVE,
        PLAYER_BACKPACK_CHANGE_SIZE,
        PLAYER_BACKPACK_ACCESS_THROUGH_COMMAND_DISABLED,
        PLAYER_BACKPACK_ACCESS_THROUGH_ITEM_DISABLED,
        PLAYER_BACKPACK_NAME_CHANGED,
        PLAYER_BACKPACK_NOT_OWNER,
        PLAYER_BACKPACK_CHANGE_NAME,
        PLAYER_BACKPACK_BUY,
        PLAYER_MAX_BACKPACKS,
        PLAYER_BACKPACK_NOT_EXISTS,
        PLAYER_BACKPACK_BLACKLIST,
        PLAYER_BACKPACK_REMOVE,
        PLAYER_BACKPACK_REFUND,
        CMD_ABOUT,

        // Permissions
        NO_PERMISSION,
        NO_PERMISSION_BACKPACK_ACCESS,
        CONSOLE_COMMAND,

        // Admin
        PLAYER_NOT_FOUND,
        PLAYER_BACKPACK_USERINFO_OFFLINE,
        PLAYER_BACKPACK_SIZE_CHANGED_BLOCKED_SPACES,
        PLAYER_HAS_MAX_BACKPACKS,
        PLAYER_SET_BACKPACK_SIZE,
        PLAYER_SET_PLAYER_ALL_BACKPACK_SIZE,
        PLAYER_SET_ALL_BACKPACK_SIZE,
        PLAYER_RELOAD_COMPLETE,
        PLAYER_DATABASE_NOT_CONNECTED,
        PLAYER_DATABASE_SYNCHRONIZED,
        PLAYER_BACKPACK_ADD,
        PLAYER_BACKPACK_GIVE_OFFLINE,
        PLAYER_USERINFO,

        // Errors
        ARGUMENT_NOT_NUMBER,
        ARGUMENT_INVALID_NUMBER,
        ARGUMENT_MISSING,
        ARGUMENT_PLAYER_MISSING,
        ARGUMENT_BACKPACK_NOT_FOUND,
        ARGUMENT_BACKPACK_NAME_NOT_GIVEN,
        ARGUMENT_NEED_CHANGE_FOR_ALL_PLAYERS,
        ECONOMY_NOT_ENOUGH_MONEY,
        COMMAND_DISABLED,

        // Other
        TRUE,
        FALSE,

        // Flags
        FLAG_NONE,
        // Negative
        FLAG_ITEMS_DROP,
        FLAG_BACKPACK_DELETE,
        // Positive
        FLAG_ITEMS_SAVE;
    }

    public enum BackpackFlags
    {
        // Items
        ITEMS_DROP,
        ITEMS_SAVE,

        // Backpack
        BACKPACK_DELETE;
    }

    public static List<BackpackFlags> getFlags(List<String> rawFlags)
    {
        List<BackpackFlags> returnFlags = new ArrayList<>();
        for (String tempFlag : rawFlags)
        {
            try
            {
                returnFlags.add(BackpackFlags.valueOf(tempFlag));
            }
            catch (IllegalArgumentException e) {}
        }
        return returnFlags;
    }

    public enum Permission
    {
        ADMIN,
        USERINFO,
        SETSIZE,
        RELOAD,
        GIVE,
        BACKPACK_SAVE,
        OPEN,
        SYNC,
        BACKPACK_ACCESS;
    }

    public enum Sound
    {
        OPEN,
        CLOSE,
        BUY,
        NO_MONEY,
        REMOVE;
    }

}
