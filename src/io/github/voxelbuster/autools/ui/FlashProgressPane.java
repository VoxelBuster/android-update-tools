package io.github.voxelbuster.autools.ui;

import io.github.voxelbuster.autools.api.Globals;

import javax.swing.*;
import java.awt.*;

public class FlashProgressPane extends JPanel {

    public int progress = 0;

    public String statusStr = "";

    private JProgressBar progressBar = new JProgressBar();
    private JLabel statusLabel = new JLabel();
    private JTextArea logcat = new JTextArea();

    public FlashProgressPane(ParentWindow parent) {
        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        setLayout(new GridLayout(0, 1));

        JLabel header = new JLabel("Flashing Progress");
        header.setFont(new Font("Helvetica", 18, Font.BOLD));

        progressBar.setMinimum(0);

        int operations = 0;
        operations += Globals.roms.size();
        if (Globals.wipeCache) operations++;
        if (Globals.wipeDalvik) operations++;
        if (Globals.clean) operations++;
        if (Globals.backupSystem) operations++;
        if (Globals.backupData) operations++;

        progressBar.setMaximum(operations);

        this.add(header);
        this.add(progressBar);
        this.add(statusLabel);
        this.add(logcat);

    }

    public void setProgress(int progress) {
        this.progress = progress;
        progressBar.setValue(progress);
    }

    public int getProgress() {
        return this.progress;
    }

    public void setStatus(String statusStr) {
        this.statusStr = statusStr;
        this.statusLabel.setText(statusStr);
    }

    public String getStatusStr() {
        return this.statusStr;
    }

    public void print(String s) {
        logcat.append(s);
    }

    public void println(String s) {
        logcat.append(s);
        logcat.append("\n");
    }

}
