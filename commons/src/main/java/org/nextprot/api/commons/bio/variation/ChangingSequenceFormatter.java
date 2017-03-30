package org.nextprot.api.commons.bio.variation;

import org.nextprot.api.commons.bio.AminoAcidCode;

/**
 * Format changing amino-acids
 *
 * Created by fnikitin on 10/07/15.
 */
public interface ChangingSequenceFormatter {

    void format(SequenceVariation sequenceVariation, AminoAcidCode.CodeType type, StringBuilder collector);
}
