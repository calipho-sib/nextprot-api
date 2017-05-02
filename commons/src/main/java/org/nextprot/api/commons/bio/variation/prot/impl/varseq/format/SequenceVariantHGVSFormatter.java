package org.nextprot.api.commons.bio.variation.prot.impl.varseq.format;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.varseq.VaryingSequenceFormatter;
import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;

/**
 * Formatting changing aas in HGVS format
 *
 * Created by fnikitin on 07/09/15.
 */
public class SequenceVariantHGVSFormatter implements VaryingSequenceFormatter {

    @Override
    public void format(SequenceVariation sequenceVariation, AminoAcidCode.CodeType type, StringBuilder sb) {

        sb.append(AminoAcidCode.formatAminoAcidCode(type, sequenceVariation.getVaryingSequence().getFirstAminoAcid()));
        sb.append(sequenceVariation.getVaryingSequence().getFirstAminoAcidPos());
        if (sequenceVariation.getVaryingSequence().isMultipleAminoAcids())
            sb.append("_").append(AminoAcidCode.formatAminoAcidCode(type, sequenceVariation.getVaryingSequence().getLastAminoAcid())).append(sequenceVariation.getVaryingSequence().getLastAminoAcidPos());
    }
}
