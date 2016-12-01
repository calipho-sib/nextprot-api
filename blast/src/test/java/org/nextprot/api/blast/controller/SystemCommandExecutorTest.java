package org.nextprot.api.blast.controller;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class SystemCommandExecutorTest {

    @Test
    public void executeCommand() throws Exception {

        List<String> command = new ArrayList<>();
        command.add("lsd");
        command.add("-l");
        command.add("/var/tmp");

        SystemCommandExecutor commandExecutor = new SystemCommandExecutor(command);
        int result = commandExecutor.executeCommand();

        StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
        StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();

        System.out.println("$? "+result);
        System.out.println("STDOUT");
        System.out.println(stdout);
        System.out.println("STDERR");
        System.out.println(stderr);
    }

}