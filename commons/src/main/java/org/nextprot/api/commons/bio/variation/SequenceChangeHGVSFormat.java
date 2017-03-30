package org.nextprot.api.commons.bio.variation;


import org.nextprot.api.commons.bio.variation.impl.format.hgvs.SequenceVariantHGVSFormat;

import java.text.ParseException;

/**
 * Specify how change C is formatted as String and parsed to ProteinSequenceVariation
 *
 * Created by fnikitin on 10/07/15.
 */
public interface SequenceChangeHGVSFormat<C extends SequenceChange> extends SequenceChangeFormat<C> {

    /**
     * Parse the source and build ProteinSequenceVariation with given builder
     * @param source the formatted change
     * @param builder the builder to build ProteinSequenceVariation
     * @param mode the parsing mode
     * @return an instance of ProteinSequenceVariation
     * @throws ParseException if parsing error occurs
     */
    SequenceVariation parseWithMode(String source, SequenceVariationBuilder.FluentBuilding builder,
                                    SequenceVariantHGVSFormat.ParsingMode mode) throws ParseException;
    /**
     * Attempts to match the source.
     * @return  <tt>true</tt> if matches the source
     */
    boolean matchesWithMode(String source, SequenceVariantHGVSFormat.ParsingMode mode);

    default SequenceVariation parse(String source, SequenceVariationBuilder.FluentBuilding builder) throws ParseException {

        return parseWithMode(source, builder, SequenceVariantHGVSFormat.ParsingMode.STRICT);
    }

    default boolean matches(String source) {

        return matchesWithMode(source, SequenceVariantHGVSFormat.ParsingMode.STRICT);
    }
}
