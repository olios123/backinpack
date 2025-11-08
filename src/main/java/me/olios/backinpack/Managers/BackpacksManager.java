//
// CC Creative Commons 2022
// Attribution-NoDerivatives 4.0 International
// Author olios
//

package me.olios.backinpack.Managers;

import me.olios.backinpack.API.PAPICustom;
import me.olios.backinpack.Data;
import me.olios.backinpack.Library.*;
import me.olios.backinpack.Library.Replace.ItemReplace;
import me.olios.backinpack.Main;
import me.olios.backinpack.MySQL;
import me.olios.backinpack.Objects.*;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class BackpacksManager {

	public static Map<String, BackpackObject> inventory = new HashMap<>();
	//                 UUID     backpacks

	public static void loadBackpacks()
	{
		YamlConfiguration inventories = FilesManager.getInventoriesYml();

		MessagesManager.sendLogMessage(Data.Message.LOADING_SINGLE_BACKPACK);

		if (inventories.get("data") == null || inventories.getConfigurationSection("data") == null) return;

		inventories.getConfigurationSection("data").getKeys(false).forEach(uuid ->
		{
			BackpackObject backpackObject = new BackpackObject();
			backpackObject.uuid = uuid;
			backpackObject.backpacks = new ArrayList<>();

			ConfigurationSection backpacks = inventories.getConfigurationSection("data." + uuid + ".backpacks");

			if (backpacks != null)
			{
				backpacks.getKeys(false).forEach(id ->
				{
					String name = backpacks.getString(id + ".name");
					String type = backpacks.getString(id + ".type");
					int size = backpacks.getInt(id + ".size");
					List<ItemStack> items = (List<ItemStack>) backpacks.get(id + ".items");
					boolean crafted = (boolean) backpacks.get(id + ".crafted");
					List<String> flags = backpacks.getStringList(id + ".flags");

					BackpackContentObject backpackContentObject = new BackpackContentObject();
					backpackContentObject.id = id;
					backpackContentObject.name = name;
					backpackContentObject.type = type;
					backpackContentObject.size = size;
					backpackContentObject.items = items;
					backpackContentObject.crafted = crafted;
					backpackContentObject.flags = flags;

					backpackObject.backpacks.add(backpackContentObject);
				});
			}

			inventory.put(uuid, backpackObject);
		});
	}

	public static void saveBackpacks()
	{
		if (MySQL.isConnected)
		{
			updateMySQLBackpacks();
			if (ConfigManager.config.DATABASE_TYPE.equals("sync")) return;
		}

		YamlConfiguration inventories = FilesManager.getInventoriesYml();

		if (inventory.isEmpty()) return;
		for (Map.Entry<String, BackpackObject> entry : inventory.entrySet())
		{
			String uuid = entry.getKey();
			BackpackObject backpack = entry.getValue();

			inventories.set("data." + uuid + ".backpacks", new ArrayList<>());

			if (!backpack.backpacks.isEmpty())
			{
				backpack.backpacks.forEach(backpackContent ->
				{
					String id = backpackContent.id;
					String name = ChatColor.stripColor(backpackContent.name);
					String type = backpackContent.type;
					int size = backpackContent.size;
					List<ItemStack> items = backpackContent.items;
					boolean crafted = backpackContent.crafted;
					List<String> flags = backpackContent.flags;

					inventories.set("data." + uuid + ".backpacks." + id + ".name", name);
					inventories.set("data." + uuid + ".backpacks." + id + ".type", type);
					inventories.set("data." + uuid + ".backpacks." + id + ".size", size);
					inventories.set("data." + uuid + ".backpacks." + id + ".items", items);
					inventories.set("data." + uuid + ".backpacks." + id + ".crafted", crafted);
					inventories.set("data." + uuid + ".backpacks." + id + ".flags", flags);
				});
			}
		}

		try
		{
			inventories.save(FilesManager.inventoriesFile);
		}
		catch (IOException e)
		{
			Main.errLog("An error occured: " + e.getMessage());
		}
	}

	public static BackpackObject getInventory(String uuid)
	{
		if (MySQL.isConnected && ConfigManager.config.DATABASE_TYPE.equals("sync"))
		{
			readBackpacksFromMySQL();
		}

		return inventory.get(uuid);
	}

	public static BackpackContentObject getInventoryByBackpackID(String uuid, String id)
	{
		if (MySQL.isConnected && ConfigManager.config.DATABASE_TYPE.equals("sync"))
		{
			readBackpacksFromMySQL();
		}

		BackpackObject backpackObject = getInventory(uuid);
		AtomicReference<BackpackContentObject> ret = new AtomicReference<>();

		if (backpackObject == null) return null;
		backpackObject.backpacks.forEach(backpack ->
		{
			if (backpack.id.equals(id)) ret.set(backpack);
		});

		return ret.get();
	}

	public static String createBackpack(String uuid, boolean crafted, BackpackConfig config)
	{
		boolean newBackpack;
		BackpackObject backpackObject;

		if (config.NAME == null || config.NAME.equals("none") ||
				BackpacksConfigManager.backpackConfigs.get(config.NAME) == null) return null;

		if (inventory.containsKey(uuid))
		{
			backpackObject = getInventory(uuid);
			newBackpack = false;
		}
		else
		{
			backpackObject = new BackpackObject();
			backpackObject.backpacks = new ArrayList<>();
			backpackObject.uuid = uuid;
			newBackpack = true;
		}

		// Create backpack
		BackpackContentObject backpackContentObject = new BackpackContentObject();
		backpackContentObject.name = ChatColor.stripColor(ConfigManager.config.DEFAULT_BACKPACK_NAME);
		backpackContentObject.type = config.NAME;
		backpackContentObject.id = UUID.randomUUID().toString();
		backpackContentObject.items = new ArrayList<>();
		backpackContentObject.size = config.BACKPACK_SIZE;
		backpackContentObject.crafted = crafted;
		backpackContentObject.flags = config.FLAGS;

		backpackObject.backpacks.add(backpackContentObject);

		// Save backpack
		if (newBackpack) inventory.put(uuid, backpackObject);
		else inventory.replace(uuid, backpackObject);

		saveBackpacks();

		return backpackContentObject.id;
	}

	public static void replaceBackpackInInventory(BackpackObject inv, BackpackContentObject backpack)
	{
		if (MySQL.isConnected && ConfigManager.config.DATABASE_TYPE.equals("sync"))
		{
			readBackpacksFromMySQL();
		}

		if (inv == null) return;

		for (int i = 0; i < inv.backpacks.size(); i++)
		{
			BackpackContentObject bckp = inv.backpacks.get(i);

			if (bckp.id.equals(backpack.id))
			{
				inv.backpacks.set(i, backpack);
			}
		}

		inventory.replace(inv.uuid, inv);

		saveBackpacks();
	}

	public static boolean removeBackpack(BackpackObject inv,
									  BackpackContentObject backpack)
	{
		boolean removed = inv.backpacks.remove(backpack);

		inventory.replace(inv.uuid, inv);
		saveBackpacks();

		String backpackID = backpack.id;

		// Replace all backpacks to unassigned in all players eq
		if (!Bukkit.getServer().getOnlinePlayers().isEmpty())
		{
			for (Player p : Bukkit.getServer().getOnlinePlayers())
			{
				if (p.getInventory().isEmpty()) continue;

				for (int i = 0; i <= p.getInventory().getSize(); i++)
				{
					ItemStack item = p.getInventory().getItem(i);
					if (item == null) continue;

					// Check if item is backpack
					if (isBackpack(item))
					{
						// Get backpack ID
						String id = NBTTags.getNBT(item, "backinpack.backpack-id");

						// If this backpack item is removed backpack
						if (backpackID.equals(id))
						{
							// Now an owner of unassigned backpack is player that has it in inventory
							ItemStack unassignedBackpack = createUnassignedBackpackItem();

							p.getInventory().setItem(i, unassignedBackpack);
						}
					}
				}
			}
		}

		return removed;
	}

	public static void createFirstBackpack(String uuid)
	{
		if (ConfigManager.config.DEFAULT_BACKPACK == null ||
			ConfigManager.config.DEFAULT_BACKPACK.equals("none") ||
			BackpacksConfigManager.backpackConfigs.get(ConfigManager.config.DEFAULT_BACKPACK) == null) return;

		BackpackObject inventory = BackpacksManager.getInventory(uuid);

		if (inventory == null)
		{
			BackpackConfig backpackConfig = BackpacksConfigManager.backpackConfigs.get(ConfigManager.config.DEFAULT_BACKPACK);

			String backpackID = createBackpack(uuid, false, backpackConfig);

			Player p = Bukkit.getPlayer(UUID.fromString(uuid));

			// Add backpack as item
			if (ConfigManager.config.DEFAULT_BACKPACK_GIVE_AS_ITEM_ENABLE && p != null)
			{
				// Create backpack item
				ItemStack backpackItem = BackpacksManager.createBackpackItem(uuid,
						backpackID, backpackConfig.STYLE_ITEM);

				if (p.getInventory().getItem(ConfigManager.config.DEFAULT_BACKPACK_GIVE_AS_ITEM_SLOT) != null)
				{
					// Give to first empty slot
					if (p.getInventory().firstEmpty() != -1)
					{
						p.getInventory().addItem(backpackItem);
					}
				}
				// Give to selected slot
				else
				{
					p.getInventory().setItem(ConfigManager.config.DEFAULT_BACKPACK_GIVE_AS_ITEM_SLOT, backpackItem);
				}
			}
		}
	}

	public static void buyBackpack(Player p, BackpackConfig backpackConfig)
	{
		double backpackCost = backpackConfig.COST;

		String uuid = p.getUniqueId().toString();

		BackpackObject inventory = BackpacksManager.getInventory(uuid);

		if (EconomyManager.getBalance(uuid) < backpackCost)
		{
			MessagesManager.sendMessage(p, Data.Message.ECONOMY_NOT_ENOUGH_MONEY);
			p.closeInventory();

			SoundsManager.playSound(p, Data.Sound.NO_MONEY);
			return;
		}

		if (inventory != null && inventory.backpacks.size() >= ConfigManager.config.MAX_BACKPACKS)
		{
			MessagesManager.sendMessage(p, Data.Message.PLAYER_MAX_BACKPACKS);
			p.closeInventory();
			return;
		}

		// Remove money from bank
		EconomyManager.withdraw(uuid, backpackCost);

		// Successfully created new backpack
		String newBackpackID = BackpacksManager.createBackpack(uuid, false, backpackConfig);

		// Send message
		MessagesManager.sendMessage(p, Data.Message.PLAYER_BACKPACK_BUY);

		// Sound
		SoundsManager.playSound(p, Data.Sound.BUY);

		// Add as item if can
		if (backpackConfig.GIVE_AS_ITEM)
		{
			// Create item
			ItemStack backpackItem = BackpacksManager.createBackpackItem(uuid,
					newBackpackID, backpackConfig.STYLE_ITEM);

			// Inventory is full
			if (p.getInventory().firstEmpty() == -1)
			{
				p.getWorld().dropItemNaturally(p.getLocation(), backpackItem);
			}
			else p.getInventory().addItem(backpackItem);
		}

		p.closeInventory();
	}

	public static ItemStack createBackpackItem(String uuid,
											   String id,
											   Map<String, Object> backpackData)
	{
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));

		// Add backpack placeholders
		Map<String, Object> placeholders = PAPICustom.getBackpackPlaceholders(uuid, id);

		// Get item
		ItemStack backpack = ItemReplace.item(
				GUIConfigManager.getItem(backpackData,
				offlinePlayer,
				placeholders));

		// Identify as backpack
		NBTTags.addNBT(backpack, "backinpack.backpack", "1"); // 1 for assigned
		// Add information about owner of this backpack
		NBTTags.addNBT(backpack, "backinpack.backpack-owner", uuid);
		// Add backpack ID to item
		NBTTags.addNBT(backpack, "backinpack.backpack-id", id);
		// Add assignment info
		NBTTags.addNBT(backpack, "backinpack.assigned", "true");

		return backpack;
	}

	public static ItemStack createBackpackItem(String uuid,
											   String id,
											   ItemStack item)
	{
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));

		// Add backpack placeholders
		Map<String, Object> placeholders = PAPICustom.getBackpackPlaceholders(uuid, id);

		// Get item
		ItemStack backpack = ItemReplace.item(item, offlinePlayer, placeholders);

		// Identify as backpack
		NBTTags.addNBT(backpack, "backinpack.backpack", "1"); // 1 for assigned
		// Add information about owner of this backpack
		NBTTags.addNBT(backpack, "backinpack.backpack-owner", uuid);
		// Add backpack ID to item
		NBTTags.addNBT(backpack, "backinpack.backpack-id", id);
		// Add assignment info
		NBTTags.addNBT(backpack, "backinpack.assigned", "true");

		return backpack;
	}

	public static ItemStack createUnassignedBackpackItem()
	{
		Map<String, Object> unassignedBackpackItem = GUIConfigManager.getData("backpack-style-to-assignment");

		// Create item
		ItemStack unassignedBackpack = ItemReplace.item(
				GUIConfigManager.getItem(unassignedBackpackItem));

		// Identify as unassigned backpack
		NBTTags.addNBT(unassignedBackpack, "backinpack.backpack", "0"); // 0 for unassigned
//		// Add information about owner of this backpack
//		NBTTags.addNBT(unassignedBackpack, "backinpack.backpack-owner", uuid);
		// Add backpack ID to item
		NBTTags.addNBT(unassignedBackpack, "backinpack.backpack-id", "");
		// Add assignment info
		NBTTags.addNBT(unassignedBackpack, "backinpack.assigned", "false");
		// Random ID
		NBTTags.addNBT(unassignedBackpack, "backinpack.random-ID", UUID.randomUUID().toString());

		return unassignedBackpack;
	}

	public static void refreshPlaceholders(Player p)
	{
		if (MySQL.isConnected && ConfigManager.config.DATABASE_TYPE.equals("sync"))
		{
			readBackpacksFromMySQL();
		}

		List<ItemStack> contents = Arrays.stream(p.getInventory().getContents()).toList();

		int pos = 0;
		for (ItemStack item : contents)
		{
			if (item != null)
			{
				if (isBackpack(item) && isAssigned(item))
				{
					String owner = NBTTags.getNBT(item, "backinpack.backpack-owner");
					String backpackID = NBTTags.getNBT(item, "backinpack.backpack-id");

					// Player owner
					OfflinePlayer offlineOwner = Bukkit.getOfflinePlayer(UUID.fromString(owner));

					// Get backpack content from item
					BackpackContentObject backpack = BackpacksManager.getInventoryByBackpackID(offlineOwner.getUniqueId().toString(), backpackID);

					// Get backpack by type
					for (Map.Entry<String, BackpackConfig> entry : BackpacksConfigManager.backpackConfigs.entrySet())
					{
						String type = entry.getKey();
						BackpackConfig config = entry.getValue();

						// If types are equal
						if (type.equals(backpack.type))
						{
							ItemStack newBackpackItem = config.STYLE_ITEM;


							// Move old NBTTags to new item
							for (Map.Entry<String, String> entryNBT : NBTTags.getAllValues(item).entrySet())
							{
								String key = entryNBT.getKey().replace("backinpack:", "");
								NBTTags.addNBT(newBackpackItem, key, entryNBT.getValue());
							}


							// Get backpack placeholders
							Map<String, Object> backpackPlaceholders = PAPICustom.getBackpackPlaceholders(offlineOwner.getUniqueId().toString(), backpack);


							// Replace item
							if (offlineOwner.isOnline())
								newBackpackItem = ItemReplace.itemPlayer(newBackpackItem, offlineOwner.getPlayer(), backpackPlaceholders);
							else
								newBackpackItem = ItemReplace.item(newBackpackItem, offlineOwner, backpackPlaceholders);

							// Replace item in inventory
							p.getInventory().setItem(pos, newBackpackItem);

							break;
						}
					}
				}
			}
			pos++;
		}
	}

	public static void updateMySQLBackpacks()
	{
		try
		{
			for (Map.Entry<String, BackpackObject> entry : inventory.entrySet())
			{
				String uuid = entry.getKey();
				String username = Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName();
				BackpackObject inventory = entry.getValue();

				// Convert list to JSON
				String JSON = Json.toJson(inventory);

				// User exists
				if (MySQL.resultNotNull(
						MySQL.queryGet("SELECT * FROM backpacks WHERE `uuid`='" + uuid + "'")))
				{
					String query = "UPDATE `backpacks` SET `uuid`=?,`username`=?,`backpacks`=? WHERE `uuid`=?";

					PreparedStatement statement = MySQL.connection.prepareStatement(query);
					statement.setString(1, uuid);
					statement.setString(2, username);
					statement.setString(3, JSON);
					statement.setString(4, uuid);
					statement.executeUpdate();
				}
				// New user
				else
				{
					String query = "INSERT INTO `backpacks`(`uuid`, `username`, `backpacks`) VALUES (?,?,?)";
					PreparedStatement statement = MySQL.connection.prepareStatement(query);
					statement.setString(1, uuid);
					statement.setString(2, username);
					statement.setString(3, JSON);
					statement.executeUpdate();
				}
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	public static void backpacksUpdater()
	{
		new BukkitRunnable()
		{
			@Override
			public void run()
			{
				// Get all inventories
				for (Iterator<BackpackObject> iterator = inventory.values().iterator();
					 iterator.hasNext();)
				{
					BackpackObject inventory = iterator.next();

					// Get all backpacks from inventory
					for (BackpackContentObject backpack : inventory.backpacks)
					{
						String backpackID = backpack.id;

						Cache.OpenedBackpack openedBackpack = CacheManager.getOpenedBackpackID(backpackID);

						// This backpacks is not opened
						if (openedBackpack == null || openedBackpack.backpackID == null) continue;
						// Sender does not exist or owner opened his backpack
						if (openedBackpack.sender == null ||
								openedBackpack.owner.equals(openedBackpack.sender)) continue;

						// Replace items in all inventory

//						// Replace items for sender
//						Inventory senderInv = Bukkit.getPlayer(UUID.fromString(openedBackpack.sender)).getOpenInventory().getTopInventory();
//
//						for (int i = 0; i < backpack.items.size(); i++)
//						{
//							senderInv.setItem(i, backpack.items.get(i));
//						}

						// Replace items for owner
						if (Bukkit.getOfflinePlayer(UUID.fromString(openedBackpack.owner)).isOnline())
						{
							Inventory ownerInv = Bukkit.getPlayer(UUID.fromString(openedBackpack.sender)).getOpenInventory().getTopInventory();

							for (int i = 0; i < backpack.items.size(); i++)
							{
								ownerInv.setItem(i, backpack.items.get(i));
							}
						}
					}
				}

			}

		}.runTaskTimer(Data.plugin, 0, 5); // 0.25s
	}

	public static void readBackpacksFromMySQL()
	{
		try
		{
			ResultSet rs = MySQL.queryGet("SELECT * FROM backpacks");
			while (rs.next())
			{
				String uuid = rs.getString("uuid");
				String JSON = rs.getString("data");

				BackpackObject inv = Json.fromJsonInventory(JSON);
				inv.uuid = uuid;

				if (inventory.containsKey(uuid)) inventory.replace(uuid, inv);
				else inventory.put(uuid, inv);
			}
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	//
	//    ___ ___  _  _ _____ ___  ___  _    _        ___ ___ ___ _____ ___ ___  _  _
	//   / __/ _ \| \| |_   _| _ \/ _ \| |  | |      / __| __/ __|_   _|_ _/ _ \| \| |
	//  | (_| (_) | .` | | | |   / (_) | |__| |__    \__ \ _| (__  | |  | | (_) | .` |
	//   \___\___/|_|\_| |_| |_|_\\___/|____|____|   |___/___\___| |_| |___\___/|_|\_|
	//
	public static boolean isBackpack(ItemStack item)
	{
		// NBT tags that can have backpack or unassigned backpack
		List<String> allowedNTB = new ArrayList<>();
		allowedNTB.add("backinpack.backpack");
		allowedNTB.add("backinpack.backpack-id");
		allowedNTB.add("backinpack.assigned");
		allowedNTB.add("backinpack.backpack-owner");
		allowedNTB.add("backinpack.status");

		boolean hasNBT = false;

		for (String x : allowedNTB)
		{
			if (NBTTags.hasNBT(item, x)) hasNBT = true;
		}

		return hasNBT;
	}

	public static boolean isUnassignedBackpack(ItemStack item)
	{
		// Check if item has NBT "backinpack.random-ID"
		return NBTTags.hasNBT(item, "backinpack.random-ID");
	}

	/**
	 * @param item
	 * @return If return "true" is assigned, if "false" unassigned
	 */
	public static boolean isAssigned(ItemStack item)
	{
		if (NBTTags.hasNBT(item, "backinpack.assigned") &&
				NBTTags.getNBT(item, "backinpack.assigned").equals("true")) return true;
		else return false;
	}

}
