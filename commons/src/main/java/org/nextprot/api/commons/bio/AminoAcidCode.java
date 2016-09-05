package org.nextprot.api.commons.bio;

import com.google.common.base.Preconditions;

import java.text.ParseException;
import java.util.*;

/**
 * Amino-acids with their representation in one letter and three letter codes.
 *
 * Created by fnikitin on 09/07/15.
 */
public enum AminoAcidCode {

    GLYCINE("Gly", "G"),
    PROLINE("Pro", "P"),
    ALANINE("Ala", "A"),
    VALINE("Val", "V"),
    LEUCINE("Leu", "L"),
    ISOLEUCINE("Ile", "I"),
    METHIONINE("Met", "M"),
    CYSTEINE("Cys", "C"),
    PHENYLALANINE("Phe", "F"),
    TYROSINE("Tyr", "Y"),
    TRYPTOPHAN("Trp", "W"),
    HISTIDINE("His", "H"),
    LYSINE("Lys", "K"),
    ARGININE("Arg", "R"),
    GLUTAMINE("Gln", "Q"),
    ASPARAGINE("Asn", "N"),
    GLUTAMIC_ACID("Glu", "E"),
    ASPARTIC_ACID("Asp", "D"),
    SERINE("Ser", "S"),
    THREONINE("Thr", "T"),
    SELENOCYSTEINE("Sec", "U"),
    PYRROLYSINE("Pyl", "O"),
    STOP("Ter", "*"),
    // ambiguous amino-acids
    ASX("Asx", "B"),
    XLE("Xle", "J"),
    GLX("Glx", "Z"),
    XAA("Xaa", "X")
    ;

    public enum AACodeType { ONE_LETTER, THREE_LETTER }

    private final String code3;
    private final String code1;

    private static final Map<String, AminoAcidCode> AMINO_ACID_CODE_MAP;
    private static final Map<AminoAcidCode, EnumSet<AminoAcidCode>> AMINO_ACID_AMBIGUITIES;
    private static final EnumSet<AminoAcidCode> NON_AMBIGUOUS_AMINO_ACIDS;
    private static final EnumSet<AminoAcidCode> AMBIGUOUS_AMINO_ACIDS;

    static {
        NON_AMBIGUOUS_AMINO_ACIDS = EnumSet.of(GLYCINE, PROLINE, ALANINE, VALINE, LEUCINE, ISOLEUCINE, METHIONINE, CYSTEINE,
                PHENYLALANINE, TYROSINE, THREONINE, TRYPTOPHAN, HISTIDINE, LYSINE, ARGININE, GLUTAMINE, ASPARAGINE,
                GLUTAMIC_ACID, ASPARTIC_ACID, SERINE, THREONINE, SELENOCYSTEINE, PYRROLYSINE, STOP);

        AMBIGUOUS_AMINO_ACIDS = EnumSet.of(ASX, XLE, GLX, XAA);

        AMINO_ACID_CODE_MAP = new HashMap<>(AminoAcidCode.values().length);
        for (AminoAcidCode aac : AminoAcidCode.values()) {

            AMINO_ACID_CODE_MAP.put(aac.get1LetterCode(), aac);
            AMINO_ACID_CODE_MAP.put(aac.get3LetterCode(), aac);
        }

        AMINO_ACID_AMBIGUITIES  = new HashMap<>(4);
        AMINO_ACID_AMBIGUITIES.put(ASX, EnumSet.of(ASPARTIC_ACID, ASPARAGINE));
        AMINO_ACID_AMBIGUITIES.put(XLE, EnumSet.of(LEUCINE, ISOLEUCINE));
        AMINO_ACID_AMBIGUITIES.put(GLX, EnumSet.of(GLUTAMIC_ACID, GLUTAMINE));
        AMINO_ACID_AMBIGUITIES.put(XAA, NON_AMBIGUOUS_AMINO_ACIDS);
    }

    AminoAcidCode(String code3, String code1) {

        this.code3 = code3;
        this.code1 = code1;
    }

    /**
     * @return the amino-acid 3-letter code
     */
    public String get3LetterCode() {
        return code3;
    }

    /**
     * @return the amino-acid 1-letter code
     */
    public String get1LetterCode() {
        return code1;
    }

    /**
     * @return true if matches the given aminoAcidCode else false
     */
    public boolean match(AminoAcidCode aminoAcidCode) {

        if (AMBIGUOUS_AMINO_ACIDS.contains(this))
            return AMINO_ACID_AMBIGUITIES.get(this).contains(aminoAcidCode);
        return this == aminoAcidCode;
    }

    /**
     * @return true if amino-acid code is valid (1- or 3-letter code)
     */
    public static boolean isValidAminoAcid(String code) {

        return AMINO_ACID_CODE_MAP.containsKey(code);
    }

    /**
     * @return an AminoAcidCode given a string
     */
    public static AminoAcidCode valueOfAminoAcid(String code) {

        if (isValidAminoAcid(code)) return AMINO_ACID_CODE_MAP.get(code);

        throw new IllegalArgumentException("No enum constant AminoAcid." + code);
    }

    /**
     * @return set of non ambiguous amino-acids
     */
    public static Set<AminoAcidCode> nonAmbiguousAminoAcidValues() {

        return NON_AMBIGUOUS_AMINO_ACIDS;
    }

    /**
     * @return set of ambiguous amino-acids
     */
    public static Set<AminoAcidCode> ambiguousAminoAcidValues() {

        return AMBIGUOUS_AMINO_ACIDS;
    }

    public static AminoAcidCode[] valueOfOneLetterCodeSequence(String sequence) {

        List<Integer> ucs = new ArrayList<>();

        for (int i=0 ; i<sequence.length() ; i++) {
            char c = sequence.charAt(i);
            if (Character.isUpperCase(c) || c == '*') ucs.add(i);
        }

        if (ucs.get(0) != 0) throw new IllegalArgumentException("First amino-acid is not known: Not a valid sequence of AminoAcid sequence");

        AminoAcidCode[] codes = new AminoAcidCode[ucs.size()];

        int i=0;
        while (i<ucs.size()) {

            int start = ucs.get(i);
            int end = ((i+1) < ucs.size()) ? ucs.get(i+1) : sequence.length();

            codes[i] = AminoAcidCode.valueOfAminoAcid(sequence.substring(start, end));

            i++;
        }

        return codes;
    }

    public static AminoAcidCode[] asArray(AminoAcidCode aminoAcidCode) {

        Preconditions.checkNotNull(aminoAcidCode);

        return new AminoAcidCode[] { aminoAcidCode };
    }

    public static String formatAminoAcidCode(AACodeType type, AminoAcidCode... aas) {

        StringBuilder sb = new StringBuilder();

        for (AminoAcidCode aa : aas) {

            sb.append((type == AACodeType.ONE_LETTER) ? aa.get1LetterCode() : aa.get3LetterCode());
        }

        return sb.toString();
    }

    public static AminoAcidCode valueOfAminoAcidCode(String code1, String code2and3) throws ParseException {

        if (code2and3 == null) {
            if (!AminoAcidCode.isValidAminoAcid(code1)) {
                throw new ParseException(code1+": invalid AminoAcidCode", 0);
            }
            return AminoAcidCode.valueOfAminoAcid(code1);
        }
        else if (!AminoAcidCode.isValidAminoAcid(code1 + code2and3)) {
            throw new ParseException(code1 + code2and3 + ": invalid AminoAcidCode", 2);
        }
        return AminoAcidCode.valueOfAminoAcid(code1 + code2and3);
    }
}
