package org.nextprot.api.blast.service;


import org.nextprot.api.blast.domain.BlastPConfig;
import org.nextprot.api.blast.domain.BlastProgramOutput;

public interface BlastService {

    /**
     * Run blastp on a given protein sequence
     * @param query a blastp query
     * @return the blast result
     */
    BlastProgramOutput blastProteinSequence(BlastPConfig query);

    /**
     * Run blastp on a given isoform sequence
     * @param query a blastp query
     * @return the blast result
     */
    BlastProgramOutput blastIsoformSequence(BlastPConfig.BlastPIsoformConfig query);

    /**
     * Create blast nextprot database
     * @param config configuration object
     * @return report
     */
    BlastProgramOutput makeNextprotBlastDb(BlastProgram.Config config);
}
