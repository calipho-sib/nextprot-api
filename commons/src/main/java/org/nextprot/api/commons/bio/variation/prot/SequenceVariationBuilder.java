package org.nextprot.api.commons.bio.variation.prot;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.impl.seqchange.PTM;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChange;

/**
 * Fluent interface for building a <code>SequenceVariation</code>
 *
 * Created by fnikitin on 09/07/15.
 */
public interface SequenceVariationBuilder {

    /** build an instance of SequenceVariation */
    SequenceVariation build();

    /** collect data through the process */
    DataCollector getDataCollector();

    interface Start { }

    /** start fluent building */
    interface StartBuilding extends Start {

        /** give a amino-acid sequence to build from */
        StartBuildingFromAAs fromAAs(String aas);

        /** select a single affected amino-acid residue */
        ChangingAminoAcid selectAminoAcid(AminoAcidCode affectedAA, int affectedAAPos);

        /** select a range of affected amino-acid residues */
        ChangingAminoAcidRange selectAminoAcidRange(AminoAcidCode firstAffectedAA, int firstAffectedAAPos, AminoAcidCode lastAffectedAA, int lastAffectedAAPos);
    }

    /** with sequence branch building */
    interface StartBuildingFromAAs extends Start {

        /** select a single affected amino-acid residue */
        ChangingAminoAcid selectAminoAcid(int affectedAAPos);

        /** select a range of affected amino-acid residues */
        ChangingAminoAcidRange selectAminoAcidRange(int firstAffectedAAPos, int lastAffectedAAPos);
    }

    /** mutations affecting only one amino-acid */
    interface ChangingAminoAcid extends ChangingAminoAcidRange {

        /** substitutedBy an amino-acid by another one */
        SequenceVariationBuilder thenSubstituteWith(AminoAcidCode aa);

        /** A frameshift appears just after the affected amino-acid leading to a codon stop in this frame */
        SequenceVariationBuilder thenFrameshift(AminoAcidCode newAminoAcidCode, int newTerminationPosition);

        /** modifies affected amino-acid with modification */
        SequenceVariationBuilder thenAddModification(PTM modification);

        /** change translation initiation (start of stop codon) extending the normal translational reading frame at the
         * N- or C-terminal end with one or more amino acids */
        SequenceVariationBuilder thenInitiationExtension(int newUpstreamInitPos, AminoAcidCode newAminoAcidCode);

        SequenceVariationBuilder thenTerminationExtension(int newDownstreamTermPos, AminoAcidCode newVariantTermAminoAcidCode);
    }

    /** mutations affecting any sequence of amino-acid */
    interface ChangingAminoAcidRange {

        /** delete all affected amino-acids */
        SequenceVariationBuilder thenDelete();

        /** inserts given aas after specific AA */
        SequenceVariationBuilder thenInsert(AminoAcidCode... aas);

        /** duplicates changing aas and insert right after */
        SequenceVariationBuilder thenDuplicate();

        /** delete all affected amino-acids and inserts given aas */
        SequenceVariationBuilder thenDeleteAndInsert(AminoAcidCode... aas);
    }

    /**
     * Collect data to build SequenceVariation
     */
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
