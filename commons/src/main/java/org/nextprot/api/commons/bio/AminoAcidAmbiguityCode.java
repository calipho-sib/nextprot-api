package org.nextprot.api.commons.bio;

import java.util.EnumSet;

/**
 * Ambigous amino-acids with their representation in one letter and three letter codes.
 *
 * http://wiki.thegpm.org/wiki/Amino_acid_symbols
 */
public enum AminoAcidAmbiguityCode {

    ASX("Asx", "B", EnumSet.of(AminoAcidCode.ASPARTIC_ACID, AminoAcidCode.ASPARAGINE)),
    XLE("Xle", "J", EnumSet.of(AminoAcidCode.LEUCINE, AminoAcidCode.ISOLEUCINE)),
    GLX("Glx", "Z", EnumSet.of(AminoAcidCode.GLUTAMIC_ACID, AminoAcidCode.GLUTAMINE)),
    XAA("Xaa", "X", EnumSet.allOf(AminoAcidCode.class));

    public enum AACodeType {ONE_LETTER, THREE_LETTER}

    private final String code3;
    private final String code1;
    private final EnumSet<AminoAcidCode> matchingAminoAcidCode;

    AminoAcidAmbiguityCode(String code3, String code1, EnumSet<AminoAcidCode> matchingAminoAcidCode) {

        this.code3 = code3;
        this.code1 = code1;
        this.matchingAminoAcidCode = matchingAminoAcidCode;
    }

    public String get3LetterCode() {
        return code3;
    }

    public String get1LetterCode() {
        return code1;
    }

    public boolean match(AminoAcidCode aminoAcidCode) {

        return matchingAminoAcidCode.contains(aminoAcidCode);
    }
}