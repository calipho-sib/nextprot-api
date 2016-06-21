package org.nextprot.api.commons.bio.variation;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcidCode;

import java.util.Arrays;
import java.util.Objects;

/**
 * Insertions add one or more aas after one existing aa
 *
 * Created by fnikitin on 10/07/15.
 */
public class Insertion implements ProteinSequenceChange<AminoAcidCode[]> {

    private final int insertAfterPos;
    private final AminoAcidCode[] aas;

    public Insertion(int insertAfterPos, AminoAcidCode... aas) {

        Preconditions.checkNotNull(aas);
        Preconditions.checkArgument(aas.length > 0);
        Preconditions.checkArgument(insertAfterPos>=0);

        this.aas = aas;
        this.insertAfterPos = insertAfterPos;
    }

    /**
     * @return a copy of aas array
     */
    @Override
    public AminoAcidCode[] getValue() {

        return Arrays.copyOf(aas, aas.length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Insertion)) return false;
        Insertion insertion = (Insertion) o;
        return Objects.equals(insertAfterPos, insertion.insertAfterPos) &&
                Objects.equals(aas, insertion.aas);
    }

    @Override
    public int hashCode() {
        return Objects.hash(insertAfterPos, aas);
    }

    public int getInsertAfterPos() {
        return insertAfterPos;
    }
}
