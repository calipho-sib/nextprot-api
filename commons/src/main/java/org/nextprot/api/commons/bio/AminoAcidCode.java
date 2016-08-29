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
    STOP("Ter", "*")
    ;

    public enum AACodeType { ONE_LETTER, THREE_LETTER }

    private final String code3;
    private final String code1;
    private static final Map<String, AminoAcidCode> aminoAcidCodeMap;

    static {
        aminoAcidCodeMap = new HashMap<>(AminoAcidCode.values().length);
        for (AminoAcidCode aac : AminoAcidCode.values()) {

            aminoAcidCodeMap.put(aac.get1LetterCode(), aac);
            aminoAcidCodeMap.put(aac.get3LetterCode(), aac);
        }
    }

    AminoAcidCode(String code3, String code1) {

        this.code3 = code3;
        this.code1 = code1;
    }

    public String get3LetterCode() {
        return code3;
    }

    public String get1LetterCode() {
        return code1;
    }

    public static boolean isValidAminoAcid(String code) {

        return aminoAcidCodeMap.containsKey(code);
    }

    public static AminoAcidCode valueOfAminoAcid(String code) {

        if (isValidAminoAcid(code)) return aminoAcidCodeMap.get(code);

        throw new IllegalArgumentException("No enum constant AminoAcid." + code);
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
