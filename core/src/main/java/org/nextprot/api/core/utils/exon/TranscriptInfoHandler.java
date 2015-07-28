package org.nextprot.api.core.utils.exon;

import org.nextprot.api.core.domain.AminoAcid;
import org.nextprot.api.core.domain.Exon;

/**
 * Callback methods called by ExonInfosExtractor while fetching exon informations
 *
 * Created by fnikitin on 22/07/15.
 */
public interface TranscriptInfoHandler {

    void startHandlingTranscript();
    void startHandlingExon(Exon exon);
    void handleCodingExon(Exon exon, AminoAcid first, AminoAcid last, ExonCategory category);
    void handleCodingExonError(ExonBoundError exonBoundError);
    void handleNonCodingExon(Exon exon, ExonCategory cat);
    void endHandlingExon(Exon exon);
    void endHandlingTranscript();
}
