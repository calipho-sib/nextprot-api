package org.nextprot.api.commons.bio.variation.prot;

import org.nextprot.api.commons.bio.AminoAcidCode;

/**
 * Format SequenceVariation as recommended by the Human Genome Variation Society
 *
 * @param <T> the format type
 */
public interface SequenceVariationFormatter<T> {

    /**
     * Convert sequence variation in T
     * @param sequenceVariation the sequence variation to format
     * @param type the aa letter code type
     * @return a converted sequence variation
     */
    T format(SequenceVariation sequenceVariation, AminoAcidCode.CodeType type);

    default T format(SequenceVariation mutation) {

        return format(mutation, AminoAcidCode.CodeType.ONE_LETTER);
    }
}
