package org.nextprot.api.commons.utils;

import com.google.common.base.Preconditions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * A wrapper class around {@code ProcessBuilder} that execute an external command and expose standard and error outputs
 */
// Updated code coming from article http://alvinalexander.com/java/java-exec-processbuilder-process-1
public class SystemCommandExecutor {

    private final List<String> command;
    private ThreadedStreamHandler inputStreamHandler;
    private ThreadedStreamHandler errorStreamHandler;

    /**
     * Pass in the system command you want to run as a List of Strings, as shown here:
     * <p>
     * List<String> commands = new ArrayList<String>();
     * commands.add("/sbin/ping");
     * commands.add("-c");
     * commands.add("5");
     * commands.add("www.google.com");
     * SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commands);
     * commandExecutor.executeCommand();
     * <p>
     * Note: I've removed the other constructor that was here to support executing
     * the sudo command. I'll add that back in when I get the sudo command
     * working to the point where it won't hang when the given password is
     * wrong.
     *
     * @param command The command you want to run.
     */
    public SystemCommandExecutor(final List<String> command) {
        Objects.requireNonNull(command, "Cannot execute undefined command.");
        Preconditions.checkArgument(!command.isEmpty(), "Cannot execute empty command.");
        this.command = command;
    }

    public int executeCommand() throws IOException, InterruptedException {

        ProcessBuilder pb = new ProcessBuilder(command);
        Process process = pb.start();

        InputStream inputStream = process.getInputStream();
        InputStream errorStream = process.getErrorStream();

        // these need to run as java threads to get the standard output and error from the command.
        List<Callable<String>> tasks = new ArrayList<>();
        tasks.add(inputStreamHandler = new ThreadedStreamHandler(inputStream));
        tasks.add(errorStreamHandler = new ThreadedStreamHandler(errorStream));

        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.invokeAll(tasks);
        executor.shutdown();
        executor.awaitTermination(10L, TimeUnit.SECONDS);

        return process.waitFor();
    }

    /**
     * @return true if command has been executed else false
     */
    public boolean hasBeenExecuted() {
        return inputStreamHandler != null;
    }

    /**
     * Get the standard output (stdout) from the command you just exec'd.
     */
    public String getLastExecutionStandardOutput() {

        if (!hasBeenExecuted())
            return "no command has been executed";

        return inputStreamHandler.getOutputBuffer().toString();
    }

    /**
     * Get the standard error (stderr) from the command you just exec'd.
     */
    public String getLastExecutionStandardError() {

        if (!hasBeenExecuted())
            return "no command has been executed";

        return errorStreamHandler.getOutputBuffer().toString();
    }

    private static class ThreadedStreamHandler implements Callable<String> {

        private final InputStream inputStream;
        private StringBuilder outputBuffer = new StringBuilder();

        ThreadedStreamHandler(InputStream inputStream) {
            this.inputStream = inputStream;
        }

        @Override
        public String call() throws IOException {

            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    outputBuffer.append(line).append("\n");
                }
            }
            return outputBuffer.toString();
        }

        public StringBuilder getOutputBuffer() {
            return outputBuffer;
        }
    }
}
