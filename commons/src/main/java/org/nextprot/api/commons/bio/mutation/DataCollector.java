package org.nextprot.api.commons.bio.mutation;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcidCode;

/**
 * Created by fnikitin on 09/07/15.
 */
class DataCollector {

    private AminoAcidCode firstAffectedAminoAcidCode;
    private int firstAffectedAminoAcidPos;
    private AminoAcidCode lastAffectedAminoAcidCode;
    private int lastAffectedAminoAcidPos;
    private Mutation mutation;

    public void setFirstAffectedAminoAcid(AminoAcidCode firstAffectedAminoAcidCode, int firstAffectedAminoAcidPos) {

        Preconditions.checkNotNull(firstAffectedAminoAcidCode);
        Preconditions.checkArgument(firstAffectedAminoAcidPos > 0);

        this.firstAffectedAminoAcidCode = firstAffectedAminoAcidCode;
        this.firstAffectedAminoAcidPos = firstAffectedAminoAcidPos;
    }

    public AminoAcidCode getFirstAffectedAminoAcidCode() {
        return firstAffectedAminoAcidCode;
    }

    public int getFirstAffectedAminoAcidPos() {
        return firstAffectedAminoAcidPos;
    }

    public void setLastAffectedAminoAcid(AminoAcidCode lastAffectedAminoAcidCode, int lastAffectedAminoAcidPos) {

        Preconditions.checkNotNull(firstAffectedAminoAcidCode);
        Preconditions.checkArgument(firstAffectedAminoAcidPos > 0);

        this.lastAffectedAminoAcidCode = lastAffectedAminoAcidCode;
        this.lastAffectedAminoAcidPos = lastAffectedAminoAcidPos;
    }

    public AminoAcidCode getLastAffectedAminoAcidCode() {
        return lastAffectedAminoAcidCode;
    }

    public int getLastAffectedAminoAcidPos() {
        return lastAffectedAminoAcidPos;
    }

    public Mutation getMutation() {
        return mutation;
    }

    public void setMutation(Mutation mutation) {
        this.mutation = mutation;
    }
}
