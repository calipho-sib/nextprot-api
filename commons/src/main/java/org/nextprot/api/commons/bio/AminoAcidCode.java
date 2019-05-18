package org.nextprot.api.commons.bio;

import com.fasterxml.jackson.annotation.JsonValue;
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
    SELENOCYSTEINE("Sec", "U"),		// encoded by TGA codon like STOP, 45 isoforms contain it
    PYRROLYSINE("Pyl", "O"),      	// encoded by TAG codon like STOP  NO isoforms contains it
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
    private static final Map<String,AminoAcidCode> CODON_AMINO_ACID_MAP = new HashMap<>(); 

    static {
    	
    	CODON_AMINO_ACID_MAP.put("GCA", ALANINE);
    	CODON_AMINO_ACID_MAP.put("GCC", ALANINE);
    	CODON_AMINO_ACID_MAP.put("GCG", ALANINE);
    	CODON_AMINO_ACID_MAP.put("GCT", ALANINE);
    	CODON_AMINO_ACID_MAP.put("TGC", CYSTEINE);
    	CODON_AMINO_ACID_MAP.put("TGT", CYSTEINE);
    	CODON_AMINO_ACID_MAP.put("GAC", ASPARTIC_ACID);
    	CODON_AMINO_ACID_MAP.put("GAT", ASPARTIC_ACID);
    	CODON_AMINO_ACID_MAP.put("GAA", GLUTAMIC_ACID);
    	CODON_AMINO_ACID_MAP.put("GAG", GLUTAMIC_ACID);
    	CODON_AMINO_ACID_MAP.put("TTC", PHENYLALANINE);
    	CODON_AMINO_ACID_MAP.put("TTT", PHENYLALANINE);
    	CODON_AMINO_ACID_MAP.put("GGA", GLYCINE);
    	CODON_AMINO_ACID_MAP.put("GGC", GLYCINE);
    	CODON_AMINO_ACID_MAP.put("GGG", GLYCINE);
    	CODON_AMINO_ACID_MAP.put("GGT", GLYCINE);
    	CODON_AMINO_ACID_MAP.put("CAC", HISTIDINE);
    	CODON_AMINO_ACID_MAP.put("CAT", HISTIDINE);
    	CODON_AMINO_ACID_MAP.put("ATA", ISOLEUCINE);
    	CODON_AMINO_ACID_MAP.put("ATC", ISOLEUCINE);
    	CODON_AMINO_ACID_MAP.put("ATT", ISOLEUCINE);
    	CODON_AMINO_ACID_MAP.put("AAA", LYSINE);
    	CODON_AMINO_ACID_MAP.put("AAG", LYSINE);
    	CODON_AMINO_ACID_MAP.put("CTA", LEUCINE);
    	CODON_AMINO_ACID_MAP.put("CTC", LEUCINE);
    	CODON_AMINO_ACID_MAP.put("CTG", LEUCINE);
    	CODON_AMINO_ACID_MAP.put("CTT", LEUCINE);
    	CODON_AMINO_ACID_MAP.put("TTA", LEUCINE);
    	CODON_AMINO_ACID_MAP.put("TTG", LEUCINE);
    	CODON_AMINO_ACID_MAP.put("ATG", METHIONINE);
    	CODON_AMINO_ACID_MAP.put("AAC", ASPARAGINE);
    	CODON_AMINO_ACID_MAP.put("AAT", ASPARAGINE);
    	CODON_AMINO_ACID_MAP.put("CCA", PROLINE);
    	CODON_AMINO_ACID_MAP.put("CCC", PROLINE);
    	CODON_AMINO_ACID_MAP.put("CCG", PROLINE);
    	CODON_AMINO_ACID_MAP.put("CCT", PROLINE);
    	CODON_AMINO_ACID_MAP.put("CAA", GLUTAMINE);
    	CODON_AMINO_ACID_MAP.put("CAG", GLUTAMINE);
    	CODON_AMINO_ACID_MAP.put("AGA", ARGININE);
    	CODON_AMINO_ACID_MAP.put("AGG", ARGININE);
    	CODON_AMINO_ACID_MAP.put("CGA", ARGININE);
    	CODON_AMINO_ACID_MAP.put("CGC", ARGININE);
    	CODON_AMINO_ACID_MAP.put("CGG", ARGININE);
    	CODON_AMINO_ACID_MAP.put("CGT", ARGININE);
    	CODON_AMINO_ACID_MAP.put("AGC", SERINE);
    	CODON_AMINO_ACID_MAP.put("AGT", SERINE);
    	CODON_AMINO_ACID_MAP.put("TCA", SERINE);
    	CODON_AMINO_ACID_MAP.put("TCC", SERINE);
    	CODON_AMINO_ACID_MAP.put("TCG", SERINE);
    	CODON_AMINO_ACID_MAP.put("TCT", SERINE);
    	CODON_AMINO_ACID_MAP.put("ACA", THREONINE);
    	CODON_AMINO_ACID_MAP.put("ACC", THREONINE);
    	CODON_AMINO_ACID_MAP.put("ACG", THREONINE);
    	CODON_AMINO_ACID_MAP.put("ACT", THREONINE);
    	CODON_AMINO_ACID_MAP.put("GTA", VALINE);
    	CODON_AMINO_ACID_MAP.put("GTC", VALINE);
    	CODON_AMINO_ACID_MAP.put("GTG", VALINE);
    	CODON_AMINO_ACID_MAP.put("GTT", VALINE);
    	CODON_AMINO_ACID_MAP.put("TGG", TRYPTOPHAN);
    	CODON_AMINO_ACID_MAP.put("TAC", TYROSINE);
    	CODON_AMINO_ACID_MAP.put("TAT", TYROSINE);
    	CODON_AMINO_ACID_MAP.put("TAA", STOP);
    	CODON_AMINO_ACID_MAP.put("TAG", STOP);   // or PYRROLYSINE    : NO isoforms contain it
    	CODON_AMINO_ACID_MAP.put("TGA", STOP);   // or SELENOCYSTEINE : 45 isoforms contain it

    	
    	
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
    @JsonValue
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
     * Check that the given amino-acid match this amino-acid
     * @param aminoAcidCode amino-acid to check
     * @return true if matches the given aminoAcidCode else false
     */
    public boolean match(AminoAcidCode aminoAcidCode) {

        if (isAmbiguous())
            return AMINO_ACID_AMBIGUITIES.get(this).contains(aminoAcidCode);
        return this == aminoAcidCode;
    }

    /**
     * Check validity of the given amino-acid code string
     * @param code the amino-acid 1- or 3- letter code
     * @return true if amino-acid code is valid else false
     */
    public static boolean isValidAminoAcid(String code) {

        return AMINO_ACID_CODE_MAP.containsKey(code);
    }

    /**
     * Get an instance of AminoAcidCode for the given amino-acid code string
     * @param code the amino-acid 1- or 3- letter code
     * @return an AminoAcidCode given a string
     */
    public static AminoAcidCode valueOfAminoAcid(String code) {

        if (!isValidAminoAcid(code)) {
            throw new IllegalArgumentException("No enum constant AminoAcid." + code);
        }
        return AMINO_ACID_CODE_MAP.get(code);
    }

    /**
     * Get the set of non ambiguous amino-acids
     * @return immutable set of non ambiguous amino-acids
     */
    public static Set<AminoAcidCode> nonAmbiguousAminoAcidValues() {

        return NON_AMBIGUOUS_AMINO_ACIDS;
    }

    /**
     * Get the set of ambiguous amino-acids
     * @return immutable set of ambiguous amino-acids
     */
    public static Set<AminoAcidCode> ambiguousAminoAcidValues() {

        return AMBIGUOUS_AMINO_ACIDS;
    }


    /**
     * Parse sequence and make an instance of AminoAcidCode array (auto CodeType deduction)
     * @param sequence the sequence to parse
     * @return an array of AminoAcidCode
     */
    public static AminoAcidCode[] valueOfAminoAcidCodeSequence(String sequence) {

        Preconditions.checkNotNull(sequence);

        if (sequence.length()>=3 && AminoAcidCode.isValidAminoAcid(sequence.substring(0, 3))) {

            return valueOfAminoAcidCodeSequence(sequence, CodeType.THREE_LETTER);
        }

        return valueOfAminoAcidCodeSequence(sequence, CodeType.ONE_LETTER);
    }

    /**
     * Parse sequence and make an instance of AminoAcidCode array
     * @param sequence the sequence to parse
     * @param codeType the amino-acid code type of the given sequence
     * @return an array of AminoAcidCode
     */
    public static AminoAcidCode[] valueOfAminoAcidCodeSequence(String sequence, CodeType codeType) {

        Preconditions.checkNotNull(sequence);
        Preconditions.checkNotNull(codeType);

        if ((sequence.length() % codeType.getCodeLen()) != 0) {

            throw new IllegalArgumentException("Invalid sequence length: " + sequence + " length is not a multiple of " + codeType);
        }

        int aminoAcidCount = sequence.length()/codeType.getCodeLen();

        AminoAcidCode[] aminoAcidCodes = new AminoAcidCode[aminoAcidCount];

        int from=0;
        int aaIndex=0;
        while (from<=sequence.length()-codeType.getCodeLen()) {

            int to = from+codeType.getCodeLen();

            aminoAcidCodes[aaIndex] = AminoAcidCode.valueOfAminoAcid(sequence.substring(from, to));
            from = to;
            aaIndex++;
        }

        return aminoAcidCodes;
    }

    /**
     * Format AminoAcidCodes into string
     * @param type the amino-acid code type (1- or 3- letter code)
     * @param aas amino-acids to format
     * @return a formatted string
     */
    public static String formatAminoAcidCode(CodeType type, AminoAcidCode... aas) {

        StringBuilder sb = new StringBuilder();

        for (AminoAcidCode aa : aas) {

            sb.append((type == CodeType.ONE_LETTER) ? aa.get1LetterCode() : aa.get3LetterCode());
        }

        return sb.toString();
    }

    /**
     * Get an instance of AminoAcidCode given
     * @param code amino-acid code (1- or 3- letters)
     * @return an AminoAcidCode
     * @throws ParseException if code is not well formatted
     */
    public static AminoAcidCode parseAminoAcidCode(String code) throws ParseException {

        Preconditions.checkNotNull(code);
        Preconditions.checkArgument(code.length() == 1 || code.length() == 3, "amino-acid code should be in 1 letter or 3 letters format");

        if (!AminoAcidCode.isValidAminoAcid(code)) {
            throw new ParseException(code+": invalid AminoAcidCode", 0);
        }

        return AminoAcidCode.valueOfAminoAcid(code);
    }

    /**
     * Get an instance of AminoAcidCode given
     * @param code1 one letter amino-acid code
     * @return an AminoAcidCode
     * @throws IllegalArgumentException if code1 is not found
     */
    public static AminoAcidCode valueOfAminoAcid1LetterCode(char code1) {

        try {
            return parseAminoAcidCode(String.valueOf(code1));
        } catch (ParseException e) {
            throw new IllegalArgumentException("unknown 1-letter code amino-acid "+code1, e);
        }
    }
    
    /**
     * Get an instance of AminoAcidCode given a codon
     * @param codon a String of three characters representing three nucleotides letters
     * @return an AminoAcidCode
     * @throws IllegalArgumentException if codon is not found
     */
    public static AminoAcidCode valueOfAminoAcidFromCodon(String codon) {

    	AminoAcidCode aa = CODON_AMINO_ACID_MAP.get(codon);
    	if (aa != null) {
            return aa;
        } else {
            throw new IllegalArgumentException("unknown codon "+ codon);
        }
    }
    
    
    
    
}
