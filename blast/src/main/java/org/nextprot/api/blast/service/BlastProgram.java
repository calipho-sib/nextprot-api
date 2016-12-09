package org.nextprot.api.blast.service;

import org.nextprot.api.blast.controller.SystemCommandExecutor;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.commons.utils.ExceptionWithReason;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

/**
 * A base object for blast programs
 */
public abstract class BlastProgram<I, O, C extends BlastProgram.Config> {

    private String name;
    protected final C config;

    protected BlastProgram(String name, C config) {

        Objects.requireNonNull(config, "missing blast configuration");

        this.name = name;
        this.config = config;
    }

    public O run(I input) throws ExceptionWithReason {

        try {
            File tempFastaDbFile = createTempFastaFile(input);

            O out = execute(buildCommandLine(tempFastaDbFile));

            if (!config.isDebugMode()) {
                Files.deleteIfExists(tempFastaDbFile.toPath());
            }

            return out;
        } catch (IOException | InterruptedException e) {

            throw new NextProtException("could not run "+name, e);
        }
    }

    protected O execute(List<String> commandLine) throws IOException, InterruptedException, ExceptionWithReason {

        SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commandLine);
        commandExecutor.executeCommand();

        String stderr = commandExecutor.getStandardErrorFromCommand().toString();

        if (!stderr.isEmpty()) {

            ExceptionWithReason ewr = new ExceptionWithReason();
            ewr.getReason().addCause(name+ " exception", stderr.replace("\n", " "));
            ewr.getReason().setMessage("Error while executing "+name);

            throw ewr;
        }

        return buildFromStdout(commandExecutor.getStandardOutputFromCommand().toString());
    }

    private File createTempFastaFile(I input) throws IOException {

        File tempFastaFile = File.createTempFile(name, ".fasta");

        PrintWriter pw = new PrintWriter(tempFastaFile);
        writeFastaContent(pw, input);
        pw.close();

        return tempFastaFile;
    }

    /**
     * Build command line to execute by blast program
     * @param fastaFile a fasta file that need to be created
     * @return command line list
     */
    protected abstract List<String> buildCommandLine(File fastaFile);

    /**
     *
     * @param output the command output
     * @return
     * @throws IOException
     */
    protected abstract O buildFromStdout(String output) throws IOException;

    protected abstract void writeFastaContent(PrintWriter pw, I input);

    /**
     * Configuration object for blast suite program execution
     */
    public static class Config implements Serializable {

        private final String binPath;
        private final String nextprotBlastDbPath;
        private boolean isDebugMode = false;

        public Config(String binPath, String nextprotBlastDbPath) {

            if (binPath == null)
                throw new NextProtException("Internal error: bin path is missing");

            if (nextprotBlastDbPath == null)
                throw new NextProtException("Internal error: nextprot blast db path is missing");

            this.binPath = binPath;
            this.nextprotBlastDbPath = nextprotBlastDbPath;
        }

        public String getNextprotBlastDbPath() {
            return nextprotBlastDbPath;
        }

        public String getBinPath() {
            return binPath;
        }

        public boolean isDebugMode() {
            return isDebugMode;
        }

        public void setDebugMode(boolean debugMode) {
            isDebugMode = debugMode;
        }
    }
}
