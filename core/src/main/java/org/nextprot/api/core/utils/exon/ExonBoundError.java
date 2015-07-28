package org.nextprot.api.core.utils.exon;

import org.nextprot.api.core.domain.AminoAcid;
import org.nextprot.api.core.domain.Exon;

/**
 * An exon with a bound error
 *
 * Created by fnikitin on 28/07/15.
 */
public class ExonBoundError {

    public enum AminoAcidOutOfBound {FIRST, LAST};

    private final Exon exon;
    private final AminoAcid first;
    private final AminoAcid last;
    private final AminoAcidOutOfBound aminoAcidOutOfBound;
    private final int isoformLength;

    public ExonBoundError(Exon exon, AminoAcid first, AminoAcid last, AminoAcidOutOfBound aminoAcidOutOfBound, int isoformLength) {
        this.exon = exon;
        this.first = first;
        this.last = last;
        this.aminoAcidOutOfBound = aminoAcidOutOfBound;
        this.isoformLength = isoformLength;
    }

    public Exon getExon() {
        return exon;
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
