package org.nextprot.api.blast.service;


import org.nextprot.api.blast.domain.BlastPConfig;

import java.util.Map;

public interface BlastPService {

    /**
     * Run blastp with the given config
     * @param config the blastp config
     * @param header query header
     * @param sequence the protein sequence (fasta)
     * @return a json formatted string
     */
    String blastProteinSequence(BlastPConfig config, String header, String sequence);

    Map<String, String> blastEntry(BlastPConfig config, String entryName);

    String blastIsoform(BlastPConfig config, String isoformName, Integer from, Integer to);
}
