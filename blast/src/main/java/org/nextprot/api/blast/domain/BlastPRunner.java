package org.nextprot.api.blast.domain;

import org.nextprot.api.blast.controller.SystemCommandExecutor;
import org.nextprot.api.commons.exception.NextProtException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class BlastPRunner {

    private File tempQueryFile;
    private final BlastPConfig config;

    public BlastPRunner(BlastPConfig config) {

        this.config = config;
    }

    // prepare input from config: create file from sequence query ?
    // run blastp external command line program
    //// blastp -db db/nextprot -query query/NX_P52701_211-239.fasta -outfmt 5
    // get output string
    //

    public String run(String query) throws NextProtException {

        try {
            writeTempQueryFile(query);

            // execute my command
            SystemCommandExecutor commandExecutor = new SystemCommandExecutor(buildCommandLine());

            commandExecutor.executeCommand();

            StringBuilder stdout = commandExecutor.getStandardOutputFromCommand();

            StringBuilder stderr = commandExecutor.getStandardErrorFromCommand();

            if (!stderr.toString().isEmpty()) {
                throw new NextProtException("BlastP error when executing "+query+": "+stderr.toString());
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

        command.add(config.getBlastDirPath()+"/blastp");
        command.add("-db");
        command.add(config.getNextprotDatabasePath());
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
