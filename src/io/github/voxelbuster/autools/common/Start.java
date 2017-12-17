package io.github.voxelbuster.autools.common;

import io.github.voxelbuster.autools.api.ApiCommands;
import io.github.voxelbuster.autools.api.Globals;
import io.github.voxelbuster.autools.api.ResourceManager;
import io.github.voxelbuster.autools.ui.ParentWindow;
import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Start {

    public static void main(String[] args) throws Exception {
        Globals.os_name = System.getProperty("os.name");
        Globals.java_version = System.getProperty("java.version");

        System.getProperties().list(System.out);

        Globals.runtime = Runtime.getRuntime();

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        ResourceManager.init();

        if (Globals.os_name.contains("Windows")) {
            Globals.adbPath = ResourceManager.getPath("adb_win");
        } else if (Globals.os_name.contains("Mac")) {
            Globals.adbPath = ResourceManager.getPath("adb_mac");
        } else {
            Globals.adbPath = ResourceManager.getPath("adb_linux");
        }
        if (!(new File(Globals.adbPath + "adb.exe").exists() || new File(Globals.adbPath + "adb").exists())) {
            ResourceManager.fetchADB();
        }
        try {
            if (Globals.os_name.contains("nux")) {
                for (String ln : ApiCommands.concat(ApiCommands.runCommand("chmod +x " + Globals.adbPath + "adb"),
                        ApiCommands.runCommand("chmod +x " + Globals.adbPath + "fastboot"))) {
                    System.out.println(ln);
                }
            }
            for (String ln : ApiCommands.runCommand(Globals.adbPath + "adb start-server")) {
                System.out.println(ln);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "adb start-server failed. Please make sure the " +
                            "correct adb is in the res directory and is executable.", "ADB error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }

        Thread guiThread = new Thread(() -> Globals.parentWindow = new ParentWindow());

        guiThread.start();

        Globals.adbConn = new JadbConnection();
        Globals.devices = (ArrayList<JadbDevice>) Globals.adbConn.getDevices();

    }
}
