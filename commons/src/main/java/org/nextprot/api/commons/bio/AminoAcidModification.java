package org.nextprot.api.commons.bio;

import java.util.HashSet;
import java.util.Set;

/**
 * Amino-acids with their representation in one letter and three letter codes.
 *
 * Created by fnikitin on 09/07/15.
 */
public enum AminoAcidModification {

    Acetyl("Ac"),             // acetylation
    Carbohydrate("carb"),     // glycosylation
    Dimethyl("dimethyl"),     // dimethylation
    Geranylgeranyl("ger"),    // geranylgeranylation
    Farnesyl("SFarn"),        // farnesylation
    Myristate("myr"),         // myristoylation
    Nitro("nitro"),           // nitration
    Phosphate("P"),           // phosphorylation
    Palmitate("palm"),        // palmitoylation
    PolyADP_Ribose("PAR"),    // polyADP-ribosylation
    S_Nitroso("SNO"),         // S-nitrosation
    SUMO("sumo"),             // SUMOylation
    Ubiquitin("ubi")          // ubiquitination
    ;

    private final static Set<String> validNames;

    static {
        validNames = new HashSet<>(AminoAcidModification.values().length);
        for (AminoAcidModification mod : AminoAcidModification.values()) {

            validNames.add(mod.getName());
        }
    }
    private final String name;

    AminoAcidModification(String name) {

        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static boolean isValidAminoAcidModiciation(String name) {

        return validNames.contains(name);
    }

    public static AminoAcidModification valueOfAminoAcidModification(String name) {

        switch (name.toLowerCase()) {
            case "ac":
                return Acetyl;
            case "carb":
                return Carbohydrate;
            case "dimethyl":
                return Dimethyl;
            case "ger":
                return Geranylgeranyl;
            case "sfarn":
                return Farnesyl;
            case "myr":
                return Myristate;
            case "nitro":
                return Nitro;
            case "p":
                return Phosphate;
            case "palm":
                return Palmitate;
            case "par":
                return PolyADP_Ribose;
            case "sno":
                return S_Nitroso;
            case "sumo":
                return SUMO;
            case "ubi":
                return Ubiquitin;
            default:
                throw new IllegalArgumentException("No enum constant AminoAcidModification." + name);
        }
    }
}
