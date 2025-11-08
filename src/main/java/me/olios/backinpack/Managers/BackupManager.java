package me.olios.backinpack.Managers;

import me.olios.backinpack.Data;
import me.olios.backinpack.Main;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class BackupManager {

    public static void createBackup()
    {
        try
        {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
            String date = simpleDateFormat.format(new Date());

            int i = 1;
            while (new File(Data.backupPath,
                    Data.pluginVersion + "_" + date + "_" + i + ".zip").exists())
            {
                i++;
            }

            final List<String> srcFiles = new ArrayList<>();
            srcFiles.add(Data.pluginPath + "data");
            srcFiles.add(Data.pluginPath + "languages");
            srcFiles.add(Data.pluginPath + "config.yml");
            srcFiles.add(Data.pluginPath + "database.yml");

            final FileOutputStream fos = new FileOutputStream(Data.backupPath + Data.pluginVersion + "_" + date + "_" + i + ".zip");
            ZipOutputStream zipOut = new ZipOutputStream(fos);

            for (String srcFile : srcFiles)
            {
                File fileToZip = new File(srcFile);
                if (fileToZip.isDirectory())
                {
                    zipFile(fileToZip, fileToZip.getName(), zipOut);
                }
                else
                {
                    FileInputStream fis = new FileInputStream(fileToZip);
                    ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                    zipOut.putNextEntry(zipEntry);

                    byte[] bytes = new byte[1024];
                    int length;
                    while((length = fis.read(bytes)) >= 0) {
                        zipOut.write(bytes, 0, length);
                    }
                    fis.close();
                }
            }

            zipOut.close();
            fos.close();
        }
        catch (IOException e)
        {
            Main.errLog("Backup could not be created!");
        }
    }

    public static void deleteOldBackups()
    {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy");
        long currentTimestamp = new Date().getTime() / 1000;
        long WEEK = 604800;

        try
        {
            File[] backupFiles = new File(Data.backupPath).listFiles();

            for (File file : backupFiles)
            {
                String fileDate = file.getName().split("_")[1];
                long createBackupTimestamp = simpleDateFormat.parse(fileDate).getTime() / 1000;

                if (currentTimestamp - WEEK > createBackupTimestamp)
                {
                    file.delete();
                }
            }

        }
        catch (ParseException e)
        {
            Main.errLog("The old backup could not be deleted!");
        }
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut)
    {
        try
        {
            if (fileToZip.isHidden())
            {
                return;
            }
            if (fileToZip.isDirectory())
            {
                if (fileName.endsWith("/")) {
                    zipOut.putNextEntry(new ZipEntry(fileName));
                    zipOut.closeEntry();
                } else
                {
                    zipOut.putNextEntry(new ZipEntry(fileName + "/"));
                    zipOut.closeEntry();
                }
                File[] children = fileToZip.listFiles();
                for (File childFile : children)
                {
                    zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
                }
                return;
            }
            FileInputStream fis = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileName);
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0)
            {
                zipOut.write(bytes, 0, length);
            }
            fis.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
