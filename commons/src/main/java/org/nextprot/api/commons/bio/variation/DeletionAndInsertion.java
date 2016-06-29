package org.nextprot.api.commons.bio.variation;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcid;

import java.util.Arrays;

/**
 * Deletion/insertions (delins) replace one or more aas with one or more other aas
 *
 * Created by fnikitin on 10/07/15.
 */
public class DeletionAndInsertion implements ProteinSequenceChange<AminoAcid[]> {

    private final AminoAcid[] aas;

    public DeletionAndInsertion(AminoAcid... aas) {

        Preconditions.checkNotNull(aas);
        Preconditions.checkArgument(aas.length>0);

        this.aas = aas;
    }

    /**
     * @return a copy of aas array
     */
    @Override
    public AminoAcid[] getValue() {

        return Arrays.copyOf(aas, aas.length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DeletionAndInsertion)) return false;
        DeletionAndInsertion that = (DeletionAndInsertion) o;
        return Arrays.equals(aas, that.aas);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(aas);
    }
}
