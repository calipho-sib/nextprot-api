package org.nextprot.api.blast.service;

import org.nextprot.api.blast.domain.BlastSearchParams;
import org.nextprot.api.blast.domain.BlastSequenceInput;
import org.nextprot.api.blast.domain.gen.BlastResult;
import org.nextprot.api.blast.domain.gen.Report;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Executes locally installed blastP program with protein sequence query
 */
public class BlastPRunner extends BlastProgram<BlastPRunner.FastaEntry, Report, BlastSequenceInput> {

    public BlastPRunner(BlastSequenceInput config) {

        super("blastp", config);
        Objects.requireNonNull(config.getBinPath(), "binary path is missing");
    }

    @Override
    protected void writeFastaInput(PrintWriter pw, FastaEntry fastaEntry) {

        BlastProgram.writeFastaEntry(pw, fastaEntry.getHeader(), fastaEntry.getSequence());
    }

    @Override
    protected void preConfig(FastaEntry fastaEntry, BlastSequenceInput config) {

        config.setTitle(fastaEntry.getHeader());
        config.setSequence(fastaEntry.getSequence());
    }

    @Override
    protected Report buildOutputFromStdout(String stdout) throws IOException {

        return BlastResult.fromJson(stdout).getBlastOutput2().get(0).getReport();
    }

    @Override
    protected List<String> buildCommandLine(BlastSequenceInput input, File fastaFile) {

        List<String> command = new ArrayList<>();

        command.add(input.getBinPath());
        command.add("-db");
        command.add(input.getNextprotBlastDbPath());
        command.add("-query");
        command.add(fastaFile.getAbsolutePath());
        command.add("-outfmt");
        command.add("15");

        if (input.getSearchParams() != null) {

            BlastSearchParams searchParams = input.getSearchParams();

            if (searchParams.getMatrix() != null) {
                command.add("-matrix");
                command.add(searchParams.getMatrix().toString());
            }
            if (searchParams.getEvalue() != null) {
                command.add("-evalue");
                command.add(String.valueOf(searchParams.getEvalue()));
            }
            if (searchParams.getGapOpen() != null) {
                command.add("-gapopen");
                command.add(String.valueOf(searchParams.getGapOpen()));
            }
            if (searchParams.getGapExtend() != null) {
                command.add("-gapextend");
                command.add(String.valueOf(searchParams.getGapExtend()));
            }
        }

        return command;
    }

    /** A fasta sequence query */
    public static class FastaEntry {

        private final String header;
        private final String sequence;

        public FastaEntry(String header, String sequence) {
            this.header = header;
            this.sequence = sequence;
        }

        public String getHeader() {
            return header;
        }

        public String getSequence() {
            return sequence;
        }
    }
}
