package io.github.voxelbuster.autools.ui;

import javax.swing.*;
import java.awt.*;

public class FlashProgressPane extends JPanel {

    public FlashProgressPane(ParentWindow parent) {
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setLayout(new GridLayout(0, 1));

        JLabel header = new JLabel("Flashing Progress");
        header.setFont(new Font("Helvetica", 18, Font.BOLD));

        JProgressBar progressBar = new JProgressBar();
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);

        JLabel status = new JLabel();

        JTextArea logcat = new JTextArea();

        this.add(header);
        this.add(progressBar);
        this.add(status);
        this.add(logcat);
    }
}
