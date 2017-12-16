package io.github.voxelbuster.autools.api;

import io.github.voxelbuster.autools.ui.FlashProgressPane;
import io.github.voxelbuster.autools.ui.ParentWindow;
import jdk.nashorn.internal.scripts.JO;
import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;

public class Globals {
    public static String os_name, java_version;

    public static final String WINDOW_TITLE = "Android Update Tools";
    public static final Dimension DEFAULT_DIM = new Dimension(1280, 1024);

    public static ParentWindow parentWindow;

    public static JadbConnection adbConn;
    public static ArrayList<JadbDevice> devices;
    public static JadbDevice selectedDevice;

    public static ArrayList<String> roms = new ArrayList<>();
    public static ArrayList<Task.Partition> partitions = new ArrayList<>();
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
                    if (backupSystem) {
                        ui.setStatus("Backing up system");
                        String backupDir = System.getProperty("user.home") + "/android_update_tools/backups/";
                        String backupName = "system_backup-" + Calendar.getInstance().getTime().toString().replace(" ", "_") + ".ab";
                        JOptionPane.showMessageDialog(null, "Your device may ask you to confirm backing up data. Confirm it to continue.");
                        for (String ln : Commands.runCommand("adb backup -system -f " + backupDir + backupName)) {
                            System.out.println(ln);
                            ui.println(ln);
                        }
                        ui.setProgress(ui.getProgress() + 1);
                    }
                    if (backupData) {
                        ui.setStatus("Backing up data");
                        String backupDir = System.getProperty("user.home") + "/android_update_tools/backups/";
                        String backupName = "data_backup-" + Calendar.getInstance().getTime().toString().replace(" ", "_") + ".ab";
                        JOptionPane.showMessageDialog(null, "Your device may ask you to confirm backing up data. Confirm it to continue.");
                        for (String ln : Commands.runCommand("adb backup -apk -shared -all -f " + backupDir + backupName)) {
                            System.out.println(ln);
                            ui.println(ln);
                        }
                        ui.setProgress(ui.getProgress() + 1);
                    }
                    if (Globals.clean) {
                        ui.setStatus("Rebooting to bootloader...");
                        selectedDevice.executeShell("reboot", "fastboot");
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
                        ui.setStatus("Cleaning device");
                        ui.println("Cleaning Data, System, and Cache. Recovery and Boot will be left alone.");
                        for (String ln : Commands.runCommand("fastboot erase data")) {
                            if (ln.contains(deviceHash)) {
                                rebooted = true;
                            }
                        }
                        for (String ln : Commands.runCommand("fastboot erase system")) {
                            if (ln.contains(deviceHash)) {
                                rebooted = true;
                            }
                        }
                        for (String ln : Commands.runCommand("fastboot erase cache")) {
                            if (ln.contains(deviceHash)) {
                                rebooted = true;
                            }
                        }
                        ui.setProgress(ui.getProgress() + 1);
                    }

                    boolean flashBoot = false;
                    for (String rom : roms) {
                        if (rom.endsWith(".img")) {
                            flashBoot = true;
                        }
                    }
                    if (flashBoot) {
                        ui.setStatus("Rebooting to bootloader...");
                        selectedDevice.executeShell("reboot", "fastboot");
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
                                    "This will likely wipe your device. Are you sure you want to continue?\nWARNING:" +
                                    "If OEM unlocking is not enabled in your device settings, do not continue as it may brick your device!", "Unlock warning", JOptionPane.YES_NO_OPTION);
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
                        for (int i = 0; i < roms.size(); i++) {
                            switch (partitions.get(i)) {
                                case RECOVERY:
                                    if (roms.get(i).endsWith(".img")) {
                                        for (String o : Commands.runCommand("fastboot flash recovery " + roms.get(i))) {
                                            System.out.println(o);
                                            ui.println(o);
                                        }
                                        roms.remove(i);
                                        ui.setProgress(ui.getProgress() + 1);
                                    }
                                    break;
                                case BOOT:
                                    if (roms.get(i).endsWith(".img")) {
                                        for (String o : Commands.runCommand("fastboot flash boot " + roms.get(i))) {
                                            System.out.println(o);
                                            ui.println(o);
                                        }
                                        roms.remove(i);
                                        ui.setProgress(ui.getProgress() + 1);
                                    }
                                    break;
                                case SYSTEM:
                                    if (roms.get(i).endsWith(".img")) {
                                        for (String o : Commands.runCommand("fastboot flash system " + roms.get(i))) {
                                            System.out.println(o);
                                            ui.println(o);
                                        }
                                        roms.remove(i);
                                        ui.setProgress(ui.getProgress() + 1);
                                    }
                                    break;
                                default:
                                    System.err.println("Not a flashable image.");
                            }
                        }
                    }
                    ui.setStatus("Rebooting to recovery...");
                    selectedDevice.executeShell("reboot", "recovery");
                    boolean rebooted = false;
                    String deviceHash = selectedDevice.getSerial();
                    while (!rebooted) {
                        for (String ln : Commands.runCommand("adb devices")) {
                            if (ln.contains(deviceHash)) {
                                rebooted = true;
                            }
                        }
                    }
                    for (String rom : roms) {
                        if (rom.endsWith(".zip")) {
                            ui.setStatus("Sideloading firmware " + rom);
                            for (String o : Commands.runCommand("adb sideload " + rom)) {
                                System.out.println(o);
                                ui.println(o);
                            }
                            ui.setProgress(ui.getProgress() + 1);
                        }
                    }
                    if (wipeCache) {
                        ui.setStatus("Wiping cache");
                        for (String o : Commands.runCommand("adb shell recovery --wipe_cache")) {
                            System.out.println(o);
                            ui.println(o);
                        }
                        ui.setProgress(ui.getProgress() + 1);
                    }
                    if (wipeDalvik) {
                        ui.setStatus("Wiping Dalvik");
                        for (String o : Commands.runCommand("adb shell rm -r /data/dalvik-cache")) {
                            System.out.println(o);
                            ui.println(o);
                        }
                        for (String o : Commands.runCommand("adb shell rm -r /cache/dalvik-cache")) {
                            System.out.println(o);
                            ui.println(o);
                        }
                        ui.setProgress(ui.getProgress() + 1);
                    }
                    ui.setStatus("Rebooting to system");
                    for (String o : Commands.runCommand("adb reboot system")) {
                        System.out.println(o);
                        ui.println(o);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "An exception occurred while flashing.\n" +
                            "If the problem continues please copy the console log and contact the developer.", "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                    parentWindow.setWindowState(ParentWindow.WindowState.HOME);
                }
            }
        });

        flashThread.start();
    }
}
