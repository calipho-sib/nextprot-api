package org.nextprot.api.commons.bio.variation.varseq;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.SequenceVariation;

/**
 * Format changing amino-acids
 *
 * Created by fnikitin on 10/07/15.
 */
public interface VaryingSequenceFormatter {

    void format(SequenceVariation sequenceVariation, AminoAcidCode.CodeType type, StringBuilder collector);
}
