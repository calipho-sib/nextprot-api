package org.nextprot.api.blast.service;


import org.nextprot.api.blast.domain.gen.*;

public interface BlastResultUpdaterService {

    /**
     * Update of the original blast report
     * @param blastReport the original report
     * @param proteinSequence the protein sequence blasted
     */
    void update(Report blastReport, String proteinSequence);

    /**
     * Update a blast output description object
     * @param description the description to update
     * @param isoAccession isoform accession
     * @param entryAccession entry accession
     */
    void updateDescription(Description description, String isoAccession, String entryAccession);
}
