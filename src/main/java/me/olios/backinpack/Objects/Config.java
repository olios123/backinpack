package me.olios.backinpack.Objects;

import me.olios.backinpack.Data;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Config {

    // General
    public boolean CHECK_UPDATES;
    public String LANGUAGE;
    public boolean CLEARER_LOGS;
    public boolean BACKUP;
    public boolean DATABASE;
    public String DATABASE_TYPE;

    // Backpack
    public int STANDARD_BACKPACK_SIZE;
    public int MAX_BACKPACKS;
    public String DEFAULT_BACKPACK;
    public boolean DEFAULT_BACKPACK_GIVE_AS_ITEM_ENABLE;
    public int DEFAULT_BACKPACK_GIVE_AS_ITEM_SLOT;
    public boolean BACKPACK_ACCESS_GUI;
    public boolean BACKPACK_ACCESS_ITEM;
    public boolean BACKPACK_PERMISSION;
    public String PLAYER_DEAD;
    public boolean CREATE_BACKPACK_CRAFTING;
    public boolean CREATE_BACKPACK_BUY;
    public String DEFAULT_BACKPACK_NAME;
    public String CREATED_BACKPACK;
    public boolean OTHER_PLAYERS_OPEN_BACKPACK;

    // Crafting
    public boolean CRAFTING_ENABLE;
    public Material CRAFTING_1;
    public Material CRAFTING_2;
    public Material CRAFTING_3;
    public Material CRAFTING_4;
    public Material CRAFTING_5;
    public Material CRAFTING_6;
    public Material CRAFTING_7;
    public Material CRAFTING_8;
    public Material CRAFTING_9;

    // Removing backpack
    public boolean REMOVING_BACKPACKS_ENABLE;
    public int REMOVING_BACKPACKS_REFUND;

    // Blacklist
    public List<String> BLACKLIST_ITEMS;

    // Commands
    public List<String> DISABLED_COMMANDS;

    // Admin
    public boolean ADMIN_OPEN_PLAYER_BACKPACK;

    // Sounds
    // open-sound
    public boolean OPEN_SOUND_ENABLE;
    public String OPEN_SOUND_SOUND;
    // close-sound
    public boolean CLOSE_SOUND_ENABLE;
    public String CLOSE_SOUND_SOUND;
    // buy-backpack-sound
    public boolean BUY_BACKPACK_SOUND_ENABLE;
    public String BUY_BACKPACK_SOUND_SOUND;
    // no-enough-money-sound
    public boolean NO_ENOUGH_MONEY_SOUND_ENABLE;
    public String NO_ENOUGH_MONEY_SOUND_SOUND;
    // remove-backpack-sound
    public boolean REMOVE_BACKPACK_SOUND_ENABLE;
    public String REMOVE_BACKPACK_SOUND_SOUND;

}
