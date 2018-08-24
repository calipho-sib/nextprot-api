package org.nextprot.api.commons.bio.variation.prot.seqchange;


import org.nextprot.api.commons.bio.variation.prot.*;

import java.text.ParseException;

/**
 * Specify how change C is formatted as String and parsed to ProteinSequenceVariation
 *
 * Created by fnikitin on 10/07/15.
 */
public interface SequenceChangeHGVSFormat<C extends SequenceChange<?>>
        extends SequenceChangeFormat<SequenceVariationBuilder.StartBuilding, C> {

    /**
     * Parse the source and build ProteinSequenceVariation with given builder
     * @param source the formatted change
     * @param builder the builder to build ProteinSequenceVariation
     * @param mode the parsing mode
     * @return an instance of ProteinSequenceVariation
     * @throws ParseException if parsing error occurs
     */
    SequenceVariation parseWithMode(String source, SequenceVariationBuilder.StartBuilding builder,
                                    ParsingMode mode) throws ParseException, SequenceVariationBuildException;
    /**
     * Attempts to match the source.
     * @return  <tt>true</tt> if matches the source
     */
    boolean matchesWithMode(String source, ParsingMode mode);

    default SequenceVariation parse(String source, SequenceVariationBuilder.StartBuilding builder) throws ParseException, SequenceVariationBuildException {

        return parseWithMode(source, builder, ParsingMode.STRICT);
    }

    default boolean matches(String source) {

        return matchesWithMode(source, ParsingMode.STRICT);
    }
}
