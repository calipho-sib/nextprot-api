package org.nextprot.api.blast.service;


import org.nextprot.api.blast.domain.BlastPConfig;

@FunctionalInterface
public interface BlastPService {

    /**
     * Run blastp with the given config
     * @param config the blastp config
     * @param query the query sequence (fasta)
     * @return a json formatted string
     */
    String runBlastP(BlastPConfig config, String query);
}
