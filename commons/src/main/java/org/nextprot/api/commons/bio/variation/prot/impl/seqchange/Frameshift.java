package org.nextprot.api.commons.bio.variation.prot.impl.seqchange;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChange;

import java.util.Objects;

/**
 * A frameshift affects a protein sequence after amino-acid leading to a truncated protein.
 *
 * Created by fnikitin on 10/07/15.
 */
public class Frameshift implements SequenceChange<Frameshift.Change> {

    private final Change change;

    public Frameshift(Change change) {

        this.change = change;
    }

    @Override
    public Change getValue() {
        return change;
    }

    @Override
    public Type getType() {
        return Type.FRAMESHIFT;
    }

    public static class Change {

        private final AminoAcidCode changedAminoAcid;
        private final int newTerminationPosition;

        public Change(AminoAcidCode changedAminoAcid, int newTerminationPosition) {

            Preconditions.checkArgument(newTerminationPosition>1);
            this.changedAminoAcid = changedAminoAcid;
            this.newTerminationPosition = newTerminationPosition;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Change)) return false;
            Change change = (Change) o;
            return newTerminationPosition == change.newTerminationPosition &&
                    changedAminoAcid == change.changedAminoAcid;
        }

        @Override
        public int hashCode() {
            return Objects.hash(changedAminoAcid, newTerminationPosition);
        }

        public AminoAcidCode getChangedAminoAcid() {
            return changedAminoAcid;

        }

        public int getNewTerminationPosition() {
            return newTerminationPosition;
        }
    }
}
