package org.nextprot.api.commons.bio.variation.format.hgvs;

import org.nextprot.api.commons.bio.variation.format.ChangingAAsFormat;
import org.nextprot.api.commons.bio.variation.ProteinSequenceVariation;
import org.nextprot.api.commons.bio.variation.format.ProteinSequenceVariationFormat;

import static org.nextprot.api.commons.bio.variation.format.AbstractProteinSequenceVariationFormat.formatAminoAcidCode;

/**
 * HGVS implementation of mutated aas
 *
 * Created by fnikitin on 07/09/15.
 */
public class HGVSFormat implements ChangingAAsFormat {

    @Override
    public void format(StringBuilder sb, ProteinSequenceVariation proteinSequenceVariation, ProteinSequenceVariationFormat.AACodeType type) {

        sb.append("p.");

        sb.append(formatAminoAcidCode(type, proteinSequenceVariation.getFirstChangingAminoAcid()));
        sb.append(proteinSequenceVariation.getFirstChangingAminoAcidPos());
        if (proteinSequenceVariation.isAminoAcidRange())
            sb.append("_").append(formatAminoAcidCode(type, proteinSequenceVariation.getLastChangingAminoAcid())).append(proteinSequenceVariation.getLastChangingAminoAcidPos());
    }
}
