package org.nextprot.api.blast.controller;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SystemCommandExecutorTest {

    @Test
    public void executeCommand() throws Exception {

        List<String> command = new ArrayList<>();
        command.add("ls");
        command.add("-l");
        command.add("/var/tmp");

        SystemCommandExecutor commandExecutor = new SystemCommandExecutor(command);
        int result = commandExecutor.executeCommand();

        StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
        StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();

        Assert.assertEquals(0, result);
        Assert.assertTrue(!stdout.toString().isEmpty());
        Assert.assertTrue(stderr.toString().isEmpty());
    }

    @Test(expected = IOException.class)
    public void commandShouldFail() throws Exception {

        List<String> command = new ArrayList<>();
        command.add("lsd");
        command.add("-l");
        command.add("/var/tmp");

        SystemCommandExecutor commandExecutor = new SystemCommandExecutor(command);
        commandExecutor.executeCommand();
    }

}