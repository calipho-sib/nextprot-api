package org.nextprot.api.blast.service;


import org.nextprot.api.blast.domain.BlastPParams;
import org.nextprot.api.blast.domain.BlastProgramOutput;

public interface BlastService {

    /**
     * Run blastp on a given protein sequence
     * @param params params needed to execute blastp
     * @return the blast result
     */
    BlastProgramOutput blastProteinSequence(BlastPParams params);

    /**
     * Run blastp on a given isoform sequence
     * @param params params needed to execute blastp
     * @return the blast result
     */
    BlastProgramOutput blastIsoformSequence(BlastPParams.BlastPIsoformParams params);

    /**
     * Create blast nextprot database
     * @param params configuration object
     * @return report
     */
    BlastProgramOutput makeNextprotBlastDb(BlastProgram.Params params);
}
