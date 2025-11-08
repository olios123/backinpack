//
// CC Creative Commons 2022
// Attribution-NoDerivatives 4.0 International
// Author olios
//

package me.olios.backinpack.Managers;


import me.olios.backinpack.Data;
import me.olios.backinpack.Main;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.util.List;

public class FilesManager {

    public static boolean firstStart = false;

    // Folders
    public static final File languageFolder = new File(Data.languagesPath);
    public static final File dataFolder = new File(Data.dataPath);
    public static final File backpacksFolder = new File(Data.backpacksPath);
    public static final File guiFolder = new File(Data.guiPath);
    public static final File backupFolder = new File(Data.pluginPath + "backup");

    // Files
    public static File configFile = new File(Data.pluginPath, "config.yml");
    public static File inventoriesFile = new File(Data.dataPath, "inventories.yml");
    public static File databaseFile = new File(Data.pluginPath, "database.yml");

    public static File deFile = new File(languageFolder, "de.yml");
    public static File enFile = new File(languageFolder, "en.yml");
    public static File esFile = new File(languageFolder, "es.yml");
    public static File frFile = new File(languageFolder, "fr.yml");
    public static File plFile = new File(languageFolder, "pl.yml");
    public static File trFile = new File(languageFolder, "tr.yml");
    public static File viFile = new File(languageFolder, "vi.yml");
    public static File zhSiFile = new File(languageFolder, "zh_si.yml");
    public static File zhTrFile = new File(languageFolder, "zh_tr.yml");


    // YamlConfigurations
    public static final YamlConfiguration configYml = new YamlConfiguration();
    private static final YamlConfiguration inventoriesYml = new YamlConfiguration();
    private static final YamlConfiguration databaseYml = new YamlConfiguration();

    private static final YamlConfiguration deYml = new YamlConfiguration();
    private static final YamlConfiguration enYml = new YamlConfiguration();
    private static final YamlConfiguration esYml = new YamlConfiguration();
    private static final YamlConfiguration frYml = new YamlConfiguration();
    private static final YamlConfiguration plYml = new YamlConfiguration();
    private static final YamlConfiguration trYml = new YamlConfiguration();
    private static final YamlConfiguration viYml = new YamlConfiguration();
    private static final YamlConfiguration zhSiYml = new YamlConfiguration();
    private static final YamlConfiguration zhTrYml = new YamlConfiguration();


    public static void manageFiles()
    {
        if (!new File(Data.pluginPath).exists())
        {
            firstStart = true;
        }

        if (!languageFolder.exists()) languageFolder.mkdirs();
        if (!dataFolder.exists()) dataFolder.mkdirs();
        if (!backpacksFolder.exists()) backpacksFolder.mkdirs();
        if (!backupFolder.exists()) backupFolder.mkdirs();
        if (!guiFolder.exists()) guiFolder.mkdirs();
    }

    public static void loadYmlFiles()
    {
        try
        {
            configYml.load(configFile);
            inventoriesYml.load(inventoriesFile);
            databaseYml.load(databaseFile);

            deYml.load(deFile);
            enYml.load(enFile);
            esYml.load(esFile);
            frYml.load(frFile);
            plYml.load(plFile);
            trYml.load(trFile);
            viYml.load(viFile);
            zhSiYml.load(zhSiFile);
            zhTrYml.load(zhTrFile);

        }
        catch (IOException | InvalidConfigurationException e)
        {
            e.printStackTrace();
        }
    }

    public static YamlConfiguration getConfigYml()
    {
        return configYml;
    }
    public static YamlConfiguration getInventoriesYml()
    {
        return inventoriesYml;
    }
    public static YamlConfiguration getDatabaseYml()
    {
        return databaseYml;
    }

    public static void updateFiles()
    {
        YamlConfiguration yml = Main.getResourceYML("archive/2.1.1-BETA/config.yml");

        // Check if the file is outupdated
        
        // TODO: 30.09.2023 file updater 
    }

    private String readFileVersion(String filePath)
    {
        try
        {
            Path path = Paths.get(Data.pluginPath + filePath);
            UserDefinedFileAttributeView view = Files.getFileAttributeView(path, UserDefinedFileAttributeView.class);

            List<String> attributes = view.list();
            if (attributes.contains("version"))
            {
                ByteBuffer buffer = ByteBuffer.allocate(view.size("version"));
                view.read("version", buffer);
                buffer.flip();
                return StandardCharsets.UTF_8.decode(buffer).toString();
            }
            else
            {
                Main.errLog("Error updating file: This file cannot be updated.");
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }
}
