package org.nextprot.api.blast.service;

import org.nextprot.api.blast.domain.BlastConfig;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Create blast database from nextprot sequences
 */
public class BlastDbMaker extends BlastProgram<String, String> {

    public BlastDbMaker(BlastConfig config) {

        super("makeblastdb", config);
        Objects.requireNonNull(config.getMakeBlastDbBinPath(), "makeblastdb binary path is missing");
    }

    protected List<String> buildCommandLine(File fastaFile) {

        List<String> command = new ArrayList<>();

        command.add(config.getMakeBlastDbBinPath());
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

        /*
Building a new DB, current time: 12/08/2016 16:10:14
New DB name:   /tmp/nextprot
New DB title:  nextprotdb
Sequence type: Protein
Deleted existing Protein BLAST database named /tmp/nextprot
Keep Linkouts: T
Keep MBits: T
Maximum file size: 1000000000B
Adding sequences from FASTA; added 1 sequences in 0.000381947 seconds.


version: 3.0
db_name: /tmp/nextprot
db_title: nextprotdb
seq_type: Protein
count_added_seq: 1
         */

        return stdout;
    }


    protected void writeFastaContent(PrintWriter pw, String content) {

        pw.write(content);
    }
}
