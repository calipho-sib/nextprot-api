package org.nextprot.api.commons.bio.variation.impl;

import org.nextprot.api.commons.bio.AminoAcidCode;

/**
 * A sequence change in the translation termination (stop codon) extending the normal
 * translational reading frame at the C-terminal end with one or more amino acids.
 *
 * Format (C-terminal): “p”“Ter_position”“new_amino_acid”“ext”“Ter+position_new_termination_site”, e.g. p.Ter110GlnextTer17
 *
 * Created by fnikitin on 10/07/15.
 */
public class ExtensionTermination extends Extension {

    ExtensionTermination(int newDownstreamTermPos, AminoAcidCode newAminoAcid) {

        super(newDownstreamTermPos, newAminoAcid);
    }

    @Override
    protected boolean isPosOk(int newDownstreamTermPos) {

        return newDownstreamTermPos > 0;
    }

    @Override
    public Type getType() {
        return Type.EXTENSION_TERM;
    }

}
