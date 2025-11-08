package me.olios.backinpack.API;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.olios.backinpack.Data;
import me.olios.backinpack.Managers.BackpacksManager;
import me.olios.backinpack.Managers.ConfigManager;
import me.olios.backinpack.Managers.MessagesManager;
import me.olios.backinpack.Objects.BackpackContentObject;
import me.olios.backinpack.Objects.BackpackObject;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

public class PAPIExpansion extends PlaceholderExpansion {

    @Override
    public String getAuthor()
    {
        return "olios";
    }

    @Override
    public String getIdentifier()
    {
        return "BackInPack";
    }

    @Override
    public String getVersion()
    {
        return Data.pluginVersion;
    }

    @Override
    public boolean canRegister()
    {
        return true;
    }

    @Override
    public boolean persist()
    {
        return true;
    }

    @Override // Player is offline
    public String onRequest(OfflinePlayer offlinePlayer, String params)
    {
        String convertedParams = params.toLowerCase();

        // Data from player
        String uuid = offlinePlayer.getUniqueId().toString();
        BackpackObject backpack = BackpacksManager.getInventory(uuid);

        switch (convertedParams)
        {
            case "player_uuid":
                return offlinePlayer.getUniqueId().toString();

            case "player_nickname":
            case "player_username":
            case "player_name":
                return offlinePlayer.getName();

            case "player_backpacks_count":
            case "player_backpacks_size":
                return String.valueOf(backpack.backpacks.size());

            case "player_backpacks_list":
                String list = "";

                for (BackpackContentObject b : backpack.backpacks)
                {
                    int itemsSize = 0;
                    for (ItemStack item : b.items)
                    {
                        if (item != null) itemsSize++;
                    }

                    list += "- " + b.name + " (" + itemsSize + "/" + b.size + ")\n";
                }

                return list;

            // If placeholder was not found
            default:
                return null;
        }
    }
}
