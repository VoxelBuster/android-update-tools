package io.github.voxelbuster.autools.ui;

import io.github.voxelbuster.autools.api.ResourceManager;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class HomePane extends JPanel {

    public HomePane(ParentWindow parent) {
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.setLayout(new GridLayout(0, 1));

        JLabel header = new JLabel("What would you like to do?", SwingConstants.CENTER);

        JPanel flash = new JPanel();
        JPanel backup = new JPanel();
        JPanel restore = new JPanel();

        flash.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parent.setWindowState(ParentWindow.WindowState.DEVICES);
                parent.setIntent(ParentWindow.Intent.FLASH);
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

        backup.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parent.setWindowState(ParentWindow.WindowState.DEVICES);
                parent.setIntent(ParentWindow.Intent.BACKUP);
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

        restore.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                parent.setWindowState(ParentWindow.WindowState.DEVICES);
                parent.setIntent(ParentWindow.Intent.RESTORE);
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

        flash.setLayout(new GridLayout());
        backup.setLayout(new GridLayout());
        restore.setLayout(new GridLayout());

        flash.add(new JLabel(ResourceManager.getIcon("flash_icon")));
        flash.add(new JLabel("Flash or Update"));

        flash.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, new Color(127, 127, 127), new Color(0, 0, 0)));

        backup.add(new JLabel(ResourceManager.getIcon("backup_icon")));
        backup.add(new JLabel("Back Up"));

        backup.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, new Color(127, 127, 127), new Color(0, 0, 0)));

        restore.add(new JLabel(ResourceManager.getIcon("restore_icon")));
        restore.add(new JLabel("Restore or Wipe"));

        restore.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED, new Color(127, 127, 127), new Color(0, 0, 0)));

        header.setFont(new Font("Helvetica", Font.BOLD, 28));

        this.add(header);

        this.add(flash);
        this.add(backup);
        this.add(restore);

    }
}
