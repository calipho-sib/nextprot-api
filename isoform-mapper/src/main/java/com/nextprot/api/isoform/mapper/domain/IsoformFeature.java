package com.nextprot.api.isoform.mapper.domain;

import org.nextprot.api.commons.bio.AminoAcidCode;

public interface IsoformFeature {

    AminoAcidCode getFirstChangingAminoAcid();

    int getFirstChangingAminoAcidPos();

    AminoAcidCode getLastChangingAminoAcid();

    int getLastChangingAminoAcidPos();
}
