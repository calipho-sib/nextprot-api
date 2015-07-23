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
            try {
                extractInfosFromExon(exon);
            } catch (SequenceIndexOutOfBoundsException e) {

                handler.endWithException(exon, e);
                break;
            }
            handler.endHandlingExon(exon);
        }
        handler.endHandlingTranscript();
    }

    private void extractInfosFromExon(final Exon exon) throws SequenceIndexOutOfBoundsException {

        ExonCategory exonCategory = categorizer.categorize(exon);
        handler.handleExonCategory(exon, exonCategory);

        if (exonCategory.isCoding()) extractAminoAcids(isoformSequence, exon, exonCategory);
    }

    private void moveToNextFirstPos() {

        if (currentPhase == 0) currentIsoformPos++;
    }

    private void moveToNextLastPos() {

        currentIsoformPos = currentTranscriptLen / 3;
        currentPhase = currentTranscriptLen % 3;

        if (currentPhase == 0) currentIsoformPos--;
    }

    private void extractAminoAcids(String isoformSequence, Exon exon, ExonCategory cat) throws SequenceIndexOutOfBoundsException {

        int startPositionExon = exon.getFirstPositionOnGene();
        int endPositionExon = exon.getLastPositionOnGene();

        if (cat == ExonCategory.START || cat == ExonCategory.MONO)
            startPositionExon = startPositionIsoform;
        if (cat == ExonCategory.STOP  || cat == ExonCategory.MONO)
            endPositionExon = endPositionIsoform;

        moveToNextFirstPos();
        handler.handleFirstAA(exon, newAminoAcid(isoformSequence, currentIsoformPos, currentPhase));

        // update transcript length
        currentTranscriptLen += endPositionExon - startPositionExon + 1;

        moveToNextLastPos();
        handler.handleLastAA(exon, newAminoAcid(isoformSequence, currentIsoformPos, currentPhase));
    }

    private AminoAcid newAminoAcid(String isoformSequence, int aaPosition, int phase) throws SequenceIndexOutOfBoundsException {

        if (aaPosition >= isoformSequence.length()) throw new SequenceIndexOutOfBoundsException(accession, aaPosition, isoformSequence.length());

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
        public void handleFirstAA(Exon exon, AminoAcid aa) {
            exon.setFirstAminoAcid(aa);
        }

        @Override
        public void handleLastAA(Exon exon, AminoAcid aa) {
            exon.setLastAminoAcid(aa);
        }

        @Override
        public void handleExonCategory(Exon exon, ExonCategory cat) {
            exon.setCodingStatus(cat.toString());
        }

        @Override
        public void endHandlingExon(Exon exon) {}

        @Override
        public void endHandlingTranscript() {}

        @Override
        public void endWithException(Exon exon, SequenceIndexOutOfBoundsException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
