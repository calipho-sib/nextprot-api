package org.nextprot.api.commons.bio.variation.prot;

import org.nextprot.api.commons.bio.variation.prot.impl.SequenceVariationImpl;

import java.text.ParseException;
import java.util.Objects;

/**
 * Parse a string and build an instance of {@code SequenceVariation}
 */
public interface SequenceVariationParser {

    /**
     * Parse a string and build a SequenceVariation
     * @param source the string to parse
     * @param builder the builder building SequenceVariation
     * @return a SequenceVariation
     * @throws ParseException
     */
    SequenceVariation parse(String source, SequenceVariationBuilder.Start builder) throws ParseException, SequenceVariationBuildException;

    /**
     * Parses text from the beginning of the given string to produce a ProteinMutation in permissive or strict mode
     *
     * @param source a standard or closely standard HGV text.
     * @return A <code>SequenceVariation</code> parsed from the string.
     * @exception ParseException if the specified string cannot be parsed.
     */
    default SequenceVariation parse(String source) throws ParseException, SequenceVariationBuildException {

        Objects.requireNonNull(source);

        return parse(source, new SequenceVariationImpl.StartBuilding());
    }

}
