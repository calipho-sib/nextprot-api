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
    void handleFirstAA(Exon exon, AminoAcid aa);
    void handleLastAA(Exon exon, AminoAcid aa);
    void handleExonCategory(Exon exon, ExonCategory cat);
    void endHandlingExon(Exon exon);
    void endHandlingTranscript();
    void endWithException(Exon exon, SequenceIndexOutOfBoundsException e);
}
