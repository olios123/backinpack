//
// CC Creative Commons 2022
// Attribution-NoDerivatives 4.0 International
// Author olios
//

package me.olios.backinpack;

import me.olios.backinpack.API.PAPIExpansion;
import me.olios.backinpack.Events.PlayerCrafting;
import me.olios.backinpack.Events.PlayerDeath;
import me.olios.backinpack.Events.PlayerJoin;
import me.olios.backinpack.Events.PlayerQuit;
import me.olios.backinpack.Library.Replace.StringReplace;
import me.olios.backinpack.Managers.*;
import me.olios.backinpack.Recipes.Backpack;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public final class Main extends JavaPlugin implements Listener {

//    // Initialize JeffLib
//    {
//        JeffLib.init(this);
//    }

    public static Main instance;



    // Console colors
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_DARK_BLUE = "\u001B[34m";
    public static final String ANSI_DARK_GREEN = "\u001B[32m";
    public static final String ANSI_DARK_AQUA = "\u001B[36m";
    public static final String ANSI_DARK_RED = "\u001B[31m";
    public static final String ANSI_DARK_PURPLE = "\u001B[35m";
    public static final String ANSI_GOLD = "\u001B[33m";
    public static final String ANSI_GRAY = "\u001B[37m";
    public static final String ANSI_DARK_GRAY = "\u001B[90m";
    public static final String ANSI_BLUE = "\u001B[94m";
    public static final String ANSI_GREEN = "\u001B[92m";
    public static final String ANSI_AQUA = "\u001B[96m";
    public static final String ANSI_RED = "\u001B[91m";
    public static final String ANSI_LIGHT_PURPLE = "\u001B[95m";
    public static final String ANSI_YELLOW = "\u001B[93m";
    public static final String ANSI_WHITE = "\u001B[97m";
    public static final String ANSI_OBFUSCATED = "\u001B[8m";
    public static final String ANSI_BOLD = "\u001B[1m";
    public static final String ANSI_STRIKETHROUGH = "\u001B[9m";
    public static final String ANSI_UNDERLINE = "\u001B[4m";
    public static final String ANSI_ITALIC = "\u001B[3m";
    public static final String ANSI_RESET = "\u001B[0m";

//    public static final String ANSI_GREEN = "\u001B[32m";
//    public static final String ANSI_GOLD = "\u001B[33m";
//    public static final String ANSI_RED = "\u001B[31m";
//    public static final String ANSI_YELLOW = "\u001B[93m";
//    public static final String ANSI_BLUE = "\u001B[34m";
//
//    public static final String ANSI_RESET = "\u001B[0m";



    public static void l (Object obj)
    {
        Bukkit.getConsoleSender().sendMessage(ANSI_DARK_GREEN + "[BackInPack] " + obj);
    }
    public static void log(String msg)
    {
        if (ConfigManager.config.CLEARER_LOGS)
        {
            Bukkit.getLogger().info(ANSI_GREEN + "[BackInPack] " + msg + ANSI_RESET);
            return;
        }

        Bukkit.getLogger().info("[BackInPack] " + msg + ANSI_RESET);
    }
    public static void errLog(String msg)
    {
        Bukkit.getLogger().severe(ANSI_DARK_RED + "[BackInPack] " + msg + ANSI_RESET);
    }
    public static void warnLog(String msg)
    {
        Bukkit.getLogger().warning(ANSI_GOLD + "[BackInPack] " + msg + ANSI_RESET);
    }

    @Override
    public void onEnable()
    {
        try
        {
            instance = this;
            Data.plugin = this;
            Data.version = this.getDescription().getVersion();
            Data.SSID = Data.SSID();

            // Check if plugin PlaceholderAPI is installed on server
            if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null)
            {
                Bukkit.getPluginManager().registerEvents(this, this);
                new PAPIExpansion().register();
                Main.log("PlaceholderAPI plugin found, placeholders registered.");
                Data.PAPI = true;
            }
            else
            {
                /**
                 * *****************************************************************
                 * *                           BackInPack                          *
                 * *                                                               *
                 * * Download the PlaceholderAPI plugin and add it to your server. *
                 * *              Placeholders won't work without it!              *
                 * *    https://www.spigotmc.org/resources/placeholderapi.6245/    *
                 * *                                                               *
                 * *****************************************************************
                 */

                // Plugin PlaceholderAPI was not found
                Main.errLog("\n*****************************************************************\n" +
                        "*                           BackInPack                          *\n" +
                        "*                                                               *\n" +
                        "* Download the PlaceholderAPI plugin and add it to your server. *\n" +
                        "*              Placeholders won't work without it!              *\n" +
                        "*    https://www.spigotmc.org/resources/placeholderapi.6245/    *\n" +
                        "*                                                               *\n" +
                        "*****************************************************************");
            }

            // Check if plugin Vault is installed on server
            if (Bukkit.getPluginManager().getPlugin("Vault") != null)
            {
                if (EconomyManager.registerVault())
                {
                    Main.log("Vault plugin found, economy registered.");
                }
            }
            else
            {
                /**
                 * ***********************************************************
                 * *                        BackInPack                       *
                 * *                                                         *
                 * *   Download the Vault plugin and add it to your server.  *
                 * * The ability to buy a backpack will not work without it! *
                 * *     https://www.spigotmc.org/resources/vault.34315/     *
                 * *                                                         *
                 * ***********************************************************
                 */

                // Plugin Vault aws not found
                Main.errLog("\n***********************************************************\n" +
                        "*                        BackInPack                       *\n" +
                        "*                                                         *\n" +
                        "*   Download the Vault plugin and add it to your server.  *\n" +
                        "* The ability to buy a backpack will not work without it! *\n" +
                        "*     https://www.spigotmc.org/resources/vault.34315/     *\n" +
                        "*                                                         *\n" +
                        "***********************************************************");
            }

            String lgMsg = "&6Back&r&9In&r&6Pack&r plugin is starting...\n" +
                    "&6  ___ &9___ &6___ \n" +
                    "&6 | _ )&9_ _&6| _ \\&r | &6Version: &a" + Data.pluginVersion + "\n" +
                    "&6 | _ \\&9| |&6|  _/&r | &6SSID: &a" + Data.SSID + "\n" +
                    "&6 |___/&9___&6|_|  &r | &6Server: &a" + Bukkit.getServer().getBukkitVersion() + "\n" + "&6              ";


            // Checking for BETA version of a plugin
            if (Data.pluginVersion.contains("BETA"))
            {
                // Send message to OP-s
                for (Player player : Bukkit.getServer().getOnlinePlayers())
                {
                    // Check if OP
                    if (player.isOp())
                    {
                        player.sendMessage(StringReplace.string("&6BackInPack - WARNING\n" +
                                "&6You are using the BETA version of the BackInPack plugin, " +
                                "&6be sensitive to errors and possible incorrect functioning of the plugin. " +
                                "&6If such a situation occurs, it is recommended to use the latest version" +
                                "&6of the plugin without the \"BETA\" tag."));
                    }
                }

                // Send message to console
                Main.log(ANSI_GOLD + "BackInPack - WARNING");
                Main.log(ANSI_GOLD + "You are using the BETA version of the BackInPack plugin,");
                Main.log(ANSI_GOLD + "be sensitive to errors and possible incorrect functioning of the plugin.");
                Main.log(ANSI_GOLD + "If such a situation occurs, it is recommended to use the latest version");
                Main.log(ANSI_GOLD + "of the plugin without the \"BETA\" tag.");
            }


            // Register events
            registerEvents(this);
            registerEvents(new PlayerDeath());
            registerEvents(new GUIManager());
            registerEvents(new PlayerJoin());
            registerEvents(new PlayerQuit());
            registerEvents(new ChatManager());
            registerEvents(new PlayerCrafting());

            // Read server version
            getVersion();

            // Files configuration
            FilesManager.manageFiles();
            createFile("config.yml");
            createFile("database.yml");
            createFile("data/inventories.yml");
            if (FilesManager.firstStart)
            {
                createFile("backpacks/README.yml");
                createFile("backpacks/default.yml");
                createFile("backpacks/copper.yml");
                createFile("backpacks/iron.yml");
                createFile("backpacks/diamond.yml");
            }
            // Language files
            createFile("languages/de.yml");
            createFile("languages/en.yml");
            createFile("languages/es.yml");
            createFile("languages/fr.yml");
            createFile("languages/pl.yml");
            createFile("languages/tr.yml");
            createFile("languages/vi.yml");
            createFile("languages/zh_si.yml");
            createFile("languages/zh_tr.yml");

            createFile("GUI/selectAssignment.yml");
            createFile("GUI/removeBackpack.yml");
            createFile("GUI/backpacks.yml");
            createFile("GUI/backpack.yml");
            createFile("GUI/choseBuy.yml");
            FilesManager.loadYmlFiles();
            MessagesManager.setLanguageFile();
            saveDefaultConfig();

            Main.log("Controll string");
            // Log message
            Main.log(StringReplace.cmd(lgMsg));
            Main.log("Controll string");

            // Load config and check if everything is ok
            // Check config correctness
            File localConfig = getConfigLocal();
            AtomicBoolean missing = new AtomicBoolean(false);
            if (localConfig.exists())
            {
                YamlConfiguration yml = YamlConfiguration.loadConfiguration(localConfig);
                if (yml != null)
                {
                    YamlConfiguration config = FilesManager.getConfigYml();
                    List<String> keys = config.getConfigurationSection("").getKeys(false).stream().toList();

                    yml.getConfigurationSection("").getKeys(false).forEach(key ->
                    {
                        if (!keys.contains(key)) missing.set(true);
                    });

                    localConfig.delete();
                }
            }
            ConfigManager.loadConfig();

            // Update Checker
            if (ConfigManager.config.CHECK_UPDATES)
            {
                new UpdateChecker(this, Data.resourceId).getVersion(version ->
                {
                    if (!Data.plugin.getDescription().getVersion().equals(version))
                    {
                        // Plugin can be updated
                        Data.canUpdate = true;

                        // Info about update (console)
                        MessagesManager.sendUpdateInfo();

                        // Info about update for players is they are admins
                        for (Player player : Bukkit.getServer().getOnlinePlayers())
                        {
                            if (player.isOp())
                            {
                                // Info about update (player)
                                MessagesManager.sendUpdateInfo(player);
                            }
                        }
                    }
                });
            }

            // Some variables are missing
            if (missing.get())
            {
                Main.errLog(ANSI_RED + "\n************************************************************\n" +
                                "*                         BackInPack                       *\n" +
                                "*                                                          *\n" +
                                "*         An error was found in the config.yml file        *\n" +
                                "************************************************************\n" +
                                "*    Values are missing in the main configuration file.    *\n" +
                                "*    This may be due to file corruption or updating to     *\n" +
                                "* a newer version of the plugin. Re-create the config.yml  *\n" +
                                "*  file by deleting it and restarting the server or copy   *\n" +
                                "*              the missing values from GitHub.             *\n" +
                                "************************************************************" + ANSI_RESET);
            }

            // Read backpacks config
            BackpacksConfigManager.backpackConfigs();

            // Database
            if (ConfigManager.config.DATABASE) MySQL.connect();

            BackpacksManager.loadBackpacks();

//             Backpack updater
//            BackpacksManager.backpacksUpdater();

            // Add backpack crafting recipe if config allows to create backpacks through crafting
            if (ConfigManager.config.CRAFTING_ENABLE) Bukkit.addRecipe(Backpack.getRecipe());

////             Backpacks updater
//            BackpacksManager.updateOpenedBackpacks();

            // Plugin is enabled :)
            MessagesManager.sendLogMessage(Data.Message.ENABLE_PLUGIN);
//        Main.warnLog("You are running the plugin in BETA version. You may encounter errors while using.");
//        Main.warnLog("Please report bugs to spigotmc.org");

            // Run backup
            if (ConfigManager.config.BACKUP)
            {
                // On run
                BackupManager.createBackup();
                BackupManager.deleteOldBackups();

                // Every hour
                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        BackupManager.createBackup();
                        BackupManager.deleteOldBackups();
                    }
                }.runTaskTimer(Data.plugin, 3600 * 20, 3600 * 20);
            }

            // Update and read MySQL data every second
