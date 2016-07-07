package org.nextprot.api.commons.bio.variation.seq;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcidCode;

import java.util.Arrays;

/**
 * Deletion/insertions (delins) replace one or more aas with one or more other aas
 *
 * Created by fnikitin on 10/07/15.
 */
public class DeletionAndInsertion implements SequenceChange<AminoAcidCode[]> {

    private final AminoAcidCode[] aas;

    public DeletionAndInsertion(AminoAcidCode... aas) {

        Preconditions.checkNotNull(aas);
        Preconditions.checkArgument(aas.length>0);

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
        return Type.DELETION_INSERTION;
    }
}
