package io.github.voxelbuster.autools.ui;

import javax.swing.*;
import java.awt.*;

public class DevicesPane extends JPanel {
    public DevicesPane(ParentWindow parent) {
        this.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        this.setLayout(new GridLayout(0, 1));

        JLabel header = new JLabel("Select Device", SwingConstants.CENTER);

        header.setFont(new Font("Helvetica", Font.BOLD, 28));

        this.add(header);
    }
}
