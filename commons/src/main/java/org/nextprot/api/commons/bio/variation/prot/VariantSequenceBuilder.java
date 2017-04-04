package org.nextprot.api.commons.bio.variation.prot;

import org.nextprot.api.commons.bio.variation.prot.varseq.VaryingSequence;

/**
 * Factory that build variant sequence from an reference protein sequence and a SequenceVariation
 *
 * Created by fnikitin on 03.04.17.
 */
public interface VariantSequenceBuilder {

    /**
     * Select from position in reference sequence
     * @param varyingSequence the sequence positions of amino-acids affected by this change
     * @return the first position selected on the reference sequence
     */
    int selectBeginPositionInReferenceSequence(VaryingSequence varyingSequence);

    /**
     * Select to position in reference sequence
     * @param varyingSequence the sequence positions of amino-acids affected by this change
     * @return the last position selected on the reference sequence (or -1 if undefined)
     */
    int selectEndPositionInReferenceSequence(VaryingSequence varyingSequence);

    /**
     * Get the amino-acids to replace in the reference sequence
     * @param referenceSequence the reference protein sequence
     * @param sequenceVariation the sequence variation
     * @return a replacement sequence of amino-acids
     */
    String getAminoAcidReplacementString(String referenceSequence, SequenceVariation sequenceVariation);

    /**
     * Get the portion of amino-acids that change in the original sequence
     * @param referenceSequence the reference protein sequence
     * @param varyingSequence the sequence positions of amino-acids affected by this change
     * @return a substring of amino-acids
     */
    default String getAminoAcidTargetStringInReferenceSequence(String referenceSequence, VaryingSequence varyingSequence) {

        return referenceSequence.substring(selectBeginPositionInReferenceSequence(varyingSequence)-1, selectEndPositionInReferenceSequence(varyingSequence));
    }

    /**
     * Create a new sequence variant from a given reference sequence and its variation
     * @param referenceSequence reference sequence
     * @param sequenceVariation the sequence variation
     * @return a new variant sequence
     */
    default String buildVariantSequence(String referenceSequence, SequenceVariation sequenceVariation) {

        int selectionBegin = selectBeginPositionInReferenceSequence(sequenceVariation.getVaryingSequence());
        int selectionEnd = selectEndPositionInReferenceSequence(sequenceVariation.getVaryingSequence());

        StringBuilder sb = new StringBuilder(referenceSequence.substring(0, selectionBegin - 1));
        //The sequence of char values to be replaced
        sb.append(getAminoAcidReplacementString(referenceSequence, sequenceVariation));

        if (selectionEnd >=0) {
            sb.append(referenceSequence.substring(selectionEnd));
        }

        return sb.toString();
    }
}
