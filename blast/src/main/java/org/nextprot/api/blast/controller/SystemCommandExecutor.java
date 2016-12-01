package org.nextprot.api.blast.controller;

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

// Updated code coming from article http://alvinalexander.com/java/java-exec-processbuilder-process-1
public class SystemCommandExecutor {

    private final List<String> commandInformation;
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
     * @param commandInformation The command you want to run.
     */
    public SystemCommandExecutor(final List<String> commandInformation) {
        Objects.requireNonNull(commandInformation, "The commandInformation is required.");
        this.commandInformation = commandInformation;
    }

    public int executeCommand() throws IOException, InterruptedException {

        ProcessBuilder pb = new ProcessBuilder(commandInformation);
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
     * Get the standard output (stdout) from the command you just exec'd.
     */
    public StringBuilder getStandardOutputFromCommand() {
        return inputStreamHandler.getOutputBuffer();
    }

    /**
     * Get the standard error (stderr) from the command you just exec'd.
     */
    public StringBuilder getStandardErrorFromCommand() {
        return errorStreamHandler.getOutputBuffer();
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
