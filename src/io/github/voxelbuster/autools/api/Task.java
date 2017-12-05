package io.github.voxelbuster.autools.api;

import se.vidstige.jadb.JadbDevice;

import java.io.File;

public class Task {
    private JadbDevice device;

    public enum Partition {
        SYSTEM,
        DATA,
        STORAGE,
        CACHE,
        DALVIK,
        BOOT,
        RECOVERY
    }

    public void setDevice(JadbDevice device) {
        this.device = device;
    }

    public void flashRom(File rom) {

    }

    public void flashRecovery(File img) {

    }

    public void wipe(Partition part) {

    }
}
