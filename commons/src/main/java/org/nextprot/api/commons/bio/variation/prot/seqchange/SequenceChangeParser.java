package org.nextprot.api.commons.bio.variation.prot.seqchange;

import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariationBuilder;

import java.text.ParseException;

/**
 * Parse to ProteinSequenceVariation
 *
 * Created by fnikitin on 10/07/15.
 */
public interface SequenceChangeParser<B extends SequenceVariationBuilder.Start> {

    /**
     * Parse the source and build ProteinSequenceVariation with given builder
     * @param source the formatted change
     * @param builder the builder to build ProteinSequenceVariation
     * @return an instance of ProteinSequenceVariation
     * @throws ParseException if parsing error occurs
     */
    SequenceVariation parse(String source, B builder) throws ParseException;

    /**
     * Attempts to match the source.
     * @return <tt>true</tt> if matches the source
     */
    boolean matches(String source);
}
