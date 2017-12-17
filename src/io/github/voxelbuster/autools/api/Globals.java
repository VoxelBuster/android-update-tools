package io.github.voxelbuster.autools.api;

import io.github.voxelbuster.autools.ui.FlashProgressPane;
import io.github.voxelbuster.autools.ui.ParentWindow;
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
    public static String adbPath;

    // TODO finish and clean code
    public static void runFlash(FlashProgressPane ui) {
        flashThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (backupSystem) {
                        ui.setStatus("Backing up system");
                        String backupDir = System.getProperty("user.home") + "/android_update_tools/backups/";
                        String backupName = "system_backup-" + Calendar.getInstance().getTime().toString()
                                .replace(" ", "_") + ".ab";
                        JOptionPane.showMessageDialog(null, "Your device may ask you to " +
                                "confirm backing up data. Confirm it to continue.");
                        for (String ln : ApiCommands.runAdbCommand(selectedDevice, "backup", "-system",
                                "-f", backupDir + backupName)) {
                            System.out.println(ln);
                            ui.println(ln);
                        }
                        ui.setProgress(ui.getProgress() + 1);
                    }
                    if (backupData) {
                        ui.setStatus("Backing up data");
                        String backupDir = System.getProperty("user.home") + "/android_update_tools/backups/";
                        String backupName = "data_backup-" + Calendar.getInstance().getTime().toString()
                                .replace(" ", "_") + ".ab";
                        JOptionPane.showMessageDialog(null, "Your device may ask you to " +
                                "confirm backing up data. Confirm it to continue.");
                        for (String ln : ApiCommands.runAdbCommand(selectedDevice, "backup", "-apk",
                                "-shared", "-all", backupDir + backupName)) {
                            System.out.println(ln);
                            ui.println(ln);
                        }
                        ui.setProgress(ui.getProgress() + 1);
                    }
                    if (Globals.clean) {
                        ui.setStatus("Rebooting to bootloader...");
                        for (String ln : ApiCommands.runAdbCommand(selectedDevice, "reboot",
                                "fastboot")) {
                            System.out.println(ln);
                            ui.println(ln);
                        }
                        //wait for device to come back online
                        boolean rebooted = false;
                        String deviceHash = selectedDevice.getSerial();
                        while (!rebooted) {
                            for (String ln : ApiCommands.runCommand(Globals.adbPath + "fastboot devices")) {
                                if (ln.contains(deviceHash)) {
                                    rebooted = true;
                                }
                            }
                        }
                        ui.setStatus("Cleaning device");
                        ui.println("Cleaning Data, System, and Cache. Recovery and Boot will be left alone.");
                        for (String ln : ApiCommands.runCommand(Globals.adbPath + "fastboot erase data")) {
                            if (ln.contains(deviceHash)) {
                                rebooted = true;
                            }
                        }
                        for (String ln : ApiCommands.runCommand(Globals.adbPath + "fastboot erase system")) {
                            if (ln.contains(deviceHash)) {
                                rebooted = true;
                            }
                        }
                        for (String ln : ApiCommands.runCommand(Globals.adbPath + "fastboot erase cache")) {
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
                        for (String ln : ApiCommands.runAdbShellCommand(selectedDevice, "reboot", "fastboot")) {
                            ui.println(ln);
                            System.out.println(ln);
                        }
                        //wait for device to come back online
                        boolean rebooted = false;
                        String deviceHash = selectedDevice.getSerial();
                        while (!rebooted) {
                            for (String ln : ApiCommands.runCommand(Globals.adbPath + "fastboot devices")) {
                                if (ln.contains(deviceHash)) {
                                    rebooted = true;
                                }
                            }
                        }
                        ui.setStatus("Getting device info");
                        ApiCommands.runCommand(Globals.adbPath + "fastboot -s " + deviceHash);
                        boolean isUnlocked = false;
                        for (String ln : ApiCommands.runCommand(Globals.adbPath + "fastboot oem device-info")) {
                            if (ln.contains("unlocked") && ln.contains("true")) {
                                isUnlocked = true;
                            }
                            System.out.println(ln);
                            ui.println(ln);
                        }
                        if (!isUnlocked) {
                            int choice = JOptionPane.showConfirmDialog(null, "Your bootloader " +
                                    "is locked and will need to be unlocked.\n" +
                                    "This will likely wipe your device. Are you sure you want to continue?\nWARNING:" +
                                    "If OEM unlocking is not enabled in your device settings, do not continue as it " +
                                    "may brick your device!", "Unlock warning", JOptionPane.YES_NO_OPTION);
                            if (choice == JOptionPane.YES_OPTION) {
                                ui.setStatus("Unlocking bootloader");
                                for (String ln : ApiCommands.runCommand(Globals.adbPath + "fastboot oem unlock")) {
                                    System.out.println(ln);
                                    ui.println(ln);
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
                                        for (String ln : ApiCommands.runCommand(Globals.adbPath + "fastboot flash recovery " +
                                                roms.get(i))) {
                                            System.out.println(ln);
                                            ui.println(ln);
                                        }
                                        roms.remove(i);
                                        ui.setProgress(ui.getProgress() + 1);
                                    }
                                    break;
                                case BOOT:
                                    if (roms.get(i).endsWith(".img")) {
                                        for (String ln : ApiCommands.runCommand(Globals.adbPath + "fastboot flash boot " +
                                                roms.get(i))) {
                                            System.out.println(ln);
                                            ui.println(ln);
                                        }
                                        roms.remove(i);
                                        ui.setProgress(ui.getProgress() + 1);
                                    }
                                    break;
                                case SYSTEM:
                                    if (roms.get(i).endsWith(".img")) {
                                        for (String ln : ApiCommands.runCommand(Globals.adbPath + "fastboot flash system " +
                                                roms.get(i))) {
                                            System.out.println(ln);
                                            ui.println(ln);
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
                    for (String ln : ApiCommands.runAdbShellCommand(selectedDevice, "reboot", "recovery")) {
                        ui.println(ln);
                        System.out.println(ln);
                    }
                    boolean rebooted = false;
                    String deviceHash = selectedDevice.getSerial();
                    while (!rebooted) {
                        for (String ln : ApiCommands.runCommand("adb devices")) {
                            if (ln.contains(deviceHash)) {
                                rebooted = true;
                            }
                        }
                    }
                    for (String rom : roms) {
                        if (rom.endsWith(".zip")) {
                            ui.setStatus("Sideloading firmware " + rom);
                            for (String ln : ApiCommands.runAdbCommand(selectedDevice, "sideload", rom)) {
                                System.out.println(ln);
                                ui.println(ln);
                            }
                            ui.setProgress(ui.getProgress() + 1);
                        }
                    }
                    if (wipeCache) {
                        ui.setStatus("Wiping cache");
                        for (String ln : ApiCommands.runAdbShellCommand(selectedDevice,"recovery", "--wipe_cache")) {
                            System.out.println(ln);
                            ui.println(ln);
                        }
                        ui.setProgress(ui.getProgress() + 1);
                    }
                    if (wipeDalvik) {
                        ui.setStatus("Wiping Dalvik");
                        for (String ln : ApiCommands.runAdbShellCommand(selectedDevice,"rm", "-r", "/data/dalvik-cache")) {
                            System.out.println(ln);
                            ui.println(ln);
                        }
                        for (String ln : ApiCommands.runAdbShellCommand(selectedDevice,"rm", "-r", "/cache/dalvik-cache")) {
                            System.out.println(ln);
                            ui.println(ln);
                        }
                        ui.setProgress(ui.getProgress() + 1);
                    }
                    ui.setStatus("Rebooting to system");
                    for (String ln : ApiCommands.runCommand("adb reboot system")) {
                        System.out.println(ln);
                        ui.println(ln);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, "An exception occurred while flashing." +
                                    "\nIf the problem continues please copy the console log and contact the developer.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                    parentWindow.setWindowState(ParentWindow.WindowState.HOME);
                }
            }
        });

        flashThread.start();
    }
}
