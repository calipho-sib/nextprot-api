package org.nextprot.api.commons.bio.variation.seq;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.seq.impl.AminoAcidModification;

/**
 * Fluent interface for building <code>SequenceVariation</code>s
 *
 * Created by fnikitin on 09/07/15.
 */
public interface SequenceVariationBuilder {

    /** starting creation */
    interface StartBuilding {

        /** select a single affected amino-acid residue */
        SingleAminoAcidMutation aminoAcid(AminoAcidCode affectedAA, int affectedAAPos);

        /** select a range of affected amino-acid residues */
        AminoAcidMutation aminoAcids(AminoAcidCode firstAffectedAA, int firstAffectedAAPos, AminoAcidCode lastAffectedAA, int lastAffectedAAPos);
    }

    /** mutations affecting any sequence of amino-acid */
    interface AminoAcidMutation {

        /** delete all affected amino-acids */
        SequenceVariationBuilder deletes();

        /** inserts given aas after specific AA */
        SequenceVariationBuilder inserts(AminoAcidCode... aas);

        /** duplicates changing aas and insert right after */
        SequenceVariationBuilder duplicates();

        /** delete all affected amino-acids and inserts given aas */
        SequenceVariationBuilder deletedAndInserts(AminoAcidCode... aas);

        /** delete all affected amino-acids and inserts given aas */
        SequenceVariationBuilder modifies(AminoAcidModification change);
    }

    /** mutations affecting only one amino-acid */
    interface SingleAminoAcidMutation extends AminoAcidMutation {

        /** substitutedBy an amino-acid by another one */
        SequenceVariationBuilder substitutedBy(AminoAcidCode aa);

        /** A frameshift appears just after the affected amino-acid leading to a codon stop in this frame */
        SequenceVariationBuilder thenFrameshift(AminoAcidCode newAminoAcidCode, int newTerminationPosition);
    }

    /** build an instance of ProteinMutation */
    SequenceVariation build();

    /** collect data through the process */
    DataCollector getDataCollector();

    class DataCollector {

        private AminoAcidCode firstChangingAminoAcid;
        private int firstChangingAminoAcidPos;
        private AminoAcidCode lastChangingAminoAcid;
        private int lastChangingAminoAcidPos;
        private SequenceChange<?> sequenceChange;

        public void setFirstChangingAminoAcid(AminoAcidCode firstAffectedAminoAcid, int firstAffectedAminoAcidPos) {

            Preconditions.checkNotNull(firstAffectedAminoAcid);
            Preconditions.checkArgument(firstAffectedAminoAcidPos > 0);

            this.firstChangingAminoAcid = firstAffectedAminoAcid;
            this.firstChangingAminoAcidPos = firstAffectedAminoAcidPos;
        }

        public AminoAcidCode getFirstChangingAminoAcid() {
            return firstChangingAminoAcid;
        }

        public int getFirstChangingAminoAcidPos() {
            return firstChangingAminoAcidPos;
        }

        public void setLastChangingAminoAcid(AminoAcidCode lastAffectedAminoAcid, int lastAffectedAminoAcidPos) {

            Preconditions.checkNotNull(firstChangingAminoAcid);
            Preconditions.checkArgument(firstChangingAminoAcidPos > 0);

            this.lastChangingAminoAcid = lastAffectedAminoAcid;
            this.lastChangingAminoAcidPos = lastAffectedAminoAcidPos;
        }

        public AminoAcidCode getLastChangingAminoAcid() {
            return lastChangingAminoAcid;
        }

        public int getLastChangingAminoAcidPos() {
            return lastChangingAminoAcidPos;
        }

        public SequenceChange<?> getSequenceChange() {
            return sequenceChange;
        }

        public void setSequenceChange(SequenceChange<?> sequenceChange) {
            this.sequenceChange = sequenceChange;
        }
    }
}
