package org.nextprot.api.blast.service;


import org.nextprot.api.blast.domain.BlastPConfig;
import org.nextprot.api.blast.domain.gen.BlastResult;

public interface BlastService {

    /**
     * Run blastp on a given protein sequence
     * @param query a blastp query
     * @param header query header
     * @param sequence the protein sequence (fasta)
     * @return the blast result
     */
    BlastResult blastProteinSequence(BlastPConfig query, String header, String sequence);

    /**
     * Run blastp on a given isoform sequence
     * @param query a blastp query
     * @param isoformAccession an isoform accession
     * @param begin1BasedIndex first index (1-based number)
     * @param end1BasedIndex last index (1-based number)
     * @return the blast result
     */
    BlastResult blastIsoformSequence(BlastPConfig query, String isoformAccession, Integer begin1BasedIndex, Integer end1BasedIndex);

    /**
     * Create blast nextprot database
     * @param config configuration object
     * @return report
     */
    String makeNextprotBlastDb(BlastProgram.Config config);
}
