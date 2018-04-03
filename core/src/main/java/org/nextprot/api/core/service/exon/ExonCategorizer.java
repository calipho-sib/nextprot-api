package org.nextprot.api.core.service.exon;

import com.google.common.base.Preconditions;
import org.nextprot.api.core.domain.exon.ExonCategory;
import org.nextprot.api.core.domain.exon.UncategorizedExon;

/**
 * Categorise exons according to gene coordinate of a protein isoform
 *
 * Created by fnikitin on 21/07/15.
 */
class ExonCategorizer {

    private final int startPositionIsoform;
    private final int endPositionIsoform;

    public ExonCategorizer(int startPositionIsoform, int endPositionIsoform) {

        Preconditions.checkArgument(startPositionIsoform>0);
        Preconditions.checkArgument(endPositionIsoform>=startPositionIsoform);

        this.startPositionIsoform = startPositionIsoform;
        this.endPositionIsoform = endPositionIsoform;
    }

    public ExonCategory categorize(UncategorizedExon exon) throws ExonInvalidBoundException {

        int startPositionExon = exon.getFirstPositionOnGene();
        int endPositionExon = exon.getLastPositionOnGene();

        if (startPositionExon > endPositionExon) {

            throw new ExonInvalidBoundException(exon);
        }

        ExonCategory exonCategory;

        // not coding exons in the beginning of the transcript
        if (endPositionExon < startPositionIsoform) {
            exonCategory = ExonCategory.NOT_CODING;
            // ************ SPI ******************* EPI *******************
            // **<SPE>***EPE***********************************************
        }

        // end codon or stop only exon
        else if (startPositionExon > endPositionIsoform) {
            // Some kind of hack has probably been done in the db here !!
            // We consider exon to be of kind STOP_ONLY if it is closed to the last coding exon !!
            if (startPositionExon - endPositionIsoform < 3) {
                exonCategory = ExonCategory.STOP_ONLY;
            }
            else {
                exonCategory = ExonCategory.NOT_CODING;
            }
            // ************ SPI ******************* EPI *******************
            // ********************************************SPE*<EPE>*******
        }

        // start codon
        else if (startPositionExon <= startPositionIsoform && endPositionExon < endPositionIsoform) {
            exonCategory = ExonCategory.START;
            // ************ SPI ******************* EPI *******************
            // *******SPE**********<EPE>***********************************
        }

        // end codon
        else if (endPositionExon >= endPositionIsoform && startPositionExon > startPositionIsoform) {
            exonCategory = ExonCategory.STOP;
            // ************ SPI ******************* EPI *******************
            // *********************<SPE>******************EPE*************
        }

        // Case where only one exon can translate the whole isoform
        else if (startPositionExon <= startPositionIsoform) {
            exonCategory = ExonCategory.MONO;
            // ************ SPI ******************* EPI *******************
            // *************SPE**********************************EPE*******
        } else {

            // In the last case it must be a coding exon
            exonCategory = ExonCategory.CODING;
        }

        return exonCategory;
    }
}
