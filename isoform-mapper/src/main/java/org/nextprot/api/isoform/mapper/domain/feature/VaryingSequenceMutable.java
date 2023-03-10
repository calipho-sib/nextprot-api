package org.nextprot.api.isoform.mapper.domain.feature;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.varseq.VaryingSequence;

/**
 * A simple mutable implementation of VaryingSequence
 *
 * Created by fnikitin on 04.04.17.
 */
public class VaryingSequenceMutable implements VaryingSequence {

    private AminoAcidCode first, last;
    private int firstPos, lastPos;

    public static VaryingSequenceMutable valueOf(VaryingSequence varyingSequence) {

        VaryingSequenceMutable vs = new VaryingSequenceMutable();

        vs.first = varyingSequence.getFirstAminoAcid();
        vs.firstPos = varyingSequence.getFirstAminoAcidPos();
        vs.last = varyingSequence.getLastAminoAcid();
        vs.lastPos = varyingSequence.getLastAminoAcidPos();

        return vs;
    }

    public void setFirst(AminoAcidCode first) {
        this.first = first;
    }

    public void setLast(AminoAcidCode last) {
        this.last = last;
    }

    public void setFirstPos(int firstPos) {
        this.firstPos = firstPos;
    }

    public void setLastPos(int lastPos) {
        this.lastPos = lastPos;
    }

    @Override
    public AminoAcidCode getFirstAminoAcid() {
        return first;
    }

    @Override
    public int getFirstAminoAcidPos() {
        return firstPos;
    }

    @Override
    public AminoAcidCode getLastAminoAcid() {
        return last;
    }

    @Override
    public int getLastAminoAcidPos() {
        return lastPos;
    }
}
