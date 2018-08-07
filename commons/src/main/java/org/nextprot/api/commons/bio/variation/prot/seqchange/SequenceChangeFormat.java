package org.nextprot.api.commons.bio.variation.prot.seqchange;

/**
 * Specify how change C is formatted as String and parsed to ProteinSequenceVariation
 *
 * Created by fnikitin on 10/07/15.
 */
public interface SequenceChangeFormat<C extends SequenceChange<?>> extends SequenceChangeFormatter<C>, SequenceChangeParser { }
