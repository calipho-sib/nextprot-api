package org.nextprot.api.commons.bio.variation.prot.impl.seqchange;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChange;

import java.util.Arrays;

/**
 * Insertions add one or more aas after one existing aa
 *
 * Created by fnikitin on 10/07/15.
 */
public class Insertion implements SequenceChange<AminoAcidCode[]> {

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
    public Type getType() {
        return Type.INSERTION;
    }

    public int getInsertAfterPos() {
        return insertAfterPos;
    }
}
