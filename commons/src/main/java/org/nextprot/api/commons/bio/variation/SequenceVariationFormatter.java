package org.nextprot.api.commons.bio.variation;

import org.nextprot.api.commons.bio.AminoAcidCode;

/**
 * Format and parse ProteinMutation as recommended by the Human Genome Variation Society
 *
 * @param <T> the format type
 */
public interface SequenceVariationFormatter<T> {

    /**
     * Convert sequence variation in T
     * @param sequenceVariation the sequence variation to format
     * @param type the aa code type
     * @return a converted sequence variation
     */
    T format(SequenceVariation sequenceVariation, AminoAcidCode.CodeType type);

    default T format(SequenceVariation mutation) {

        return format(mutation, AminoAcidCode.CodeType.ONE_LETTER);
    }
}
