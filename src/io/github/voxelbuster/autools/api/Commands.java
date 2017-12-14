package io.github.voxelbuster.autools.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Commands {
    /**
     * Starts a shell command and waits for it to finish. Sends all command output to a string array.
     * Not recommended for large processes as it blocks the current thread until the process finishes.
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
        return (String[]) lines.toArray();
    }
}
