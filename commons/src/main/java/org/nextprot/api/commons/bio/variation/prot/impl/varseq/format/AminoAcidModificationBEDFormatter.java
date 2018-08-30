package org.nextprot.api.commons.bio.variation.prot.impl.varseq.format;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.commons.bio.variation.prot.varseq.VaryingSequenceFormatter;

public class AminoAcidModificationBEDFormatter implements VaryingSequenceFormatter {

    @Override
    public void format(SequenceVariation sequenceVariation, AminoAcidCode.CodeType type, StringBuilder sb) {

        // 223
        sb.append(sequenceVariation.getVaryingSequence().getFirstAminoAcidPos());
    }
}
