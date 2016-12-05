package org.nextprot.api.blast.service;


import org.nextprot.api.blast.domain.BlastPConfig;

public interface BlastPService {

    /**
     * Run blastp on a given protein sequence
     * @param config a blastp config
     * @param header query header
     * @param sequence the protein sequence (fasta)
     * @return a json formatted string of the blast result
     */
    String blastProteinSequence(BlastPConfig config, String header, String sequence);

    /**
     * Run blastp on a given isoform sequence
     * @param config a blastp config
     * @param isoformAccession an isoform accession
     * @param begin1BasedIndex first index (1-based number)
     * @param end1BasedIndex last index (1-based number)
     * @return a json formatted string of the blast result
     */
    String blastIsoform(BlastPConfig config, String isoformAccession, Integer begin1BasedIndex, Integer end1BasedIndex);
}
