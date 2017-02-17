package org.nextprot.api.commons.bio.variation.impl.format;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.SequenceChange;

/**
 * A sequence change in the translation termination (stop codon) extending the normal
 * translational reading frame at the C-terminal end with one or more amino acids.
 *
 * Format (C-terminal): “p”“Ter_position”“new_amino_acid”“ext”“Ter+position_new_termination_site”, e.g. p.Ter110GlnextTer17
 *
 * Created by fnikitin on 10/07/15.
 */
public class TerminationExtension implements SequenceChange<AminoAcidCode> {

    private final int newDownstreamTermPos;
    private final AminoAcidCode newAminoAcid;

    public TerminationExtension(int newDownstreamTermPos, AminoAcidCode newAminoAcid) {

        Preconditions.checkNotNull(newAminoAcid);
        Preconditions.checkArgument(newDownstreamTermPos > 0);

        this.newDownstreamTermPos = newDownstreamTermPos;
        this.newAminoAcid = newAminoAcid;
    }

    @Override
    public AminoAcidCode getValue() {

        return newAminoAcid;
    }

    @Override
    public Type getType() {
        return Type.EXTENSION_TERM;
    }

    /**
     * @return new position of new termination site
     */
    public int getNewDownstreamTermPos() {

        return newDownstreamTermPos;
    }
}
