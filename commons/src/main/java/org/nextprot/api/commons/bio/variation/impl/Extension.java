package org.nextprot.api.commons.bio.variation.impl;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.SequenceChange;

abstract class Extension implements SequenceChange<AminoAcidCode> {

    private final int newPos;
    private final AminoAcidCode newAminoAcid;

    Extension(int newPos, AminoAcidCode newAminoAcid) {

        Preconditions.checkNotNull(newAminoAcid);
        Preconditions.checkArgument(isPosOk(newPos));

        this.newPos = newPos;
        this.newAminoAcid = newAminoAcid;
    }

    protected abstract boolean isPosOk(int newPos);

    @Override
    public AminoAcidCode getValue() {

        return newAminoAcid;
    }

    /**
     * @return new position of new initiation or termination site
     */
    public int getNewPos() {

        return newPos;
    }
}
