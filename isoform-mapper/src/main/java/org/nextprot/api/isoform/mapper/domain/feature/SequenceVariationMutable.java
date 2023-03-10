package org.nextprot.api.isoform.mapper.domain.feature;

import org.nextprot.api.commons.bio.variation.prot.SequenceVariation;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChange;
import org.nextprot.api.commons.bio.variation.prot.varseq.VaryingSequence;

/**
 * A simple mutable implementation of SequenceVariation
 *
 * Created by fnikitin on 04.04.17.
 */
public class SequenceVariationMutable implements SequenceVariation {

    private VaryingSequence varyingSequence;
    private SequenceChange change;

    public void setSequenceChange(SequenceChange change) {
        this.change = change;
    }

    public void setVaryingSequence(VaryingSequence varyingSequence) {
        this.varyingSequence = varyingSequence;
    }

    @Override
    public VaryingSequence getVaryingSequence() {
        return varyingSequence;
    }

    @Override
    public SequenceChange getSequenceChange() {
        return change;
    }
}
