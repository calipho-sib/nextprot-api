package org.nextprot.api.commons.bio.variation.impl;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.SequenceChange;

/**
 * A sequence change in the translation initiation (start codon) extending the normal
 * translational reading frame at the N-terminal end.
 *
 * Format (N-terminal): “p”“Met1”“ext”“position_new_initiation_site”, e.g. p.Met1ext-5
 *
 * Created by fnikitin on 10/07/15.
 */
public class InitiationExtension implements SequenceChange<AminoAcidCode> {

    private final int newUpstreamInitPos;
    private final AminoAcidCode newAminoAcid;

    public InitiationExtension(int newUpstreamInitPos, AminoAcidCode newAminoAcid) {

        Preconditions.checkNotNull(newAminoAcid);
        Preconditions.checkArgument(newUpstreamInitPos < 0);

        this.newUpstreamInitPos = newUpstreamInitPos;
        this.newAminoAcid = newAminoAcid;
    }

    @Override
    public AminoAcidCode getValue() {

        return newAminoAcid;
    }

    @Override
    public Type getType() {
        return Type.EXTENSION;
    }

    /**
     * @return new position of new initiation or termination site
     */
    public int getNewUpstreamInitPos() {

        return newUpstreamInitPos;
    }
}
