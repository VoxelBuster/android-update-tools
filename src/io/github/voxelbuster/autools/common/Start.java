package io.github.voxelbuster.autools.common;

import io.github.voxelbuster.autools.api.Globals;
import io.github.voxelbuster.autools.ui.ParentWindow;

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
           InputStreamReader isr =  new InputStreamReader(Globals.runtime.exec("res\\adb\\adb.exe devices").getInputStream());
            BufferedReader br = new BufferedReader(isr);

            String line = null;

            while ((line = br.readLine()) != null)
                if (line.startsWith("List")) {
                    continue;
                } else {
                    Globals.devices.add(line);
                }
                System.out.println(line);

        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "ADB did not run properly on startup.\nPlease make sure it is in the adb folder or reinstall.", "ADB Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
