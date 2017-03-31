package org.nextprot.api.commons.bio.variation.impl;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.SequenceChange;

/**
 * Substitution of an amino-acid by another one
 *
 * Created by fnikitin on 10/07/15.
 */
public class Substitution implements SequenceChange<AminoAcidCode> {

    private final AminoAcidCode substitutedAminoAcid;

    Substitution(AminoAcidCode substitutedAminoAcid) {

        Preconditions.checkNotNull(substitutedAminoAcid);

        this.substitutedAminoAcid = substitutedAminoAcid;
    }

    /**
     * @return the substituent amino-acid
     */
    @Override
    public AminoAcidCode getValue() {
        return substitutedAminoAcid;
    }

    @Override
    public Type getType() {

        return Type.SUBSTITUTION;
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
}
