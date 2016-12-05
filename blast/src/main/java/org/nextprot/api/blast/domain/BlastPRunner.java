package org.nextprot.api.blast.domain;

import org.nextprot.api.blast.controller.SystemCommandExecutor;
import org.nextprot.api.commons.exception.NextProtException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static com.hp.hpl.jena.sparql.vocabulary.VocabTestQuery.query;

/**
 * Executes locally installed blastP program with protein sequence query
 */
public class BlastPRunner {

    private File tempQueryFile;
    private final BlastPConfig config;

    public BlastPRunner(BlastPConfig config) {

        this.config = config;
    }

    public String run(String header, String sequence) throws NextProtException {

        try {
            StringBuilder fasta = new StringBuilder();
            fasta.append(">").append(header).append("\n").append(sequence);

            writeTempQueryFile(fasta.toString());

            SystemCommandExecutor commandExecutor = new SystemCommandExecutor(buildCommandLine());
            commandExecutor.executeCommand();

            StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();
            StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();

            if (!stderr.toString().isEmpty()) {
                throw new NextProtException("BlastP error when executing " + query + ": " + stderr.toString());
            }

            if (!config.isDebugMode()) {
                deleteTempQueryFile();
            }

            return stdout.toString();
        } catch (IOException | InterruptedException e) {

            throw new NextProtException("BlastP exception", e);
        }
    }

    List<String> buildCommandLine() {

        List<String> command = new ArrayList<>();

        command.add(config.getBlastDirPath() + "/blastp");
        command.add("-db");
        command.add(config.getNextprotDatabasePath() + "/nextprot");
        command.add("-query");
        command.add(tempQueryFile.getAbsolutePath());
        command.add("-outfmt");
        command.add("15");

        return command;
    }

    private void writeTempQueryFile(String sequenceQuery) throws IOException {

        tempQueryFile = File.createTempFile("blastp_query", "fasta");

        PrintWriter pw = new PrintWriter(tempQueryFile);
        pw.write(sequenceQuery);
        pw.close();
    }

    private void deleteTempQueryFile() throws IOException {

        Files.deleteIfExists(tempQueryFile.toPath());
    }
}
