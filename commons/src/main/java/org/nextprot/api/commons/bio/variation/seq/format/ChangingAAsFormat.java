package org.nextprot.api.commons.bio.variation.seq.format;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.seq.ProteinSequenceVariation;

/**
 * Formats amino-acids that change
 *
 * Created by fnikitin on 10/07/15.
 */
public interface ChangingAAsFormat {

    void format(StringBuilder sb, ProteinSequenceVariation proteinSequenceVariation, AminoAcidCode.AACodeType type);
}
