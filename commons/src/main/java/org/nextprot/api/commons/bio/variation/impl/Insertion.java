package org.nextprot.api.commons.bio.variation.impl;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.SequenceChange;

import java.util.Arrays;

/**
 * Insertions add one or more aas after one existing aa
 *
 * Created by fnikitin on 10/07/15.
 */
public class Insertion implements SequenceChange<AminoAcidCode[]> {

    private final int insertAfterPos;
    private final AminoAcidCode[] insertedAminoAcids;

    Insertion(int insertAfterPos, AminoAcidCode... insertedAminoAcids) {

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

    @Override
    public Operator getOperator() {

        return new Operator() {
            @Override
            public PositionType getChangingPositionType() {
                return PositionType.FIRST_FIRST;
            }

            @Override
            public String getVariatingPart() {
                return AminoAcidCode.formatAminoAcidCode(AminoAcidCode.CodeType.ONE_LETTER, getValue());
            }
        };
    }

    public int getInsertAfterPos() {
        return insertAfterPos;
    }
}
