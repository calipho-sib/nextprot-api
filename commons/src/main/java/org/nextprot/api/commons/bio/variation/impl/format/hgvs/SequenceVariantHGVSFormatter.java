package org.nextprot.api.commons.bio.variation.impl.format.hgvs;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.ChangingSequenceFormatter;
import org.nextprot.api.commons.bio.variation.SequenceVariation;

/**
 * Formatting changing aas in HGVS format
 *
 * Created by fnikitin on 07/09/15.
 */
public class SequenceVariantHGVSFormatter implements ChangingSequenceFormatter {

    @Override
    public void format(SequenceVariation sequenceVariation, AminoAcidCode.CodeType type, StringBuilder sb) {

        sb.append(AminoAcidCode.formatAminoAcidCode(type, sequenceVariation.getChangingSequence().getFirstAminoAcid()));
        sb.append(sequenceVariation.getChangingSequence().getFirstAminoAcidPos());
        if (sequenceVariation.getChangingSequence().isMultipleAminoAcids())
            sb.append("_").append(AminoAcidCode.formatAminoAcidCode(type, sequenceVariation.getChangingSequence().getLastAminoAcid())).append(sequenceVariation.getChangingSequence().getLastAminoAcidPos());
    }
}
