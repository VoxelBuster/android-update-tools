package io.github.voxelbuster.autools.ui;

import io.github.voxelbuster.autools.api.Globals;

import javax.swing.*;
import java.awt.*;

public class ParentWindow extends JFrame {

    public ParentWindow() {
        this.setTitle(Globals.WINDOW_TITLE);
        this.setSize(Globals.DEFAULT_DIM);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(true);
        this.setLayout(new FlowLayout());
        this.setVisible(true);
    }
}
