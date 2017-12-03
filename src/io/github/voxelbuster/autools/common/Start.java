package io.github.voxelbuster.autools.common;

import io.github.voxelbuster.autools.api.Globals;
import io.github.voxelbuster.autools.api.ResourceManager;
import io.github.voxelbuster.autools.ui.ParentWindow;
import jdk.nashorn.internal.scripts.JO;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

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
        new File("devices.txt").delete();
        Globals.runtime = Runtime.getRuntime();
        try {
            InputStreamReader isr;
            if (Globals.os_name.contains("Windows")) {
                isr = new InputStreamReader(Globals.runtime.exec("res/adb/adb.exe devices").getInputStream());
            } else {
                isr = new InputStreamReader(Globals.runtime.exec("adb devices").getInputStream());
            }
            BufferedReader br = new BufferedReader(isr);

            String line = null;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("List")) {
                    continue;
                } else {
                    Globals.devices.add(line);
                }
                System.out.println(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
            if (Globals.os_name.contains("Windows")) {
                JOptionPane.showMessageDialog(null, "ADB did not run properly on startup.\nPlease make sure it is in the adb folder or reinstall.", "ADB Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "ADB is not properly installed on your computer.\nPlease install it in order to continue.\nhttps://developer.android.com/studio/releases/platform-tools.html", "ADB Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
