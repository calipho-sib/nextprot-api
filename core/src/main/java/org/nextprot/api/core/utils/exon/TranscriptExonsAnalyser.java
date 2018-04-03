package org.nextprot.api.core.utils.exon;

import org.nextprot.api.core.domain.AminoAcid;
import org.nextprot.api.core.domain.exon.*;

import java.util.*;

/**
 * Compute phases and categorize exons of isoform transcripts
 *
 * Created by fnikitin on 22/07/15.
 */
public class TranscriptExonsAnalyser {

    private final ExonsAnalysis exonsAnalysis;

    private ExonCategorizer categorizer;

    private int startPositionIsoformOnGene;
    private int endPositionIsoformOnGene;
    private int currentTranscriptLen;
    private int currentIsoformPos;
    private int currentPhase;

    public TranscriptExonsAnalyser(ExonsAnalysis exonsAnalysis) {

        Objects.requireNonNull(exonsAnalysis);

        this.exonsAnalysis = exonsAnalysis;
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
    public Results analyse(String isoformSequence, int startPositionIsoformOnGene, int endPositionIsoformOnGene, final Collection<UncategorizedExon> exonList) {

        Results results = new Results();

        List<UncategorizedExon> exonsSorted = new ArrayList<>(exonList);
        exonsSorted.sort(Comparator.comparingInt(Exon::getFirstPositionOnGene));

        init(startPositionIsoformOnGene, endPositionIsoformOnGene);

        exonsAnalysis.started();
        for (UncategorizedExon exon : exonsSorted) {

            exonsAnalysis.startedExon(exon);
            try {
                ExonCategory exonCategory = categorizer.categorize(exon);

                if (exonCategory.isCoding()) {
                    analyseCodingExon(isoformSequence, exon, exonCategory);
                }
                else {
                    exonsAnalysis.analysedNonCodingExon(exon, exonCategory);
                }

                if (exonCategory == ExonCategory.START) {
                    ExonStart start = new ExonStart(startPositionIsoformOnGene);
                    start.fillFrom(exon);

                    results.addValidExon(start);
                }
                else if (exonCategory == ExonCategory.STOP) {
                    ExonStop stop = new ExonStop(endPositionIsoformOnGene);
                    stop.fillFrom(exon);
                    results.addValidExon(stop);
                }
                else {
                    CategorizedExon categorizedExon = new CategorizedExon(exonCategory);
                    categorizedExon.fillFrom(exon);

                    results.addValidExon(categorizedExon);
                }

                exonsAnalysis.terminated(exon);
            } catch (InvalidExonException e) {

                results.addInvalidExonException(e);
            }
        }
        exonsAnalysis.terminated();

        return results;
    }

    private void moveToNextFirstPos() {

        if (currentPhase == 0) currentIsoformPos++;
    }

    private void moveToNextLastPos() {

        currentIsoformPos = currentTranscriptLen / 3;
        currentPhase = currentTranscriptLen % 3;

        if (currentPhase == 0) currentIsoformPos--;
    }

    private void analyseCodingExon(String isoformSequence, UncategorizedExon exon, ExonCategory cat) throws ExonOutOfIsoformBoundException {

        int startPositionExonOnGene = calcStartPositionExonOnGene(exon, cat);
        int endPositionExonOnGene = calcEndPositionExonOnGene(exon, cat);

        moveToNextFirstPos();
        AminoAcid first = newAminoAcid(isoformSequence, currentIsoformPos, currentPhase);

        currentTranscriptLen += endPositionExonOnGene - startPositionExonOnGene + 1;

        moveToNextLastPos();
        AminoAcid last = newAminoAcid(isoformSequence, currentIsoformPos, currentPhase);

        if (first.getPosition() > isoformSequence.length() || last.getPosition() > isoformSequence.length()) {

            ExonOutOfIsoformBoundException exception = createExonOutOfIsoformBoundException(exon, first, last, isoformSequence.length());

            exonsAnalysis.analysedCodingExonFailed(exon, exception);
            throw exception;
        }

        exonsAnalysis.analysedCodingExon(exon, first, last, cat);
    }

    private int calcStartPositionExonOnGene(Exon exon, ExonCategory cat) {

        int startPositionExonOnGene = exon.getFirstPositionOnGene();

        if (cat == ExonCategory.START || cat == ExonCategory.MONO)
            startPositionExonOnGene = startPositionIsoformOnGene;

        return startPositionExonOnGene;
    }

    private int calcEndPositionExonOnGene(Exon exon, ExonCategory cat) {

        int endPositionExonOnGene = exon.getLastPositionOnGene();

        if (cat == ExonCategory.STOP  || cat == ExonCategory.MONO)
            endPositionExonOnGene = endPositionIsoformOnGene;

        return endPositionExonOnGene;
    }

    private ExonOutOfIsoformBoundException createExonOutOfIsoformBoundException(UncategorizedExon exon, AminoAcid first, AminoAcid last, int isoformLength) {

        if (first.getPosition() > isoformLength) {

            return new ExonOutOfIsoformBoundException(exon, first, last,
                    ExonOutOfIsoformBoundException.AminoAcidOutOfBound.FIRST, isoformLength);
        }
        else if (last.getPosition() > isoformLength) {

            return new ExonOutOfIsoformBoundException(exon, first, last,
                    ExonOutOfIsoformBoundException.AminoAcidOutOfBound.LAST, isoformLength);
        }

        throw new IllegalStateException("should throw a ExonOutOfIsoformBoundException !");
    }

    private AminoAcid newAminoAcid(String isoformSequence, int aaPosition, int phase) {

        if (aaPosition >= isoformSequence.length()) return new AminoAcid(aaPosition + 1, phase, '?');

        return new AminoAcid(aaPosition + 1, phase, isoformSequence.charAt(aaPosition));
    }

    public static class Results {

        private final List<InvalidExonException> exceptions = new ArrayList<>();
        private final List<Exon> validExons = new ArrayList<>();

        void addInvalidExonException(InvalidExonException e) {

            exceptions.add(e);
        }

        void addValidExon(Exon exon) {

            validExons.add(exon);
        }

        public List<Exon> getValidExons() {

            return Collections.unmodifiableList(validExons);
        }

        public List<InvalidExonException> getExceptionList() {

            return Collections.unmodifiableList(exceptions);
        }

        public boolean hasMappingErrors() {

            return !exceptions.isEmpty();
        }
    }
}
