package org.nextprot.api.commons.bio.variation;

/**
 * Created by fnikitin on 31.03.17.
 */
public class SequenceChangeOperation {

    /**
     * Transform reference sequence into variant sequence given an operator
     */
    public String transform(String referenceSequence, SequenceVariation sequenceVariation) {

        SequenceChange.Operator operator = sequenceVariation.getSequenceChange().getOperator();

        if (operator != null) {
            int firstPos = sequenceVariation.getChangingSequence().getFirstAminoAcidPos();
            int lastPos = sequenceVariation.getChangingSequence().getLastAminoAcidPos();

            switch (operator.getChangingPositionType()) {

                case FIRST_FIRST:
                    lastPos = firstPos;
                    break;
                case LAST_LAST:
                    firstPos = lastPos;
                    break;
            }

            return new StringBuilder()
                    .append(referenceSequence.substring(0, firstPos - 1))
                    .append(operator.getVariatingPart())
                    .append(referenceSequence.substring(lastPos))
                    .toString();
        }
        System.err.println("not operation possible of this variation type");

        return null;
    }
}

/**
 * Some examples:
 *
 * VARIATION TYPE                                     \   OPERATIONS (on reference sequence)                     OFFSET
 * ---------------------------------------------------------------------------------------------------------------------
 * Duplication : CEBPA-p.Arg306_Lys313dup             |   1) select K313         2) replace by K RNVETQQK     | last:last
 * Delins      : BRCA1-p.Lys503_Pro508delinsLysLeuPro |   1) select K503RKRRP508 2) replace by KLP            | first:last
 * Del         : APC-p.Val2843del                     |   1) select V2843        2) replace by "" (or delete) | first:last
 * Subst       : WT1-iso1-p.Gly201Asp                 |   1) select Gly201       2) replace by Asp            | first:first
 * Ins         : CEBPA-p.Gln311_Gln312insLeu          |   1) select Q311         2) replace by GlnLeu         | first:first
 * Ext term    : SDHD-p.*160Leuext*3                  |   NA (missing genomic sequence)                       | NA
 * Frameshift  : APC-p.Ser673Phefs*10                 |   NA (missing genomic sequence)                       | NA
 */