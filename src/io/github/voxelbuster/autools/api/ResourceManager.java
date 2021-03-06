package io.github.voxelbuster.autools.api;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ResourceManager {
    private static HashMap<String, String> paths = new HashMap<>();

    public static void init() {
        String[] keys = {
                "adb_win",
                "adb_linux",
                "adb_mac",
                "app_icon",
                "flash_icon",
                "backup_icon",
                "restore_icon",
                "romsdb",
                "adb_dl"
        };

        String[] vals = {
                "res/adb/win/",
                "res/adb/linux/",
                "res/adb/mac/",
                "res/img/app_icon.png",
                "res/img/flash_icon.png",
                "res/img/backup_icon.png",
                "res/img/restore_icon.png",
                "res/data/romsdb.csv",
                "https://dl.google.com/android/repository/platform-tools-latest-"
        };

        for (int i = 0; i < keys.length; i++) {
            paths.put(keys[i], vals[i]);
        }
    }

    public static ImageIcon getIcon(String key) {
        return new ImageIcon(paths.get(key));
    }

    public static String getPath(String key) {
        return paths.get(key);
    }

    public static void fetchADB() {
        String adbPath;
        String suffix;
        if (Globals.os_name.contains("Windows")) {
            adbPath = getPath("adb_win");
            suffix = "windows.zip";
        } else if (Globals.os_name.contains("Mac")) {
            adbPath = getPath("adb_mac");
            suffix = "darwin.zip";
        } else {
            adbPath = getPath("adb_linux");
            suffix = "linux.zip";
        }
        URL resource = null;
        try {
            resource = new URL(getPath("adb_dl") + suffix);
        } catch (Exception e) {
            System.err.println("Bad download URL");
            JOptionPane.showMessageDialog(null, "Could not download ADB, please install it manually", "Download Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        ReadableByteChannel rbc = null;
        try {
            rbc = Channels.newChannel(resource.openStream());
            FileOutputStream fos = new FileOutputStream("res/adb/adb-temp.zip");
            fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Could not download ADB, please install it manually", "Download Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        unzip("res/adb/adb-temp.zip", getPath(adbPath));
    }

    public static void unzip(String zipFilePath, String destDir) {
        File dir = new File(destDir);
        if (!dir.exists()) dir.mkdirs();
        FileInputStream fis;
        byte[] buffer = new byte[1024];
        try {
            fis = new FileInputStream(zipFilePath);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = ze.getName();
                File newFile = new File(destDir + File.separator + fileName);
                System.out.println("Unzipping to " + newFile.getAbsolutePath());
                new File(newFile.getParent()).mkdirs();
                FileOutputStream fos = new FileOutputStream(newFile);
                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }
                fos.close();
                zis.closeEntry();
                ze = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
