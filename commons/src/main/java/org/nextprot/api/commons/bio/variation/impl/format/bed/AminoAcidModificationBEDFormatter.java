package org.nextprot.api.commons.bio.variation.impl.format.bed;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.ChangingSequenceFormatter;
import org.nextprot.api.commons.bio.variation.SequenceVariation;

public class AminoAcidModificationBEDFormatter implements ChangingSequenceFormatter {

    @Override
    public void format(SequenceVariation sequenceVariation, AminoAcidCode.CodeType type, StringBuilder sb) {

        // Tyr223
        sb.append(AminoAcidCode.formatAminoAcidCode(type, sequenceVariation.getFirstChangingAminoAcid()));
        sb.append(sequenceVariation.getFirstChangingAminoAcidPos());
    }
}
