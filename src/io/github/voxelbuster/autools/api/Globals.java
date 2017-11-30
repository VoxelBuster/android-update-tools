package io.github.voxelbuster.autools.api;

import io.github.voxelbuster.autools.ui.ParentWindow;

import java.awt.*;
import java.util.ArrayList;

public class Globals {
    public static final String WINDOW_TITLE = "Android Update Tools";
    public static final Dimension DEFAULT_DIM = new Dimension(1280, 1024);

    public static String os_name, java_version;

    public static ParentWindow parentWindow;

    public static Runtime runtime;
    
    public static ArrayList<String> devices = new ArrayList<>();
}
