package org.nextprot.api.commons.bio.variation.format.hgvs;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.ProteinSequenceVariation;
import org.nextprot.api.commons.bio.variation.format.ChangingAAsFormat;

/**
 * HGVS implementation of mutated aas
 *
 * Created by fnikitin on 07/09/15.
 */
public class HGVSFormat implements ChangingAAsFormat {

    @Override
    public void format(StringBuilder sb, ProteinSequenceVariation proteinSequenceVariation, AminoAcidCode.AACodeType type) {

        sb.append("p.");

        sb.append(AminoAcidCode.formatAminoAcidCode(type, proteinSequenceVariation.getFirstChangingAminoAcid()));
        sb.append(proteinSequenceVariation.getFirstChangingAminoAcidPos());
        if (proteinSequenceVariation.isAminoAcidRange())
            sb.append("_").append(AminoAcidCode.formatAminoAcidCode(type, proteinSequenceVariation.getLastChangingAminoAcid())).append(proteinSequenceVariation.getLastChangingAminoAcidPos());
    }
}
