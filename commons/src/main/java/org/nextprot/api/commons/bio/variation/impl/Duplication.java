package org.nextprot.api.commons.bio.variation.impl;


import org.nextprot.api.commons.bio.variation.SequenceChange;

/**
 * A copy of one or more amino acids are inserted directly 3' of the original copy of that sequence.
 * Duplication may only be used when the additional copy is directly 3’-flanking the original copy (a “tandem duplication”).
 *
 * Created by fnikitin on 10/07/15.
 */
public class Duplication implements SequenceChange<Integer> {

    private final int insertAfterPos;

    Duplication(int insertAfterPos) {

        this.insertAfterPos = insertAfterPos;
    }

    @Override
    public Integer getValue() {

        return insertAfterPos;
    }

    @Override
    public Type getType() {
        return Type.DUPLICATION;
    }

    public int getInsertAfterPos() {
        return insertAfterPos;
    }
}
