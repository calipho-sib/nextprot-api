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

    Glycine ("Gly", 'G'),
    Proline ("Pro", 'P'),
    Alanine ("Ala", 'A'),
    Valine ("Val", 'V'),
    Leucine ("Leu", 'L'),
    Isoleucine ("Ile", 'I'),
    Methionine ("Met", 'M'),
    Cysteine ("Cys", 'C'),
    Phenylalanine ("Phe", 'F'),
    Tyrosine ("Tyr", 'Y'),
    Tryptophan ("Trp", 'W'),
    Histidine ("His", 'H'),
    Lysine ("Lys", 'K'),
    Arginine ("Arg", 'R'),
    Glutamine ("Gln", 'Q'),
    Asparagine ("Asn", 'N'),
    GlutamicAcid ("Glu", 'E'),
    AsparticAcid ("Asp", 'D'),
    Serine ("Ser", 'S'),
    Threonine("Thr", 'T'),
    Selenocysteine("Sec", 'U'),
    Pyrrolysine("Pyl", 'O'),
    Stop("Ter", '*')
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
                    return Glycine;
                case "Pro":
                    return Proline;
                case "Ala":
                    return Alanine;
                case "Val":
                    return Valine;
                case "Leu":
                    return Leucine;
                case "Ile":
                    return Isoleucine;
                case "Met":
                    return Methionine;
                case "Cys":
                    return Cysteine;
                case "Phe":
                    return Phenylalanine;
                case "Tyr":
                    return Tyrosine;
                case "Trp":
                    return Tryptophan;
                case "His":
                    return Histidine;
                case "Lys":
                    return Lysine;
                case "Arg":
                    return Arginine;
                case "Gln":
                    return Glutamine;
                case "Asn":
                    return Asparagine;
                case "Glu":
                    return GlutamicAcid;
                case "Asp":
                    return AsparticAcid;
                case "Ser":
                    return Serine;
                case "Thr":
                    return Threonine;
                case "Sec":
                    return Selenocysteine;
                case "Pyl":
                    return Pyrrolysine;
                case "Ter":
                    return Stop;
                default:
                    throw new IllegalArgumentException("No enum constant AminoAcid." + code);
            }
        }
    }

    public static AminoAcidCode valueOfOneLetterCode(char code) {

        switch (code) {
            case 'G': return Glycine;
            case 'P': return Proline;
            case 'A': return Alanine;
            case 'V': return Valine;
            case 'L': return Leucine;
            case 'I': return Isoleucine;
            case 'M': return Methionine;
            case 'C': return Cysteine;
            case 'F': return Phenylalanine;
            case 'Y': return Tyrosine;
            case 'W': return Tryptophan;
            case 'H': return Histidine;
            case 'K': return Lysine;
            case 'R': return Arginine;
            case 'Q': return Glutamine;
            case 'N': return Asparagine;
            case 'E': return GlutamicAcid;
            case 'D': return AsparticAcid;
            case 'S': return Serine;
            case 'T': return Threonine;
            case 'U': return Selenocysteine;
            case 'O': return Pyrrolysine;
            case '*': return Stop;
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
