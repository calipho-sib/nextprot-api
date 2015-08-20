package org.nextprot.api.core.utils.exon;

import org.nextprot.api.core.domain.AminoAcid;
import org.nextprot.api.core.domain.Exon;

/**
 * A logger for TranscriptExonsAnalyser
 *
 * Created by fnikitin on 22/07/15.
 */
public class ExonsAnalysisLogger implements ExonsAnalysisListener {

    private StringBuilder sb;

    @Override
    public void started() {
        sb = new StringBuilder();
    }

    @Override
    public void startedExon(Exon exon) {}

    @Override
    public void analysedCodingExon(Exon exon, AminoAcid first, AminoAcid last, ExonCategory category) {
        sb.append(first.getBase()).append("").append(first.getPosition()).append("(+").append(first.getPhase()).append(")-");
        sb.append(category).append("-").append(last.getBase()).append("").append(last.getPosition()).append("(+").append(last.getPhase()).append(") ");
    }

    @Override
    public void analysedCodingExonFailed(Exon exon, ExonOutOfBoundError exonOutOfBoundError) {

        AminoAcid first = exonOutOfBoundError.getFirst();

        if (exonOutOfBoundError.getAminoAcidOutOfBound() == ExonOutOfBoundError.AminoAcidOutOfBound.LAST) {
            sb.append(first.getBase()).append("").append(first.getPosition()).append("(+").append(first.getPhase()).append(")-");
            sb.append("ERROR-?(" + (exonOutOfBoundError.getLast().getPosition()-1) + ">=" + exonOutOfBoundError.getIsoformLength() + "!)");
        } else {
            sb.append("?(" + (first.getPosition()-1) + ">=" + exonOutOfBoundError.getIsoformLength() + "!)-ERROR-...");
        }
    }

    @Override
    public void analysedNonCodingExon(Exon exon, ExonCategory category) {
        sb.append(category.getTypeString()).append(" ");
    }

    @Override
    public void terminated(Exon exon) {}

    @Override
    public void terminated() {}

    public String getLog() {

        return sb.toString();
    }
}
