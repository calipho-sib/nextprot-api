package org.nextprot.api.core.utils.exon;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.core.domain.AminoAcid;
import org.nextprot.api.core.domain.exon.UncategorizedExon;

/**
 * A logger for TranscriptExonsAnalyser
 *
 * Created by fnikitin on 22/07/15.
 */
public class ExonsAnalysisWithLogging implements ExonsAnalysis {

    private StringBuilder sb;

    @Override
    public void started() {
        sb = new StringBuilder();
    }

    @Override
    public void startedExon(UncategorizedExon exon) {}

    @Override
    public void analysedCodingExon(UncategorizedExon exon, AminoAcid first, AminoAcid last, ExonCategory category) {

        ExonsAnalysis.super.analysedCodingExon(exon, first, last, category);

        sb.append(first.getPosition());
        sb.append("[");
        sb.append(getAACode(first, true)).append("(+").append(first.getPhase()).append(")--");
        sb.append(category).append("--");
        sb.append(getAACode(last, false)).append("(+").append(last.getPhase()).append(")");
        sb.append("]");
        sb.append(last.getPosition()).append(" ");
    }

    @Override
    public void analysedCodingExonFailed(UncategorizedExon exon, ExonOutOfIsoformBoundException exonOutOfIsoformBoundException) {

        AminoAcid first = exonOutOfIsoformBoundException.getFirst();

        if (exonOutOfIsoformBoundException.getAminoAcidOutOfBound() == ExonOutOfIsoformBoundException.AminoAcidOutOfBound.LAST) {
            sb.append(first.getPosition());
            sb.append("[");
            sb.append(getAACode(first, true)).append("(+").append(first.getPhase()).append(")--");
            sb.append("OUT-OF-BOUND--");
            sb.append("NA");
            sb.append("]!");
            sb.append(exonOutOfIsoformBoundException.getLast().getPosition()).append(">").append(exonOutOfIsoformBoundException.getIsoformLength()).append("!");
        } else {
            sb.append(first.getPosition());
            sb.append("!");
            sb.append(exonOutOfIsoformBoundException.getFirst().getPosition()).append(">").append(exonOutOfIsoformBoundException.getIsoformLength()).append("!");
            sb.append("[");
            sb.append(getAACode(first, true)).append("(+").append(first.getPhase()).append(")--");
            sb.append("OUT-OF-BOUND--");
            sb.append("NA");
            sb.append("]!");
            sb.append(exonOutOfIsoformBoundException.getLast().getPosition()).append(">").append(exonOutOfIsoformBoundException.getIsoformLength()).append("!");
        }
    }

    @Override
    public void analysedNonCodingExon(UncategorizedExon exon, ExonCategory category) {

        ExonsAnalysis.super.analysedNonCodingExon(exon, category);

        sb.append(category.getTypeString()).append(" ");
    }

    @Override
    public void terminated(UncategorizedExon exon) {}

    @Override
    public void terminated() {}

    public String getMessage() {

        return sb.toString();
    }

    private String getAACode(AminoAcid aa, boolean first) {

        String aa3code = AminoAcidCode.valueOfAminoAcid1LetterCode(aa.getBase()).get3LetterCode().toUpperCase();

        int phase = aa.getPhase();

        if (phase == 0) {
            return aa3code;
        }
        else {
            return (first) ? aa3code.substring(phase) : aa3code.substring(0, phase);
        }
    }
}
