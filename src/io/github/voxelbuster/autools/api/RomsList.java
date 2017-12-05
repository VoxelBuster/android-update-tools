package io.github.voxelbuster.autools.api;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;

import javax.swing.*;
import java.io.File;
import java.io.FileReader;

public class RomsList {
    public static String[] getRomsForDevice(String codename) {
        try {
            CSVParser parser = CSVParser.parse(new FileReader(new File(ResourceManager.getPath("romsdb"))), CSVFormat.DEFAULT);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Could not load the roms database.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        return new String[0];
    }

    public static String getTwrpUrl(String codename) {
        String trueCn = codename.split("-")[codename.split("-").length-1];
        return "";
    }
}
