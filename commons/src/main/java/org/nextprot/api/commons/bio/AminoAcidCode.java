package org.nextprot.api.commons.bio;

import com.google.common.base.Preconditions;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Amino-acids with their representation in one letter and three letter codes.
 *
 * Created by fnikitin on 09/07/15.
 */
public enum AminoAcidCode {

    GLYCINE("Gly", 'G'),
    PROLINE("Pro", 'P'),
    ALANINE("Ala", 'A'),
    VALINE("Val", 'V'),
    LEUCINE("Leu", 'L'),
    ISOLEUCINE("Ile", 'I'),
    METHIONINE("Met", 'M'),
    CYSTEINE("Cys", 'C'),
    PHENYLALANINE("Phe", 'F'),
    TYROSINE("Tyr", 'Y'),
    TRYPTOPHAN("Trp", 'W'),
    HISTIDINE("His", 'H'),
    LYSINE("Lys", 'K'),
    ARGININE("Arg", 'R'),
    GLUTAMINE("Gln", 'Q'),
    ASPARAGINE("Asn", 'N'),
    GLUTAMIC_ACID("Glu", 'E'),
    ASPARTIC_ACID("Asp", 'D'),
    SERINE("Ser", 'S'),
    THREONINE("Thr", 'T'),
    SELENOCYSTEINE("Sec", 'U'),
    PYRROLYSINE("Pyl", 'O'),
    STOP("Ter", '*')
    ;

    public enum AACodeType { ONE_LETTER, THREE_LETTER }

    private final String code3;
    private final char code1;
    private final static Set<String> validCodes;

    static {
        validCodes = new HashSet<>(46);
        for (AminoAcidCode aac : AminoAcidCode.values()) {

            validCodes.add(String.valueOf(aac.code1));
            validCodes.add(aac.code3);
        }
    }

    AminoAcidCode(String code3, char code1) {

        this.code3 = code3;
        this.code1 = code1;
    }

    public String get3LetterCode() {
        return code3;
    }

    public char get1LetterCode() {
        return code1;
    }

    public static boolean isValidAminoAcid(String code) {

        return validCodes.contains(code);
    }

    public static AminoAcidCode valueOfAminoAcid(String code) {

        switch (code) {
            case "Gly":
            case "G":
                return GLYCINE;
            case "Pro":
            case "P":
                return PROLINE;
            case "Ala":
            case "A":
                return ALANINE;
            case "Val":
            case "V":
                return VALINE;
            case "Leu":
            case "L":
                return LEUCINE;
            case "Ile":
            case "I":
                return ISOLEUCINE;
            case "Met":
            case "M":
                return METHIONINE;
            case "Cys":
            case "C":
                return CYSTEINE;
            case "Phe":
            case "F":
                return PHENYLALANINE;
            case "Tyr":
            case "Y":
                return TYROSINE;
            case "Trp":
            case "W":
                return TRYPTOPHAN;
            case "His":
            case "H":
                return HISTIDINE;
            case "Lys":
            case "K":
                return LYSINE;
            case "Arg":
            case "R":
                return ARGININE;
            case "Gln":
            case "Q":
                return GLUTAMINE;
            case "Asn":
            case "N":
                return ASPARAGINE;
            case "Glu":
            case "E":
                return GLUTAMIC_ACID;
            case "Asp":
            case "D":
                return ASPARTIC_ACID;
            case "Ser":
            case "S":
                return SERINE;
            case "Thr":
            case "T":
                return THREONINE;
            case "Sec":
            case "U":
                return SELENOCYSTEINE;
            case "Pyl":
            case "O":
                return PYRROLYSINE;
            case "Ter":
            case "*":
                return STOP;
            default:
                throw new IllegalArgumentException("No enum constant AminoAcid." + code);
        }
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

            sb.append((type == AACodeType.ONE_LETTER) ?
                    String.valueOf(aa.get1LetterCode()) :
                    String.valueOf(aa.get3LetterCode()));
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
