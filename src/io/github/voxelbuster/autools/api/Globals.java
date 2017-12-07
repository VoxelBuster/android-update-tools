package io.github.voxelbuster.autools.api;

import io.github.voxelbuster.autools.ui.FlashProgressPane;
import io.github.voxelbuster.autools.ui.ParentWindow;
import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class Globals {
    public static String os_name, java_version;

    public static final String WINDOW_TITLE = "Android Update Tools";
    public static final Dimension DEFAULT_DIM = new Dimension(1280, 1024);

    public static ParentWindow parentWindow;

    public static JadbConnection adbConn;
    public static ArrayList<JadbDevice> devices;
    public static JadbDevice selectedDevice;

    public static ArrayList<String> roms = new ArrayList<>();
    public static boolean wipeDalvik, wipeCache, backupSystem, backupData, clean;

    public static Runtime runtime;

    public static void runFlash(FlashProgressPane ui) {
        Thread flashThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (Globals.clean) {
                        ui.setStatus("Rebooting to recovery...");
                        selectedDevice.executeShell("reboot", "recovery");
                        //wait for device to come back online
                        //TODO finish task
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "An exception occoured while flashing.\n" +
                            "If the problem continues please copy the console log and contact the developer.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        flashThread.start();
    }
}
