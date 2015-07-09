package org.nextprot.api.commons.bio.mutation;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcidCode;

/**
 * Substitution of an amino-acid by another one
 *
 * Created by fnikitin on 10/07/15.
 */
public class Substitution implements Mutation<AminoAcidCode> {

    private final AminoAcidCode aa;

    public Substitution(AminoAcidCode aa) {

        Preconditions.checkNotNull(aa);

        this.aa = aa;
    }

    /**
     * @return the substituent amino-acid
     */
    @Override
    public AminoAcidCode getValue() {
        return aa;
    }
}
