package org.nextprot.api.commons.bio.variation.prot.seqchange;

import org.nextprot.api.commons.bio.variation.prot.SequenceVariationBuilder;

/**
 * Specify how change C is formatted as String and parsed to ProteinSequenceVariation
 *
 * Created by fnikitin on 10/07/15.
 */
public interface SequenceChangeFormat<B extends SequenceVariationBuilder.Start, C extends SequenceChange<?>>
        extends SequenceChangeFormatter<C>, SequenceChangeParser<B> { }
