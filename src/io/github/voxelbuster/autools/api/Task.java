package io.github.voxelbuster.autools.api;

public class Task {
    public enum Partition {
        SYSTEM,
        DATA,
        STORAGE,
        CACHE,
        DALVIK,
        BOOT,
        SIDELOAD, RECOVERY
    }
}
