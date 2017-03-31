package org.nextprot.api.commons.bio.variation.impl;


import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.SequenceChange;

import java.util.Arrays;

/**
 * A copy of one or more amino acids are inserted directly 3' of the original copy of that sequence.
 * Duplication may only be used when the additional copy is directly 3’-flanking the original copy (a “tandem duplication”).
 *
 * Created by fnikitin on 10/07/15.
 */
public class Duplication implements SequenceChange<AminoAcidCode[]> {

    private final AminoAcidCode[] aas;
    private final int insertAfterPos;

    Duplication(int insertAfterPos, AminoAcidCode[] aas) {

        this.insertAfterPos = insertAfterPos;
        this.aas = aas;
    }

    /**
     * @return a copy of aas array
     */
    @Override
    public AminoAcidCode[] getValue() {

        return Arrays.copyOf(aas, aas.length);
    }

    @Override
    public Type getType() {
        return Type.DUPLICATION;
    }

    @Override
    public Operator getOperator() {

        return new Operator() {
            @Override
            public PositionType getChangingPositionType() {
                return PositionType.LAST_LAST;
            }

            @Override
            public String getVariatingPart() {
                return AminoAcidCode.formatAminoAcidCode(AminoAcidCode.CodeType.ONE_LETTER, aas);
            }
        };
    }

    public int getInsertAfterPos() {
        return insertAfterPos;
    }
}
