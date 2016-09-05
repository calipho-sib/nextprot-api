package org.nextprot.api.commons.bio.variation.impl.format.bed;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.SequenceVariation;
import org.nextprot.api.commons.bio.variation.ChangingAAsFormat;

public class BEDFormat implements ChangingAAsFormat {

    @Override
    public void format(StringBuilder sb, SequenceVariation sequenceVariation, AminoAcidCode.CodeType type) {

        // Tyr223
        sb.append(AminoAcidCode.formatAminoAcidCode(type, sequenceVariation.getFirstChangingAminoAcid()));
        sb.append(sequenceVariation.getFirstChangingAminoAcidPos());
    }
}
