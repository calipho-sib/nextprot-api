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
public class BlastDbMaker extends BlastProgram<Map<String, String>, String, BlastProgram.Params> {

    private static final String ISOFORM_REX_EXP= "^NX_[^-]+-\\d+$";

    public BlastDbMaker(Params params) {

        super("makeblastdb", params);
        Objects.requireNonNull(params.getBinPath(), "makeblastdb binary path is missing");
    }

    /**
     * Create temporary fasta file from map of isoform sequences
     * @param isoformSequences isoform sequences
     */
    @Override
    protected void writeFastaInput(PrintWriter pw, Map<String, String> isoformSequences) {

        for (Map.Entry<String, String> entry : isoformSequences.entrySet()) {

            if (!entry.getKey().matches(ISOFORM_REX_EXP))
                throw new NextProtException(entry.getKey()+": invalid isoform accession");

            BlastProgram.writeFastaEntry(pw, entry.getKey(), entry.getValue());
        }
    }

    @Override
    protected List<String> buildCommandLine(Params params, File fastaFile) {

        List<String> command = new ArrayList<>();

        command.add(params.getBinPath());
        command.add("-dbtype");
        command.add("prot");
        command.add("-title");
        command.add("nextprot");
        command.add("-in");
        command.add(fastaFile.getAbsolutePath());
        command.add("-out");
        command.add(params.getNextprotBlastDbPath());

        return command;
    }

    @Override
    protected String buildOutputFromStdout(String stdout) throws IOException {

        return stdout;
    }
}
