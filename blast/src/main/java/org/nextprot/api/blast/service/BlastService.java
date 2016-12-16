package org.nextprot.api.blast.service;


import org.nextprot.api.blast.domain.BlastIsoformInput;
import org.nextprot.api.blast.domain.BlastProgramOutput;
import org.nextprot.api.blast.domain.BlastSequenceInput;

public interface BlastService {

    /**
     * Run blastp on a given protein sequence
     * @param params params needed to execute blastp
     * @return the blast result
     */
    BlastProgramOutput blastProteinSequence(BlastSequenceInput params);

    /**
     * Run blastp on a given isoform sequence
     * @param params params needed to execute blastp
     * @return the blast result
     */
    BlastProgramOutput blastIsoformSequence(BlastIsoformInput params);

    /**
     * Create blast nextprot database
     * @param params configuration object
     * @return report
     */
    BlastProgramOutput makeNextprotBlastDb(BlastProgram.Params params);
}
