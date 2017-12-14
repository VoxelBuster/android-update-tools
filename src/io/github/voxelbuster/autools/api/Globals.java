package io.github.voxelbuster.autools.api;

import io.github.voxelbuster.autools.ui.FlashProgressPane;
import io.github.voxelbuster.autools.ui.ParentWindow;
import jdk.nashorn.internal.scripts.JO;
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

    public static Thread flashThread;

    // TODO flash all images in correct order
    // TODO mark the partition that each fb image is to be flashed to
    // TODO finish and clean code
    public static void runFlash(FlashProgressPane ui) {
        flashThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (Globals.clean) {
                        boolean flashBoot = false;
                        for (String rom: roms) {
                            if (rom.endsWith(".img")) {
                                flashBoot = true;
                            }
                        }
                        if (flashBoot) {
                            ui.setStatus("Rebooting to bootloader...");
                            selectedDevice.executeShell("reboot", "bootloader");
                            //wait for device to come back online
                            boolean rebooted = false;
                            String deviceHash = selectedDevice.getSerial();
                            while (!rebooted) {
                                for (String ln : Commands.runCommand("fastboot devices")) {
                                    if (ln.contains(deviceHash)) {
                                        rebooted = true;
                                    }
                                }
                            }
                            ui.setStatus("Getting device info");
                            Commands.runCommand("fastboot -s " + deviceHash);
                            boolean isUnlocked = false;
                            for (String ln : Commands.runCommand("fastboot oem device-info")) {
                                if (ln.contains("unlocked") && ln.contains("true")) {
                                    isUnlocked = true;
                                }
                                System.out.println(ln);
                                ui.println(ln);
                            }
                            if (!isUnlocked) {
                                int choice = JOptionPane.showConfirmDialog(null, "Your bootloader is locked and will need to be unlocked.\n" +
                                        "This will likely wipe your device. Are you sure you want to continue?","Unlock warning",JOptionPane.YES_NO_OPTION);
                                if (choice == JOptionPane.YES_OPTION) {
                                    ui.setStatus("Unlocking bootloader");
                                    for (String o : Commands.runCommand("fastboot oem unlock")) {
                                        System.out.println(o);
                                        ui.println(o);
                                    }
                                } else {
                                    ui.setStatus("Canceling");
                                    ui.setProgress(65535);
                                    return;
                                }
                            }
                            for (String rom : roms) {
                                if (rom.endsWith(".img")) {
                                    for(String o : Commands.runCommand("fastboot flash recovery " + rom)) {
                                        System.out.println(o);
                                        ui.println(o);
                                    }
                                }
                            }
                        }
                        ui.setStatus("Rebooting to recovery...");
                        selectedDevice.executeShell("reboot", "recovery");
                        //wait for device to come back online
                        //TODO finish task

                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "An exception occurred while flashing.\n" +
                            "If the problem continues please copy the console log and contact the developer.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        flashThread.start();
    }
}
