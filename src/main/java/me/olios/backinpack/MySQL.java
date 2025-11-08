package me.olios.backinpack;

import me.olios.backinpack.Managers.FilesManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import java.sql.*;

public class MySQL {

    public static Connection connection;
    public static boolean isConnected = false;


    private static String HOST;
    private static String PORT;
    private static String USER;
    private static String PASSWORD;
    private static String DATABASE;

    public static void connect()
    {
        YamlConfiguration databaseYml = FilesManager.getDatabaseYml();
        HOST = databaseYml.getString("host");
        PORT = databaseYml.getString("port");
        USER = databaseYml.getString("user");
        PASSWORD = databaseYml.getString("password");
        DATABASE = databaseYml.getString("database");

        try
        {
            connection = DriverManager.getConnection("jdbc:mysql://"+HOST+":"+PORT+"/", USER, PASSWORD);

            isConnected = true;
            checkDatabase();
            Main.log("Connected to the MySQL database.");
        }
        catch (SQLException e)
        {
            Main.errLog("Unable to connect to the database " + e.getMessage());
        }

        Bukkit.getScheduler().runTaskTimer(Data.plugin, () ->
        {
            if (!checkConnection() || connection == null)
            {
                isConnected = false;

                try
                {
                    connection = DriverManager.getConnection("jdbc:mysql://"+HOST+":"+PORT+"/", USER, PASSWORD);
                    isConnected = true;
                    checkDatabase();
                    Main.log("Reconnected to MySQL database.");
                    Main.warnLog("It is recommended to restart the server after reconnecting to the database.");
                }
                catch (SQLException e)
                {
                    Main.warnLog("Failed to connect to MySQL database. Retrying in 5 seconds...");
                }
            }
        }, 0, 100);
    }

    private static boolean checkConnection()
    {
        try (Connection con = DriverManager.getConnection("jdbc:mysql://"+HOST+":"+PORT+"/", USER, PASSWORD))
        {
            return con.isValid(5);
        }
        catch (SQLException e)
        {
            connection = null;
            return false;
        }
    }


    private static void checkDatabase()
    {
        PreparedStatement preparedStatement = null;

        if (!isConnected) return;

        try
        {
            ResultSet resultSet = connection.getMetaData().getCatalogs();
            boolean databaseExist = false;

            while (resultSet.next())
            {
                String databaseName = resultSet.getString(1);
                if (databaseName.equals(DATABASE)) databaseExist = true;
            }
            resultSet.close();

            if (databaseExist)
            {
                preparedStatement = connection.prepareStatement("USE `"+DATABASE+"`");
                preparedStatement.execute();
            } else
            {
                preparedStatement = connection.prepareStatement("CREATE DATABASE " + DATABASE);
                preparedStatement.execute();
                preparedStatement = connection.prepareStatement("USE `"+DATABASE+"`");
                preparedStatement.execute();
            }

            /**
             * Tables
             */
            preparedStatement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " +
                    "`backpacks` (\n" +
                    "  `uuid` varchar(36) NOT NULL,\n" +
                    "  `username` text NOT NULL,\n" +
                    "  `backpacks` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin NOT NULL CHECK (json_valid(`backpacks`)),\n" +
                    "  PRIMARY KEY (`uuid`),\n" +
                    "  UNIQUE KEY `uuid` (`uuid`)\n" +
                    ") ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci");
            preparedStatement.execute();

            preparedStatement.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            Main.errLog("Unable to verify that the database is properly constructed");
        }
    }

    public static boolean querySet(String query) // INSERT INTO, UPDATE
    {
        boolean correctSave = true;
        try
        {
            PreparedStatement preparedStatement = null;
            preparedStatement = connection.prepareStatement("USE `"+DATABASE+"`");
            preparedStatement.execute();

            if (!isConnected) return false;

            preparedStatement = connection.prepareStatement(query);
            preparedStatement.executeUpdate();

            preparedStatement.close();
        } catch(SQLException e)
        {
            e.printStackTrace();
            Main.errLog("Unable to update data in database");
            correctSave = false;
        }
        return correctSave;
    }

    public static ResultSet queryGet(String query) // SELECT
    {
        ResultSet resultSet = null;
        try
        {
            PreparedStatement preparedStatement = null;
            preparedStatement = connection.prepareStatement("USE `"+DATABASE+"`");
            preparedStatement.execute();

            if (!isConnected) return resultSet;

            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return resultSet;
    }

    public static boolean resultNotNull(ResultSet resultSet)
    {
        try
        {
            while (resultSet.next())
            {
                return true;
            }
            resultSet.close();
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }

}
