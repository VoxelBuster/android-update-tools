package io.github.voxelbuster.autools.api;

import io.github.voxelbuster.autools.ui.ParentWindow;
import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;

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
}
