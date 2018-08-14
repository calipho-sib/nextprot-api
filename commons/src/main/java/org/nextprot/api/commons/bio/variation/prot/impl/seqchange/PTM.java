package org.nextprot.api.commons.bio.variation.prot.impl.seqchange;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChange;

/**
 * A PTM defined with a descriptor defined in UniProt control vocabulary
 */
public class PTM implements SequenceChange<String> {

    private final String ptmId;

    public PTM(String ptmId) {

        Preconditions.checkNotNull(ptmId);
        Preconditions.checkArgument(ptmId.matches("PTM-\\d{4}"));
        this.ptmId = ptmId;
    }

    public static PTM N_ACETYLATION(AminoAcidCode aminoAcidCode) {

        switch (aminoAcidCode) {
            case ARGININE:
                return new PTM("PTM-0180");
            case LYSINE:
                return new PTM("PTM-0190");
            case ALANINE:
                return new PTM("PTM-0199");
            case ASPARTIC_ACID:
                return new PTM("PTM-0200");
            case CYSTEINE:
                return new PTM("PTM-0201");
            case GLUTAMIC_ACID:
                return new PTM("PTM-0202");
            case GLYCINE:
                return new PTM("PTM-0203");
            case ISOLEUCINE:
                return new PTM("PTM-0204");
            case METHIONINE:
                return new PTM("PTM-0205");
            case PROLINE:
                return new PTM("PTM-0206");
            case SERINE:
                return new PTM("PTM-0207");
            case THREONINE:
                return new PTM("PTM-0208");
            case TYROSINE:
                return new PTM("PTM-0209");
            case VALINE:
                return new PTM("PTM-0210");
            default:
                throw new IllegalArgumentException(aminoAcidCode + " cannot be N-acetylated");
        }
    }

    public static PTM O_ACETYLATION(AminoAcidCode aminoAcidCode) {

        switch (aminoAcidCode) {
            case SERINE:
                return new PTM("PTM-0232");
            case THREONINE:
                return new PTM("PTM-0233");
            default:
                throw new IllegalArgumentException(aminoAcidCode + " cannot be O-acetylated");
        }
    }

    // TODO
    public static PTM DIMETHYLATION() {
        return new PTM("");
    }

    // TODO
    public static PTM GERANYLGERANYLATION() {
        return new PTM("");
    }

    // TODO
    public static PTM FARNESYLATION() {
        return new PTM("");
    }

    // TODO
    public static PTM MYRISTOYLATION() {
        return new PTM("");
    }

    // TODO
    public static PTM NITRATION() {
        return new PTM("");
    }

    // TODO
    public static PTM PHOSPHORYLATION() {
        return new PTM("");
    }

    // TODO
    public static PTM PALMITOYLATION() {
        return new PTM("");
    }

    // TODO
    public static PTM POLY_ADP_RIBOSYLATION() {
        return new PTM("");
    }

    public static PTM S_NITROSYLATION() {
        return new PTM("PTM-0280");
    }

    // TODO
    public static PTM SUMOYLATION() {
        return new PTM("");
    }

    // TODO
    public static PTM UBIQUITINATION() {
        return new PTM("");
    }

    /**
     * @return A PTM id describing the modification
     */
    @Override
    public String getValue() {
        return ptmId;
    }

    @Override
    public Type getType() {

        return Type.PTM;
    }
}
