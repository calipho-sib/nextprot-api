package org.nextprot.api.commons.bio.variation.seq;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcidCode;

/**
 * Fluent interface for building <code>ProteinSequenceVariation</code>s
 *
 * Created by fnikitin on 09/07/15.
 */
public interface ProteinSequenceVariationBuilder {

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
        ProteinSequenceVariationBuilder deletes();

        /** inserts given aas after specific AA */
        ProteinSequenceVariationBuilder inserts(AminoAcidCode... aas);

        /** duplicates changing aas and insert right after */
        ProteinSequenceVariationBuilder duplicates();

        /** delete all affected amino-acids and inserts given aas */
        ProteinSequenceVariationBuilder deletedAndInserts(AminoAcidCode... aas);
    }

    /** mutations affecting only one amino-acid */
    interface SingleAminoAcidMutation extends AminoAcidMutation {

        /** substitutedBy an amino-acid by another one */
        ProteinSequenceVariationBuilder substitutedBy(AminoAcidCode aa);

        /** A frameshift appears just after the affected amino-acid leading to a codon stop in this frame */
        ProteinSequenceVariationBuilder thenFrameshift(AminoAcidCode newAminoAcidCode, int newTerminationPosition);
    }

    /** build an instance of ProteinMutation */
    ProteinSequenceVariation build();

    /** collect data through the process */
    DataCollector getDataCollector();

    class DataCollector {

        private AminoAcidCode firstChangingAminoAcid;
        private int firstChangingAminoAcidPos;
        private AminoAcidCode lastChangingAminoAcid;
        private int lastChangingAminoAcidPos;
        private ProteinSequenceChange<?> proteinSequenceChange;

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

        public ProteinSequenceChange<?> getProteinSequenceChange() {
            return proteinSequenceChange;
        }

        public void setProteinSequenceChange(ProteinSequenceChange<?> proteinSequenceChange) {
            this.proteinSequenceChange = proteinSequenceChange;
        }
    }
}
