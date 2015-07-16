package org.nextprot.api.commons.bio;

import java.util.ArrayList;
import java.util.List;

/**
 * Amino-acid 1- and 3-letter symbols
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

    private final String code3;
    private final char code1;

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

    public static AminoAcidCode valueOfCode(String code) {

        if (code.length() == 1)
            return valueOfCode1AA(code.charAt(0));
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
                    throw new IllegalArgumentException("No enum constant AminoAcidCode." + code);
            }
        }
    }

    public static AminoAcidCode valueOfCode1AA(char code) {

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
            default: throw new IllegalArgumentException( "No enum constant AminoAcidCode." + code);
        }
    }

    public static AminoAcidCode[] valueOfCodeSequence(String sequence) {

        List<Integer> ucs = new ArrayList<>();

        for (int i=0 ; i<sequence.length() ; i++) {
            char c = sequence.charAt(i);
            if (Character.isUpperCase(c) || c == '*') ucs.add(i);
        }

        if (ucs.get(0) != 0) throw new IllegalArgumentException("First amino-acid is not known: Not a valid sequence of AminoAcidCode sequence");

        AminoAcidCode[] codes = new AminoAcidCode[ucs.size()];

        int i=0;
        while (i<ucs.size()) {

            int start = ucs.get(i);
            int end = ((i+1) < ucs.size()) ? ucs.get(i+1) : sequence.length();

            codes[i] = AminoAcidCode.valueOfCode(sequence.substring(start, end));

            i++;
        }

        return codes;
    }
}
