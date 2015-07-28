package org.nextprot.api.core.utils.exon;

import com.google.common.base.Preconditions;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nextprot.api.core.domain.AminoAcid;
import org.nextprot.api.core.domain.Exon;

import java.util.*;

/**
 * Compute phases and categorize exons of isoform transcripts
 *
 * Created by fnikitin on 22/07/15.
 */
public class TranscriptInfosExtractor {

    private final static Log LOGGER = LogFactory.getLog(TranscriptInfosExtractor.class);
    private static final TranscriptInfoHandler DEFAULT_HANDLER = new TranscriptInfoHandlerImpl();

    private final List<Exon> exons;
    private final TranscriptInfoHandler handler;
    private String accession;

    private ExonCategorizer categorizer;

    private String isoformSequence;
    private int startPositionIsoform;
    private int endPositionIsoform;
    private int currentTranscriptLen;
    private int currentIsoformPos;
    private int currentPhase;

    public TranscriptInfosExtractor() {

        this(DEFAULT_HANDLER);
    }

    public TranscriptInfosExtractor(TranscriptInfoHandler handler) {

        Preconditions.checkNotNull(handler);

        this.handler = handler;
        exons = new ArrayList<>();
    }

    private void init(String accession, String isoformSequence, int startPositionIsoform, int endPositionIsoform, Collection<Exon> exons) {

        this.accession = accession;
        this.isoformSequence = isoformSequence;
        this.startPositionIsoform = startPositionIsoform;
        this.endPositionIsoform = endPositionIsoform;
        this.currentTranscriptLen = 0;
        this.currentIsoformPos = -1;
        this.currentPhase = 0;
        this.exons.addAll(exons);

        Collections.sort(this.exons, new Comparator<Exon>() {
            @Override
            public int compare(Exon e1, Exon e2) {
                return e1.getFirstPositionOnGene() - e2.getFirstPositionOnGene();
            }
        });

        categorizer = new ExonCategorizer(startPositionIsoform, endPositionIsoform);
    }

    public void extract(String isoformSequence, int startPositionIsoform, int endPositionIsoform, Collection<Exon> exons) {

        extract("", isoformSequence, startPositionIsoform, endPositionIsoform, exons);
    }

    public void extract(String accession, String isoformSequence, int startPositionIsoform, int endPositionIsoform, Collection<Exon> exons) {

        init(accession, isoformSequence, startPositionIsoform, endPositionIsoform, exons);

        handler.startHandlingTranscript();
        for (Exon exon : exons) {

            handler.startHandlingExon(exon);
            ExonCategory exonCategory = categorizer.categorize(exon);

            if (exonCategory.isCoding()) {
                if (!handleCodingExon(isoformSequence, exon, exonCategory)) break;
            }
            else {
                handler.handleNonCodingExon(exon, exonCategory);
            }
            handler.endHandlingExon(exon);
        }
        handler.endHandlingTranscript();
    }

    private void moveToNextFirstPos() {

        if (currentPhase == 0) currentIsoformPos++;
    }

    private void moveToNextLastPos() {

        currentIsoformPos = currentTranscriptLen / 3;
        currentPhase = currentTranscriptLen % 3;

        if (currentPhase == 0) currentIsoformPos--;
    }

    private boolean handleCodingExon(String isoformSequence, Exon exon, ExonCategory cat) {

        int startPositionExon = exon.getFirstPositionOnGene();
        int endPositionExon = exon.getLastPositionOnGene();

        if (cat == ExonCategory.START || cat == ExonCategory.MONO)
            startPositionExon = startPositionIsoform;
        if (cat == ExonCategory.STOP  || cat == ExonCategory.MONO)
            endPositionExon = endPositionIsoform;

        moveToNextFirstPos();
        AminoAcid first = newAminoAcid(isoformSequence, currentIsoformPos, currentPhase);

        // update transcript length
        currentTranscriptLen += endPositionExon - startPositionExon + 1;

        moveToNextLastPos();
        AminoAcid last = newAminoAcid(isoformSequence, currentIsoformPos, currentPhase);

        if (first.getPosition() > isoformSequence.length()) {
            handler.handleCodingExonError(new ExonBoundError(exon, first, last,
                    ExonBoundError.AminoAcidOutOfBound.FIRST, isoformSequence.length()));
            return false;
        }
        else if (last.getPosition() > isoformSequence.length()) {
            handler.handleCodingExonError(new ExonBoundError(exon, first, last,
                    ExonBoundError.AminoAcidOutOfBound.LAST, isoformSequence.length()));
            return false;
        }

        handler.handleCodingExon(exon, first, last, cat);

        return true;
    }

    private AminoAcid newAminoAcid(String isoformSequence, int aaPosition, int phase) {

        if (aaPosition >= isoformSequence.length()) return new AminoAcid(aaPosition + 1, phase, '?');

        return new AminoAcid(aaPosition + 1, phase, isoformSequence.charAt(aaPosition));
    }

    /**
     * Update exon on the fly in this default implementation
     */
    private static class TranscriptInfoHandlerImpl implements TranscriptInfoHandler {

        @Override
        public void startHandlingTranscript() {}

        @Override
        public void startHandlingExon(Exon exon) {}

        @Override
        public void handleCodingExon(Exon exon, AminoAcid first, AminoAcid last, ExonCategory category) {
            exon.setFirstAminoAcid(first);
            exon.setLastAminoAcid(last);
            exon.setCodingStatus(category.getTypeString());
        }

        @Override
        public void handleNonCodingExon(Exon exon, ExonCategory category) {
            exon.setCodingStatus(category.getTypeString());
        }

        @Override
        public void endHandlingExon(Exon exon) {}

        @Override
        public void endHandlingTranscript() {}

        @Override
        public void handleCodingExonError(ExonBoundError exonBoundError) {

            StringBuilder sb = new StringBuilder("SequenceIndexOutOfBoundsException: index (");

            sb.append(exonBoundError.getOutOfBoundAminoAcid().getPosition()-1);

            sb.append(") must be less than size (").append(exonBoundError.getIsoformLength()).append(")");

            LOGGER.error(sb.toString());
        }
    }
}
