package io.github.voxelbuster.autools.ui;

import io.github.voxelbuster.autools.api.Globals;
import se.vidstige.jadb.managers.PropertyManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class FlashPane extends JTabbedPane {
    public FlashPane(ParentWindow parent) {
        String codename = "";
        try {
            codename = new PropertyManager(Globals.selectedDevice).getprop().get("ro.product.model");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Could not get device codename. You will be unable to find roms automatically.", "build.prop error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel selectRom = new JPanel();
        selectRom.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        selectRom.setLayout(new GridLayout(0, 1));

        JPanel flashOpt = new JPanel();
        flashOpt.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        flashOpt.setLayout(new GridLayout(0, 1));

        this.addTab("Select Rom", selectRom);
        this.addTab("Flash Options", flashOpt);

        JList<String> romList = new JList<>();
        JPanel romButtons = new JPanel();

        JButton cancelButton = new JButton("Cancel");
        JButton addButton = new JButton("Add");
        JButton rmButton = new JButton("Remove");

        cancelButton.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parent.setWindowState(ParentWindow.WindowState.HOME);
            }
            @Override
            public void mousePressed(MouseEvent e) {}
            @Override
            public void mouseReleased(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });
    }
}
