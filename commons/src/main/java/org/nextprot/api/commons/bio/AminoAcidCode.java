package org.nextprot.api.commons.bio;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableSet;

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

    /** Type of amino-acid code defined by its number of letters */
    public enum CodeType {

        ONE_LETTER(1),
        THREE_LETTER(3)
        ;
        private final int len;
        CodeType(int len) {

            this.len = len;
        }

        public int getCodeLen() {
            return len;
        }
    }

    private final String code3;
    private final String code1;

    private static final Map<String, AminoAcidCode> AMINO_ACID_CODE_MAP;
    private static final Map<AminoAcidCode, Set<AminoAcidCode>> AMINO_ACID_AMBIGUITIES;
    private static final Set<AminoAcidCode> NON_AMBIGUOUS_AMINO_ACIDS;
    private static final Set<AminoAcidCode> AMBIGUOUS_AMINO_ACIDS;

    static {
        NON_AMBIGUOUS_AMINO_ACIDS = ImmutableSet.copyOf(EnumSet.of(GLYCINE, PROLINE, ALANINE, VALINE, LEUCINE, ISOLEUCINE, METHIONINE, CYSTEINE,
                PHENYLALANINE, TYROSINE, THREONINE, TRYPTOPHAN, HISTIDINE, LYSINE, ARGININE, GLUTAMINE, ASPARAGINE,
                GLUTAMIC_ACID, ASPARTIC_ACID, SERINE, THREONINE, SELENOCYSTEINE, PYRROLYSINE, STOP));

        AMBIGUOUS_AMINO_ACIDS = ImmutableSet.copyOf(EnumSet.of(ASX, XLE, GLX, XAA));

        AMINO_ACID_CODE_MAP = new HashMap<>(AminoAcidCode.values().length);
        for (AminoAcidCode aac : AminoAcidCode.values()) {

            AMINO_ACID_CODE_MAP.put(aac.get1LetterCode(), aac);
            AMINO_ACID_CODE_MAP.put(aac.get3LetterCode(), aac);
        }

        AMINO_ACID_AMBIGUITIES = new EnumMap<>(AminoAcidCode.class);
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
     * @return true if this aminoAcidCode is ambiguous else false
     */
    public boolean isAmbiguous() {

        return AMBIGUOUS_AMINO_ACIDS.contains(this);
    }

    /**
     * @return true if matches the given aminoAcidCode else false
     */
    public boolean match(AminoAcidCode aminoAcidCode) {

        if (isAmbiguous())
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
     * @return immutable set of non ambiguous amino-acids
     */
    public static Set<AminoAcidCode> nonAmbiguousAminoAcidValues() {

        return NON_AMBIGUOUS_AMINO_ACIDS;
    }

    /**
     * @return immutable set of ambiguous amino-acids
     */
    public static Set<AminoAcidCode> ambiguousAminoAcidValues() {

        return AMBIGUOUS_AMINO_ACIDS;
    }


    /**
     * Parse sequence and make an instance of AminoAcidCode array by deducing code type
     * @param sequence the sequence to parse
     */
    public static AminoAcidCode[] valueOfAminoAcidCodeSequence(String sequence) {

        Preconditions.checkNotNull(sequence);

        if (sequence.length()>=3 && AminoAcidCode.isValidAminoAcid(sequence.substring(0, 3))) {

            return valueOfAminoAcidCodeSequence(sequence, CodeType.THREE_LETTER);
        }

        return valueOfAminoAcidCodeSequence(sequence, CodeType.ONE_LETTER);
    }

    /**
     * Parse sequence and make an instance of AminoAcidCode array.
     * @param sequence the sequence to parse
     * @param codeType the code type
     */
    public static AminoAcidCode[] valueOfAminoAcidCodeSequence(String sequence, CodeType codeType) {

        Preconditions.checkNotNull(sequence);
        Preconditions.checkNotNull(codeType);

        if ((sequence.length() % codeType.getCodeLen()) != 0) throw new IllegalArgumentException("Invalid sequence length: "+sequence +" length is not a multiple of "+codeType);

        int aminoAcidCount = sequence.length()/codeType.getCodeLen();

        AminoAcidCode[] aminoAcidCodes = new AminoAcidCode[aminoAcidCount];

        int from=0;
        int aa_index=0;
        while (from<=sequence.length()-codeType.getCodeLen()) {

            int to = from+codeType.getCodeLen();

            aminoAcidCodes[aa_index] = AminoAcidCode.valueOfAminoAcid(sequence.substring(from, to));
            from = to;
            aa_index++;
        }

        return aminoAcidCodes;
    }

    public static AminoAcidCode[] asArray(AminoAcidCode aminoAcidCode) {

        Preconditions.checkNotNull(aminoAcidCode);

        return new AminoAcidCode[] { aminoAcidCode };
    }

    public static String formatAminoAcidCode(CodeType type, AminoAcidCode... aas) {

        StringBuilder sb = new StringBuilder();

        for (AminoAcidCode aa : aas) {

            sb.append((type == CodeType.ONE_LETTER) ? aa.get1LetterCode() : aa.get3LetterCode());
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
