package org.nextprot.api.core.utils.exon;

import org.nextprot.api.core.domain.AminoAcid;
import org.nextprot.api.core.domain.Exon;

/**
 * A listener for various analysis events of TranscriptExonsAnalyser.
 *
 * Created by fnikitin on 22/07/15.
 */
public interface ExonsAnalysis {

    /** The transcript analysis is about to be started. */
    void started();

    /**
     * The specified exon is about to be analysed.
     * @param exon the exon which is about to be analysed
     */
    void startedExon(Exon exon);

    /**
     * The analysed coding exon is about to be handled.
     * @param exon the analysed coding exon
     * @param first the first exon aa
     * @param last the last exon aa
     * @param category the exon category
     */
    default void analysedCodingExon(Exon exon, AminoAcid first, AminoAcid last, ExonCategory category) {

        exon.setFirstAminoAcid(first);
        exon.setLastAminoAcid(last);
        exon.setExonCategory(category);
    }

    /**
     * Handling the analysed coding exon which generates an error.
     * @param exon the exon that failed
     */
    void analysedCodingExonFailed(Exon exon, ExonOutOfIsoformBoundException outOfBoundException);

    /**
     * The analysed non-coding exon is about to be handled.
     * @param exon the analysed non-coding exon
     * @param category the exon category
     */
    default void analysedNonCodingExon(Exon exon, ExonCategory category) {

        exon.setExonCategory(category);
    }

    /**
     * The specified exon analysis has been terminated.
     * @param exon the exon analysis that has been terminated
     */
    void terminated(Exon exon);

    /** The analysis has been terminated */
    void terminated();
}
