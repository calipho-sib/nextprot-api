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
    private final AminoAcidCode[] insertedAminoAcids;

    public Insertion(int insertAfterPos, AminoAcidCode... insertedAminoAcids) {

        Preconditions.checkNotNull(insertedAminoAcids);
        Preconditions.checkArgument(insertedAminoAcids.length > 0);
        Preconditions.checkArgument(insertAfterPos>=0);

        this.insertedAminoAcids = insertedAminoAcids;
        this.insertAfterPos = insertAfterPos;
    }

    /**
     * @return a copy of aas array
     */
    @Override
    public AminoAcidCode[] getValue() {

        return Arrays.copyOf(insertedAminoAcids, insertedAminoAcids.length);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Insertion)) return false;
        Insertion insertion = (Insertion) o;
        return Objects.equals(insertAfterPos, insertion.insertAfterPos) &&
                Objects.equals(insertedAminoAcids, insertion.insertedAminoAcids);
    }

    @Override
    public int hashCode() {
        return Objects.hash(insertAfterPos, insertedAminoAcids);
    }

    public int getInsertAfterPos() {
        return insertAfterPos;
    }
}
