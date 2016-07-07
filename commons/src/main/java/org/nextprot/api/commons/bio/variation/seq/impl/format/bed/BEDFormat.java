package org.nextprot.api.commons.bio.variation.seq.impl.format.bed;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.seq.SequenceVariation;
import org.nextprot.api.commons.bio.variation.seq.ChangingAAsFormat;

public class BEDFormat implements ChangingAAsFormat {

    @Override
    public void format(StringBuilder sb, SequenceVariation sequenceVariation, AminoAcidCode.AACodeType type) {

        // Tyr223
        sb.append(AminoAcidCode.formatAminoAcidCode(type, sequenceVariation.getFirstChangingAminoAcid()));
        sb.append(sequenceVariation.getFirstChangingAminoAcidPos());
    }
}
