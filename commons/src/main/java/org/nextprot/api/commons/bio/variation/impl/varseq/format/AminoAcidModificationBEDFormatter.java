package org.nextprot.api.commons.bio.variation.impl.varseq.format;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.varseq.VaryingSequenceFormatter;
import org.nextprot.api.commons.bio.variation.SequenceVariation;

public class AminoAcidModificationBEDFormatter implements VaryingSequenceFormatter {

    @Override
    public void format(SequenceVariation sequenceVariation, AminoAcidCode.CodeType type, StringBuilder sb) {

        // Tyr223
        sb.append(AminoAcidCode.formatAminoAcidCode(type, sequenceVariation.getVaryingSequence().getFirstAminoAcid()));
        sb.append(sequenceVariation.getVaryingSequence().getFirstAminoAcidPos());
    }
}
