package org.nextprot.api.commons.bio.variation;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcid;

/**
 * Fluent interface for building <code>ProteinSequenceVariation</code>s
 *
 * Created by fnikitin on 09/07/15.
 */
public interface ProteinSequenceVariationBuilder {

    /** starting creation */
    interface StartBuilding {

        /** select a single affected amino-acid residue */
        SingleAminoAcidMutation aminoAcid(AminoAcid affectedAA, int affectedAAPos);

        /** select a range of affected amino-acid residues */
        AminoAcidMutation aminoAcids(AminoAcid firstAffectedAA, int firstAffectedAAPos, AminoAcid lastAffectedAA, int lastAffectedAAPos);
    }

    /** mutations affecting any sequence of amino-acid */
    interface AminoAcidMutation {

        /** delete all affected amino-acids */
        ProteinSequenceVariationBuilder deleted();

        /** inserts given aas after specific AA */
        ProteinSequenceVariationBuilder inserts(AminoAcid... aas);

        /** delete all affected amino-acids and inserts given aas */
        ProteinSequenceVariationBuilder deletedAndInserts(AminoAcid... aas);
    }

    /** mutations affecting only one amino-acid */
    interface SingleAminoAcidMutation extends AminoAcidMutation {

        /** substitutedBy an amino-acid by another one */
        ProteinSequenceVariationBuilder substitutedBy(AminoAcid aa);

        /** A frameshift appears just after the affected amino-acid leading to a codon stop in this frame */
        ProteinSequenceVariationBuilder thenFrameshift(int stopCodonPos);
    }

    /** build an instance of ProteinMutation */
    ProteinSequenceVariation build();

    /** collect data through the process */
    DataCollector getDataCollector();

    class DataCollector {

        private AminoAcid firstChangingAminoAcid;
        private int firstChangingAminoAcidPos;
        private AminoAcid lastChangingAminoAcid;
        private int lastChangingAminoAcidPos;
        private ProteinSequenceChange proteinSequenceChange;

        public void setFirstChangingAminoAcid(AminoAcid firstAffectedAminoAcid, int firstAffectedAminoAcidPos) {

            Preconditions.checkNotNull(firstAffectedAminoAcid);
            Preconditions.checkArgument(firstAffectedAminoAcidPos > 0);

            this.firstChangingAminoAcid = firstAffectedAminoAcid;
            this.firstChangingAminoAcidPos = firstAffectedAminoAcidPos;
        }

        public AminoAcid getFirstChangingAminoAcid() {
            return firstChangingAminoAcid;
        }

        public int getFirstChangingAminoAcidPos() {
            return firstChangingAminoAcidPos;
        }

        public void setLastChangingAminoAcid(AminoAcid lastAffectedAminoAcid, int lastAffectedAminoAcidPos) {

            Preconditions.checkNotNull(firstChangingAminoAcid);
            Preconditions.checkArgument(firstChangingAminoAcidPos > 0);

            this.lastChangingAminoAcid = lastAffectedAminoAcid;
            this.lastChangingAminoAcidPos = lastAffectedAminoAcidPos;
        }

        public AminoAcid getLastChangingAminoAcid() {
            return lastChangingAminoAcid;
        }

        public int getLastChangingAminoAcidPos() {
            return lastChangingAminoAcidPos;
        }

        public ProteinSequenceChange getProteinSequenceChange() {
            return proteinSequenceChange;
        }

        public void setProteinSequenceChange(ProteinSequenceChange proteinSequenceChange) {
            this.proteinSequenceChange = proteinSequenceChange;
        }
    }
}
