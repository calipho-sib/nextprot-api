package org.nextprot.api.commons.bio.variation;

import org.nextprot.api.commons.bio.AminoAcidCode;

/**
 * Formats amino-acids that change
 *
 * Created by fnikitin on 10/07/15.
 */
public interface ChangingAAsFormat {

    void format(StringBuilder sb, SequenceVariation sequenceVariation, AminoAcidCode.AACodeType type);
}
