package org.nextprot.api.commons.bio.variation;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcid;

import java.util.Objects;

/**
 * Substitution of an amino-acid by another one
 *
 * Created by fnikitin on 10/07/15.
 */
public class Substitution implements ProteinSequenceChange<AminoAcid> {

    private final AminoAcid aa;

    public Substitution(AminoAcid aa) {

        Preconditions.checkNotNull(aa);

        this.aa = aa;
    }

    /**
     * @return the substituent amino-acid
     */
    @Override
    public AminoAcid getValue() {
        return aa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Substitution)) return false;
        Substitution that = (Substitution) o;
        return aa == that.aa;
    }

    @Override
    public int hashCode() {
        return Objects.hash(aa);
    }
}
