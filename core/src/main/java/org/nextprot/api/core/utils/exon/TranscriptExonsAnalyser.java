package org.nextprot.api.core.utils.exon;

import org.nextprot.api.core.domain.AminoAcid;
import org.nextprot.api.core.domain.GenericExon;

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
    public Results analyse(String isoformSequence, int startPositionIsoformOnGene, int endPositionIsoformOnGene, final Collection<GenericExon> exonList) {

        Results results = new Results();

        List<GenericExon> exonsSorted = new ArrayList<>(exonList);
        exonsSorted.sort(Comparator.comparingInt(GenericExon::getFirstPositionOnGene));

        init(startPositionIsoformOnGene, endPositionIsoformOnGene);

        exonsAnalysis.started();
        for (GenericExon exon : exonsSorted) {

            exonsAnalysis.startedExon(exon);
            try {
                ExonCategory exonCategory = categorizer.categorize(exon);

                if (exonCategory.isCoding()) {
                    analyseCodingExon(isoformSequence, exon, exonCategory);
                }
                else {
                    exonsAnalysis.analysedNonCodingExon(exon, exonCategory);
                }

                results.addValidExon(exon);
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

    private int calcStartPositionExonOnGene(GenericExon exon, ExonCategory cat) {

        int startPositionExonOnGene = exon.getFirstPositionOnGene();

        if (cat == ExonCategory.START || cat == ExonCategory.MONO)
            startPositionExonOnGene = startPositionIsoformOnGene;

        return startPositionExonOnGene;
    }

    private void analyseCodingExon(String isoformSequence, GenericExon exon, ExonCategory cat) throws ExonOutOfIsoformBoundException {

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

    private int calcEndPositionExonOnGene(GenericExon exon, ExonCategory cat) {

        int endPositionExonOnGene = exon.getLastPositionOnGene();

        if (cat == ExonCategory.STOP  || cat == ExonCategory.MONO)
            endPositionExonOnGene = endPositionIsoformOnGene;

        return endPositionExonOnGene;
    }

    private ExonOutOfIsoformBoundException createExonOutOfIsoformBoundException(GenericExon exon, AminoAcid first, AminoAcid last, int isoformLength) {

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
        private final List<GenericExon> validExons = new ArrayList<>();

        void addInvalidExonException(InvalidExonException e) {

            exceptions.add(e);
        }

        void addValidExon(GenericExon exon) {

            validExons.add(exon);
        }

        public List<GenericExon> getValidExons() {

            return Collections.unmodifiableList(validExons);
        }

        public List<InvalidExonException> getExceptionList() {

            return Collections.unmodifiableList(exceptions);
        }

        public boolean isSuccess() {

            return exceptions.isEmpty();
        }
    }
}
