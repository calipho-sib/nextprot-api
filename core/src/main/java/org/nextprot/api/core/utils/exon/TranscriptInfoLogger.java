package org.nextprot.api.core.utils.exon;

import org.nextprot.api.core.domain.AminoAcid;
import org.nextprot.api.core.domain.Exon;

/**
 * Created by fnikitin on 22/07/15.
 */
public class TranscriptInfoLogger implements TranscriptInfoHandler {

    private StringBuilder sb;

    @Override
    public void startHandlingTranscript() {
        sb = new StringBuilder();
    }

    @Override
    public void startHandlingExon(Exon exon) {}

    @Override
    public void handleCodingExon(Exon exon, AminoAcid first, AminoAcid last, ExonCategory category) {
        sb.append(first.getBase()).append("").append(first.getPosition()).append("(+").append(first.getPhase()).append(")-");
        sb.append(category).append("-").append(last.getBase()).append("").append(last.getPosition()).append("(+").append(last.getPhase()).append(") ");
    }

    @Override
    public void handleCodingExonError(ExonBoundError exonBoundError) {

        AminoAcid first = exonBoundError.getFirst();

        if (exonBoundError.getAminoAcidOutOfBound() == ExonBoundError.AminoAcidOutOfBound.LAST) {
            sb.append(first.getBase()).append("").append(first.getPosition()).append("(+").append(first.getPhase()).append(")-");
            sb.append("ERROR-?(" + (exonBoundError.getLast().getPosition()-1) + ">=" + exonBoundError.getIsoformLength() + "!)");
        } else {
            sb.append("?(" + (first.getPosition()-1) + ">=" + exonBoundError.getIsoformLength() + "!)-ERROR-...");
        }
    }

    @Override
    public void handleNonCodingExon(Exon exon, ExonCategory category) {
        sb.append(category.getTypeString()).append(" ");
    }

    @Override
    public void endHandlingExon(Exon exon) {}

    @Override
    public void endHandlingTranscript() {}

    public String getInfos() {

        return sb.toString();
    }
}
