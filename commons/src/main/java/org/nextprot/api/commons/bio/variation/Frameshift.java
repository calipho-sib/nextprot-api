package org.nextprot.api.commons.bio.variation;

import com.google.common.base.Preconditions;

import java.util.Objects;

/**
 * A frameshift affects a protein sequence after amino-acid leading to a truncated protein.
 *
 * Created by fnikitin on 10/07/15.
 */
public class Frameshift implements ProteinSequenceChange<Integer> {

    private final int stopCodonPos;

    public Frameshift(int stopCodonPos) {

        Preconditions.checkArgument(stopCodonPos>0);

        this.stopCodonPos = stopCodonPos;
    }

    /**
     * @return the position of the codon stop in the new reading frame
     */
    @Override
    public Integer getValue() {
        return stopCodonPos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Frameshift)) return false;
        Frameshift that = (Frameshift) o;
        return stopCodonPos == that.stopCodonPos;
    }

    @Override
    public int hashCode() {
        return Objects.hash(stopCodonPos);
    }
}
