package org.nextprot.api.commons.bio.variation.prot.seqchange;

import org.nextprot.api.commons.bio.variation.prot.SequenceVariationBuildException;
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
     * @throws ParseException if error occurs while parsing source
     * @throws SequenceVariationBuildException if error occurs while building SequenceVariation
     */
    SequenceVariation parse(String source, B builder) throws ParseException, SequenceVariationBuildException;

    /**
     * Attempts to match the source.
     * @return <tt>true</tt> if matches the source
     */
    boolean matches(String source);
}
