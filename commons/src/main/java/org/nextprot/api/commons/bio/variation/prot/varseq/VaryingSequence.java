package org.nextprot.api.commons.bio.variation.prot.varseq;

import org.nextprot.api.commons.bio.AminoAcidCode;

/**
 * The changing part defining and locating the amino-acids that change on reference sequence
 */
public interface VaryingSequence {

    /** @return the first amino-acid affected by the change */
    AminoAcidCode getFirstAminoAcid();

    /** @return the first amino-acid position affected by the change */
    int getFirstAminoAcidPos();

    /** @return the last amino-acid affected by the change */
    AminoAcidCode getLastAminoAcid();

    /** @return the last amino-acid position affected by the change */
    int getLastAminoAcidPos();

    /** @return true if many amino-acids are affected by the change */
    default boolean isMultipleAminoAcids() {
        return getLastAminoAcidPos()- getFirstAminoAcidPos() > 0;
    }
}
