package org.nextprot.api.core.utils.exon;

import org.nextprot.api.core.domain.AminoAcid;

/**
 * An exon out of bound error
 *
 * Created by fnikitin on 28/07/15.
 */
public class ExonOutOfBoundError {

    public enum AminoAcidOutOfBound {FIRST, LAST};

    private final AminoAcid first;
    private final AminoAcid last;
    private final AminoAcidOutOfBound aminoAcidOutOfBound;
    private final int isoformLength;

    public ExonOutOfBoundError(AminoAcid first, AminoAcid last, AminoAcidOutOfBound aminoAcidOutOfBound, int isoformLength) {
        this.first = first;
        this.last = last;
        this.aminoAcidOutOfBound = aminoAcidOutOfBound;
        this.isoformLength = isoformLength;
    }

    public int getIsoformLength() {
        return isoformLength;
    }

    public AminoAcid getFirst() {
        return first;
    }

    public AminoAcid getLast() {
        return last;
    }

    public AminoAcid getOutOfBoundAminoAcid() {
        return (aminoAcidOutOfBound == AminoAcidOutOfBound.FIRST) ? first : last;
    }

    public AminoAcid getInBoundAminoAcid() {
        return (aminoAcidOutOfBound == AminoAcidOutOfBound.FIRST) ? last : first;
    }

    public AminoAcidOutOfBound getAminoAcidOutOfBound() {
        return aminoAcidOutOfBound;
    }
}
