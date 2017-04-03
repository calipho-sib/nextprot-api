package org.nextprot.api.commons.bio.variation;

import org.nextprot.api.commons.bio.AminoAcidCode;

import java.util.Optional;

/**
 * Operator used to build a sequence variant from an original sequence and SequenceVariation
 *
 * Created by fnikitin on 31.03.17.
 */
public enum SequenceChangeOperator implements VariantSequenceFactory {

    DELETION() {
        @Override
        protected int selectPositionStart(ChangingSequence changingSequence) {
            return changingSequence.getFirstAminoAcidPos();
        }

        @Override
        protected int selectPositionEnd(ChangingSequence changingSequence) {
            return changingSequence.getLastAminoAcidPos();
        }

        @Override
        protected String variant(String originalSequence, SequenceVariation sequenceVariation) {
            return "";
        }
    },
    DUPLICATION() {

        @Override
        protected int selectPositionStart(ChangingSequence changingSequence) {
            return changingSequence.getLastAminoAcidPos();
        }

        @Override
        protected int selectPositionEnd(ChangingSequence changingSequence) {
            return changingSequence.getLastAminoAcidPos();
        }

        @Override
        protected String variant(String originalSequence, SequenceVariation sequenceVariation) {

            // p.Leu103_Met106dup
            //     .--.
            //     v  v
            // ...MLISM...
            // ...MLISMLISM...
            // [original=M, variant=MLISM]
            return originalSequence.substring(
                    sequenceVariation.getChangingSequence().getFirstAminoAcidPos() - 2,
                    sequenceVariation.getChangingSequence().getLastAminoAcidPos());
        }
    },
    SUBSTITUTION() {

        @Override
        protected int selectPositionStart(ChangingSequence changingSequence) {
            return changingSequence.getFirstAminoAcidPos();
        }

        @Override
        protected int selectPositionEnd(ChangingSequence changingSequence) {
            return changingSequence.getFirstAminoAcidPos();
        }

        @Override
        protected String variant(String originalSequence, SequenceVariation sequenceVariation) {

            return AminoAcidCode.formatAminoAcidCode(AminoAcidCode.CodeType.ONE_LETTER, ((SequenceChange<AminoAcidCode>)sequenceVariation.getSequenceChange())
                    .getValue());
        }
    },
    INSERTION() {

        @Override
        protected int selectPositionStart(ChangingSequence changingSequence) {
            return changingSequence.getFirstAminoAcidPos();
        }

        @Override
        protected int selectPositionEnd(ChangingSequence changingSequence) {
            return changingSequence.getFirstAminoAcidPos();
        }

        @Override
        protected String variant(String originalSequence, SequenceVariation sequenceVariation) {

            return AminoAcidCode.formatAminoAcidCode(AminoAcidCode.CodeType.ONE_LETTER, sequenceVariation.getChangingSequence().getFirstAminoAcid()) +
                    AminoAcidCode.formatAminoAcidCode(AminoAcidCode.CodeType.ONE_LETTER, ((SequenceChange<AminoAcidCode[]>) sequenceVariation.getSequenceChange()).getValue());
        }
    },
    DELETION_INSERTION() {

        @Override
        protected int selectPositionStart(ChangingSequence changingSequence) {
            return changingSequence.getFirstAminoAcidPos();
        }

        @Override
        protected int selectPositionEnd(ChangingSequence changingSequence) {
            return changingSequence.getLastAminoAcidPos();
        }

        @Override
        protected String variant(String originalSequence, SequenceVariation sequenceVariation) {

            return AminoAcidCode.formatAminoAcidCode(AminoAcidCode.CodeType.ONE_LETTER, ((SequenceChange<AminoAcidCode[]>)sequenceVariation.getSequenceChange())
                    .getValue());
        }
    }
    ;

    protected abstract int selectPositionStart(ChangingSequence changingSequence);
    protected abstract int selectPositionEnd(ChangingSequence changingSequence);
    protected abstract String variant(String originalSequence, SequenceVariation sequenceVariation);

    public String original(String originalSequence, ChangingSequence changingSequence) {

        return originalSequence.substring(selectPositionStart(changingSequence)-1, selectPositionEnd(changingSequence));
    }

    @Override
    public String buildVariantSequence(String originalSequence, SequenceVariation sequenceVariation) {

        int selectionStart = selectPositionStart(sequenceVariation.getChangingSequence());
        int selectionEnd = selectPositionEnd(sequenceVariation.getChangingSequence());

        return new StringBuilder()
                .append(originalSequence.substring(0, selectionStart - 1))
                .append(variant(originalSequence, sequenceVariation))
                .append(originalSequence.substring(selectionEnd))
                .toString();
    }

    public static Optional<SequenceChangeOperator> findOperator(SequenceChange sequenceChange) {

        SequenceChange.Type sequenceType = sequenceChange.getType();

        switch (sequenceType) {

            case DELETION:
                return Optional.of(DELETION);
            case DUPLICATION:
                return Optional.of(DUPLICATION);
            case INSERTION:
                return Optional.of(INSERTION);
            case SUBSTITUTION:
                return Optional.of(SUBSTITUTION);
            case DELETION_INSERTION:
                return Optional.of(DELETION_INSERTION);
        }
        return Optional.empty();
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