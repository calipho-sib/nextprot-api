package org.nextprot.api.commons.utils;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SystemCommandExecutorTest {

    @Test
    public void lsCommandShouldSucceed() throws Exception {

        List<String> command = new ArrayList<>();
        command.add("ls");
        command.add("-l");
        command.add("/var/tmp");

        SystemCommandExecutor commandExecutor = new SystemCommandExecutor(command);

        Assert.assertTrue(!commandExecutor.hasBeenExecuted());
        int result = commandExecutor.executeCommand();
        Assert.assertTrue(commandExecutor.hasBeenExecuted());

        String stdout = commandExecutor.getLastExecutionStandardOutput();
        String stderr = commandExecutor.getLastExecutionStandardError();

        Assert.assertEquals(0, result);
        Assert.assertTrue(!stdout.isEmpty());
        Assert.assertTrue(stderr.isEmpty());
    }

    @Test(expected = IOException.class)
    public void lsCommandShouldFail() throws Exception {

        List<String> command = new ArrayList<>();
        command.add("lsd");
        command.add("-l");
        command.add("/var/tmp");

        SystemCommandExecutor commandExecutor = new SystemCommandExecutor(command);
        commandExecutor.executeCommand();
    }

    @Test(expected = NullPointerException.class)
    public void cannotInstanciateNullCommand() throws Exception {

        new SystemCommandExecutor(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cannotInstanciateEmptyCommand() throws Exception {

        new SystemCommandExecutor(Collections.emptyList());
    }
}