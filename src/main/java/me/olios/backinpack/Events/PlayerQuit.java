package me.olios.backinpack.Events;

import me.olios.backinpack.Managers.BackpacksManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuit implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    private static void onPlayerQuitEvent(PlayerQuitEvent e)
    {
        Player p = e.getPlayer();

        BackpacksManager.saveBackpacks();
    }
}
