package io.github.voxelbuster.autools.common;

import io.github.voxelbuster.autools.api.Globals;
import io.github.voxelbuster.autools.api.ResourceManager;
import io.github.voxelbuster.autools.ui.ParentWindow;
import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;

import javax.swing.*;
import java.util.ArrayList;

public class Start {

    public static void main(String[] args) throws Exception {
        Globals.os_name = System.getProperty("os.name");
        Globals.java_version = System.getProperty("java.version");

        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        ResourceManager.init();

        Thread guiThread = new Thread(new Runnable() {
            @Override
            public void run() {
                Globals.parentWindow = new ParentWindow();
            }
        });

        guiThread.start();

        // TODO launch adb server so user can run without adb installed

        Globals.adbConn = new JadbConnection();
        Globals.devices = (ArrayList<JadbDevice>) Globals.adbConn.getDevices();

    }
}
