package org.nextprot.api.commons.bio.variation.prot.impl.seqchange;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChange;

/**
 * A glycosylation with a PTM descriptor
 */
public class Glycosylation implements SequenceChange<AminoAcidModification> {

    private final String ptmId;

    public Glycosylation(String ptmId) {

        Preconditions.checkNotNull(ptmId);
        Preconditions.checkArgument(ptmId.matches("PTM-\\d{4}"));
        this.ptmId = ptmId;
    }

    /**
     * @return A PTM id describing the modification
     */
    @Override
    public AminoAcidModification getValue() {
        return AminoAcidModification.GLYCOSYLATION;
    }

    @Override
    public Type getType() {

        return Type.PTM;
    }

    public String getPTMId() {

        return ptmId;
    }
}
