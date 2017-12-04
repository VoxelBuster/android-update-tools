package io.github.voxelbuster.autools.ui;

import io.github.voxelbuster.autools.api.Globals;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;
import se.vidstige.jadb.managers.PropertyManager;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class DevicesPane extends JPanel {
    JList<String> deviceList;

    public DevicesPane(ParentWindow parent) throws IOException, JadbException {
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.setLayout(new GridLayout(0, 1));

        JLabel header = new JLabel("Select Device", SwingConstants.CENTER);

        header.setFont(new Font("Helvetica", Font.BOLD, 28));

        DefaultListModel<String> listModel = new DefaultListModel<>();

        for (JadbDevice device : Globals.devices) {
            Map<String, String> propMap = new PropertyManager(device).getprop();
            String productName = propMap.get("ro.product.device");
            listModel.addElement(productName);
        }

        deviceList = new JList<>(listModel);

        this.add(header);
    }

    public void refreshDevices() throws IOException, JadbException {
        Globals.devices = (ArrayList<JadbDevice>) Globals.adbConn.getDevices();

        DefaultListModel<String> listModel = new DefaultListModel<>();

        for (JadbDevice device : Globals.devices) {
            Map<String, String> propMap = new PropertyManager(device).getprop();
            String productName = propMap.get("ro.product.device");
            listModel.addElement(productName);
        }

        deviceList.setModel(listModel);
    }
}
