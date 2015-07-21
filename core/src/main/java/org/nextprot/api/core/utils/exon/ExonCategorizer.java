package org.nextprot.api.core.utils.exon;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.Exon;

/**
 * Categorise exons according to gene coordinate of a protein isoform
 *
 * Created by fnikitin on 21/07/15.
 */
public class ExonCategorizer {

    private final int startPositionIsoform;
    private final int endPositionIsoform;

    public ExonCategorizer(int startPositionIsoform, int endPositionIsoform) {

        Preconditions.checkArgument(startPositionIsoform>0);
        Preconditions.checkArgument(endPositionIsoform>=startPositionIsoform);

        this.startPositionIsoform = startPositionIsoform;
        this.endPositionIsoform = endPositionIsoform;
    }

    public ExonType categorize(Exon exon) {

        int startPositionExon = exon.getFirstPositionOnGene();
        int endPositionExon = exon.getLastPositionOnGene();

        Preconditions.checkArgument(startPositionIsoform <= endPositionIsoform, "The start position of the isoform on the gene " + startPositionIsoform + " can not be bigger than the end " + endPositionIsoform);
        Preconditions.checkArgument(startPositionExon <= endPositionExon, "The start position of the exon on the gene " + startPositionIsoform + " can not be bigger than the end " + endPositionIsoform);

        ExonType codingStatus;

        // not coding exons in the beginning of the transcript
        if (endPositionExon < startPositionIsoform) {
            codingStatus = ExonType.NOT_CODING;
            // ************ SPI ******************* EPI *******************
            // **<SPE>***EPE***********************************************
        }

        // end codon or stop only exon
        else if (startPositionExon > endPositionIsoform) {
            // Some kind of hack has probably been done in the db here !!
            // We consider exon to be of kind STOP_ONLY if it is closed to the last coding exon !!
            if (startPositionExon - endPositionIsoform < 3) codingStatus = ExonType.STOP_ONLY;
            else codingStatus = ExonType.NOT_CODING;

            // ************ SPI ******************* EPI *******************
            // ********************************************SPE*<EPE>*******
        }

        // start codon
        else if (startPositionExon <= startPositionIsoform && endPositionExon < endPositionIsoform) {
            codingStatus = ExonType.START;
            // ************ SPI ******************* EPI *******************
            // *******SPE**********<EPE>***********************************
        }

        // end codon
        else if (endPositionExon >= endPositionIsoform && startPositionExon > startPositionIsoform && startPositionExon < endPositionIsoform) {
            codingStatus = ExonType.STOP;
            // ************ SPI ******************* EPI *******************
            // *********************<SPE>******************EPE*************
        }

        // Case where only one exon can translate the whole isoform
        else if (startPositionExon <= startPositionIsoform && endPositionExon >= endPositionIsoform) {
            codingStatus = ExonType.MONO;
            // ************ SPI ******************* EPI *******************
            // *************SPE**********************************EPE*******
        } else {

            // In the last case it must be a coding exon
            codingStatus = ExonType.CODING;
        }

        return codingStatus;
    }

    private boolean isStopOnlyDeducedFromGeneSeq(Exon previous, String geneSequence) {

        if (previous != null) {

            // 1. startPositionExon > endPositionIsoform
            // 2. first codon of this exon is a CODON STOP:(XXX)
            //  3 possible cases:
            //  PREV-EXON+0_LAST-CODON: ... CUR-EXON_LAST-CODON: XXX
            //  PREV-EXON+1_LAST-CODON: ..X CUR-EXON_LAST-CODON: XX.
            //  PREV-EXON+2_LAST-CODON: .XX CUR-EXON_LAST-CODON: X..

            String firstCodon = getFirstCodon(previous, geneSequence);

            if (firstCodon.equals("TAA") || firstCodon.equals("TAG") || firstCodon.equals("TGA"))
                return true;
        }
        return false;
    }

    private String getFirstCodon(Exon previous, String geneSequence) {

        Preconditions.checkNotNull(previous);

        // prev: phase | curr
        //  XXX: 0     | NNN
        //  XXN: 1     | NNX
        //  XNN: 2     | NXX
        int genePos = previous.getLastPositionOnGene();
        int phase = previous.getLastAminoAcid().getPhase();

        return geneSequence.substring(genePos-phase, genePos+3-phase);
    }
}
