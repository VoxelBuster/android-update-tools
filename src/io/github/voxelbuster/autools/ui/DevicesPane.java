package io.github.voxelbuster.autools.ui;

import io.github.voxelbuster.autools.api.Globals;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;
import se.vidstige.jadb.managers.PropertyManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class DevicesPane extends JPanel {
    JList<String> deviceList;

    private int selected = -1;

    public DevicesPane(ParentWindow parent) throws Exception {
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.setLayout(new GridLayout(0, 1));

        JLabel header = new JLabel("Select Device", SwingConstants.CENTER);

        header.setFont(new Font("Helvetica", Font.BOLD, 28));

        DefaultListModel<String> listModel = new DefaultListModel<>();

        deviceList = new JList<>(listModel);

        refreshDevices();

        JPanel buttonsPanel = new JPanel();

        JButton cancelButton = new JButton("Cancel");
        cancelButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parent.setWindowState(ParentWindow.WindowState.HOME);
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        JButton refreshButton = new JButton("Refresh");
        refreshButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    refreshDevices();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (JadbException e1) {
                    e1.printStackTrace();
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        JButton nextButton = new JButton("Next >>");
        nextButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selected = deviceList.getSelectedIndex();
                if (selected > -1) {
                    Globals.selectedDevice = Globals.devices.get(selected);
                    parent.setWindowState(ParentWindow.WindowState.INTENT);
                } else {
                    JOptionPane.showMessageDialog(null, "No device selected. Please select a device with USB debugging turned on.", "No device", JOptionPane.INFORMATION_MESSAGE);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        buttonsPanel.add(refreshButton);
        buttonsPanel.add(cancelButton);
        buttonsPanel.add(nextButton);

        this.add(header);
        this.add(deviceList);
        this.add(buttonsPanel);
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
