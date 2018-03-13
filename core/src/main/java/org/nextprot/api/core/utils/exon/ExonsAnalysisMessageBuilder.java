package org.nextprot.api.core.utils.exon;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.core.domain.AminoAcid;
import org.nextprot.api.core.domain.Exon;

/**
 * A logger for TranscriptExonsAnalyser
 *
 * Created by fnikitin on 22/07/15.
 */
public class ExonsAnalysisMessageBuilder implements ExonsAnalysisListener {

    private StringBuilder sb;

    @Override
    public void started() {
        sb = new StringBuilder();
    }

    @Override
    public void startedExon(Exon exon) {}

    @Override
    public void analysedCodingExon(Exon exon, AminoAcid first, AminoAcid last, ExonCategory category) {

        sb.append(first.getPosition());
        sb.append("[");
        sb.append(getAACode(first, true)).append("(+").append(first.getPhase()).append(")--");
        sb.append(category).append("--");
        sb.append(getAACode(last, false)).append("(+").append(last.getPhase()).append(")");
        sb.append("]");
        sb.append(last.getPosition()).append(" ");
    }

    @Override
    public void analysedCodingExonFailed(Exon exon, ExonOutOfBoundError exonOutOfBoundError) {

        AminoAcid first = exonOutOfBoundError.getFirst();

        if (exonOutOfBoundError.getAminoAcidOutOfBound() == ExonOutOfBoundError.AminoAcidOutOfBound.LAST) {
            sb.append(first.getBase()).append("").append(first.getPosition()).append("(+").append(first.getPhase()).append(")-");
            sb.append("ERROR-?(").append(exonOutOfBoundError.getLast().getPosition() - 1).append(">=").append(exonOutOfBoundError.getIsoformLength()).append("!)");
        } else {
            sb.append("?(").append(first.getPosition() - 1).append(">=").append(exonOutOfBoundError.getIsoformLength()).append("!)-ERROR-...");
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
