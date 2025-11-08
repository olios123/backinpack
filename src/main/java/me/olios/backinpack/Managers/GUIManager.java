//
// CC Creative Commons 2022
// Attribution-NoDerivatives 4.0 International
// Author olios
//

package me.olios.backinpack.Managers;

import me.olios.backinpack.API.PAPICustom;
import me.olios.backinpack.Data;
import me.olios.backinpack.GUIs.*;
import me.olios.backinpack.Library.Blocker;
import me.olios.backinpack.Library.ItemsCompare;
import me.olios.backinpack.Library.NBTTags;
import me.olios.backinpack.Objects.*;
import me.olios.backinpack.Objects.GUI.BackpacksConfig;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class GUIManager implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    private static void onPlayerInventoryClose(InventoryCloseEvent e) 
    {
        Player p = (Player) e.getPlayer();
        String uuid = p.getUniqueId().toString();

        // BackpackGUI
        if (CacheManager.getOpenedBackpackSender(uuid) != null &&
            CacheManager.getOpenedBackpackSender(uuid).backpackID != null)
        {
            // Save backpacks
            BackpackObject inventory = BackpacksManager.getInventory(uuid);

            // Read items from backpack
            List<ItemStack> items = getInventoryItems(p);


            // Replace backpack in inventory
            Cache.OpenedBackpack openedBackpack = CacheManager.getOpenedBackpackSender(uuid);

            BackpackContentObject backpack = BackpacksManager.getInventoryByBackpackID(openedBackpack.owner, openedBackpack.backpackID);
            backpack.items = items;

            BackpacksManager.replaceBackpackInInventory(inventory, backpack);

            // Save changes
            BackpacksManager.saveBackpacks();


            // Remove players from Map - opened backpacks
            CacheManager.removeOpenedBackpack(openedBackpack);

            // Refresh placeholders
            BackpacksManager.refreshPlaceholders(p);

            // Close sound
            SoundsManager.playSound(p, Data.Sound.CLOSE);
        }
        // BackpackListGUI
        else if (CacheManager.getOpenedBackpackSender(uuid) != null &&
                CacheManager.getOpenedBackpackSender(uuid).backpackID == null)
        {
            Cache.OpenedBackpack openedBackpack = CacheManager.getOpenedBackpackSender(uuid);

            // Remove players from Map - opened backpacks
            CacheManager.removeOpenedBackpack(openedBackpack);
        }

        // BackpackListGUI
        if (BackpackListGUI.backpackListInventory == e.getInventory())
        {
            // Nothing
        }

        // AssigningBackpackGUI
        if (AssigningBackpackGUI.assigningBackpackInventory == e.getInventory())
        {
            // Clear cache about assigning backpack
            CacheManager.removeAssigningBackpack(CacheManager.getAssigningBackpack(uuid));
            return;
        }

        // SelectAssignmentGUI
        if (SelectAssignmentGUI.selectAssignmentInventory == e.getInventory())
        {
            CacheManager.removeAssigningBackpack(CacheManager.getAssigningBackpack(uuid));
            return;
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private static void onInventoryOpenEvent(InventoryOpenEvent e)
    {
        Inventory inv = e.getInventory();
        Player p = (Player) e.getPlayer();

        if (inv.isEmpty()) return;
        if (inv.equals(AssigningBackpackGUI.assigningBackpackInventory) ||
            inv.equals(BackpackGUI.backpackInventory) ||
            inv.equals(BackpackListGUI.backpackListInventory) ||
            inv.equals(RemoveBackpackGUI.removeBackpackInventory) ||
            inv.equals(SelectAssignmentGUI.selectAssignmentInventory)) return;

        /**
         * Replacing not existing backpacks in inventories
         */
        for (int i = 0; i < inv.getSize(); i++)
        {
            ItemStack item = inv.getItem(i);

            if (item == null || !BackpacksManager.isBackpack(item)) continue;

            String backpackID = NBTTags.getNBT(item, "backinpack.backpack-id");

            if (backpackID == null) continue; // Is unassigned backpack

            // Assigned backpack
            String backpackOwner = NBTTags.getNBT(item, "backinpack.backpack-owner");

            // Check if player's inventory exists
            BackpackObject inventory = BackpacksManager.getInventory(backpackOwner);

            // Player's inventory doesn't exists
            if (inventory == null)
            {
                ItemStack unassignedBackpack = BackpacksManager.createUnassignedBackpackItem();
                inv.setItem(i, unassignedBackpack);
                continue;
            }

            // Inventory exists and gets backpack
            BackpackContentObject backpack = BackpacksManager.getInventoryByBackpackID(backpackOwner, backpackID);

            // Backpack not found in player inventory
            if (backpack == null)
            {
                ItemStack unassignedBackpack = BackpacksManager.createUnassignedBackpackItem();
                inv.setItem(i, unassignedBackpack);
                continue;
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    private static void onInventoryClickEvent(InventoryClickEvent e)
    {
        Inventory inv = e.getInventory();
        ItemStack item = e.getCurrentItem();
        Player p = (Player) e.getWhoClicked();
        String uuid = p.getUniqueId().toString();

        // Blocked place ItemStack
        ItemStack blocker = Blocker.get();

        // BackpackGUI
        if (BackpackGUI.backpackInventory != null &&
            BackpackGUI.backpackInventory.getType() == inv.getType() &&
            CacheManager.getOpenedBackpackSender(uuid) != null &&
            CacheManager.getOpenedBackpackSender(uuid).backpackID != null)

        {
            // Get inventory after changes
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    Inventory newInv = p.getOpenInventory().getTopInventory();
                    ItemStack[] items = newInv.getContents();

                    // Replace data in cache
                    Cache.OpenedBackpack senderBackpack = CacheManager.getOpenedBackpackSender(uuid);
                    if (senderBackpack == null) return;
                    senderBackpack.inventory = newInv;

                    // Get owner backpack
                    BackpackContentObject backpack = BackpacksManager.getInventoryByBackpackID(
                            senderBackpack.owner,
                            senderBackpack.backpackID);
                    backpack.items = Arrays.asList(items);

                    // Save items in other backpacks
                    BackpacksManager.replaceBackpackInInventory(BackpacksManager.getInventory(senderBackpack.owner), backpack);

                    // Get every opened backpack
                    for (Cache.OpenedBackpack openedBackpack :
                            CacheManager.getAllOpenedBackpacksOwner(senderBackpack.owner))
                    {
                        // Dont change items in sender
                        if (openedBackpack.sender.equals(uuid) ||
                                openedBackpack.backpackID == null) continue;

                        Player sender = Bukkit.getPlayer(UUID.fromString(openedBackpack.sender));
                        if (sender == null ||
                                sender.getOpenInventory() == null ||
                                sender.getOpenInventory().getTopInventory() == null) continue;

                        sender.openInventory(newInv);
                    }
                }
            }.runTaskLater(Data.plugin, 5);
        }


        // Check if item exists or is blocker
        if (item == null) return;
        else if (item.equals(blocker))
        {
            e.setCancelled(true);
            return;
        }

        // AssigningBackpackGUI
        if (inv.equals(AssigningBackpackGUI.assigningBackpackInventory))
        {
            e.setCancelled(true);

            // Assigning backpack
            if (BackpacksManager.isBackpack(item))
            {
                String backpackID = NBTTags.getNBT(item, "backinpack.backpack-id");

                Cache.AssigningBackpack assigningBackpack = CacheManager.getAssigningBackpack(uuid);
                if (assigningBackpack == null) return;
                if (!uuid.equals(assigningBackpack.owner)) return;

                BackpackContentObject backpack = BackpacksManager.getInventoryByBackpackID(uuid, backpackID);

                ItemStack backpackItem = assigningBackpack.item;

                if (!NBTTags.hasNBT(backpackItem, "backinpack.random-ID"))
                {
                    // Delete cache assigning data
                    CacheManager.removeAssigningBackpack(CacheManager.getAssigningBackpack(uuid));
                    return;
                }

                // Get backpackItem identification
                String randomBackpackItemID = NBTTags.getNBT(backpackItem, "backinpack.random-ID");

                // Create backpack item in inventory
                ItemStack newBackpackItem = BackpacksManager.createBackpackItem(uuid, backpackID, BackpacksConfigManager.backpackConfigs.get(backpack.type).STYLE_ITEM);

                // Replace item
                for (int i = 0; i < p.getInventory().getContents().length; i++)
                {
                    ItemStack inventoryItem = p.getInventory().getItem(i);

                    if (inventoryItem == null ||
                            !NBTTags.hasNBT(inventoryItem, "backinpack.random-ID")) continue;

                    String randomID = NBTTags.getNBT(inventoryItem, "backinpack.random-ID");

                    if (randomID.equals(randomBackpackItemID))
                    {
                        p.getInventory().setItem(i, newBackpackItem);
                    }
                }

                // Delete cache assigning data
                CacheManager.removeAssigningBackpack(CacheManager.getAssigningBackpack(uuid));

                // Done
                p.closeInventory();
            }

            return;
        }


//        // BackpackGUI
//        if (BackpackGUI.backpackInventory != null &&
//            BackpackGUI.backpackInventory.getType() == inv.getType() &&
//            CacheManager.getOpenedBackpackOwner(uuid) != null &&
//            CacheManager.getOpenedBackpackOwner(uuid).backpackID != null)
//        {
//            Cache.OpenedBackpack openedBackpack = CacheManager.getOpenedBackpackOwner(uuid);
//            openedBackpack.inventory = inv;
//
//            // Update items in backpacks
//            CacheManager.replaceOpenedBackpack(CacheManager.getOpenedBackpackOwner(uuid), openedBackpack);
//        }


        // BackpackListGUI
        if (inv.equals(BackpackListGUI.backpackListInventory))
        {
            e.setCancelled(true);

            String GLOBAL_backpackID = null;
            String GLOBAL_owner = null;
            BackpacksConfig config = GUIConfigManager.getBackpacks(p);

            // Buy a new backpack
            if (e.getRawSlot() == 48 &&
                    !e.getCurrentItem().equals(Blocker.get()) || e.getRawSlot() == 49)
            {
                if (e.getCurrentItem().equals(config.removeBackpack))
                {
                    RemoveBackpackGUI.openGUI(p);
                    return;
                }
                else // Buy backpack
                {
                    ChoseBuyGUI.openGUI(p);
                }
            }
            else if (e.getRawSlot() == 50 && e.getCurrentItem().equals(config.removeBackpack))
            {
                RemoveBackpackGUI.openGUI(p);
            }

            // Get backpack ID from item
            if (BackpacksManager.isBackpack(item))
            {
                String owner = NBTTags.getNBT(item, "backinpack.backpack-owner");
                String backpackID = NBTTags.getNBT(item, "backinpack.backpack-id");

                // Change name
                if (e.getClick().equals(ClickType.RIGHT))
                {
                    e.setCancelled(true);
                    p.closeInventory();

                    MessagesManager.sendMessage(p, Data.Message.PLAYER_BACKPACK_CHANGE_NAME);

                    CacheManager.createChangeBackpackName(owner, backpackID);

                    return;
                }

                // Set GLOBAL values
                GLOBAL_backpackID = backpackID;
                GLOBAL_owner = owner;
            }
            else return;

            // Opened by sender
            if (CacheManager.getOpenedBackpackSender(uuid) != null)
            {
                BackpackGUI.openGUI(GLOBAL_owner, GLOBAL_backpackID, p);

                return;
            }

            // Open GUI - owner only
            BackpackGUI.openGUI(GLOBAL_owner, GLOBAL_backpackID, null);
            return;
        }

        // SelectAssignmentGUI
        if (inv.equals(SelectAssignmentGUI.selectAssignmentInventory))
        {
            e.setCancelled(true);

            Cache.AssigningBackpack assigningBackpack = CacheManager.getAssigningBackpack(uuid);
            BackpackObject inventory = BackpacksManager.getInventory(uuid);

            // Creating a new backpack
            if (e.getRawSlot() == 11)
            {
                if (inventory != null && inventory.backpacks.size() >= ConfigManager.config.MAX_BACKPACKS)
                {
                    MessagesManager.sendMessage(p, Data.Message.PLAYER_MAX_BACKPACKS);
                    p.closeInventory();
                    return;
                }

                // Create new backpack
                String backpackID = BackpacksManager.createBackpack(uuid, true, BackpacksConfigManager.backpackConfigs.get(ConfigManager.config.CREATED_BACKPACK));

                BackpackContentObject backpack = BackpacksManager.getInventoryByBackpackID(uuid, backpackID);

                // Create backpack item in inventory
                ItemStack newBackpackItem = BackpacksManager.createBackpackItem(uuid, backpackID,
                        BackpacksConfigManager.backpackConfigs.get(backpack.type).STYLE_ITEM);

                if (!NBTTags.hasNBT(assigningBackpack.item, "backinpack.random-ID"))
                {
                    // Delete cache assigning data
                    CacheManager.removeAssigningBackpack(CacheManager.getAssigningBackpack(uuid));
                    return;
                }

                // Get backpackItem identification
                String randomBackpackItemID = NBTTags.getNBT(assigningBackpack.item, "backinpack.random-ID");

                // Replace item
                for (int i = 0; i < p.getInventory().getContents().length; i++)
                {
                    ItemStack inventoryItem = p.getInventory().getItem(i);

                    if (inventoryItem == null ||
                            !NBTTags.hasNBT(inventoryItem, "backinpack.random-ID")) continue;

                    String randomID = NBTTags.getNBT(inventoryItem, "backinpack.random-ID");

                    if (randomID.equals(randomBackpackItemID))
                    {
                        p.getInventory().setItem(i, newBackpackItem);
                    }
                }

                // Delete cache assigning data
                CacheManager.removeAssigningBackpack(CacheManager.getAssigningBackpack(uuid));

                // Done
                p.closeInventory();
            }
            // Assigning to existing backpack
            else if (e.getRawSlot() == 15)
            {
                AssigningBackpackGUI.openGUI(p, assigningBackpack.item);
            }
            return;
        }


        // ChoseBuyGUI
        if (inv.equals(ChoseBuyGUI.choseButInventory))
        {
            e.setCancelled(true);

            // Cancel buying
            if (ItemsCompare.isSimilar(e.getCurrentItem(), GUIConfigManager.getChoseBuy(p).cancel))
            {
                BackpackListGUI.openGUI(uuid);
                return;
            }

            String backpackToBuyName = NBTTags.getNBT(item, "backinpack.backpack-buy");

            if (BackpacksConfigManager.backpackConfigs.get(backpackToBuyName) == null) return;

            // Buy backpack
            BackpacksManager.buyBackpack(p,
                    BackpacksConfigManager.backpackConfigs.get(backpackToBuyName));

            return;
        }


        // RemoveBackpackGUI
        if (inv.equals(RemoveBackpackGUI.removeBackpackInventory))
        {
            e.setCancelled(true);

            if (e.getRawSlot() == 49)
            {
                BackpackListGUI.openGUI(uuid);
            }
            else
            {
                if (e.getCurrentItem().equals(Blocker.get())) return;
                if (!BackpacksManager.isBackpack(item)) return;

                String id = NBTTags.getNBT(item, "backinpack.backpack-id");

                BackpackObject inventory = BackpacksManager.getInventory(uuid);
                BackpackContentObject backpack = BackpacksManager.getInventoryByBackpackID(uuid, id);

                Map<String, Object> backpackPlaceholder = PAPICustom.getBackpackPlaceholders(uuid, backpack);

                boolean removed = BackpacksManager.removeBackpack(inventory, backpack);

                // Backpack removed
                if (removed)
                {
                    // Drop all items from backpack
                    if (backpack.items.size() > 0)
                    {
                        backpack.items.forEach(i ->
                        {
                            if (i != null && !i.equals(Blocker.get()))
                                p.getWorld().dropItemNaturally(p.getLocation(), i);
                        });
                    }

                    // Send message
                    MessagesManager.sendMessage(p, Data.Message.PLAYER_BACKPACK_REMOVE,
                            backpackPlaceholder);

                    p.closeInventory();

                    SoundsManager.playSound(p, Data.Sound.REMOVE);

                    // Refund if can
                    if (ConfigManager.config.REMOVING_BACKPACKS_ENABLE &&
                            ConfigManager.config.CREATE_BACKPACK_BUY &&
                            Data.VAULT && !backpack.crafted)
                    {
                        EconomyManager.deposit(uuid, ConfigManager.config.REMOVING_BACKPACKS_REFUND);

                        // Refund info
                        MessagesManager.sendMessage(p, Data.Message.PLAYER_BACKPACK_REFUND,
                                backpackPlaceholder);
                    }
                }
                return;
            }
        }

        // Click in player eq
        if (BackpacksManager.isBackpack(item))
        {
            // Check if player is owner of this backpack
            String owner = NBTTags.getNBT(item, "backinpack.backpack-owner");
            String backpackID = NBTTags.getNBT(item, "backinpack.backpack-id");

            if (e.getInventory().getType().equals(InventoryType.CRAFTING))
            {
                // Player is owner of this backpack
                if (owner == null || !owner.equals(uuid)) return;

                if (e.getClick().equals(ClickType.RIGHT))
                {
                    p.closeInventory();
                    e.setCancelled(true);

                    // If this backpack need to be assigned
                    if (!NBTTags.getNBT(item, "backinpack.assigned").equals("true"))
                    {
                        AssigningBackpackGUI.openGUI(p, item);
                    }
                    else
                    {
                        MessagesManager.sendMessage(p, Data.Message.PLAYER_BACKPACK_CHANGE_NAME);

                        CacheManager.createChangeBackpackName(owner, backpackID);
                    }

                    return;
                }
            }
        }
    }

    // Open backpack using item
    @EventHandler(priority = EventPriority.HIGH)
    private void onPlayerInteractEvent(PlayerInteractEvent e)
    {
        Player p = e.getPlayer();
        p.recalculatePermissions(); // Refresh permissions
        String uuid = p.getUniqueId().toString();

        if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK)
        {
            if (e.getItem() == null) return;

            ItemStack currentItem = e.getItem();

            if (!BackpacksManager.isBackpack(currentItem)) return;
            e.setCancelled(true);

            // If this backpack need to be assigned
            if (BackpacksManager.isUnassignedBackpack(currentItem))
            {
                // Backpack can be assigned and created
                if (ConfigManager.config.CREATE_BACKPACK_CRAFTING)
                {
                    SelectAssignmentGUI.openGUI(p, currentItem);
                }
                // Backpack can be only assigned
                else AssigningBackpackGUI.openGUI(p, currentItem);
                return;
            }


            String ownerUUID = NBTTags.getNBT(currentItem, "backinpack.backpack-owner");
            String backpackID = NBTTags.getNBT(currentItem, "backinpack.backpack-id");

            BackpackObject inventory = BackpacksManager.getInventory(ownerUUID);
            BackpackContentObject backpack = BackpacksManager.getInventoryByBackpackID(uuid, backpackID);

            // Inventory doesn't exist or backpack in this inventory not exists so replace to unassigned backpack
            if (inventory == null || BackpacksManager.getInventoryByBackpackID(ownerUUID, backpackID) == null)
            {
                ItemStack unassignedBackpack = BackpacksManager.createUnassignedBackpackItem();

                for (int i = 0; i < p.getInventory().getSize(); i++)
                {
                    ItemStack item = p.getInventory().getItem(i);

                    // Replace current backpack to unassigned
                    if (item != null && item.equals(e.getItem()))
                    {
                        p.getInventory().setItem(i, unassignedBackpack);
                        break;
                    }
                }

                // Send info to player
                MessagesManager.sendMessage(p, Data.Message.PLAYER_BACKPACK_NOT_EXISTS);
                return;
            }

            // Check if player is admin or has permission
            if (PermissionsManager.checkPermissions(p, Data.Permission.OPEN) &&
                    (ConfigManager.config.ADMIN_OPEN_PLAYER_BACKPACK || p.isOp()))
            {
                // Open backpack
                e.setCancelled(true);
                BackpackGUI.openGUI(ownerUUID, backpackID, p);
                return;
            }
            // Else check if other players can open backpacks
            else if (!ConfigManager.config.OTHER_PLAYERS_OPEN_BACKPACK &&
                    !inventory.uuid.equals(ownerUUID))
            {
                MessagesManager.sendMessage(p, Data.Message.PLAYER_BACKPACK_NOT_OWNER);
                return;
            }

            // Check if player can access backpack using item
            if (!ConfigManager.config.BACKPACK_ACCESS_ITEM)
            {
                MessagesManager.sendMessage(p, Data.Message.PLAYER_BACKPACK_ACCESS_THROUGH_ITEM_DISABLED);
                return;
            }

            // Open backpack
            BackpackGUI.openGUI(ownerUUID, backpackID, p); // Sender is owner or other player
        }
    }

    // Disable placing backpacks
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onBlockPlace(BlockPlaceEvent e)
    {
        Player p = e.getPlayer();
        p.recalculatePermissions(); // Refresh permissions

        ItemStack item = e.getItemInHand();

        // If this item is backpack
        if (NBTTags.hasNBT(item, "backinpack.backpack-id") ||
            NBTTags.hasNBT(item, "backinpack.backpack-owner")) e.setCancelled(true);
    }

    public static List<ItemStack> getInventoryItems(Player player)
    {
        // Blocked place ItemStack
        ItemStack blocker = Blocker.get();

        // Read items from backpack
        List<ItemStack> items = new ArrayList<>();

        AtomicBoolean illegalItems = new AtomicBoolean(false);

        for (int i = 0; i <= 53; i++)
        {
            if (player.getOpenInventory().getTopInventory().getItem(i) != null &&
                    !player.getOpenInventory().getTopInventory().getItem(i).equals(blocker))
            {
                // Check if items is allowed to store in backpack
                ItemStack item = player.getOpenInventory().getTopInventory().getItem(i);
                String material = item.getType().toString();

                // Check if item is backpack
                if (item.getType().equals(Material.PLAYER_HEAD) &&
                        NBTTags.hasNBT(item, "backinpack.backpack-id"))
                {
                    items.add(null);
                    player.getWorld().dropItemNaturally(player.getLocation(), item);
                    illegalItems.set(true);
                    continue;
                }

                // Check if in config is any banned item
                if (ConfigManager.config.BLACKLIST_ITEMS.size() > 0)
                {
                    // Check if item material contains banned keys
                    ConfigManager.config.BLACKLIST_ITEMS.forEach(black ->
                    {
                        // Item is banned
                        if (material.contains(black))
                        {
                            player.getWorld().dropItemNaturally(player.getLocation(), item);
                            items.add(null);
                            illegalItems.set(true);
                        }
                        // Item is legal and add it to list
                        else items.add(item);
                    });
                }
                // All items are legal and add it to list
                else items.add(item);
            }
            else items.add(null);
        }

        if (illegalItems.get())
        {
            MessagesManager.sendMessage(player, Data.Message.PLAYER_BACKPACK_BLACKLIST);
        }

        return items;
    }

    public static void protectHelmet()
    {
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                for (Player player : Bukkit.getOnlinePlayers())
                {
                    PlayerInventory playerInventory = player.getInventory();
                    ItemStack helmet = playerInventory.getHelmet();

                    if (helmet != null &&
                        NBTTags.hasNBT(helmet, "backinpack.backpack-id") &&
                        NBTTags.hasNBT(helmet, "backinpack.backpack-owner"))
                    {
                        if (playerInventory.firstEmpty() != -1)
                        {
                            playerInventory.setItem(playerInventory.firstEmpty(), helmet);
                        }
                        else
                        {
                            player.getWorld().dropItemNaturally(player.getLocation(), helmet);
                        }
                        playerInventory.setItem(39, null);
                    }

                }
            }
        }.runTaskTimer(Data.plugin, 0, 20);
    }

}