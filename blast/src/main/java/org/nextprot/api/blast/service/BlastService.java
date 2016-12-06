package org.nextprot.api.blast.service;


import org.nextprot.api.blast.domain.BlastConfig;
import org.nextprot.api.blast.domain.gen.BlastResult;

public interface BlastService {

    /**
     * Run blastp on a given protein sequence
     * @param config a blastp config
     * @param header query header
     * @param sequence the protein sequence (fasta)
     * @return the blast result
     */
    BlastResult blastProteinSequence(BlastConfig config, String header, String sequence);

    /**
     * Run blastp on a given isoform sequence
     * @param config a blastp config
     * @param isoformAccession an isoform accession
     * @param begin1BasedIndex first index (1-based number)
     * @param end1BasedIndex last index (1-based number)
     * @return the blast result
     */
    BlastResult blastIsoformSequence(BlastConfig config, String isoformAccession, Integer begin1BasedIndex, Integer end1BasedIndex);
}
