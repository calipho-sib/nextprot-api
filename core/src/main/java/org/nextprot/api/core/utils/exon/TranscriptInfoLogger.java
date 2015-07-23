package org.nextprot.api.core.utils.exon;

import org.nextprot.api.core.domain.AminoAcid;
import org.nextprot.api.core.domain.Exon;

/**
 * Created by fnikitin on 22/07/15.
 */
public class TranscriptInfoLogger implements TranscriptInfoHandler {

    private StringBuilder sb;
    private ExonCategory current;

    @Override
    public void startHandlingTranscript() {
        sb = new StringBuilder();
    }

    @Override
    public void startHandlingExon(Exon exon) {}

    @Override
    public void handleFirstAA(Exon exon, AminoAcid aa) {

        sb.append(aa.getBase()).append("").append(aa.getPosition()).append("(+").append(aa.getPhase()).append(")-");
    }

    @Override
    public void handleLastAA(Exon exon, AminoAcid aa) {

        sb.append(current).append("-").append(aa.getBase()).append("").append(aa.getPosition()).append("(+").append(aa.getPhase()).append(") ");
    }

    @Override
    public void handleExonCategory(Exon exon, ExonCategory cat) {
        current = cat;
    }

    @Override
    public void endHandlingExon(Exon exon) {}

    @Override
    public void endHandlingTranscript() {}

    @Override
    public void endWithException(Exon exon, SequenceIndexOutOfBoundsException e) {

        sb.append("ERROR-?("+e.getIndex() +">="+e.getSize()+")!");
    }

    public String getInfos() {

        return sb.toString();
    }
}
