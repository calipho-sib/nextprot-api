package org.nextprot.api.blast.service;

import org.nextprot.api.commons.exception.NextProtException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Create blast database from nextprot sequences
 */
public class BlastDbMaker extends BlastProgram<String, String, BlastProgram.Config> {

    private static final String ISOFORM_REX_EXP= "^NX_[^-]+-\\d+$";

    public BlastDbMaker(BlastProgram.Config config) {

        super("makeblastdb", config);
        Objects.requireNonNull(config.getBinPath(), "makeblastdb binary path is missing");
    }

    /**
     * Make blast db from map of isoform sequences
     * @param isoformSequences isoform sequences
     * @return makeblastdb report
     * @throws NextProtException if an isoform accession format is not valid
     */
    public String run(Map<String, String> isoformSequences) {

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> entry : isoformSequences.entrySet()) {

            if (!entry.getKey().matches(ISOFORM_REX_EXP))
                throw new NextProtException(entry.getKey()+": invalid isoform accession");

            sb.append(">");
            sb.append(entry.getKey());
            sb.append("\n");
            sb.append(entry.getValue());
            sb.append("\n");
        }

        return run(sb.toString());
    }

    protected List<String> buildCommandLine(File fastaFile) {

        List<String> command = new ArrayList<>();

        command.add(config.getBinPath());
        command.add("-dbtype");
        command.add("prot");
        command.add("-title");
        command.add("nextprot");
        command.add("-in");
        command.add(fastaFile.getAbsolutePath());
        command.add("-out");
        command.add(config.getNextprotBlastDbPath());

        return command;
    }

    @Override
    protected String buildFromStdout(String stdout) throws IOException {

        return stdout;
    }


    protected void writeFastaContent(PrintWriter pw, String content) {

        pw.write(content);
    }
}
