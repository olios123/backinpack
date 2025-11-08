package me.olios.backinpack.Managers;

import me.olios.backinpack.Data;
import me.olios.backinpack.Main;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundsManager {

    public static void playSound(Player p, Data.Sound s)
    {
        boolean enable = true;
        Sound sound = null;

        switch (s)
        {
            case OPEN:
                enable = ConfigManager.config.OPEN_SOUND_ENABLE;
                sound = Sound.valueOf(ConfigManager.config.OPEN_SOUND_SOUND);
                break;

            case CLOSE:
                enable = ConfigManager.config.CLOSE_SOUND_ENABLE;
                sound = Sound.valueOf(ConfigManager.config.CLOSE_SOUND_SOUND);
                break;

            case BUY:
                enable = ConfigManager.config.BUY_BACKPACK_SOUND_ENABLE;
                sound = Sound.valueOf(ConfigManager.config.BUY_BACKPACK_SOUND_SOUND);
                break;

            case NO_MONEY:
                enable = ConfigManager.config.NO_ENOUGH_MONEY_SOUND_ENABLE;
                sound = Sound.valueOf(ConfigManager.config.NO_ENOUGH_MONEY_SOUND_SOUND);
                break;

            case REMOVE:
                enable = ConfigManager.config.REMOVE_BACKPACK_SOUND_ENABLE;
                sound = Sound.valueOf(ConfigManager.config.REMOVE_BACKPACK_SOUND_SOUND);
                break;
        }

        if (sound == null || !enable) return;

        p.playSound(p.getLocation(), sound, 1, 1);
    }
}
