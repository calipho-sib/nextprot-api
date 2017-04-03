package org.nextprot.api.commons.bio.variation;

/**
 * Factory that build variant sequence from an original protein sequence and a SequenceVariation
 *
 * Created by fnikitin on 03.04.17.
 */
@FunctionalInterface
public interface VariantSequenceFactory {

    /**
     * Create a new protein sequence variant from an original sequence and its variation
     * @param originalSequence protein sequence
     * @param sequenceVariation the sequence variation
     * @return a new String sequence
     */
    String buildVariantSequence(String originalSequence, SequenceVariation sequenceVariation);
}
