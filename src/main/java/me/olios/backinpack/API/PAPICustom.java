package me.olios.backinpack.API;

import me.olios.backinpack.Data;
import me.olios.backinpack.Managers.*;
import me.olios.backinpack.Objects.BackpackConfig;
import me.olios.backinpack.Objects.BackpackContentObject;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class PAPICustom {

    public static Map<String, Object> getBackpackPlaceholders(String uuid, String id)
    {
        Map<String, Object> placeholders = new HashMap<>();

        // Backpack
        BackpackContentObject backpack = BackpacksManager.getInventoryByBackpackID(uuid, id);

        // If backpack not exists return empty map
        if (backpack == null) return placeholders;

        // Add placeholders
        placeholders.put("%backinpack_backpack_name%", backpack.name);

        int backpackSize = backpack.size;
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
        if (offlinePlayer != null && offlinePlayer.hasPlayedBefore() && offlinePlayer.isOnline())
        {
            Player player = offlinePlayer.getPlayer();
            if (PermissionsManager.checkBackpackPermission(player) != -1)
                backpackSize = PermissionsManager.checkBackpackPermission(player);
        }

        placeholders.put("%backinpack_backpack_size%", backpackSize);

        AtomicInteger items = new AtomicInteger();
        backpack.items.forEach(item ->
        {
            if (item != null) items.getAndIncrement();
        });
        placeholders.put("%backinpack_backpack_items_count%", items.get());

        return placeholders;
    }

    public static Map<String, Object> getBackpackPlaceholders(String uuid,
                                                              BackpackContentObject backpack)
    {
        return getBackpackPlaceholders(uuid, backpack.id);
    }

    public static SortedMap<String, Object> getStaticPlaceholders()
    {
        SortedMap<String, Object> placeholders = new TreeMap<>();

        // Plugin
        placeholders.put("%backinpack_config_check_updates%", ConfigManager.config.CHECK_UPDATES);
        placeholders.put("%backinpack_config_language%", MessagesManager.languageCode);
        placeholders.put("%backinpack_config_clearer_logs%", ConfigManager.config.CLEARER_LOGS);
        placeholders.put("%backinpack_config_backup%", ConfigManager.config.BACKUP);
        placeholders.put("%backinpack_config_database%", ConfigManager.config.DATABASE);
        placeholders.put("%backinpack_config_database-type%", ConfigManager.config.DATABASE_TYPE);

        // Configuration
        placeholders.put("%backinpack_config_standard_backpack_size%", ConfigManager.config.STANDARD_BACKPACK_SIZE);
        placeholders.put("%backinpack_config_max_backpacks%", ConfigManager.config.MAX_BACKPACKS);
        placeholders.put("%backinpack_config_default_backpack%", ConfigManager.config.DEFAULT_BACKPACK);

        placeholders.put("%backinpack_config_backpack_access_gui%", ConfigManager.config.BACKPACK_ACCESS_GUI);
        placeholders.put("%backinpack_config_backpack_access_item%", ConfigManager.config.BACKPACK_ACCESS_ITEM);

        placeholders.put("%backinpack_config_backpack_permission%", ConfigManager.config.BACKPACK_PERMISSION);
        placeholders.put("%backinpack_config_player_dead%", ConfigManager.config.PLAYER_DEAD);

        placeholders.put("%backinpack_config_create_backpack_crafting%", ConfigManager.config.CREATE_BACKPACK_CRAFTING);
        placeholders.put("%backinpack_config_create_backpack_buy%", ConfigManager.config.CREATE_BACKPACK_BUY);

        placeholders.put("%backinpack_config_default-backpack-name%", ConfigManager.config.DEFAULT_BACKPACK_NAME);

        placeholders.put("%backinpack_config_crafting_enable%", ConfigManager.config.CRAFTING_ENABLE);
        placeholders.put("%backinpack_config_crafting_1%", ConfigManager.config.CRAFTING_1);
        placeholders.put("%backinpack_config_crafting_2%", ConfigManager.config.CRAFTING_2);
        placeholders.put("%backinpack_config_crafting_3%", ConfigManager.config.CRAFTING_3);
        placeholders.put("%backinpack_config_crafting_4%", ConfigManager.config.CRAFTING_4);
        placeholders.put("%backinpack_config_crafting_5%", ConfigManager.config.CRAFTING_5);
        placeholders.put("%backinpack_config_crafting_6%", ConfigManager.config.CRAFTING_6);
        placeholders.put("%backinpack_config_crafting_7%", ConfigManager.config.CRAFTING_7);
        placeholders.put("%backinpack_config_crafting_8%", ConfigManager.config.CRAFTING_8);
        placeholders.put("%backinpack_config_crafting_9%", ConfigManager.config.CRAFTING_9);

        for (Map.Entry<String, BackpackConfig> entry : BackpacksConfigManager.backpackConfigs.entrySet())
        {
            placeholders.put("%backinpack_config_backpack-config_" + entry.getKey() + "_backpack-size%", entry.getValue().BACKPACK_SIZE);

            placeholders.put("%backinpack_config_backpack-config_" + entry.getKey() + "_cost%", entry.getValue().COST);

            boolean giveAsItem = entry.getValue().GIVE_AS_ITEM;
            String value = "";

            if (giveAsItem) value = (String) MessagesManager.getRawMessage(Data.Message.TRUE);
            else value = (String) MessagesManager.getRawMessage(Data.Message.FALSE);

            placeholders.put("%backinpack_config_backpack-config_" + entry.getKey() + "_give-as-item%", value);

            // Flags
            placeholders.put("%backinpack_config_backpack-config_" + entry.getKey() + "_flags%", MessagesManager.getFlagsMessage(entry.getValue().FLAGS));
        }

        placeholders.put("%backinpack_config_removing-backpacks_enable%", ConfigManager.config.REMOVING_BACKPACKS_ENABLE);
        placeholders.put("%backinpack_config_removing-backpacks_refund%", ConfigManager.config.REMOVING_BACKPACKS_REFUND);

        if (ConfigManager.config.BLACKLIST_ITEMS != null)
        {
            AtomicReference<String> blackList = new AtomicReference<>("");
            ConfigManager.config.BLACKLIST_ITEMS.forEach(str ->
            {
                String s = str.toLowerCase().replace("_", " ");
                s = WordUtils.capitalize(s);
                blackList.set(blackList.get() + s + ", ");
            });
            if (blackList.get().length() > 0) blackList.set(blackList.get().substring(0, 2));

            placeholders.put("%backinpack_config_blacklist_items%", blackList.get());
        }

        if (ConfigManager.config.DISABLED_COMMANDS != null)
        {
            AtomicReference<String> disabledCommands = new AtomicReference<>("");
            ConfigManager.config.DISABLED_COMMANDS.forEach(str ->
            {
                String s = str.toLowerCase();
                disabledCommands.set(disabledCommands.get() + s + ", ");
            });
            if (disabledCommands.get().length() > 0) disabledCommands.set(disabledCommands.get().substring(0, 2));

            // Commands
            placeholders.put("%backinpack_config_disabled_commands%", disabledCommands);
        }


        // Admin
        placeholders.put("%backinpack_config_admin_open_player_backpack%", ConfigManager.config.ADMIN_OPEN_PLAYER_BACKPACK);

        // Effects
        placeholders.put("%backinpack_config_open_sound%", ConfigManager.config.OPEN_SOUND_SOUND);
        placeholders.put("%backinpack_config_close_sound%", ConfigManager.config.CLOSE_SOUND_SOUND);
        placeholders.put("%backinpack_config_buy-backpack-sound%", ConfigManager.config.BUY_BACKPACK_SOUND_SOUND);
        placeholders.put("%backinpack_config_no-enough-money-sound%", ConfigManager.config.NO_ENOUGH_MONEY_SOUND_SOUND);
        placeholders.put("%backinpack_config_remove-backpack-sound%", ConfigManager.config.REMOVE_BACKPACK_SOUND_SOUND);

        // Other
        placeholders.put("%backinpack_update-link%", Data.resourceURL);
        placeholders.put("%backinpack_version%", Data.pluginVersion);
        placeholders.put("%backinpack_can_update%", Data.canUpdate);
        placeholders.put("%backinpack_players%", BackpacksManager.inventory.size());

        return placeholders;
    }


}
