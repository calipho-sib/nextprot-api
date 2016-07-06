package com.nextprot.api.isoform.mapper.domain.impl;

import com.nextprot.api.isoform.mapper.domain.IsoformFeature;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.ProteinSequenceVariation;

public class VariantAdapter implements IsoformFeature {

    private final ProteinSequenceVariation variant;

    public VariantAdapter(ProteinSequenceVariation variant) {
        this.variant = variant;
    }

    public AminoAcidCode getFirstChangingAminoAcid() {
        return variant.getFirstChangingAminoAcid();
    }

    public int getFirstChangingAminoAcidPos() {
        return variant.getFirstChangingAminoAcidPos();
    }

    public AminoAcidCode getLastChangingAminoAcid() {
        return variant.getLastChangingAminoAcid();
    }

    public int getLastChangingAminoAcidPos() {
        return variant.getLastChangingAminoAcidPos();
    }
}
