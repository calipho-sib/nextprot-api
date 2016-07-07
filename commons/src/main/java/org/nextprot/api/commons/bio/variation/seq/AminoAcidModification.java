package org.nextprot.api.commons.bio.variation.seq;


public class AminoAcidModification implements SequenceChange<AminoAcidChange> {

    private final AminoAcidChange change;

    public AminoAcidModification(AminoAcidChange change) {
        this.change = change;
    }

    @Override
    public AminoAcidChange getValue() {
        return change;
    }

    @Override
    public Type getType() {
        return Type.PTM;
    }
}
