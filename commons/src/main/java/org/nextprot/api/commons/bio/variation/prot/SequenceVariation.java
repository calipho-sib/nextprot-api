package org.nextprot.api.commons.bio.variation.prot;

import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChange;
import org.nextprot.api.commons.bio.variation.prot.varseq.VaryingSequence;

/**
 * A {@code SequenceVariation} describes a variation of a protein sequence.
 *
 * It is composed of 2 parts:
 *
 * <ol>
 * <li>the changing part defining and locating the amino-acids that change</li>
 * <li>the sequence variation itself</li>
 * </ol>
 */
public interface SequenceVariation {

    /** @return the changing protein sequence */
    VaryingSequence getVaryingSequence();

    /** @return the protein sequence change itself */
    SequenceChange<?> getSequenceChange();

}
