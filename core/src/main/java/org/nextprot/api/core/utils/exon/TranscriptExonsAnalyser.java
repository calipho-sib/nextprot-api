package org.nextprot.api.core.utils.exon;

import com.google.common.base.Preconditions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.core.domain.AminoAcid;
import org.nextprot.api.core.domain.Exon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Compute phases and categorize exons of isoform transcripts
 *
 * Created by fnikitin on 22/07/15.
 */
public class TranscriptExonsAnalyser {

    private final static Log LOGGER = LogFactory.getLog(TranscriptExonsAnalyser.class);
    private static final ExonsAnalysisListener DEFAULT_HANDLER = new ExonsAnalysisListenerImpl();

    private final ExonsAnalysisListener handler;

    private ExonCategorizer categorizer;

    private int startPositionIsoformOnGene;
    private int endPositionIsoformOnGene;
    private int currentTranscriptLen;
    private int currentIsoformPos;
    private int currentPhase;

    public TranscriptExonsAnalyser() {

        this(DEFAULT_HANDLER);
    }

    public TranscriptExonsAnalyser(ExonsAnalysisListener handler) {

        Preconditions.checkNotNull(handler);

        this.handler = handler;
    }

    private void init(int startPositionIsoformOnGene, int endPositionIsoformOnGene) {

        this.startPositionIsoformOnGene = startPositionIsoformOnGene;
        this.endPositionIsoformOnGene = endPositionIsoformOnGene;
        this.currentTranscriptLen = 0;
        this.currentIsoformPos = -1;
        this.currentPhase = 0;

        this.categorizer = new ExonCategorizer(startPositionIsoformOnGene, endPositionIsoformOnGene);
    }

    /**
     * Analyse given exons
     * @param isoformSequence sequence in amino-acids
     * @param startPositionIsoformOnGene start position on gene of mapping isoform
     * @param endPositionIsoformOnGene end position on gene of mapping isoform
     * @param exonList the exons to analyse
     * @return true if analysis succeed
     */
    public boolean analyse(String isoformSequence, int startPositionIsoformOnGene, int endPositionIsoformOnGene, Collection<Exon> exonList) throws ExonCategorizer.ExonInvalidBoundException {

        List<Exon> exonsSorted = new ArrayList<>(exonList);
        exonsSorted.sort(Comparator.comparingInt(Exon::getFirstPositionOnGene));

        init(startPositionIsoformOnGene, endPositionIsoformOnGene);

        handler.started();
        for (Exon exon : exonsSorted) {

            handler.startedExon(exon);
            ExonCategory exonCategory = categorizer.categorize(exon);

            if (exonCategory.isCoding()) {
                boolean success = analyseCodingExon(isoformSequence, exon, exonCategory);

                // there were some errors
                if (!success) {
                    return false;
                }
            }
            else {
                handler.analysedNonCodingExon(exon, exonCategory);
            }
            handler.terminated(exon);
        }
        handler.terminated();

        return true;
    }

    private void moveToNextFirstPos() {

        if (currentPhase == 0) currentIsoformPos++;
    }

    private void moveToNextLastPos() {

        currentIsoformPos = currentTranscriptLen / 3;
        currentPhase = currentTranscriptLen % 3;

        if (currentPhase == 0) currentIsoformPos--;
    }

    private boolean analyseCodingExon(String isoformSequence, Exon exon, ExonCategory cat) {

        int startPositionExonOnGene = exon.getFirstPositionOnGene();
        int endPositionExonOnGene = exon.getLastPositionOnGene();

        if (cat == ExonCategory.START || cat == ExonCategory.MONO)
            startPositionExonOnGene = startPositionIsoformOnGene;
        if (cat == ExonCategory.STOP  || cat == ExonCategory.MONO)
            endPositionExonOnGene = endPositionIsoformOnGene;

        moveToNextFirstPos();
        AminoAcid first = newAminoAcid(isoformSequence, currentIsoformPos, currentPhase);

        // update transcript length
        currentTranscriptLen += endPositionExonOnGene - startPositionExonOnGene + 1;

        moveToNextLastPos();
        AminoAcid last = newAminoAcid(isoformSequence, currentIsoformPos, currentPhase);

        if (first.getPosition() > isoformSequence.length()) {
            handler.analysedCodingExonFailed(exon, new ExonOutOfBoundError(first, last,
                    ExonOutOfBoundError.AminoAcidOutOfBound.FIRST, isoformSequence.length()));
            return false;
        }
        else if (last.getPosition() > isoformSequence.length()) {
            handler.analysedCodingExonFailed(exon, new ExonOutOfBoundError(first, last,
                    ExonOutOfBoundError.AminoAcidOutOfBound.LAST, isoformSequence.length()));
            return false;
        }

        handler.analysedCodingExon(exon, first, last, cat);

        return true;
    }

    private AminoAcid newAminoAcid(String isoformSequence, int aaPosition, int phase) {

        if (aaPosition >= isoformSequence.length()) return new AminoAcid(aaPosition + 1, phase, '?');

        return new AminoAcid(aaPosition + 1, phase, isoformSequence.charAt(aaPosition));
    }

    /**
     * Update exon on the fly in this default implementation
     */
    private static class ExonsAnalysisListenerImpl implements ExonsAnalysisListener {

        @Override
        public void started() {}

        @Override
        public void startedExon(Exon exon) {}

        @Override
        public void terminated(Exon exon) {}

        @Override
        public void terminated() {}

        @Override
        public void analysedCodingExonFailed(Exon exon, ExonOutOfBoundError exonOutOfBoundError) {

            StringBuilder sb = new StringBuilder("SequenceIndexOutOfBoundsException: index (");

            sb.append(exonOutOfBoundError.getOutOfBoundAminoAcid().getPosition()-1);

            sb.append(") must be less than size (").append(exonOutOfBoundError.getIsoformLength()).append(")");

            //TODO there is a bug in mapping transcript <-> isoform (should be fixed in the database)
            //Should be solved with: https://issues.isb-sib.ch/browse/NEXTPROT-1005
            LOGGER.warn(sb.toString());
        }
    }
}
