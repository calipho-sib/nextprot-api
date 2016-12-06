package org.nextprot.api.blast.domain;

import org.nextprot.api.blast.controller.SystemCommandExecutor;
import org.nextprot.api.blast.domain.gen.*;
import org.nextprot.api.commons.exception.NextProtException;
import org.nextprot.api.core.service.MainNamesService;

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

    public BlastResult run(String header, String sequence) {

        return run(header, sequence, null);
    }

    public BlastResult run(String header, String sequence, MainNamesService mainNamesService) {

        try {
            writeTempQueryFile(buildFastaContent(header, sequence));

            BlastResult blastResult = executeBlast(buildCommandLine());

            if (mainNamesService != null)
                new BlastResultUpdater(mainNamesService, sequence).update(blastResult);

            return blastResult;
        } catch (IOException | InterruptedException e) {

            throw new NextProtException("BlastP exception", e);
        }
    }

    private String buildFastaContent(String header, String sequence) throws IOException {

        StringBuilder fasta = new StringBuilder();
        fasta
                .append(">").append(header).append("\n")
                .append(sequence);

        return fasta.toString();
    }

    private BlastResult executeBlast(List<String> commandLine) throws IOException, InterruptedException {

        SystemCommandExecutor commandExecutor = new SystemCommandExecutor(commandLine);
        commandExecutor.executeCommand();

        String stderr = commandExecutor.getStandardErrorFromCommand().toString();

        if (!stderr.isEmpty()) {
            throw new NextProtException("BlastP error when executing " + query + ": " + stderr);
        }

        if (!config.isDebugMode()) {
            deleteTempQueryFile();
        }

        return BlastResult.fromJson(commandExecutor.getStandardOutputFromCommand().toString());
    }

    private List<String> buildCommandLine() {

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
