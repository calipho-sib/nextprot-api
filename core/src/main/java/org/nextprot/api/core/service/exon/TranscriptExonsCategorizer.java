package org.nextprot.api.core.service.exon;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.core.domain.AminoAcid;
import org.nextprot.api.core.domain.exon.CategorizedExon;
import org.nextprot.api.core.domain.exon.Exon;
import org.nextprot.api.core.domain.exon.ExonCategory;
import org.nextprot.api.core.domain.exon.SimpleExon;

import java.util.*;

/**
 * Compute phases and categorize exons of isoform transcripts
 *
 * Created by fnikitin on 22/07/15.
 */
public class TranscriptExonsCategorizer {

    private final ExonsAnalysis exonsAnalysis;

    private ExonCategorizer categorizer;

    private int startPositionIsoformOnGene;
    private int endPositionIsoformOnGene;
    private int currentTranscriptLen;
    private int currentIsoformPos;
    private int currentPhase;

    public TranscriptExonsCategorizer(ExonsAnalysis exonsAnalysis) {

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
     * Categorize exons
     * @param exonList the exons to categorize
     * @param isoformSequence sequence in amino-acids
     * @param startPositionIsoformOnGene start position on gene of mapping isoform
     * @param endPositionIsoformOnGene end position on gene of mapping isoform
     * @return true if analysis succeed
     */
    public Results categorizeExons(final Collection<SimpleExon> exonList, String isoformSequence, int startPositionIsoformOnGene, int endPositionIsoformOnGene) {

        Results results = new Results();

        List<SimpleExon> exonsSorted = new ArrayList<>(exonList);
        exonsSorted.sort(Comparator.comparingInt(Exon::getFirstPositionOnGene));

        init(startPositionIsoformOnGene, endPositionIsoformOnGene);

        exonsAnalysis.started();
        for (SimpleExon exon : exonsSorted) {

            exonsAnalysis.startedExon(exon);
            try {
                ExonCategory exonCategory = categorizer.categorize(exon);

                if (exonCategory.isCoding()) {
                    analyseCodingExon(isoformSequence, exon, exonCategory);
                }
                else {
                    exonsAnalysis.analysedNonCodingExon(exon, exonCategory);
                }

                results.addCategorizedExon(CategorizedExon.valueOf(exonCategory, exon, startPositionIsoformOnGene, endPositionIsoformOnGene));

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

    private void analyseCodingExon(String isoformSequence, SimpleExon exon, ExonCategory cat) throws ExonOutOfIsoformBoundException {

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

    private ExonOutOfIsoformBoundException createExonOutOfIsoformBoundException(SimpleExon exon, AminoAcid first, AminoAcid last, int isoformLength) {

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

        if (aaPosition >= isoformSequence.length()) return new AminoAcid(aaPosition + 1, phase, null);

        return new AminoAcid(aaPosition + 1, phase,
                AminoAcidCode.valueOfAminoAcid1LetterCode(isoformSequence.charAt(aaPosition)));
    }

    public static class Results {

        private final List<InvalidExonException> exceptions = new ArrayList<>();
        private final List<CategorizedExon> categorizedExons = new ArrayList<>();

        void addInvalidExonException(InvalidExonException e) {

            exceptions.add(e);
        }

        void addCategorizedExon(CategorizedExon exon) {

            categorizedExons.add(exon);
        }

        public List<CategorizedExon> getCategorizedExons() {

            return Collections.unmodifiableList(categorizedExons);
        }

        public List<InvalidExonException> getExceptionList() {

            return Collections.unmodifiableList(exceptions);
        }

        public boolean hasMappingErrors() {

            return !exceptions.isEmpty();
        }
    }
}