//        new BukkitRunnable()
//        {
//            @Override
//            public void run()
//            {
//                if (MySQL.isConnected)
//                {
//                    BackpacksManager.updateMySQLBackpacks();
//                    BackpacksManager.readBackpacksFromMySQL();
//                }
//            }
//        }.runTaskTimer(Data.plugin, 0, 20);

            // Protect from putting backpacks on head
            GUIManager.protectHelmet();
        }
        catch (Exception e)
        {
            reportErrors(e);
        }
    }

    @Override
    public void onDisable()
    {
        BackpacksManager.saveBackpacks();
        BackupManager.createBackup();

        // Plugin is disabled :(
        MessagesManager.sendLogMessage(Data.Message.DISABLE_PLUGIN);
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        try
        {
            CommandsManager.manageCommand(sender, cmd, label, args);
            return true;
        }
        catch (SQLException | IOException | InvalidConfigurationException e)
        {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args)
    {
        return CommandsManager.manageCompleter(sender, cmd, alias, args);
    }

    public void getVersion()
    {
        Data.version = Bukkit.getVersion().split(" ")[2]
                .replace(")", "");
    }

    private void createFile(String filePath)
    {
        try
        {
            Main.l(!new File("plugins/BackInPack/", filePath).exists());
            if (!new File("plugins/BackInPack/", filePath).exists())
            {
                // Create if not exists
                saveResource(filePath, false);

                // Add attributes to file about version
                Path path = Paths.get(Data.pluginPath + filePath);
                UserDefinedFileAttributeView view = Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);

                // Attribute to bytes
                byte[] valueBytes = Data.pluginVersion.getBytes(StandardCharsets.UTF_8);
                ByteBuffer buffer = ByteBuffer.allocate(valueBytes.length);
                buffer.put(valueBytes);
                buffer.flip();

                // Save attribute
                view.write("version", buffer);

//                // Check if file has attribute
//                view.list().forEach(attribute ->
//                {
//                    if (!attribute.equals("version"))
//                    {
//                        // Create attribute because not exists
//                        try
//                        {
//                            view.write("version", StandardCharsets.UTF_8.encode(Data.version));
//                        }
//                        catch (IOException e)
//                        {
//                            errLog("An error occured: The attribute for file could not be created");
//                        }
//
//                    }
//                });
            }
        }
        catch (IOException e)
        {
            errLog("An error occured: The file could not be created");
        }
    }

    private static void registerEvents(Listener listener)
    {
        Data.plugin.getServer().getPluginManager().registerEvents(listener, Data.plugin);
    }

    public static Main getInstance()
    {
        return instance;
    }

    public static void reportErrors(Exception e)
    {
        try
        {
            // Report error
            Map<String, String> arguments = new HashMap<>();
            arguments.put("ip", getPublicIP());
            arguments.put("error", getStackTraceAsString(e));

            BufferedReader reader = getBufferedReader(arguments);
            if (reader == null) return;

            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = reader.readLine()) != null)
            {
                response.append(inputLine);
            }
            reader.close();
        }
        catch (IOException e1)
        {
            // Do nothing
        }
    }

    public static String getPublicIP()
    {
        try
        {
            URL url = new URL("http://httpbin.org/ip");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Analiza odpowiedzi JSON w celu uzyskania publicznego IP
            String json = response.toString();
            int startIndex = json.indexOf("\"origin\": \"") + 11;
            int endIndex = json.indexOf("\"", startIndex);
            return json.substring(startIndex, endIndex);
        }
        catch (IOException e)
        {

        }
        return null;
    }

    @NotNull
    private static BufferedReader getBufferedReader(Map<String, String> arguments)
    {
        try
        {
            URL obj = new URL(Data.REPORT_URL);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();

            // Use POST method
            connection.setRequestMethod("POST");

            // Enable sending data
            connection.setDoOutput(true);

            // Create POST
            StringBuilder postData = new StringBuilder();
            for (Map.Entry<String, String> param : arguments.entrySet())
            {
                if (postData.length() != 0) postData.append('&');
                postData.append(param.getKey());
                postData.append('=');
                postData.append(param.getValue());
            }

            // Convert to bytes
            byte[] postDataBytes = postData.toString().getBytes("UTF-8");

            // Headers
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));

            // Send POST data
            try (DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream()))
            {
                outputStream.write(postDataBytes);
            }

            // Read server response
            int responseCode = connection.getResponseCode();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            return reader;
        }
        catch (IOException e) {}

        return null;
    }

    private static String getStackTraceAsString(Exception e)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    public static YamlConfiguration getResourceYML(String path)
    {
        YamlConfiguration yml = new YamlConfiguration();

        try
        {
            InputStream inputStream = Data.plugin.getResource(path);

            File tempFile = File.createTempFile("temp", ".yml");
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            yml.load(tempFile);

            tempFile.delete();
        }
        catch (IOException | InvalidConfigurationException e)
        {
            e.printStackTrace();
        }

        return yml;
    }

    private File getConfigLocal()
    {
        InputStream is = getResource("config.yml");

        try
        {
            File tempFile = File.createTempFile("tempConfig", ".yml");

            try (OutputStream os = new FileOutputStream(tempFile))
            {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = is.read(buffer)) != -1)
                {
                    os.write(buffer, 0, bytesRead);
                }
            }
            return tempFile;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
        finally
        {
            try
            {
                is.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }
}
