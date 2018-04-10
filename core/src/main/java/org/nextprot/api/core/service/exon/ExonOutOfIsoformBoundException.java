package org.nextprot.api.core.service.exon;

import org.nextprot.api.core.domain.AminoAcid;
import org.nextprot.api.core.domain.exon.SimpleExon;

/**
 * An exon out of bound error
 *
 * Created by fnikitin on 28/07/15.
 */
public class ExonOutOfIsoformBoundException extends InvalidExonException {

    public enum AminoAcidOutOfBound {FIRST, LAST}

    private final AminoAcid first;
    private final AminoAcid last;
    private final AminoAcidOutOfBound aminoAcidOutOfBound;
    private final int isoformLength;

    public ExonOutOfIsoformBoundException(SimpleExon exon, AminoAcid first, AminoAcid last, AminoAcidOutOfBound aminoAcidOutOfBound, int isoformLength) {

        super(exon, "outofbound aa="+((aminoAcidOutOfBound == AminoAcidOutOfBound.FIRST) ? first : last) +", isoform length="+isoformLength);

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

    public AminoAcidOutOfBound getAminoAcidOutOfBound() {
        return aminoAcidOutOfBound;
    }
}
