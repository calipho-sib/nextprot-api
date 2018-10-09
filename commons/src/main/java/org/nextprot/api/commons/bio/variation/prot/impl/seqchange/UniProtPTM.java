package org.nextprot.api.commons.bio.variation.prot.impl.seqchange;

import com.google.common.base.Preconditions;
import org.nextprot.api.commons.bio.AminoAcidCode;
import org.nextprot.api.commons.bio.variation.prot.seqchange.SequenceChange;

import java.util.Objects;

/**
 * A PTM defined with a descriptor defined in UniProt control vocabulary
 */
public class UniProtPTM implements SequenceChange<String> {

    private final String ptmId;

    public UniProtPTM(String ptmId) {

        Preconditions.checkNotNull(ptmId);
        Preconditions.checkArgument(ptmId.matches("PTM-\\d{4}"));

        this.ptmId = ptmId;
    }

    public static UniProtPTM N_ACETYLATION(AminoAcidCode aminoAcidCode) {

        switch (aminoAcidCode) {
            case ARGININE:
                return new UniProtPTM("PTM-0180");
            case LYSINE:
                return new UniProtPTM("PTM-0190");
            case ALANINE:
                return new UniProtPTM("PTM-0199");
            case ASPARTIC_ACID:
                return new UniProtPTM("PTM-0200");
            case CYSTEINE:
                return new UniProtPTM("PTM-0201");
            case GLUTAMIC_ACID:
                return new UniProtPTM("PTM-0202");
            case GLYCINE:
                return new UniProtPTM("PTM-0203");
            case ISOLEUCINE:
                return new UniProtPTM("PTM-0204");
            case METHIONINE:
                return new UniProtPTM("PTM-0205");
            case PROLINE:
                return new UniProtPTM("PTM-0206");
            case SERINE:
                return new UniProtPTM("PTM-0207");
            case THREONINE:
                return new UniProtPTM("PTM-0208");
            case TYROSINE:
                return new UniProtPTM("PTM-0209");
            case VALINE:
                return new UniProtPTM("PTM-0210");
            default:
                throw new IllegalArgumentException(aminoAcidCode + " cannot be N-acetylated");
        }
    }

    public static UniProtPTM O_ACETYLATION(AminoAcidCode aminoAcidCode) {

        switch (aminoAcidCode) {
            case SERINE:
                return new UniProtPTM("PTM-0232");
            case THREONINE:
                return new UniProtPTM("PTM-0233");
            default:
                throw new IllegalArgumentException(aminoAcidCode + " cannot be O-acetylated");
        }
    }

    public static UniProtPTM N_GLYCATION(AminoAcidCode aminoAcidCode) {
        switch (aminoAcidCode) {
            case ARGININE:
                return new UniProtPTM("PTM-0515");
            case ASPARAGINE:
                return new UniProtPTM("PTM-0517");
            case HISTIDINE:
                return new UniProtPTM("PTM-0507");
            case ISOLEUCINE:
                return new UniProtPTM("PTM-0508");
            case LYSINE:
                return new UniProtPTM("PTM-0509");
            case VALINE:
                return new UniProtPTM("PTM-0510");
            default:
                throw new IllegalArgumentException(aminoAcidCode + " cannot be N-glycated");
        }
    }

    public static UniProtPTM PHOSPHORYLATION(AminoAcidCode aminoAcidCode) {
        switch (aminoAcidCode) {
            case ARGININE:
                return new UniProtPTM("PTM-0250");
            case CYSTEINE:
                return new UniProtPTM("PTM-0251");
            case HISTIDINE:
                return new UniProtPTM("PTM-0252");
            case SERINE:
                return new UniProtPTM("PTM-0253");
            case THREONINE:
                return new UniProtPTM("PTM-0254");
            case TYROSINE:
                return new UniProtPTM("PTM-0255");
            default:
                throw new IllegalArgumentException(aminoAcidCode + " cannot be phosphorylated");
        }
    }

    public static UniProtPTM S_NITROSYLATION() {

        return new UniProtPTM("PTM-0280");
    }

    // TODO depending on the use cases
    /*
    public static UniProtPTM DIMETHYLATION(AminoAcidCode aminoAcidCode) {
        return new UniProtPTM("");
    }

    public static UniProtPTM GERANYLGERANYLATION(AminoAcidCode aminoAcidCode) {
        return new UniProtPTM("");
    }

    public static UniProtPTM FARNESYLATION(AminoAcidCode aminoAcidCode) {
        return new UniProtPTM("");
    }

    public static UniProtPTM MYRISTOYLATION(AminoAcidCode aminoAcidCode) {
        return new UniProtPTM("");
    }

    public static UniProtPTM NITRATION(AminoAcidCode aminoAcidCode) {
        return new UniProtPTM("");
    }

    public static UniProtPTM PALMITOYLATION(AminoAcidCode aminoAcidCode) {
        return new UniProtPTM("");
    }

    public static UniProtPTM POLY_ADP_RIBOSYLATION(AminoAcidCode aminoAcidCode) {
        return new UniProtPTM("");
    }

    public static PTM SUMOYLATION() {
        return new PTM("");
    }

    public static PTM UBIQUITINATION() {
        return new PTM("");
    }*/

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UniProtPTM ptm = (UniProtPTM) o;
        return Objects.equals(ptmId, ptm.ptmId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ptmId);
    }
}
