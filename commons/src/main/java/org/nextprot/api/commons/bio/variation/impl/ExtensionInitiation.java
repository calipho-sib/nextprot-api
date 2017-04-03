package org.nextprot.api.commons.bio.variation.impl;

import org.nextprot.api.commons.bio.AminoAcidCode;

/**
 * A sequence change in the translation initiation (start codon) extending the normal
 * translational reading frame at the N-terminal end.
 *
 * Format (N-terminal): “p”“Met1”“ext”“position_new_initiation_site”, e.g. p.Met1ext-5
 *
 * Created by fnikitin on 10/07/15.
 */
public class ExtensionInitiation extends Extension {

    ExtensionInitiation(int newUpstreamInitPos, AminoAcidCode newAminoAcid) {

        super(newUpstreamInitPos, newAminoAcid);
    }

    @Override
    protected boolean isPosOk(int newUpstreamInitPos) {

        return newUpstreamInitPos < 0;
    }

    @Override
    public Type getType() {
        return Type.EXTENSION_INIT;
    }
}
