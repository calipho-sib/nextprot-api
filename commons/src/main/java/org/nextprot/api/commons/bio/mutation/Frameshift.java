package org.nextprot.api.commons.bio.mutation;

import com.google.common.base.Preconditions;

/**
 * A frameshift affects a protein sequence after amino-acid leading to a truncated protein.
 *
 * Created by fnikitin on 10/07/15.
 */
public class Frameshift implements Mutation<Integer> {

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
}
