package io.github.voxelbuster.autools.api;

import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class ApiCommands {
    /**
     * Starts a shell command and waits for it to finish. Sends all command output to a string array.
     * Not recommended for large processes as it blocks the current thread until the process finishes.
     *
     * @param fullCmdText
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public static String[] runCommand(String fullCmdText) throws IOException, InterruptedException {
        Process ps = Globals.runtime.exec(fullCmdText);
        int exitVal = ps.waitFor();
        InputStreamReader isr = new InputStreamReader(ps.getInputStream());
        BufferedReader br = new BufferedReader(isr);
        ArrayList<String> lines = new ArrayList<>();
        while (br.ready()) {
            lines.add(br.readLine());
        }
        lines.add("Process completed with code " + exitVal);
        String[] arr = new String[]{};
        return lines.toArray(arr);
    }

    public static String[] runAdbShellCommand(JadbDevice device, String command, String... args) throws IOException, JadbException {
        InputStreamReader isr = new InputStreamReader(device.executeShell(command, args));
        BufferedReader br = new BufferedReader(isr);
        ArrayList<String> lines = new ArrayList<>();
        while (br.ready()) {
            lines.add(br.readLine());
        }
        lines.add("Process completed");
        String[] arr = new String[]{};
        return lines.toArray(arr);
    }

    public static String[] runAdbCommand(JadbDevice device, String command, String... args) throws IOException, JadbException {
        InputStreamReader isr = new InputStreamReader(device.execute(command, args));
        BufferedReader br = new BufferedReader(isr);
        ArrayList<String> lines = new ArrayList<>();
        while (br.ready()) {
            lines.add(br.readLine());
        }
        lines.add("Process completed");
        String[] arr = new String[]{};
        return lines.toArray(arr);
    }

    public static String[] concat(String[] a, String[] b) {
        int len = a.length + b.length;
        String[] newArr = new String[len];
        System.arraycopy(a, 0, newArr, 0, a.length);
        System.arraycopy(b, 0, newArr, a.length, b.length);
        return newArr;
    }
}
