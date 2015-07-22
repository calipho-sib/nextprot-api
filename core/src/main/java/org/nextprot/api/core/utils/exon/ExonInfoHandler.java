package org.nextprot.api.core.utils.exon;

import org.nextprot.api.core.domain.AminoAcid;
import org.nextprot.api.core.domain.Exon;

/**
 * Callback methods called by ExonInfosExtractor while fetching exon informations
 *
 * Created by fnikitin on 22/07/15.
 */
public interface ExonInfoHandler {

    void startHandlingExon(Exon exon);
    void handleFirstAA(AminoAcid aa);
    void handleLastAA(AminoAcid aa);
    void handleExonCategory(ExonCategory cat);
    void endHandlingExon(Exon exon);
}
