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

        if (code.length() == 1)
            return valueOfOneLetterCode(code.charAt(0));
        else {
            switch (code) {
                case "Gly":
                    return GLYCINE;
                case "Pro":
                    return PROLINE;
                case "Ala":
                    return ALANINE;
                case "Val":
                    return VALINE;
                case "Leu":
                    return LEUCINE;
                case "Ile":
                    return ISOLEUCINE;
                case "Met":
                    return METHIONINE;
                case "Cys":
                    return CYSTEINE;
                case "Phe":
                    return PHENYLALANINE;
                case "Tyr":
                    return TYROSINE;
                case "Trp":
                    return TRYPTOPHAN;
                case "His":
                    return HISTIDINE;
                case "Lys":
                    return LYSINE;
                case "Arg":
                    return ARGININE;
                case "Gln":
                    return GLUTAMINE;
                case "Asn":
                    return ASPARAGINE;
                case "Glu":
                    return GLUTAMIC_ACID;
                case "Asp":
                    return ASPARTIC_ACID;
                case "Ser":
                    return SERINE;
                case "Thr":
                    return THREONINE;
                case "Sec":
                    return SELENOCYSTEINE;
                case "Pyl":
                    return PYRROLYSINE;
                case "Ter":
                    return STOP;
                default:
                    throw new IllegalArgumentException("No enum constant AminoAcid." + code);
            }
        }
    }

    public static AminoAcidCode valueOfOneLetterCode(char code) {

        switch (code) {
            case 'G': return GLYCINE;
            case 'P': return PROLINE;
            case 'A': return ALANINE;
            case 'V': return VALINE;
            case 'L': return LEUCINE;
            case 'I': return ISOLEUCINE;
            case 'M': return METHIONINE;
            case 'C': return CYSTEINE;
            case 'F': return PHENYLALANINE;
            case 'Y': return TYROSINE;
            case 'W': return TRYPTOPHAN;
            case 'H': return HISTIDINE;
            case 'K': return LYSINE;
            case 'R': return ARGININE;
            case 'Q': return GLUTAMINE;
            case 'N': return ASPARAGINE;
            case 'E': return GLUTAMIC_ACID;
            case 'D': return ASPARTIC_ACID;
            case 'S': return SERINE;
            case 'T': return THREONINE;
            case 'U': return SELENOCYSTEINE;
            case 'O': return PYRROLYSINE;
            case '*': return STOP;
            default: throw new IllegalArgumentException( "No enum constant AminoAcid." + code);
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

            if (type == AACodeType.ONE_LETTER) sb.append(String.valueOf(aa.get1LetterCode()));
            else sb.append(String.valueOf(aa.get3LetterCode()));
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
