package io.github.voxelbuster.autools.ui;

import io.github.voxelbuster.autools.api.Globals;
import io.github.voxelbuster.autools.api.ResourceManager;

import javax.swing.*;

public class ParentWindow extends JFrame {

    private WindowState state = WindowState.HOME;
    private Intent intent;

    public enum WindowState {
        HOME,
        DEVICES,
        INTENT,
        FLASH_PROGRESS;
    }

    public enum Intent {
        FLASH,
        BACKUP,
        RESTORE
    }

    public ParentWindow() {
        this.setTitle(Globals.WINDOW_TITLE);
        this.setSize(Globals.DEFAULT_DIM);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setResizable(true);
        this.setWindowState(WindowState.HOME);
        this.setVisible(true);

        this.setIconImage(ResourceManager.getIcon("app_icon").getImage());
    }

    public void setWindowState(WindowState state) {
        this.state = state;
        switch (state) {
            case HOME:
                setContentPane(new HomePane(this));
                revalidate();
                break;
            case DEVICES:
                try {
                    setContentPane(new DevicesPane(this));
                } catch (Exception e) {
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "ADB could not connect to your device. Make sure USB debugging is enabled.", "Error", JOptionPane.ERROR_MESSAGE);
                }
                revalidate();
                break;
            case FLASH_PROGRESS:
                FlashProgressPane fpp = new FlashProgressPane(this);
                setContentPane(fpp);
                Globals.runFlash(fpp);
                revalidate();
                break;
            case INTENT:
                switch (intent) {
                    case FLASH:
                        setContentPane(new FlashPane(this));
                        revalidate();
                        break;
                }
                break;
            default:
                return;
        }
    }

    public void setIntent(Intent intent) {
        this.intent = intent;
    }
}
