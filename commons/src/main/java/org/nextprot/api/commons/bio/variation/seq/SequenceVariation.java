package org.nextprot.api.commons.bio.variation.seq;

import org.nextprot.api.commons.bio.AminoAcidCode;

public interface SequenceVariation {

    AminoAcidCode getFirstChangingAminoAcid();

    int getFirstChangingAminoAcidPos();

    AminoAcidCode getLastChangingAminoAcid();

    int getLastChangingAminoAcidPos();

    boolean isMultipleChangingAminoAcids();

    SequenceChange getSequenceChange();
}
