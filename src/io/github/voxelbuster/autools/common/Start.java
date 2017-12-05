package io.github.voxelbuster.autools.common;

import io.github.voxelbuster.autools.api.Globals;
import io.github.voxelbuster.autools.api.ResourceManager;
import io.github.voxelbuster.autools.ui.ParentWindow;
import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;

import javax.swing.*;
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

        Thread guiThread = new Thread(() -> Globals.parentWindow = new ParentWindow());

        guiThread.start();

        // TODO launch adb server so user can run without adb installed
        Thread adbServerStart = new Thread(() -> {
            String adbPath = "";
            if (Globals.os_name.contains("Windows")) {
                adbPath = ResourceManager.getPath("adb_win");
            } else if (Globals.os_name.contains("Mac")) {
                adbPath = ResourceManager.getPath("adb_mac");
            } else {
                adbPath = ResourceManager.getPath("adb_linux");
            }
            try {
                //if (Globals.os_name.contains("nux")) Globals.runtime.exec("gksu chmod +x " + adbPath +  "adb");
                Globals.runtime.exec(adbPath + "adb start-server");
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "adb start-server failed. Please make sure the correct adb is in the res directory and is executable.", "ADB error", JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });

        adbServerStart.start();

        Globals.adbConn = new JadbConnection();
        Globals.devices = (ArrayList<JadbDevice>) Globals.adbConn.getDevices();

    }
}
