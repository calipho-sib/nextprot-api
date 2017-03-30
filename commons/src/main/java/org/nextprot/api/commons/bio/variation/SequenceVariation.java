package org.nextprot.api.commons.bio.variation;

import org.nextprot.api.commons.bio.AminoAcidCode;

/**
 * A {@code SequenceVariation} describes a variation of a protein sequence.
 *
 * It is composed of 2 parts:
 *
 * <ol>
 * <li>the changing part defining and locating the amino-acids that change</li>
 * <li>the variation itself</li>
 * </ol>
 */
public interface SequenceVariation {

    /** @return the first amino-acid affected by the change */
    AminoAcidCode getFirstChangingAminoAcid();

    /** @return the first amino-acid position affected by the change */
    int getFirstChangingAminoAcidPos();

    /** @return the last amino-acid affected by the change */
    AminoAcidCode getLastChangingAminoAcid();

    /** @return the last amino-acid position affected by the change */
    int getLastChangingAminoAcidPos();

    /** @return the protein sequence change itself */
    SequenceChange<?> getSequenceChange();

    /** @return true if many amino-acids are affected by the change */
    default boolean isMultipleChangingAminoAcids() {
        return getLastChangingAminoAcidPos()-getFirstChangingAminoAcidPos() > 0;
    }
}
