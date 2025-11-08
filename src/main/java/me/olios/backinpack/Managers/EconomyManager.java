package me.olios.backinpack.Managers;

import me.olios.backinpack.Data;
import me.olios.backinpack.Main;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.nio.Buffer;
import java.util.UUID;

public class EconomyManager {

    public static boolean registered = false;
    private static Economy economy;

    public static boolean registerVault()
    {
        RegisteredServiceProvider<Economy> rsp = Data.plugin.getServer().getServicesManager().getRegistration(Economy.class);

        if (rsp == null)
        {
            Main.errLog("There was a problem initializing the Vault plugin");
            Main.errLog("Contact Support -> " + Data.discord);
            return false;
        }

        economy = rsp.getProvider();

        if (economy == null)
        {
            Main.errLog("There was a problem initializing the Vault plugin");
            Main.errLog("Contact Support -> " + Data.discord);
            return false;
        }

        registered = true;
        Data.VAULT = true;

        return true;
    }

    public static double getBalance(String uuid)
    {
        return economy.getBalance(Bukkit.getOfflinePlayer(UUID.fromString(uuid)));
    }

    public static void deposit(String uuid, double amount)
    {
        economy.depositPlayer(Bukkit.getOfflinePlayer(UUID.fromString(uuid)), amount);
    }

    public static void withdraw(String uuid, double amount)
    {
        economy.withdrawPlayer(Bukkit.getOfflinePlayer(UUID.fromString(uuid)), amount);
    }

}
