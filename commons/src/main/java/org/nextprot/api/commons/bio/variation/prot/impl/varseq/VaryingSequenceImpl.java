package org.nextprot.api.commons.bio.variation.prot.impl.varseq;

import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.varseq.VaryingSequence;

import java.util.Objects;

/**
 * This object describes protein sequence variations of many types (substitution, deletion, delins, frameshift, ...)
 * Its instanciation is based on a fluent interface.
 *
 * See specifications at http://varnomen.hgvs.org/recommendations/protein/
 *
 * Created by fnikitin on 09/07/15.
 */
public class VaryingSequenceImpl implements VaryingSequence {

    private final AminoAcidCode firstChangingAminoAcid;
    private final int firstChangingAminoAcidPos;
    private final AminoAcidCode lastChangingAminoAcid;
    private final int lastChangingAminoAcidPos;

    public VaryingSequenceImpl(AminoAcidCode firstChangingAminoAcid, int firstChangingAminoAcidPos, AminoAcidCode lastChangingAminoAcid, int lastChangingAminoAcidPos) {

        this.firstChangingAminoAcid = firstChangingAminoAcid;
        this.firstChangingAminoAcidPos = firstChangingAminoAcidPos;
        this.lastChangingAminoAcid = lastChangingAminoAcid;
        this.lastChangingAminoAcidPos = lastChangingAminoAcidPos;
    }

    public AminoAcidCode getFirstAminoAcid() {
        return firstChangingAminoAcid;
    }

    public int getFirstAminoAcidPos() {
        return firstChangingAminoAcidPos;
    }

    public AminoAcidCode getLastAminoAcid() {
        return lastChangingAminoAcid;
    }

    public int getLastAminoAcidPos() {
        return lastChangingAminoAcidPos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VaryingSequenceImpl)) return false;
        VaryingSequenceImpl that = (VaryingSequenceImpl) o;
        return firstChangingAminoAcidPos == that.firstChangingAminoAcidPos &&
                lastChangingAminoAcidPos == that.lastChangingAminoAcidPos &&
                firstChangingAminoAcid == that.firstChangingAminoAcid &&
                lastChangingAminoAcid == that.lastChangingAminoAcid;
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstChangingAminoAcid, firstChangingAminoAcidPos, lastChangingAminoAcid, lastChangingAminoAcidPos);
    }

    @Override
    public String toString() {
        return "VaryingSequenceImpl{" +
                "firstChangingAminoAcid=" + firstChangingAminoAcid +
                ", firstChangingAminoAcidPos=" + firstChangingAminoAcidPos +
                ", lastChangingAminoAcid=" + lastChangingAminoAcid +
                ", lastChangingAminoAcidPos=" + lastChangingAminoAcidPos +
                '}';
    }
}
