package org.nextprot.api.core.utils.exon;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.AminoAcid;
import org.nextprot.api.core.domain.Exon;

import java.util.*;

/**
 * Compute phases and categorize exons of isoform transcripts
 *
 * Created by fnikitin on 22/07/15.
 */
public class ExonInfosExtractor {

    private static final ExonInfoHandler DEFAULT_HANDLER = new ExonInfoHandlerImpl();

    private final List<Exon> exons;
    private final ExonInfoHandler handler;
    private ExonCategorizer categorizer;

    private String isoformSequence;
    private int startPositionIsoform;
    private int endPositionIsoform;
    private int currentTranscriptLen;
    private int currentIsoformPos;
    private int currentPhase;

    public ExonInfosExtractor() {

        this(DEFAULT_HANDLER);
    }

    public ExonInfosExtractor(ExonInfoHandler handler) {

        Preconditions.checkNotNull(handler);

        this.handler = handler;
        exons = new ArrayList<>();
    }

    private void init(String isoformSequence, int startPositionIsoform, int endPositionIsoform, Collection<Exon> exons) {

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

        init(isoformSequence, startPositionIsoform, endPositionIsoform, exons);

        for (Exon exon : exons) {

            extractInfosFromExon(exon);
        }
    }

    private void extractInfosFromExon(final Exon exon) {

        handler.startHandlingExon(exon);

        ExonCategory exonCategory = categorizer.categorize(exon);
        handler.handleExonCategory(exonCategory);

        int startPositionExon = exon.getFirstPositionOnGene();
        int endPositionExon = exon.getLastPositionOnGene();

        if (exonCategory == ExonCategory.START)
            startPositionExon = startPositionIsoform;
        else if (exonCategory == ExonCategory.STOP)
            endPositionExon = endPositionIsoform;

        if (exonCategory != ExonCategory.STOP_ONLY)
            extractAminoAcids(isoformSequence, startPositionExon, endPositionExon);

        handler.endHandlingExon(exon);
    }

    private void moveToNextFirstPos() {

        if (currentPhase == 0) currentIsoformPos++;
    }

    private void moveToNextLastPos() {

        currentIsoformPos = currentTranscriptLen / 3;
        currentPhase = currentTranscriptLen % 3;

        if (currentPhase == 0) currentIsoformPos--;
    }

    private void extractAminoAcids(String isoformSequence, int startPositionExon, int endPositionExon) {

        moveToNextFirstPos();

        handler.handleFirstAA(newAminoAcid(isoformSequence, currentIsoformPos, currentPhase));

        // update transcript length
        currentTranscriptLen += endPositionExon - startPositionExon + 1;

        moveToNextLastPos();

        handler.handleLastAA(newAminoAcid(isoformSequence, currentIsoformPos, currentPhase));
    }

    private static AminoAcid newAminoAcid(String isoformSequence, int aaPosition, int phase) {

        return new AminoAcid(aaPosition + 1, phase, isoformSequence.charAt(aaPosition));
    }

    /**
     * Update exon on the fly in this default implementation
     */
    private static class ExonInfoHandlerImpl implements ExonInfoHandler {

        private Exon current;

        @Override
        public void startHandlingExon(Exon exon) {
            current = exon;
        }

        @Override
        public void handleFirstAA(AminoAcid aa) {
            current.setFirstAminoAcid(aa);
        }

        @Override
        public void handleLastAA(AminoAcid aa) {
            current.setLastAminoAcid(aa);
        }

        @Override
        public void handleExonCategory(ExonCategory cat) {
            current.setCodingStatus(cat.toString());
        }

        @Override
        public void endHandlingExon(Exon exon) {}
    }
}
