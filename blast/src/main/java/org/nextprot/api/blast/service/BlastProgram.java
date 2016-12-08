package org.nextprot.api.blast.service;

import org.nextprot.api.blast.controller.SystemCommandExecutor;
import org.nextprot.api.blast.domain.BlastConfig;
import org.nextprot.api.commons.exception.NextProtException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

/**
 * A base object for blast programs
 */
public abstract class BlastProgram<I, O> {

    private String name;
    protected final BlastConfig config;

    protected BlastProgram(String name, BlastConfig config) {

        Objects.requireNonNull(config, "missing blast configuration");

        this.name = name;
        this.config = config;
    }

    public O run(I input) {

        try {
            File tempFastaDbFile = createTempFastaFile(input);

            O out = execute(buildCommandLine(tempFastaDbFile));

            if (!config.isDebugMode()) {
                Files.deleteIfExists(tempFastaDbFile.toPath());
            }

            return out;
        } catch (IOException | InterruptedException e) {

            throw new NextProtException("BlastP exception", e);
        }
    }

    protected O execute(List<String> commandLine) throws IOException, InterruptedException {

        SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commandLine);
        commandExecutor.executeCommand();

        String stderr = commandExecutor.getStandardErrorFromCommand().toString();

        if (!stderr.isEmpty()) {

            StringBuilder sb = new StringBuilder("Error while executing ");
            sb.append(name);

            String params = (config.isDebugMode()) ? commandExecutor.getParameterLine(0) : commandExecutor.getParameterLine(5);

            if (!params.isEmpty()) {
                sb.append(", params='").append(params).append("'");
            }
            sb.append(", stderr='" + stderr.replace("\n", " ")+"'");

            throw new NextProtException(sb.toString());
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
}
