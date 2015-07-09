package org.nextprot.api.commons.bio.mutation;

import org.nextprot.api.commons.bio.AminoAcidCode;

/**
 * Fluent interface for building <code>ProteinMutation</code>s
 *
 * Created by fnikitin on 09/07/15.
 */
public interface ProteinMutationBuilder {

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
        ProteinMutationBuilder deleted();

        /** delete all affected amino-acids and inserts given aas */
        ProteinMutationBuilder deletedAndInserts(AminoAcidCode... aas);
    }

    /** mutations affecting only one amino-acid */
    interface SingleAminoAcidMutation extends AminoAcidMutation {

        /** substitutedBy an amino-acid by another one */
        ProteinMutationBuilder substitutedBy(AminoAcidCode aa);

        /** A frameshift appears just after the affected amino-acid leading to a codon stop in this frame */
        ProteinMutationBuilder thenFrameshift(int stopCodonPos);
    }

    /** build an instance of ProteinMutation */
    ProteinMutation build();

    /** collect data through the process */
    DataCollector getDataCollector();
}
