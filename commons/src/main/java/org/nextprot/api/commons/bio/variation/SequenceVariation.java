package org.nextprot.api.commons.bio.variation;

import org.nextprot.api.commons.bio.AminoAcidCode;

public interface SequenceVariation {

    AminoAcidCode getFirstChangingAminoAcid();

    int getFirstChangingAminoAcidPos();

    AminoAcidCode getLastChangingAminoAcid();

    int getLastChangingAminoAcidPos();

    default boolean isMultipleChangingAminoAcids() {
         return getLastChangingAminoAcidPos()-getFirstChangingAminoAcidPos() > 0;
    }

    SequenceChange getSequenceChange();
}
